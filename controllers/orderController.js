import Order from "../models/orderModel.js"; 
export const placeOrder = async (req, res) => {
  try {
    const { userId, cart, address, paymentMode, paymentDone } = req.body;
    if (!userId || !cart || !address) {
      return res.status(400).json({ message: "Missing required fields" });
    }

    if (!cart.items || cart.items.length === 0) {
      return res.status(400).json({ message: "Cart is empty" });
    }
      
    if (paymentMode === "online" ) {
      const success = Math.random() < 0.9;
      if (!success) {
        return res.status(400).json({ message: "Payment failed (simulated)" });
      }
    }

    const order = await Order.create({
      userId,
      items: cart.items.map((i) => ({
        product: i.product,
        size: i.size,
        color: i.color,
        quantity: i.quantity,
        price: i.price || 0, // optional snapshot
      })),
      totalPrice: cart.totalPrice,
      address,
      paymentMethod: paymentMode,
      status: "placed",
      isPaid: true,
      paidAt: new Date() ,
      deliveryDate: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000), // optional default: +5 days
    });

    res.status(200).json({
      message: "✅ Order placed successfully",
      order,
    });

  } catch (error) {
    console.error("Order creation error:", error);
    res.status(500).json({ error: error.message });
  }
};
