# import os
# import sys
# sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
# from beanie import init_beanie

# from databaseSchemas.OrderSchema import Order
# from databaseSchemas.ProductSchema import Product
# from databaseSchemas.UserSchema import User

# from typing import List, Optional
# from langchain_ollama import ChatOllama
# from langchain_openai import ChatOpenAI
# from langchain_core.tools import tool
# from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
# from langchain_classic.agents import AgentExecutor, create_tool_calling_agent
# from database import db
# from chatbot.state import get_user_state
# from Routes.ProductRoutes import vector_search_products
# from Routes.CartRoutes import add_to_cart as cart_add_logic
# from databaseSchemas.CartSchema import Cart, CartUpdate

# llm = ChatOllama(model="qwen3.5:2b", temperature=0)


# @tool
# async def search_for_products(query: str = "", category: str = "", color: str = "",gender=""):
#     """
#     Search for clothes by style, color, or name. 
#     It combines the provided parameters into a single search string and queries the vector database for matching products.
#     """
#     search_query=""
#     search_query=" ".join([query,color,gender,category])
#     if search_query=="":
#         search_query="trending clothes"
#     try:
#         all_products = await vector_search_products(search_query)
#     except Exception as e:
#         import traceback
#         print("VECTOR SEARCH ERROR:", e)
#         traceback.print_exc()
#         raise e
#     if(len(all_products)==0):
#         return {"message":"unable to find any product","error":True,"products":[]}
#     # return {"message":"products found","products":all_products,"error":False}
#     formatted_results = []
    
#     # Only take the top 3 results so the LLM doesn't get overwhelmed
#     for p in all_products[:3]: 
#         # Handle both Beanie Document objects and raw MongoDB dictionaries
#         name = p.name if hasattr(p, 'name') else p.get('name', 'Product')
#         item_color = p.color if hasattr(p, 'color') else p.get('color', 'Standard')
#         price = p.price if hasattr(p, 'price') else p.get('price', 0)
#         desc = p.description if hasattr(p, 'description') else p.get('description', '')
        
#         # Get the ID (handles both Pydantic/Beanie 'id' and Mongo '_id')
#         item_id = str(p.id) if hasattr(p, 'id') else str(p.get('_id', ''))
        
#         # Create a clean, human-readable string for the LLM
#         clean_string = f"- {name} in {item_color} (Price: ₹{price}). Details: {desc}. (Cart ID: {item_id})"
#         formatted_results.append(clean_string)
    
#     # Combine into a single text block
#     agent_summary = "\n".join(formatted_results)
    
#     return {
#         "message": "Products found. Please present 1 or 2 of these to the user naturally.",
#         "data": agent_summary,
#         "error": False
#     }
#     # RETURN: A list of dictionaries (each containing 'id', 'name', and 'price') OR a string message if no products are found.


# @tool
# async def add_item_to_cart(user_id: str, product_id: str):
#     """
#     Adds a specific product to the user's shopping cart.
#     It takes the user_id and product_id, constructs a cart update payload, and pushes it to the database.
#     """
#     from databaseSchemas.CartSchema import CartUpdate

# @tool
# async def add_item_to_cart(user_id: str, product_id: str):
#     """
#     Adds a specific product to the user's shopping cart.
#     """

#     try:
#         payload = CartUpdate(
#             uid=user_id,
#             product=product_id,
#             quantity=1,
#             size="M",      #default 
#             color="default"
#         )

#         result = await cart_add_logic(payload)

#         return {
#             "message": "Item added to cart successfully",
#             "error": False,
#             "data": result
#         }

#     except Exception as e:
#         return {
#                 "message": f"Failed to add item: {str(e)}",
#                 "error": True
#         }
#     # RETURN: A success string confirming the item was added, OR an error string if the database operation fails.
#     # return {"message":"added to card","error":False}
#     # pass


# tools = [search_for_products, add_item_to_cart]

# # --- Agent Setup ---

# prompt = ChatPromptTemplate.from_messages([
#     ("system", """You are the Fablefit AI Stylist. You help users find clothes and manage their cart. 
#     The current user's ID is: {user_id}. Always use this user_id when adding items to the cart.
#     IMPORTANT: All prices must be displayed in Indian Rupees (₹), NOT dollars ($).
#     Never use $ symbol.
#     Format prices like ₹608 or ₹1,494.
#     You have access to tools. Do NOT output raw JSON to the user. 
#     Execute the tools, wait for the observation, and then summarize the results in a friendly, conversational way."""),
#     MessagesPlaceholder(variable_name="chat_history"),
#     ("human", "{input}"),
#     MessagesPlaceholder(variable_name="agent_scratchpad"),
# ])

# # Use the specific tool-calling agent logic
# agent = create_tool_calling_agent(llm, tools, prompt)

# # handle_parsing_errors=True helps recover if the local model formats JSON slightly wrong
# agent_executor = AgentExecutor(
#     agent=agent, 
#     tools=tools, 
#     verbose=True, 
#     handle_parsing_errors=True,
#     max_iterations=3 # Prevents the local model from getting stuck in an infinite loop
# )

# # --- Main Chat Processor ---

# async def process_chat(user_id: str, message: str, image_results: Optional[List] = None):
#     # Prepare input with image context if available
#     final_input = message
#     if image_results:
#         context = "The user uploaded an image. Visually similar products found: "
#         # Extract names safely
#         context += ", ".join([p.name if hasattr(p, 'name') else p.get('name', 'Product') for p in image_results])
#         final_input = f"{context}\n\nUser's message: {message}"

#     try:
#         response = await agent_executor.ainvoke({
#             "input": final_input,
#             "chat_history": [], # You can wire this up to get_user_state later
#             "user_id": user_id  # Inject the user_id so the LLM knows it
#         })
#         return {"message": response["output"]}
    
#     except Exception as e:
#         print(f"Agent Execution Error: {e}")
#         return {"message": "I'm having a little trouble connecting to my stylist tools right now. Could you try again?"}

# import asyncio

# async def init_db():
#     await init_beanie(
#         database=db,
#         document_models=[Product,User,Cart,Order]
#     )
# async def main():
#     await init_db()
#     user_id = "fb_user_12345"

#     while True:
#         s = input("Enter: ")

#         if s.lower() == "e":
#             break

#         result = await process_chat(user_id, s)
#         print(result["message"])

# if __name__ == "__main__":
#     asyncio.run(main())
    
    
import os
import sys
from typing import List, Optional

from langchain_openai import ChatOpenAI
from torchgen import api

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from dotenv import load_dotenv
from langchain_ollama import ChatOllama
from langchain_core.tools import tool
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_classic.agents import AgentExecutor, create_tool_calling_agent

from Routes.ProductRoutes import vector_search_products
from Routes.CartRoutes import add_to_cart as cart_add_logic
from databaseSchemas.CartSchema import CartUpdate
load_dotenv()
# Initializing LLM
llm = ChatOllama(model=os.getenv("OLLAMA_MODEL_NAME","qwen-stylist-9b:latest"), temperature=0,
                 base_url=os.getenv("OLLAMA_BASE_URL","http://127.0.0.1:5000")
                 )
# llm = ChatOpenAI(
#     model="../models/qwen2.5-14b",
#     base_url="https://agent.rohan.org.in/v1",
#     temperature=0,
#     api_key="dummy"
# )
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
        # Fetching products using the existing route logic
        all_products = await vector_search_products(search_query, limit=5)
    except Exception as e:
        import traceback
        print("VECTOR SEARCH ERROR:", e)
        traceback.print_exc()
        return {"message": "error connecting to database", "error": True, "data": ""}
        
    if not all_products or len(all_products) == 0:
        return {"message": "unable to find any product", "error": True, "data": ""}

    # 🔹 THE DATA DIET & OPTION 2 FORMATTING 🔹
    formatted_results = []
    
    # Limit to top 3 products so the 2B model doesn't get overwhelmed
    for p in all_products[:3]: 
        name = p.get('name', 'Product')
        item_color = p.get('color', 'Standard')
        price = p.get('price', 0)
        item_id = str(p.get('_id', ''))
        
        # Forcing the Option 2 Tag into the tool output string
        clean_string = f"- {name} in {item_color} (Price: ₹{price}). Render Tag: [RENDER_PRODUCT: {item_id}]"
        formatted_results.append(clean_string)
    
    agent_summary = "\n".join(formatted_results)
    
    return {
        "message": "Products found. Present 1 or 2 of these to the user naturally using the exact Render Tag.",
        "data": agent_summary,
        "error": False
    }

@tool
async def add_item_to_cart(user_id: str, product_id: str):
    """
    Adds a specific product to the user's shopping cart.
    """
    try:
        payload = CartUpdate(
            uid=user_id,
            product=product_id,
            quantity=1,
            size="M",      
            color="Black"
        )

        await cart_add_logic(payload)

        return {
            "message": "Item added to cart successfully",
            "error": False
        }

    except Exception as e:
        return {
            "message": f"Failed to add item: {str(e)}",
            "error": True
        }


tools = [search_for_products, add_item_to_cart]

# --- Agent Setup ---

prompt = ChatPromptTemplate.from_messages([
    ("system", """You are Rasberry the Fablefit AI Stylist, a friendly, enthusiastic, and helpful personal fashion assistant. 
    Current user info:
        - ID: {user[id]}
        - Name: {user[name]}
        

    CRITICAL INSTRUCTIONS:
    1. NEVER use tables, bulleted data summaries, or analytical breakdowns. Act like a human stylist chatting with a friend.
    2. ALL prices MUST be displayed in Indian Rupees (₹), NOT dollars ($).
    3. When recommending a product, you MUST append its ID using exactly this format at the end of your sentence: [RENDER_PRODUCT: product_id_here]. Do not deviate from this format.
    
    Example Output: 
    "I found a gorgeous Light Blue Shirt for ₹1,151 that would look great on you! [RENDER_PRODUCT: 699731e020bcd9308f07c83d]"
    """),
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
    max_iterations=int(os.getenv("AGENT_ITERATION_LIMIT","5"))
)

# --- Main Chat Processor ---

async def process_chat(user_id: str, message: str, image_results: Optional[List] = None):
    # Prepare input with image context if available
    final_input = message
    if image_results:
        # Also enforce Option 2 tags for image search results
        context = "The user uploaded an image. Visually similar products found:\n"
        for p in image_results[:2]:
            name = p.get('name', 'Product')
            item_id = str(p.get('_id', ''))
            context += f"- {name}. Render Tag: [RENDER_PRODUCT: {item_id}]\n"
            
        final_input = f"{context}\n\nUser's message: {message}"

    try:
        response = await agent_executor.ainvoke({
            "input": final_input,
            "chat_history": [], 
            "user": user_id  
        })
        return {"message": response["output"]}
    
    except Exception as e:
        print(f"Agent Execution Error: {e}")
        return {"message": "I'm having a little trouble connecting to my stylist tools right now. Could you try again?"}
    
    
    