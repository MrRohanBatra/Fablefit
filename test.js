import mongoose from "mongoose";
import dotenv from "dotenv";
import fs from "fs";
import Product from "./models/productModel.js"; // your schema

dotenv.config();

await mongoose.connect(process.env.MONGODB_URI);

console.log("Connected to DB");

// Load modified JSON
const updatedData = JSON.parse(fs.readFileSync("prod_fixed.json"));

// Update each product by _id
for (const item of updatedData) {
  await Product.updateOne(
    { _id: item._id },
    { $set: { vton_category: item.vton_category } }
  );
  console.log(`Updated: ${item._id} -> ${item.vton_category}`);
}

// DELETE all products in Accessories category
const deleted = await Product.deleteMany({ category: "Accessories" });
console.log(`Deleted ${deleted.deletedCount} Accessories products`);

await mongoose.disconnect();
console.log("Done + Disconnected");
