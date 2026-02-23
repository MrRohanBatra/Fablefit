
import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from transformers import CLIPModel, CLIPProcessor
import torch
from PIL import Image
from dotenv import load_dotenv
from databaseSchemas.ProductSchema import Product
load_dotenv()

class ClipService:

    def __init__(self):
        self.enabled=os.getenv("ENABLE_CLIP_MODEL","true").lower()=="true"
        if(not self.enabled):
            print("⚠️ CLIP_SERVICE: Disabled via environment variable. Skipping model load.")
            return
        self.device = "cuda" if torch.cuda.is_available() else "cpu"

        self.model = CLIPModel.from_pretrained(
            os.getenv("CLIP_MODEL_NAME", "openai/clip-vit-base-patch32")
        ).to(self.device) # type: ignore

        self.processor = CLIPProcessor.from_pretrained(
            os.getenv("CLIP_MODEL_NAME", "openai/clip-vit-base-patch32")
        )

        self.model.eval()

    # ---------------------------------
    # TEXT EMBEDDING (SAFE WAY)
    # ---------------------------------
    def get_optimal_batch_size(self):
        if self.device != "cuda":
            return 8

        free, total = torch.cuda.mem_get_info()
        total_gb = total / (1024 ** 3)

        # Rough heuristic for CLIP ViT-B/32
        if total_gb >= 70:
            return 256      # your HPC (80GB)
        elif total_gb >= 20:
            return 128
        elif total_gb >= 10:
            return 64
        elif total_gb >= 6:
            return 32
        else:
            return 8        # your 4GB local
    def generate_text_embedding(self, text: str):
        if not self.enabled:
            return [0.0]*512
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
        if not self.enabled:
            return [0.0]*512
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
                image_features = outputs.image_embeds # type: ignore
            elif hasattr(outputs, "pooler_output"):
                # If we get here, it means the projection layer was bypassed,
                # but we will extract the tensor to prevent the app from crashing.
                image_features = outputs.pooler_output # type: ignore
            else:
                image_features = outputs[0]

            # ✅ tensor
            # image_features = outputs.image_embeds

        image_features = image_features / image_features.norm(dim=-1, keepdim=True) # type: ignore

        return image_features[0].cpu().numpy().tolist()

    # ---------------------------------
    # COMBINED PRODUCT EMBEDDING
    # ---------------------------------
    def build_clip_text(self,product:Product):
        parts = [
            product.name,
            f"Category: {product.category}",
            f"Color: {product.color}",
            f"Sizes: {', '.join(product.sizes)}",
            f"Brand: {product.companyName}",
        ]
        if product.vton_category:
            parts.append(f"Type: {product.vton_category}")
        if product.description:
            parts.append(product.description[:120])
        return ". ".join(parts)

    def generate_embedding(self, product: Product):
        if not self.enabled:
            return [0.0]*1024
        text = self.build_clip_text(product)
        image_path = product.images[0]

        text_emb = torch.tensor(self.generate_text_embedding(text), device=self.device)
        image_emb = torch.tensor(self.generate_image_embedding(image_path), device=self.device)

        combined = torch.cat([text_emb, image_emb])
        combined = combined / combined.norm()

        return combined.cpu().numpy().tolist()
    async def reindex_all_products(self):
        batch_size = self.get_optimal_batch_size()

        print(f"🧠 Using batch size: {batch_size}")

        total = await Product.count()
        cursor = Product.find_all()

        batch = []
        processed = 0

        async for product in cursor:
            batch.append(product)

            if len(batch) >= batch_size:
                await self._process_batch(batch)
                processed += len(batch)
                print(f"✅ {processed}/{total}")
                batch = []

        if batch:
            await self._process_batch(batch)

        print("🎉 Reindex complete")
    async def _process_batch(self, products):
        if not products:
            return

        texts = []
        images = []
        valid_products = []

        # -----------------------------------
        # Load inputs
        # -----------------------------------
        for product in products:
            if not product.images:
                continue

            try:
                image_path = product.images[0]

                if image_path.startswith("/"):
                    image_path = image_path[1:]

                image = Image.open(image_path).convert("RGB")

                texts.append(self.build_clip_text(product))
                images.append(image)
                valid_products.append(product)

            except Exception as e:
                print(f"⚠️ Skipping {product.id}: {e}")

        if not valid_products:
            return

        # -----------------------------------
        # Prepare batches
        # -----------------------------------
        text_inputs = self.processor(
            text=texts,
            padding=True,
            truncation=True,
            return_tensors="pt"
        )

        image_inputs = self.processor(
            images=images,
            return_tensors="pt" # type: ignore
        )

        text_inputs = {k: v.to(self.device) for k, v in text_inputs.items()}
        image_inputs = {k: v.to(self.device) for k, v in image_inputs.items()}

        # -----------------------------------
        # Inference
        # -----------------------------------
        with torch.no_grad():
            text_features = self.model.get_text_features(**text_inputs)
            image_features = self.model.get_image_features(**image_inputs)

        # -----------------------------------
        # Normalize
        # -----------------------------------
        text_features = text_features / text_features.norm(dim=-1, keepdim=True)
        image_features = image_features / image_features.norm(dim=-1, keepdim=True)

        # -----------------------------------
        # Concatenate → 1024 dims
        # -----------------------------------
        combined = torch.cat([text_features, image_features], dim=1)
        combined = combined / combined.norm(dim=-1, keepdim=True)

        # -----------------------------------
        # Write back to Mongo
        # -----------------------------------
        for product, emb in zip(valid_products, combined):
            product.embedding = emb.cpu().tolist()

        await Product.bulk_save(valid_products) # type: ignore

        # -----------------------------------
        # Cleanup (important on long runs)
        # -----------------------------------
        if self.device == "cuda":
            torch.cuda.empty_cache()

ClipServiceModel = ClipService()