
import express from "express";
import mongoose from "mongoose";
import dotenv from "dotenv";
import cors from "cors";
import fileUpload from "express-fileupload";

import connectDB from "./db.js";
import cartRoutes from "./routes/cartRoutes.js";
import orderRouter from "./routes/orderRoutes.js";
import userRouter from "./routes/userRoutes.js";
import productRouter from "./routes/poductRoutes.js";
import path from "path";
import { fileURLToPath } from "url";


const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const frontendPath = path.join(__dirname, "../frontend/dist");
console.log(__filename,__dirname,frontendPath)
dotenv.config();

const app = express();

app.use(
  cors({
    origin: "*",
    methods: ["GET", "POST", "PUT", "DELETE"],
    credentials: true,
  })
);


app.use(express.json());
app.use(express.urlencoded({ extended: true }));


app.use(
  fileUpload({
    createParentPath: true,
  })
);


connectDB();


app.get("/status", (req, res) => {
  res.status(200).send({ message: "Running" });
});



app.use("/images", express.static(path.join(__dirname, "images")));
app.use("/product_images", express.static(path.join(__dirname, "product_images")));


app.use("/api/cart", cartRoutes);
app.use("/api/orders", orderRouter);
app.use("/api/users", userRouter);
app.use("/api/products", productRouter);

app.post("/test", async (req, res) => {
  if (!req.files || !req.files.image) return res.sendStatus(500);

  const file = req.files.image;
  file.mv(`./test/${file.name}`);
  res.sendStatus(200);
});



app.use(express.static(frontendPath)); 


app.use((req, res, next) => {

  if (
    req.path.startsWith("/api") ||
    req.path.startsWith("/images") ||
    req.path.startsWith("/product_images") ||
    req.path.startsWith("/status") ||
    req.path.startsWith("/test")
  ) {
    return next();
  }

  // Everything else → frontend
  res.sendFile(path.join(frontendPath, "index.html"));
});

// ---------------------------
// START SERVER
// ---------------------------

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`✅ Server running on port ${PORT}`));
