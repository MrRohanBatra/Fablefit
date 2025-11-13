import mongoose from "mongoose";

import Cart from "./models/cartModel.js";
import dotenv from "dotenv";
import Product from "./models/productModel.jsnpm ";
dotenv.config();


await mongoose.connect(process.env.MONGODB_URI);
const data = await Product.find({});
console.log(data);
await mongoose.disconnect();