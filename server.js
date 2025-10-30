import express from "express";
import mongoose from "mongoose";
import dotenv from "dotenv";
import cors from "cors";
//import productRoutes from "./routes/productRoutes.js";
//import productRoutes from "./routes/productRoutes.js";
import productRoutes from "./routes/poductRoutes.js"
//import connectDB from "./db";
import connectDB from "./db.js"
import cartRoutes from "./routes/cartRoutes.js";
//dotenv.config();
const app = express();

app.use(express.json());
app.use(cors());



//const express=require('express');
//const app=express();
//const connectDB=require('./db');

//const PORT=3000;
//app.use(express.json());

connectDB();

// MongoDB Connection
// mongoose
//   .connect(process.env.MONGO_URI)
//   .then(() => console.log("MongoDB Connected"))
//   .catch((err) => console.log(" Error:", err));





app.use("/api/cart", cartRoutes);





// Base route
app.get("/", (req, res) => {
  res.send("E-commerce backend is running");
});

// Product routes
app.use("/api/products", productRoutes);

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
