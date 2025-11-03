import mongoose from "mongoose";
import User from "./models/userModel.js"; // adjust path
import dotenv from "dotenv";
dotenv.config();
await mongoose.connect("mongodb+srv://ayushsharma40362_db_user:W8dQ5FDOKudMWo1M@cluster0.jkxhbyw.mongodb.net/");
await User.deleteMany({});
console.log("✅ All users deleted");
await mongoose.disconnect();
