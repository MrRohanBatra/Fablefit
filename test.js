import mongoose from "mongoose";
import User from "./models/userModel.js"; // adjust path
import dotenv from "dotenv";
import Product from "./models/productModel.js";
dotenv.config();
await mongoose.connect("mongodb+srv://ayushsharma40362_db_user:W8dQ5FDOKudMWo1M@cluster0.jkxhbyw.mongodb.net/");
const data = await Product.find({});
console.log(data);
// const response = await fetch("https://fablefit.netlify.app/product.json")
// const data = await response.json();
// console.log(data);
// function filterProducts(obj) {
//     if (obj.category == "Accessories") {
//         obj.category = "Unisex";
//     }

//     return {
//         name: obj.name,
//         description: obj.description,
//         category: obj.category,
//         price: obj.price,
//         sizes: obj.sizes,
//         color: obj.color,
//         stock:obj.stock,
//         companyName: obj.companyName,
//         images: obj.images,
//         vton_category:obj.vtonCategory
//     }
// }

// data.forEach(async element => {
//     const prod = new Product(filterProducts(element));
//     await prod.save();
// });

await mongoose.disconnect();
