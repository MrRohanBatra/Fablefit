import mongoose from "mongoose";
import User from "./models/userModel.js"; // adjust path
import dotenv from "dotenv";
<<<<<<< HEAD
dotenv.config();
await mongoose.connect("mongodb+srv://ayushsharma40362_db_user:W8dQ5FDOKudMWo1M@cluster0.jkxhbyw.mongodb.net/");
await User.deleteMany({});
console.log("✅ All users deleted");
=======
import Product from "./models/productModel.js";
dotenv.config();
await mongoose.connect("mongodb+srv://ayushsharma40362_db_user:W8dQ5FDOKudMWo1M@cluster0.jkxhbyw.mongodb.net/");
const data = await Product.find({});
console.log(data);
>>>>>>> 8e9313f6 (okay)
await mongoose.disconnect();
