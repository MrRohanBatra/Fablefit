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
import path from "path";
import { fileURLToPath } from "url";
import productRouter from "./routes/poductRoutes.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);


dotenv.config(); // ✅ load .env variables

const app = express();

// --- Middleware ---
app.use(
  cors({
    origin: "*", // ✅ allow all origins (good for dev)
    methods: ["GET", "POST", "PUT", "DELETE"],
    credentials: true,
  })
);
// --- Middleware ---
app.use(
  cors({
    origin: "*", // ✅ allow all origins (good for dev)
    methods: ["GET", "POST", "PUT", "DELETE"],
    credentials: true,
  })
);
app.options(/.*/, cors());

// ✅ File upload must come BEFORE express.json()
// ✅ Always useTempFiles:true for reliability
app.use(
  fileUpload({
    // useTempFiles: true,
    // tempFileDir: path.join(__dirname, "tmp"), // safe temp dir
    createParentPath: true,
    // limits: { fileSize: 10 * 1024 * 1024 }, // optional: 10MB per file
  })
);

// ✅ Only after fileUpload
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.options(/.*/, cors());
app.use(express.json());

// --- Connect to MongoDB ---
connectDB();

// --- Root Route ---
app.get("/", (req, res) => {
  res.status(200).send({ message:"Running"});
});
app.use("/images", express.static(path.join(__dirname, "images")));

app.use("/product_images", express.static(path.join(__dirname, "product_images")));
// --- API Routes ---
// app.use("/api/products", productRoutes);
app.use("/api/cart", cartRoutes);
app.use("/api/orders", orderRouter);
app.use("/api/users/", userRouter);
app.use("/api/products/", productRouter);
app.post("/test", async (req, res) => {
  if (!req.files || !req.files.foo)
    res.sendStatus(500);
  const filename = req.files.image
  filename.mv(`./test/${filename.name}`);
  res.sendStatus(200);
})
// --- Start Server ---
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`✅ Server running on port ${PORT}`));
