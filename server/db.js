//const mongoose = require('mongoose');
import mongoose from "mongoose";
import dotenv from "dotenv";
//const dotenv=require('dotenv');
dotenv.config();
//const mongoURI = 'mongodb+srv://ayushsharma40362_db_user:W8dQ5FDOKudMWo1M@cluster0.jkxhbyw.mongodb.net/';

const connectDB=async()=>{await mongoose.connect(process.env.MONGODB_URI)
  .then(() => {
    console.log('Connected to MongoDB Atlas');
  })
  .catch((err) => {
    console.error('Error connecting to MongoDB Atlas:', err);
  });
}

//module.exports=connectDB;

export default connectDB