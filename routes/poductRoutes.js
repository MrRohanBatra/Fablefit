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
import Fuse from "fuse.js";
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
    console.log(req.body)
    const newProduct = new Product(req.body);
    // console.log(newProduct);
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
productRouter.get("/search", async (req, res) => {
  try {
    const query = req.query.s?.trim();
    if (!query) return res.status(400).json({ message: "Missing search query" });
    
    const products = await Product.find();
    
    // 🧩 Add simple synonym expansion
    const synonyms = {
      men: ["man", "mens", "male", "guys", "boy"],
      women: ["woman", "ladies", "female", "girls"],
      kids: ["child", "children", "boy", "girl", "kidswear"]
    };
    
    function expandQuery(q) {
      const words = q.toLowerCase().split(/\s+/);
      const expanded = new Set();
      for (const word of words) {
        expanded.add(word);
        if (synonyms[word]) synonyms[word].forEach(s => expanded.add(s));
      }
      return [...expanded].join(" ");
    }
    
    const expandedQuery = expandQuery(query);
    
    const fuse = new Fuse(products, {
      includeScore: true,
      threshold: 0.5, // slightly fuzzy, good balance
      keys: [
        { name: "name", weight: 0.5 },
        { name: "description", weight: 0.3 },
        { name: "category", weight: 0.1 },
        { name: "color", weight: 0.1 }
      ]
    });
    
    let results = fuse.search(expandedQuery);
    
    // 🧩 Optional fallback for partial includes
    if (results.length === 0) {
      const qWords = expandedQuery.split(" ");
      results = products
      .filter(p => {
        const text = `${p.name} ${p.description} ${p.category}`.toLowerCase();
        return qWords.some(word => text.includes(word));
      })
      .map(p => ({ item: p }));
    }
    
    const matched = results.map(r => r.item);
    console.log(`Searched for ${query} TOTAL: ${matched.length}`);
    res.status(200).json(matched);
  } catch (error) {
    console.error("Search error:", error);
    res.status(500).json({ message: error.message });
  }
});
export default productRouter;
