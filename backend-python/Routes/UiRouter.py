from fastapi import APIRouter
from typing import List
import random
from databaseSchemas.ProductSchema import Product
from models.UiModel import HomeSectionResponse, SectionType
from helpers.Utilities import Utils
Tool=Utils()
uiRouter = APIRouter(prefix="/ui")

SECTION_TITLES = [
    "Trending Now",
    "Hot Picks",
    "Editor's Choice",
    "Must Have",
    "Fresh Arrivals",
    "You Might Like",
    "Top Rated",
    "Best Sellers",
    "Street Style",
    "Season Specials"
]

SECTION_TYPES = ["HORIZONTAL_LIST", "GRID", "FEATURED"]

@uiRouter.get("/banners", response_model=List[HomeSectionResponse])
async def get_banners():
    sections = []
    shuffled_titles = random.sample(SECTION_TITLES, len(SECTION_TITLES))

    for i, title in enumerate(shuffled_titles):
        # Use the Enum values directly to avoid string mismatch errors
        section_type = random.choice(list(SectionType))

        # Determine size based on type
        if section_type == SectionType.HORIZONTAL_LIST:
            size = random.choice([5, 10, 15])
        elif section_type == SectionType.GRID:
            size = random.choice([2,4]) # Even numbers look better in 2-column grids
        else: # FEATURED
            size = 1

        collection = Product.get_pymongo_collection()
        
        # Aggregate products
        cursor = collection.aggregate([
            {"$sample": {"size": size}},
            {"$project": {"embedding": 0}} # Exclude heavy vector data
        ])
        
        raw_products = await cursor.to_list(length=size) # type: ignore

        # Prepare the list of serialized products
        serialized_products = []
        for p in raw_products:
            doc = Tool.serializeDoc(p)
            # Ensure the _id from Mongo is a string for Pydantic
            if "_id" in doc:
                doc["_id"] = str(doc["_id"])
            serialized_products.append(doc)

        sections.append({
            "id": str(i), # Becomes 'id' in the JSON response
            "title": title,
            "type": section_type,
            "products": serialized_products
        })

    random.shuffle(sections)
    return sections