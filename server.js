import express from "express";
import mongoose from "mongoose";
import dotenv from "dotenv";
import cors from "cors";
import fileUpload from "express-fileupload";
// import productRoutes from "./routes/productRoutes.js"; // ✅ fixed typo
import connectDB from "./db.js";
import cartRoutes from "./routes/cartRoutes.js";
import orderRouter from "./routes/orderRoutes.js";
import userRouter from "./routes/userRoutes.js";

dotenv.config(); // ✅ load .env variables

const app = express();

// --- Middleware ---
app.use(express.json());
app.use(
  cors({
    origin: "*", // ✅ allow all origins (good for dev)
    methods: ["GET", "POST", "PUT", "DELETE"],
    credentials: true,
  })
);
app.options(/.*/, cors());

app.use(fileUpload(
  {
    useTempFiles: false,
    createParentPath: true,
  }
))
// --- Connect to MongoDB ---
connectDB();

// --- Root Route ---
app.get("/", (req, res) => {
  res.status(200).send({ message:"Running"});
});

// --- API Routes ---
// app.use("/api/products", productRoutes);
app.use("/api/cart", cartRoutes);
app.use("/api/orders", orderRouter);
app.use("/api/users/", userRouter);
// --- Start Server ---
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`✅ Server running on port ${PORT}`));
