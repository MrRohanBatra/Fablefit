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
// Auto-fix image URLs for a single product
async function fixProductImageUrls(product) {
  if (!product || !Array.isArray(product.images)) return product;

  const CDN_PREFIX =
    "https://cdn.jsdelivr.net/gh/MrRohanBatra/Fablefit@backend-ayush/product_images/";

  let modified = false;

  product.images = product.images.map((url) => {
    if (url.includes("localhost") && url.includes("product_images")) {
      modified = true;
      const filename = url.split("/").pop();
      return CDN_PREFIX + filename;
    }
    return url;
  });

  if (modified) {
    await product.save(); // update only if changed
    console.log("🔄 Image URLs fixed for product:", product._id);
  }

  return product;
}

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
productRouter.get("/all", async (req, res) => {
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
    let product = await Product.findById(req.params.id);
    // console.log(product);
    if (!product) return res.status(404).json({ message: "Product not found" });
    product = await fixProductImageUrls(product);
    res.status(200).json(product);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});
// productRouter.get("/search", async (req, res) => {
//   try {
//     const query = req.query.s?.trim();
//     if (!query) return res.status(400).json({ message: "Missing search query" });
    
//     const products = await Product.find();
    
//     // 🧩 Add simple synonym expansion
//     const synonyms = {
//       men: ["man", "mens", "male", "guys", "boy"],
//       women: ["woman", "ladies", "female", "girls"],
//       kids: ["child", "children", "boy", "girl", "kidswear"]
//     };
    
//     function expandQuery(q) {
//       const words = q.toLowerCase().split(/\s+/);
//       const expanded = new Set();
//       for (const word of words) {
//         expanded.add(word);
//         if (synonyms[word]) synonyms[word].forEach(s => expanded.add(s));
//       }
//       return [...expanded].join(" ");
//     }
    
//     const expandedQuery = expandQuery(query);
    
//     const fuse = new Fuse(products, {
//       includeScore: true,
//       threshold: 0.5, // slightly fuzzy, good balance
//       keys: [
//         { name: "name", weight: 0.5 },
//         { name: "description", weight: 0.3 },
//         { name: "category", weight: 0.1 },
//         { name: "color", weight: 0.1 }
//       ]
//     });
    
//     let results = fuse.search(expandedQuery);
    
//     // 🧩 Optional fallback for partial includes
//     if (results.length === 0) {
//       const qWords = expandedQuery.split(" ");
//       results = products
//       .filter(p => {
//         const text = `${p.name} ${p.description} ${p.category}`.toLowerCase();
//         return qWords.some(word => text.includes(word));
//       })
//       .map(p => ({ item: p }));
//     }
    
//     const matched = results.map(r => r.item);
//     console.log(`Searched for ${query} TOTAL: ${matched.length}`);
//     res.status(200).json(matched);
//   } catch (error) {
//     console.error("Search error:", error);
//     res.status(500).json({ message: error.message });
//   }
// });
productRouter.get("/search", async (req, res) => {
  try {
    const query = req.query.s?.trim();
    if (!query) return res.status(400).json({ message: "Missing search query" });

    const products = await Product.find({ stock: { $gt: 0 } });

    // ----------------------------
    // 1️⃣ GENDER DETECTION
    // ----------------------------
    function detectGender(q) {
      const words = q.toLowerCase().split(/\s+/);

      const menWords = ["men", "man", "mens", "male", "guy"];
      const womenWords = ["women", "woman", "ladies", "female", "girl"];
      const kidsWords = ["kids", "child", "children", "kid"];

      if (words.some(w => menWords.includes(w))) return "men";
      if (words.some(w => womenWords.includes(w))) return "women";
      if (words.some(w => kidsWords.includes(w))) return "kids";
      return null;
    }

    const gender = detectGender(query);

    // ----------------------------
    // 2️⃣ SAFE SYNONYMS
    // ----------------------------
    const synonyms = {
      men: ["man", "mens", "male"],
      women: ["woman", "ladies", "female"],
      kids: ["child", "children", "kid"]
    };

    function expandQuery(q) {
      const words = q.toLowerCase().split(/\s+/);
      const expanded = new Set();

      for (const word of words) {
        expanded.add(word);
        if (synonyms[word]) {
          synonyms[word].forEach(s => expanded.add(s));
        }
      }
      return [...expanded].join(" ");
    }

    const expandedQuery = expandQuery(query);

    // ----------------------------
    // 3️⃣ FUSE SEARCH
    // ----------------------------
    const fuse = new Fuse(products, {
      includeScore: true,
      threshold: 0.5,
      keys: [
        { name: "name", weight: 0.5 },
        { name: "description", weight: 0.3 },
        { name: "category", weight: 0.1 },
        { name: "color", weight: 0.1 }
      ]
    });

    let results = fuse.search(expandedQuery);
    let matched = results.map(r => r.item);

    // ----------------------------
    // 4️⃣ APPLY GENDER FILTER
    // ----------------------------
    if (gender) {
      matched = matched.filter(p =>
        p.category?.toLowerCase().includes(gender) ||
        p.description?.toLowerCase().includes(gender) ||
        p.name?.toLowerCase().includes(gender)
      );
    }

    // ----------------------------
    // 5️⃣ FALLBACK SEARCH (WORD-BOUNDARY SAFE)
    // ----------------------------
    if (matched.length === 0) {
      const qWords = expandedQuery.split(" ");

      matched = products.filter(p => {
        const text = `${p.name} ${p.description} ${p.category}`.toLowerCase();

        return qWords.some(word => {
          const w = word.trim();
          if (!w) return false;
          const regex = new RegExp(`\\b${w}\\b`, "i");
          return regex.test(text);
        });
      });

      // Apply gender again in fallback
      if (gender) {
        matched = matched.filter(p =>
          p.category?.toLowerCase().includes(gender) ||
          p.description?.toLowerCase().includes(gender) ||
          p.name?.toLowerCase().includes(gender)
        );
      }
    }

    console.log(`Searched for "${query}" → ${matched.length} results`);
    res.status(200).json(matched);

  } catch (error) {
    console.error("Search error:", error);
    res.status(500).json({ message: error.message });
  }
});

export default productRouter;
