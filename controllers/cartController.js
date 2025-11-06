import Cart from "../models/cartModel.js";
import Product from "../models/productModel.js";

/** Helper to calculate total */
async function calculateTotal(items) {
  let total = 0;
  for (const item of items) {
    const prod = await Product.findById(item.product);
    if (prod) total += prod.price * item.quantity;
  }
  return total;
}

/** 🛒 Add or update item */
export const addToCart = async (req, res) => {
  try {
    const { uid, productId, size, color, quantity } = req.body;
    console.log(`🟢 [ADD] uid=${uid}, product=${productId}, qty=${quantity}`);

    const product = await Product.findById(productId);
    if (!product)
      return res.status(404).json({ message: "Product not found" });

    let cart = await Cart.findOne({ uid });
    if (!cart) {
      console.log(`🆕 Creating new cart for ${uid}`);
      cart = new Cart({ uid, items: [], totalPrice: 0 });
    }

    const existing = cart.items.find(
      (i) =>
        i.product.toString() === productId &&
        i.size === size &&
        i.color === color
    );

    if (existing) {
      existing.quantity += quantity;
    } else {
      cart.items.push({ product: productId, size, color, quantity });
    }

    cart.totalPrice = await calculateTotal(cart.items);
    await cart.save();

    console.log(`✅ Cart updated: ${cart.items.length} items`);
    res.status(200).json({ message: "Cart updated", cart });
  } catch (err) {
    console.error("❌ [ADD CART ERROR]", err);
    res.status(500).json({ message: "Error adding to cart", error: err.message });
  }
};

/** 🗑️ Remove an item */
export const removeFromCart = async (req, res) => {
  try {
    const { uid, productId, size, color } = req.body;
    console.log(`🔴 [REMOVE] uid=${uid}, product=${productId}`);

    const cart = await Cart.findOne({ uid });
    if (!cart)
      return res.status(404).json({ message: "Cart not found" });

    cart.items = cart.items.filter(
      (i) =>
        !(
          i.product.toString() === productId &&
          i.size === size &&
          i.color === color
        )
    );

    cart.totalPrice = await calculateTotal(cart.items);
    await cart.save();

    console.log(`✅ Item removed, total=${cart.totalPrice}`);
    res.status(200).json({ message: "Item removed", cart });
  } catch (err) {
    console.error("❌ [REMOVE ERROR]", err);
    res.status(500).json({ message: "Error removing item", error: err.message });
  }
};

/** 🔢 Update item quantity */
export const updateCartQuantity = async (req, res) => {
  try {
    const { uid, productId, size, color, quantity } = req.body;
    console.log(`🟠 [UPDATE] uid=${uid}, product=${productId}, qty=${quantity}`);

    const cart = await Cart.findOne({ uid });
    if (!cart)
      return res.status(404).json({ message: "Cart not found" });

    const item = cart.items.find(
      (i) =>
        i.product.toString() === productId &&
        i.size === size &&
        i.color === color
    );

    if (!item)
      return res.status(404).json({ message: "Item not found in cart" });

    item.quantity = quantity;
    cart.totalPrice = await calculateTotal(cart.items);
    await cart.save();

    console.log(`✅ Quantity updated`);
    res.status(200).json({ message: "Quantity updated", cart });
  } catch (err) {
    console.error("❌ [UPDATE ERROR]", err);
    res.status(500).json({ message: "Error updating quantity", error: err.message });
  }
};

/** 🧾 Get user's cart */
export const getCart = async (req, res) => {
  try {
    const { uid } = req.params;
    console.log(`📦 [GET CART] uid=${uid}`);

    let cart = await Cart.findOne({ uid });
    if (!cart) {
      console.log(`🆕 Creating new empty cart for uid=${uid}`);
      cart = new Cart({ uid, items: [], totalPrice: 0 });
      await cart.save();
    }

    console.log(`✅ Cart fetched (${cart.items.length} items)`);
    res.status(200).json(cart);
  } catch (err) {
    console.error("❌ [GET ERROR]", err);
    res.status(500).json({ message: "Error fetching cart", error: err.message });
  }
};
