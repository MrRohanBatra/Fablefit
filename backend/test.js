import mongoose from "mongoose";
import connectDB from "./db.js";
import Cart from "./models/cartModel.js";

await connectDB();
await Cart.deleteMany({});
console.log("Cart collection cleared.");
mongoose.disconnect();