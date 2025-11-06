// import express from "express";

// //import Product from "../models/productModel.js";
// import Product from "../models/productmodel.js";
// const productRouter = express.Router();

// // Add 
// productRouter.post("/add", async (req, res) => {
//   try {
//     const newProduct = new Product(req.body);
//     await newProduct.save();
//     res.status(201).json({ message: "Product added successfully", product: newProduct });
//   } catch (error) {
//     res.status(500).json({ message: error.message });
//   }
// });

// // Get 
// productRouter.get("/", async (req, res) => {
//   try {
//     const products = await Product.find();
//     res.status(200).json(products);
//   } catch (error) {
//     res.status(500).json({ message: error.message });
//   }
// });

// productRouter.post("/uploadimage")

// // Get product by ID
// productRouter.get("/:id", async (req, res) => {
//   try {
//     const product = await Product.findById(req.params.id);
//     if (!product) return res.status(404).json({ message: "Product not found" });
//     res.status(200).json(product);
//   } catch (error) {
//     res.status(500).json({ message: error.message });
//   }
// });

// export default productRouter;
import express from "express";
import fileUpload from "express-fileupload";
import path from "path";
import fs from "fs";
import Product from "../models/productModel.js"; // ✅ make sure filename casing matches exactly

const productRouter = express.Router();


// ----------------------
// 1️⃣ Image Upload Route
// ----------------------
productRouter.post("/upload", async (req, res) => {
  try {
    if (!req.files || !req.files.abcd) {
      return res.status(400).json({ success: false, message: "No image uploaded" });
    }

    const image = req.files.abcd;
    const uploadDir = "product_images/";

    // Create upload folder if it doesn't exist
    if (!fs.existsSync(uploadDir)) {
      fs.mkdirSync(uploadDir);
    }

    // Generate a unique filename
    const fileName = Date.now() + "-" + image.name;
    const filePath = path.join(uploadDir, fileName);

    // Move the file to uploads folder
    await image.mv(filePath);

    // Construct public URL
    const imageUrl = `/${uploadDir}${fileName}`;

    res.status(200).json({
      success: true,
      message: "Image uploaded successfully",
      url: imageUrl,
    });
  } catch (error) {
    console.error("Upload error:", error);
    res.status(500).json({ success: false, message: error.message });
  }
});

// ----------------------
// 2️⃣ Add Product Route
// ----------------------
productRouter.post("/add", async (req, res) => {
  try {
    const newProduct = new Product(req.body);
    await newProduct.save();
    res.status(201).json({
      success: true,
      message: "Product added successfully",
      product: newProduct,
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// ----------------------
// 3️⃣ Get All Products
// ----------------------
productRouter.get("/", async (req, res) => {
  try {
    const products = await Product.find();
    res.status(200).json(products);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// ----------------------
// 4️⃣ Get Product by ID
// ----------------------
productRouter.get("/id/:id", async (req, res) => {
  try {
    console.log("Product id ", req.params.id);
    const product = await Product.findById(req.params.id);
    // console.log(product);
    if (!product) return res.status(404).json({ message: "Product not found" });
    res.status(200).json(product);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

export default productRouter;
