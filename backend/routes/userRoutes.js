import express from "express";
import { addUser, allusers, getUser, updateUserType, uploadImage } from "../controllers/userController.js";

const userRouter = express.Router();

// Add or update user
userRouter.post("/add", addUser);

// Get user by UID
userRouter.get("/:uid", getUser);

userRouter.post("/uploadimage", uploadImage);
userRouter.post("/updatetype/:uid", updateUserType)
userRouter.get("/all/users", allusers);

export default userRouter;
