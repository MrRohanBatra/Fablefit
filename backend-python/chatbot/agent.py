# from chatbot.state import get_user_state
# from chatbot.intent import extract_intent
# from chatbot.state import reset_user_state
# from databaseSchemas.ProductSchema import Product
# from databaseSchemas.CartSchema import Cart
# from databaseSchemas.OrderSchema import Order


# #Search 
# async def search_products(query: str):
#     products = await Product.find(
#         {"name": {"$regex": query, "$options": "i"}}
#     ).to_list()

#     return [
#         {"id": str(p.id), "name": p.name, "price": p.price}
#         for p in products
#     ]


# #ADD TO CART
# async def add_to_cart(user_id, product):
#     cart = await Cart.find_one(Cart.user_id == user_id)

#     if not cart:
#         cart = Cart(user_id=user_id, items=[])

#     cart.items.append({
#         "product_id": product["id"],
#         "quantity": 1
#     })

#     await cart.save()


# #CREATE ORDER
# async def create_order(user_id, product):
#     order = Order(
#         user_id=user_id,
#         items=[{
#             "product_id": product["id"],
#             "quantity": 1
#         }],
#         total_price=product["price"]
#     )
#     await order.insert()
#     return order


# #Main
# async def process_chat(user_id: str, message: str):

#     state = get_user_state(user_id)

#     # langchain
#     intent_data = await extract_intent(message)

#     intent = intent_data.get("intent")
#     query = intent_data.get("query")
#     product_name = intent_data.get("product_name")
#     confirmation = intent_data.get("confirmation")

#     #Search 
#     if intent == "search":
#         products = await search_products(query)

#         if not products:
#             return {"message": "No products found"}

#         state["last_products"] = products

#         response = "Here are some options:\n"
#         for i, p in enumerate(products[:5]):
#             response += f"{i+1}. {p['name']} ₹{p['price']}\n"

#         response += "\nWhich one do you want?"

#         return {"message": response}

#     #Select
#     if intent == "select" and state["last_products"]:
#         for p in state["last_products"]:
#             if product_name.lower() in p["name"].lower():
#                 state["selected_product"] = p

#                 return {
#                     "message": f"Do you want to place order for {p['name']} ₹{p['price']}?"
#                 }

#         return {"message": "Product not found in list. Please choose again."}

#     if intent == "confirm" and confirmation == "yes":

#         if not state["selected_product"]:
#             return {"message": "Please select a product first"}

#         product = state["selected_product"]


#         if product.get("stock", 0) <= 0:
#             return {"message": f"{product['name']} is out of stock"}


#         await add_to_cart(user_id, product)
#         await create_order(user_id, product)

        
#         reset_user_state(user_id)

#         return {
#             "message": f"Order placed successfully for {product['name']}"
#         }

#     #Orders display
#     if intent == "show_orders":
#         orders = await Order.find(Order.user_id == user_id).to_list()

#         if not orders:
#             return {"message": "No orders found"}

#         return {"message": f"You have {len(orders)} orders"}

#     return {"message": "Sorry, I didn't understand. Try again."}




from chatbot.state import get_user_state, reset_user_state
from chatbot.intent import extract_intent

from databaseSchemas.ProductSchema import Product
from databaseSchemas.CartSchema import Cart
from databaseSchemas.OrderSchema import Order

from bson import ObjectId


#Search
async def search_products(query: str):
    products = await Product.find({
        "name": {"$regex": query, "$options": "i"}
    }).to_list()

    return [
        {
            "id": str(p.id),
            "name": p.name,
            "price": p.price,
            "stock": p.stock
        }
        for p in products
    ]


#Add to cart
async def add_to_cart(user_id, product):
    cart = await Cart.find_one(Cart.uid == user_id)

    if not cart:
        cart = Cart(uid=user_id, items=[], totalPrice=0)

    # check if already exists
    for item in cart.items:
        if item.product == product["id"]:
            item.quantity += 1
            cart.totalPrice += product["price"]
            await cart.save()
            return

    cart.items.append({
        "product": product["id"],
        "size": "M",  # temporary
        "color": None,
        "quantity": 1
    })

    cart.totalPrice += product["price"]

    await cart.save()


# Create Order
async def create_order(user_id, product):

    order = Order(
        userId=user_id,

        items=[{
            "product": ObjectId(product["id"]),
            "size": "M",
            "color": None,
            "quantity": 1,
            "price": product["price"]
        }],

        totalPrice=product["price"],
        address="Default Address",
        paymentMethod="cod",
        status="placed"
    )

    await order.insert()
    return order


# Chat
async def process_chat(user_id: str, message: str):

    state = get_user_state(user_id)

    intent_data = await extract_intent(message)

    intent = intent_data.get("intent")
    query = intent_data.get("query")
    product_name = intent_data.get("product_name")
    confirmation = intent_data.get("confirmation")

    # Search
    if intent == "search":
        products = await search_products(query)

        if not products:
            return {"message": "No products found"}

        state["last_products"] = products

        response = "Here are options:\n"
        for i, p in enumerate(products[:5]):
            response += f"{i+1}. {p['name']} ₹{p['price']}\n"

        response += "\nWhich one do you want?"

        return {"message": response}

    #Select
    if intent == "select" and state["last_products"]:
        for p in state["last_products"]:
            if product_name.lower() in p["name"].lower():
                state["selected_product"] = p

                return {
                    "message": f"Do you want to place order for {p['name']} ₹{p['price']}?"
                }

        return {"message": "Product not found. Please choose again."}

    # Confirm
    if intent == "confirm":

        if not state["selected_product"]:
            return {"message": "Please select a product first"}

        product = state["selected_product"]

        
        if product.get("stock", 0) <= 0:
            return {"message": f" {product['name']} is out of stock"}

        await add_to_cart(user_id, product)
        await create_order(user_id, product)

        reset_user_state(user_id)

        return {"message": f"Order placed for {product['name']}"}

    #Display orders 
    if intent == "show_orders":
        orders = await Order.find(Order.userId == user_id).to_list()

        if not orders:
            return {"message": "No orders found"}

        response = "Your orders:\n"
        for o in orders:
            response += f"- ₹{o.totalPrice} | {o.status}\n"

        return {"message": response}

    return {"message": "Sorry, I didn't understand"}