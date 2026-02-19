import re
import torch
from typing import Optional
from transformers import CLIPProcessor, CLIPModel

# ----------------------------
# CLIP MODEL SETUP
# ----------------------------
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
model = CLIPModel.from_pretrained("openai/clip-vit-base-patch32").to(DEVICE)
processor = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch32")
model.eval()

# ----------------------------
# 1️⃣ GENDER DETECTION
# ----------------------------
MEN_WORDS   = {"men", "man", "mens", "male", "guy"}
WOMEN_WORDS = {"women", "woman", "ladies", "female", "girl"}
KIDS_WORDS  = {"kids", "child", "children", "kid"}

def detect_gender(query: str) -> Optional[str]:
    words = set(query.lower().split())
    if words & MEN_WORDS:   return "Men"
    if words & WOMEN_WORDS: return "Women"
    if words & KIDS_WORDS:  return "Kids"
    return None

# ----------------------------
# 2️⃣ QUERY EXPANSION (SYNONYMS)
# ----------------------------
SYNONYMS = {
    "men":   ["man", "mens", "male"],
    "women": ["woman", "ladies", "female"],
    "kids":  ["child", "children", "kid"],
}

def expand_query(query: str) -> str:
    words = query.lower().split()
    expanded = list(dict.fromkeys(words))
    for word in words:
        for synonym in SYNONYMS.get(word, []):
            if synonym not in expanded:
                expanded.append(synonym)
    return " ".join(expanded)

# ----------------------------
# 3️⃣ GENDER FILTER HELPER
# ----------------------------
def matches_gender(product: dict, gender: str) -> bool:
    gender_lower = gender.lower()
    return (
        gender_lower in (product.get("category") or "").lower() or
        gender_lower in (product.get("description") or "").lower() or
        gender_lower in (product.get("name") or "").lower()
    )

# ----------------------------
# 4️⃣ TEXT TO VECTOR EMBEDDING
# ----------------------------
def text_to_embedding(text: str) -> list[float]:
    inputs = processor(text=[text], return_tensors="pt", padding=True)
    input_ids = inputs["input_ids"].to(DEVICE)
    attention_mask = inputs["attention_mask"].to(DEVICE)

    with torch.no_grad():
        text_outputs = model.text_model(input_ids=input_ids, attention_mask=attention_mask)
        text_features = model.text_projection(text_outputs.pooler_output)
        text_features = text_features / text_features.norm(dim=-1, keepdim=True)

    return text_features[0].cpu().numpy().tolist()

# ----------------------------
# 5️⃣ FALLBACK: WORD-BOUNDARY SEARCH
# ----------------------------
def fallback_search(products: list[dict], expanded_query: str, gender: Optional[str]) -> list[dict]:
    q_words = [w.strip() for w in expanded_query.split() if w.strip()]
    matched = []

    for p in products:
        text = f"{p.get('name','')} {p.get('description','')} {p.get('category','')}".lower()
        if any(re.search(rf"\b{re.escape(w)}\b", text, re.IGNORECASE) for w in q_words):
            matched.append(p)

    if gender:
        matched = [p for p in matched if matches_gender(p, gender)]

    return matched

# ----------------------------
# 6️⃣ MAIN SEARCH FUNCTION
# ----------------------------
async def search_products(db, query: str, limit: int = 20) -> list[dict]:
    query = query.strip()
    if not query:
        raise ValueError("Missing search query")

    gender     = detect_gender(query)
    expanded   = expand_query(query)
    embedding  = text_to_embedding(expanded)
    collection = db["products"]

    # Build vector search filter (only category, stock handled in $match)
    vector_filter = {}
    if gender:
        vector_filter["category"] = {"$in": [gender, "Unisex"]}

    # Build pipeline
    pipeline = [
        {
            "$vectorSearch": {
                "index": "vector_index",
                "path": "embedding",
                "queryVector": embedding,
                "numCandidates": 200,
                "limit": limit * 3,
                **({"filter": vector_filter} if vector_filter else {})
            }
        },
        {
            "$match": {"stock": {"$gt": 0}}
        },
        {
            "$project": {
                "_id": {"$toString": "$_id"},
                "name": 1,
                "description": 1,
                "category": 1,
                "price": 1,
                "color": 1,
                "sizes": 1,
                "stock": 1,
                "companyName": 1,
                "images": 1,
                "vton_category": 1,
                "createdAt": 1,
                "updatedAt": 1,
                "score": {"$meta": "vectorSearchScore"}
            }
        }
    ]

    cursor  = collection.aggregate(pipeline)
    results = await cursor.to_list(length=limit * 3)

    # Post-filter by gender (catches gender mentions in description)
    if gender:
        results = [p for p in results if matches_gender(p, gender)]

    # Fallback to text search if vector search returns nothing
    if not results:
        print("Vector search returned 0 results, falling back to text search...")
        all_products_cursor = collection.find(
            {"stock": {"$gt": 0}},
            {"embedding": 0}
        )
        all_products = await all_products_cursor.to_list(length=None)
        results = fallback_search(all_products, expanded, gender)

    print(f'Searched for "{query}" → {len(results[:limit])} results')
    return results[:limit]