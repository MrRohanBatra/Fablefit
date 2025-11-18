import mongoose from "mongoose";
import fs from "fs";
import dotenv from "dotenv";
import Product from "./models/productModel.js";

dotenv.config();

// CHANGE THESE FOR YOUR PROJECT
const CDN_PREFIX = "https://cdn.jsdelivr.net/gh/MrRohanBatra/Fablefit@backend-ayush/product_images/";

async function main() {
  try {
    console.log("📦 Connecting to MongoDB...");
    await mongoose.connect(process.env.MONGODB_URI);

    console.log("📤 Fetching all products...");
    const products = await Product.find({});

    // Backup before changes
    console.log("🛡 Creating backup: products_backup.json");
    fs.writeFileSync("products_backup.json", JSON.stringify(products, null, 2));

    let updatedCount = 0;

    for (const p of products) {
      let modified = false;

      // Ensure images field exists
      if (!p.images || !Array.isArray(p.images)) continue;

      // Update only localhost images
      p.images = p.images.map((url) => {
        if (url.includes("localhost") && url.includes("product_images")) {
          modified = true;

          const filename = url.split("/").pop(); // extract only the filename
          return CDN_PREFIX + filename; // new CDN URL
        }
        return url;
      });

      if (modified) {
        await p.save();
        updatedCount++;
      }
    }

    console.log(`✅ Migration complete.`);
    console.log(`🔄 ${updatedCount} products updated.`);
    console.log("🛡 Backup saved as: products_backup.json");
  } catch (err) {
    console.error("❌ ERROR:", err);
  } finally {
    await mongoose.disconnect();
    console.log("🔌 MongoDB disconnected.");
  }
}


export {
  main,
}
export default main;