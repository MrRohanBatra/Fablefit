import express from "express";
import { addUser, getUser, uploadImage } from "../controllers/userController.js";

const userRouter = express.Router();

// Add or update user
userRouter.post("/add", addUser);

// Get user by UID
userRouter.get("/:uid", getUser);

userRouter.post("/uploadimage", uploadImage);

export default userRouter;
