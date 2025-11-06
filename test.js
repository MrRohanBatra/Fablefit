import mongoose from "mongoose";

import Cart from "./models/cartModel.js";
import dotenv from "dotenv";
dotenv.config();


await mongoose.connect(process.env.MONGODB_URI);
const data = await Cart.deleteMany({});
console.log(data);
await mongoose.disconnect();