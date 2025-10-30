// import Cart from "../models/cartModel.js";
// //import Product from "../models/productModel.js";
// import Product from "../models/productmodel.js";
// // Add item 
// export const addToCart = async (req, res) => {
//   try {
//     //const {userId, productId, quantity} = req.body;
//     const { userId, productId, size, color, quantity } = req.body;
//     const product = await Product.findById(productId);
//     if (!product) return res.status(404).json({ message: "Product not found" });


//     let cart = await Cart.findOne({ userId });
//     // const product = await Product.findById(productId);
//     // if (!product) return res.status(404).json({ message: "Product not found" });

//     //no cart,, vreate cart
//     if(!cart){
//       cart = new Cart({
//         userId,
//         items: [{product: productId, quantity}],
//         totalPrice: product.price*quantity,
//       });
//     } 
//     else{  //product already in cart
//       const itemIndex = cart.items.findIndex(
//         (item) => item.product.toString() === productId
//       );

//       if(itemIndex>-1){
        
//         cart.items[itemIndex].quantity += quantity;  // update quantity
//       }
//       else{
//         cart.items.push({ product: productId, quantity });
//       }

//       // recalculate total
//       cart.totalPrice += product.price * quantity;
//     }

//     await cart.save();
//     res.status(200).json(cart);
//   } 
//   catch (error) {
//     res.status(500).json({ message: error.message });
//   }
// };

// // Remove item from cart
// export const removeFromCart = async (req, res) => {
//   try {
//     const { userId, productId } = req.body;
//     const cart = await Cart.findOne({ userId }).populate("items.product");

//     if(!cart)return res.status(404).json({ message: "Cart not found" });

//     const itemIndex = cart.items.findIndex(
//       (item) => item.product._id.toString() === productId
//     );

//     if (itemIndex===-1)
//       return res.status(404).json({ message: "Item not found in cart" });

//     const removedItem = cart.items[itemIndex];
//     cart.totalPrice -= removedItem.product.price * removedItem.quantity;
//     cart.items.splice(itemIndex, 1);

//     await cart.save();
//     res.status(200).json(cart);
//   } catch (error) {
//     res.status(500).json({ message: error.message });
//   }
// };

// //Cart showing
// export const getCart = async(req, res)=>{
//   try{
//     const {userId} = req.params;
//     const cart = await Cart.findOne({ userId }).populate("items.product");

//     if(!cart) return res.status(404).json({ message: "Cart not found" });

//     res.status(200).json(cart);
//   } 
//   catch (error) {
//     res.status(500).json({ message: error.message });
//   }
// };

// //Clear entire cart
// export const clearCart = async (req, res) => {
//   try {
//     const { userId } = req.body;
//     const cart = await Cart.findOneAndDelete({ userId });

//     if (!cart) return res.status(404).json({ message: "Cart not found" });

//     res.status(200).json({ message: "Cart cleared successfully" });
//   } 
//   catch (error){
//     res.status(500).json({ message: error.message });
//   }
// };




import Cart from "../models/cartModel.js";
import Product from "../models/productModel.js";

// Add item to cart
export const addToCart = async (req, res) => {
  try {
    const { userId, productId, size, color, quantity } = req.body;

    // Validate product existence
    const product = await Product.findById(productId);
    if (!product) return res.status(404).json({ message: "Product not found" });

    let cart = await Cart.findOne({ userId });

    if (!cart) {
      //  Create new cart if user doesn't have one
      cart = new Cart({ userId, items: [], totalPrice: 0 });
    }

    // Find existing item (same product + size)
    const existingItem = cart.items.find(
      (item) =>
        item.product.toString() === productId &&
        item.size === size &&
        item.color === color
    );

    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      cart.items.push({ product: productId, size, color, quantity });
    }

    // Recalculate total price
    let total = 0;
    for (const item of cart.items) {
      const prod = await Product.findById(item.product);
      total += prod.price * item.quantity;
    }
    cart.totalPrice = total;

    await cart.save();
    res.status(200).json({ message: "Cart updated successfully", cart });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Error adding to cart", error: err.message });
  }
};

// 🗑️ Remove item from cart
export const removeFromCart = async (req, res) => {
  try {
    const { userId, productId, size } = req.body;

    const cart = await Cart.findOne({ userId });
    if (!cart) return res.status(404).json({ message: "Cart not found" });

    // Filter out the item
    cart.items = cart.items.filter(
      (item) =>
        !(item.product.toString() === productId && item.size === size)
    );

    // Recalculate total
    let total = 0;
    for (const item of cart.items) {
      const prod = await Product.findById(item.product);
      total += prod.price * item.quantity;
    }
    cart.totalPrice = total;

    await cart.save();
    res.status(200).json({ message: "Item removed", cart });
  } catch (err) {
    res.status(500).json({ message: "Error removing item", error: err.message });
  }
};

// Update item quantity
export const updateCartQuantity = async (req, res) => {
  try {
    const { userId, productId, size, quantity } = req.body;

    const cart = await Cart.findOne({ userId });
    if (!cart) return res.status(404).json({ message: "Cart not found" });

    const item = cart.items.find(
      (item) =>
        item.product.toString() === productId && item.size === size
    );
    if (!item) return res.status(404).json({ message: "Item not found in cart" });

    item.quantity = quantity;

    // Recalculate total
    let total = 0;
    for (const item of cart.items) {
      const prod = await Product.findById(item.product);
      total += prod.price * item.quantity;
    }
    cart.totalPrice = total;

    await cart.save();
    res.status(200).json({ message: "Quantity updated", cart });
  } catch (err) {
    res.status(500).json({ message: "Error updating quantity", error: err.message });
  }
};

// Get user's cart
export const getCart = async (req, res) => {
  try {
    const { userId } = req.params;

    const cart = await Cart.findOne({ userId }).populate("items.product");
    if (!cart) return res.status(404).json({ message: "Cart not found" });

    res.status(200).json(cart);
  } catch (err) {
    res.status(500).json({ message: "Error fetching cart", error: err.message });
  }
};
