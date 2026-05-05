# from langchain_openai import ChatOpenAI
import os
import sys
from typing import List, Optional
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from databaseSchemas.UserSchema import User

from dotenv import load_dotenv
from langchain_ollama import ChatOllama
from langchain_core.tools import tool
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_classic.agents import AgentExecutor, create_tool_calling_agent

from Routes.ProductRoutes import vector_search_products
from Routes.CartRoutes import add_to_cart as cart_add_logic
from Routes.OrderRoutes import place_order as place_order_logic, OrderPlaceRequest
from databaseSchemas.CartSchema import CartUpdate

load_dotenv()

llm = ChatOllama(
    model=os.getenv("OLLAMA_MODEL_NAME", "qwen-stylist-9b:latest"), 
    temperature=0,
    base_url=os.getenv("OLLAMA_BASE_URL", "[http://127.0.0.1:5000](http://127.0.0.1:5000)")
)

@tool
async def search_for_products(query: str = "", category: str = "", color: str = "", gender: str = ""):
    """
    Search for clothes by style, color, or name. 
    It combines the provided parameters into a single search string and queries the vector database.
    """
    search_query = " ".join(filter(None, [query, color, gender, category]))
    if not search_query.strip():
        search_query = "trending clothes"
        
    try:
        all_products = await vector_search_products(search_query, limit=5)
    except Exception as e:
        return {"message": "error connecting to database", "error": True, "data": ""}
        
    if not all_products:
        return {"message": "unable to find any product", "error": True, "data": ""}

    formatted_results = []
    for p in all_products[:3]: 
        name = p.get('name', 'Product')
        item_color = p.get('color', 'Standard')
        price = p.get('price', 0)
        item_id = str(p.get('_id', ''))
        clean_string = f"- {name} in {item_color} (Price: ₹{price}). Render Tag: [RENDER_PRODUCT: {item_id}]"
        formatted_results.append(clean_string)
    
    return {
        "message": "Products found. Present 1 or 2 of these to the user naturally using the exact Render Tag.",
        "data": "\n".join(formatted_results),
        "error": False
    }

@tool
async def add_item_to_cart(user_id: str, product_id: str):
    """
    Adds a specific product to the user's shopping cart.
    """
    try:
        payload = CartUpdate(uid=user_id, product=product_id, quantity=1, size="M", color="Black")
        await cart_add_logic(payload)
        return {"message": "Item added to cart successfully", "error": False}
    except Exception as e:
        return {"message": f"Failed to add item: {str(e)}", "error": True}

@tool
async def place_order_for_user(user_id: str):
    """
    Places a Cash on Delivery (COD) order for all items currently in the user's cart.
    Requires the user's ID and a shipping address.
    """
    try:
        user_address=await User.find_one(User.uid==user_id)
        request_data = OrderPlaceRequest(user_id=user_id, address=user_address.address[0])
        result = await place_order_logic(request_data)
        return {
            "message": f"Order placed successfully! Order ID: {result.order_id}",
            "error": False
        }
    except Exception as e:
        return {"message": f"Could not place order: {str(e)}", "error": True}

tools = [search_for_products, add_item_to_cart, place_order_for_user]

prompt = ChatPromptTemplate.from_messages([
    ("system", """You are Rasberry the Fablefit AI Stylist.
    Current user info:
        - ID: {user_id}
        - Name: {user_name}
        
    CAPABILITIES:
    1. Search for clothes.
    2. Add items to cart.
    3. Place orders: If the user says "buy this", "order my cart", or "checkout", ask for their address if you don't have it, then use the place_order_for_user tool.

    CRITICAL INSTRUCTIONS:
    1. Act like a human stylist, not a bot. No tables or raw JSON.
    2. Prices in ₹ only.
    3. Use [RENDER_PRODUCT: product_id_here] for product recommendations.
    4. When an order is placed successfully, congratulate the user and mention the delivery is on its way.
    """),
    MessagesPlaceholder(variable_name="chat_history"),
    ("human", "{input}"),
    MessagesPlaceholder(variable_name="agent_scratchpad"),
])

agent = create_tool_calling_agent(llm, tools, prompt)
agent_executor = AgentExecutor(
    agent=agent, 
    tools=tools, 
    verbose=True, 
    handle_parsing_errors=True,
    max_iterations=int(os.getenv("MAX_ITER","5"))
)

async def process_chat(user_id: str, message: str, image_results: Optional[List] = None):
    final_input = message
    if image_results:
        context = "The user uploaded an image. Visually similar products:\n"
        for p in image_results[:2]:
            context += f"- {p.get('name')}. Render Tag: [RENDER_PRODUCT: {str(p.get('_id'))}]\n"
        final_input = f"{context}\n\nUser's message: {message}"

    try:
        user = await User.find_one(User.uid == user_id)
        if not user: raise ValueError("User not found")
        
        response = await agent_executor.ainvoke({
            "input": final_input,
            "chat_history": [], 
            "user_id": user.uid,
            "user_name": user.name
        })
        return {"message": response["output"]}
    except Exception as e:
        return {"message": "I'm having a little trouble with my tools. Try again in a moment!"}