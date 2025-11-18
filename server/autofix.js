// import Product from "./models/productModel.js";

// const CDN_PREFIX =
//   "https://cdn.jsdelivr.net/gh/MrRohanBatra/Fablefit@backend-ayush/product_images/";

// export async function runOneTimeImageFix() {
//   try {
//     console.log("🔍 Running one-time product image check...");

//     // Find only products that still have localhost URLs
//     const products = await Product.find({
//       images: { $elemMatch: { $regex: "localhost" } }
//     });

//     if (products.length === 0) {
//       console.log("✅ All product image URLs already clean. No changes needed.");
//       return;
//     }

//     console.log(`⚠️ Found ${products.length} products with localhost URLs.`);

//     for (const p of products) {
//       let modified = false;

//       p.images = p.images.map((url) => {
//         if (url.includes("localhost")) {
//           modified = true;
//           const filename = url.split("/").pop(); // extract only filename
//           return CDN_PREFIX + filename;
//         }
//         return url;
//       });

//       if (modified) {
//         await p.save();
//         console.log(`✔ Updated product: ${p._id}`);
//       }
//     }

//     console.log("🎉 One-time image fix complete.");
//   } catch (err) {
//     console.error("❌ Error in one-time image fix:", err);
//   }
// }
