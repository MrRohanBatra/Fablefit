import mongoose from "mongoose";
import dotenv from "dotenv";
import Order from "./models/orderModel.js";

dotenv.config();
await mongoose.connect(process.env.MONGODB_URI);

await Order.updateOne(
  { _id: "691b5136641cd0ae6aee72a7" },
  {
    $set: {
      createdAt: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000), // 4 days ago
      deliveryDate: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000) // 2 days later
    }
  }
)


console.log("Order timestamps updated for testing!");
await mongoose.disconnect();
