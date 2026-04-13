import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from beanie import init_beanie

from databaseSchemas.OrderSchema import Order
from databaseSchemas.ProductSchema import Product
from databaseSchemas.UserSchema import User

from typing import List, Optional
from langchain_ollama import ChatOllama
from langchain_openai import ChatOpenAI
from langchain_core.tools import tool
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_classic.agents import AgentExecutor, create_tool_calling_agent
from database import db
from chatbot.state import get_user_state
from Routes.ProductRoutes import vector_search_products
from Routes.CartRoutes import add_to_cart as cart_add_logic
from databaseSchemas.CartSchema import Cart, CartUpdate

llm = ChatOllama(model="qwen3.5:2b", temperature=0)


@tool
async def search_for_products(query: str = "", category: str = "", color: str = "",gender=""):
    """
    Search for clothes by style, color, or name. 
    It combines the provided parameters into a single search string and queries the vector database for matching products.
    """
    search_query=""
    search_query=" ".join([query,color,gender,category])
    if search_query=="":
        search_query="trending clothes"
    try:
        all_products = await vector_search_products(search_query)
    except Exception as e:
        import traceback
        print("VECTOR SEARCH ERROR:", e)
        traceback.print_exc()
        raise e
    if(len(all_products)==0):
        return {"message":"unable to find any product","error":True,"products":[]}
    return {"message":"products found","products":all_products,"error":False}
    # RETURN: A list of dictionaries (each containing 'id', 'name', and 'price') OR a string message if no products are found.


@tool
async def add_item_to_cart(user_id: str, product_id: str):
    """
    Adds a specific product to the user's shopping cart.
    It takes the user_id and product_id, constructs a cart update payload, and pushes it to the database.
    """
    
    # RETURN: A success string confirming the item was added, OR an error string if the database operation fails.
    return {"message":"added to card","error":False}
    pass


tools = [search_for_products, add_item_to_cart]

# --- Agent Setup ---

prompt = ChatPromptTemplate.from_messages([
    ("system", """You are the Fablefit AI Stylist. You help users find clothes and manage their cart. 
    The current user's ID is: {user_id}. Always use this user_id when adding items to the cart.
    IMPORTANT: All prices must be displayed in Indian Rupees (₹), NOT dollars ($).
    Never use $ symbol.
    Format prices like ₹608 or ₹1,494.
    You have access to tools. Do NOT output raw JSON to the user. 
    Execute the tools, wait for the observation, and then summarize the results in a friendly, conversational way."""),
    MessagesPlaceholder(variable_name="chat_history"),
    ("human", "{input}"),
    MessagesPlaceholder(variable_name="agent_scratchpad"),
])

# Use the specific tool-calling agent logic
agent = create_tool_calling_agent(llm, tools, prompt)

# handle_parsing_errors=True helps recover if the local model formats JSON slightly wrong
agent_executor = AgentExecutor(
    agent=agent, 
    tools=tools, 
    verbose=True, 
    handle_parsing_errors=True,
    max_iterations=3 # Prevents the local model from getting stuck in an infinite loop
)

# --- Main Chat Processor ---

async def process_chat(user_id: str, message: str, image_results: Optional[List] = None):
    # Prepare input with image context if available
    final_input = message
    if image_results:
        context = "The user uploaded an image. Visually similar products found: "
        # Extract names safely
        context += ", ".join([p.name if hasattr(p, 'name') else p.get('name', 'Product') for p in image_results])
        final_input = f"{context}\n\nUser's message: {message}"

    try:
        response = await agent_executor.ainvoke({
            "input": final_input,
            "chat_history": [], # You can wire this up to get_user_state later
            "user_id": user_id  # Inject the user_id so the LLM knows it
        })
        return {"message": response["output"]}
    
    except Exception as e:
        print(f"Agent Execution Error: {e}")
        return {"message": "I'm having a little trouble connecting to my stylist tools right now. Could you try again?"}

import asyncio

async def init_db():
    await init_beanie(
        database=db,
        document_models=[Product,User,Cart,Order]
    )
async def main():
    await init_db()
    user_id = "fb_user_12345"

    while True:
        s = input("Enter: ")

        if s.lower() == "e":
            break

        result = await process_chat(user_id, s)
        print(result["message"])

if __name__ == "__main__":
    asyncio.run(main())
    
    
