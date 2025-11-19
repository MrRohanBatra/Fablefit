import express from "express";
import { getall, placeOrder } from "../controllers/orderController.js";
import Order from "../models/orderModel.js";
const orderRouter = express.Router();
orderRouter.post("/place", placeOrder)
orderRouter.get("/getall",getall)
// function computeStatus(order) {
//   const createdAt = new Date(order.createdAt);
//   // createdAt.setDate(createdAt.getDate() - 3);
//   const deliveryDate = new Date(order.deliveryDate);
//   const now = new Date();
//   const diffDays = Math.floor((deliveryDate - now) / (1000 * 60 * 60 * 24));//Math.floor((now - createdAt) / (1000 * 60 * 60 * 24));
//   const since = Math.floor((now - createdAt) / (1000 * 60 * 60 * 24));
//   console.log("\n==============================");
//   console.log("📦 Order Status Evaluation");
//   console.log("==============================");
//   console.log(`🆔 Order ID: ${order._id}`);
//   console.log(`📅 Order Created At: ${createdAt.toLocaleString()}`);
//   console.log(`📅 Order Delivery Date: ${deliveryDate.toLocaleString()}`);
  
//   console.log(`⏱️ Current Time: ${now.toLocaleString()}`);
//   console.log(`Status Difference: ${diffDays} day(s)`);
//   console.log(`📆 Days Since Order: ${since} day(s)`);

//   let status = "";

//   if (diffDays <= 1) {
//     status = "placed";
//   } else if (diffDays <= 3) {
//     status = "shipped";
//   } else if (diffDays <= 6) {
//     status = "out-for-delivery";
//   } else {
//     status = "delivered";
//   }

//   console.log(`🔎 Computed Status: ${status.toUpperCase()}`);
//   console.log("==============================\n");

//   return status;
// }

function computeStatus(order) {
  const createdAt = new Date(order.createdAt);
  const now = new Date();

  const diffDays = Math.floor((now - createdAt) / (1000 * 60 * 60 * 24));

  console.log("\n==============================");
  console.log("📦 Order Status Evaluation");
  console.log("==============================");
  console.log(`🆔 Order ID: ${order._id}`);
  console.log(`📅 Order Created At: ${createdAt.toLocaleString()}`);
  console.log(`⏱️ Current Time: ${now.toLocaleString()}`);
  console.log(`📆 Days Since Order: ${diffDays} day(s)`);

  let status = "";

  if (diffDays <= 1) {
    status = "placed";
  } else if (diffDays <= 4) {
    status = "shipped";
  } else if (diffDays <= 7) {
    status = "out-for-delivery";
  } else {
    status = "delivered";
  }

  console.log(`🔎 Computed Status: ${status.toUpperCase()}`);
  console.log("==============================\n");

  return status;
}


// 👉 GET all orders of a specific user
orderRouter.get("/user/:uid", async (req, res) => {
  try {
    const uid = req.params.uid;

    console.log("\n📥 Fetching orders for UID:", uid);

    const orders = await Order.find({ userId: uid })
      .populate("items.product")
      .sort({ createdAt: -1 });

    console.log("📦 Orders found:", orders.length);

    const updatedOrders = orders.map(order => {
      const computedStatus = computeStatus(order);

      console.log("📝 FINAL STATUS returned:", computedStatus, "\n");

      return {
        ...order._doc,
        status: computedStatus,
      };
    });

    res.json(updatedOrders);

  } catch (err) {
    console.error("❌ Order Fetch Error:", err);
    res.status(500).json({ message: "Failed to load orders" });
  }
});


export default orderRouter;
