import mongoose from "mongoose";
import dotenv from "dotenv";
import Product from "./models/productModel.js";

dotenv.config();

await mongoose.connect(process.env.MONGODB_URI);

const ids = [
  "691af59aef1884c0aef1dc45",
  "691af524ef1884c0aef1dc3c"
];

for (const id of ids) {
  const res = await Product.deleteOne({ _id: id });
  console.log("Deleted:", id, "->", res.deletedCount);
}

await mongoose.disconnect();
