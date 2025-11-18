import Order from "../models/orderModel.js";
import Cart from "../models/cartModel.js";
import Product from "../models/productModel.js";

export const placeOrder = async (req, res) => {
  try {
    const { uid, address, paymentMethod, cart } = req.body;

    // Validate required fields
    if (!uid) {
      return res.status(400).json({ message: "Missing uid" });
    }
    if (!cart || !cart.items || cart.items.length === 0) {
      return res.status(400).json({ message: "Cart is empty" });
    }
    if (!address) {
      return res.status(400).json({ message: "Address is required" });
    }

    // Build order items with product snapshot price
    const orderItems = [];

    for (const item of cart.items) {
      const product = await Product.findById(item.product);

      if (!product) {
        return res.status(400).json({
          message: `Product not found: ${item.product}`,
        });
      }

      // Optional: reduce stock
      if (product.stock !== undefined) {
        if (product.stock < item.quantity) {
          return res.status(400).json({
            message: `${product.name} is out of stock`,
          });
        }

        product.stock -= item.quantity;
        await product.save();
      }

      orderItems.push({
        product: product._id,
        size: item.size,
        color: item.color,
        quantity: item.quantity,
        price: product.price, // snapshot price
      });
    }

    // Create the Order
    const order = new Order({
      userId: uid,                 // Firebase UID
      items: orderItems,
      totalPrice: cart.totalPrice, // snapshot
      address,
      paymentMethod: paymentMethod || "cod",
      status: "placed",
      isPaid: paymentMethod === "online",
      paidAt: paymentMethod === "online" ? new Date() : null,
    });

    await order.save();

    // Clear MongoDB Cart after placing order
    await Cart.findOneAndUpdate(
      { uid },
      { items: [], totalPrice: 0 },
      { new: true }
    );

    return res.status(201).json({
      message: "Order placed successfully",
      order,
    });
  } catch (error) {
    console.error("Order Error:", error);
    return res.status(500).json({
      message: "Failed to place order",
      error: error.message,
    });
  }
};
export const getall = async (req, res) => {
  try {
    const orders = await Order.find({});    
    res.status(200).json(orders);
  }
  catch (error) {
    res.status(500).json({ message: error.message });
  }
};

