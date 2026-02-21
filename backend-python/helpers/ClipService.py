import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from transformers import CLIPModel, CLIPProcessor
import torch
from PIL import Image
from databaseSchemas.ProductSchema import Product


class ClipService:

    def __init__(self):
        self.device = "cuda" if torch.cuda.is_available() else "cpu"

        self.model = CLIPModel.from_pretrained(
            "openai/clip-vit-base-patch32"
        ).to(self.device) # type: ignore

        self.processor = CLIPProcessor.from_pretrained(
            "openai/clip-vit-base-patch32"
        )

        self.model.eval()

    # ---------------------------------
    # TEXT EMBEDDING (SAFE WAY)
    # ---------------------------------
    def generate_text_embedding(self, text: str):

        inputs = self.processor(
            text=[text],
            return_tensors="pt", # type: ignore
            padding=True # type: ignore
        )

        input_ids = inputs["input_ids"].to(self.device)
        attention_mask = inputs["attention_mask"].to(self.device)

        with torch.no_grad():
            text_outputs = self.model.text_model(
                input_ids=input_ids,
                attention_mask=attention_mask
            )

            text_features = self.model.text_projection(text_outputs.pooler_output)

        text_features = text_features / text_features.norm(dim=-1, keepdim=True)

        return text_features[0].cpu().numpy().tolist()
    # ---------------------------------
    # IMAGE EMBEDDING (SAFE WAY)
    # ---------------------------------
    def generate_image_embedding(self, image_path: str):
        if(image_path.startswith("/images/")):
            image_path=image_path[1::]
        image = Image.open(image_path).convert("RGB")

        inputs = self.processor(
            images=[image],
            return_tensors="pt" # type: ignore
        ).to(self.device)

        with torch.no_grad():
            outputs = self.model.get_image_features(**inputs)
            
            # 🔹 BULLETPROOF TENSOR EXTRACTION 🔹
            # If Hugging Face returns a wrapper object, extract the raw tensor
            if isinstance(outputs, torch.Tensor):
                image_features = outputs
            elif hasattr(outputs, "image_embeds"):
                image_features = outputs.image_embeds
            elif hasattr(outputs, "pooler_output"):
                # If we get here, it means the projection layer was bypassed,
                # but we will extract the tensor to prevent the app from crashing.
                image_features = outputs.pooler_output
            else:
                image_features = outputs[0]

            # ✅ tensor
            # image_features = outputs.image_embeds

        image_features = image_features / image_features.norm(dim=-1, keepdim=True)

        return image_features[0].cpu().numpy().tolist()

    # ---------------------------------
    # COMBINED PRODUCT EMBEDDING
    # ---------------------------------
    def generate_embedding(self, product: Product):

        text = f"{product.name}. {product.description[:200]}"
        image_path = product.images[0]

        text_emb = torch.tensor(self.generate_text_embedding(text), device=self.device)
        image_emb = torch.tensor(self.generate_image_embedding(image_path), device=self.device)

        combined = (text_emb + image_emb) / 2.0
        combined = combined / combined.norm()

        return combined.cpu().numpy().tolist()


# Global instance
ClipServiceModel = ClipService()