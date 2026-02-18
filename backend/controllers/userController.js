import User from "../models/userModel.js";
import path from "path";
import fs from "fs";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// export const addUser = async (req, res) => {
//   try {
//     const { uid, phone, address, vton_image, type } = req.body;

//     if (!uid) return res.status(400).json({ message: "UID is required" });

//     let user = await User.findOne({ uid });

//       if (user) {
//           console.log("updating user");
//       user.phone = phone ?? user.phone;
//           if (address) {
//               user.address = address;
//               user.markModified("address");
//           }

//       user.vton_image = vton_image ?? user.vton_image;
//       user.type = type ?? user.type;
//       await user.save();
//       return res.status(200).json({ message: "User updated", user });
//     }
//       console.log("creating user");
//     user = await User.create({
//       uid,
//       phone,
//       address,
//       vton_image,
//       type,
//     });

//     res.status(201).json({ message: "User created", user });
//   } catch (error) {
//     res.status(500).json({ message: error.message });
//   }
// };
export const addUser = async (req, res) => {
  try {
    const { uid, phone, address, vton_image, type } = req.body;

    if (!uid) return res.status(400).json({ message: "UID is required" });

    console.log("📥 Received user data:", { uid, phone, address, vton_image, type });

    let user = await User.findOne({ uid });

    if (user) {
      console.log("🔄 Updating existing user:", uid);

      if (phone) user.phone = phone;
      if (type) user.type = type;
      if (vton_image) {
        console.log("🖼️ Updating vton_image:", vton_image);
        user.vton_image = vton_image;
      }

      if (address) {
        console.log("🏠 Updating address:", address);
        user.address = address;
        user.markModified("address"); // force save nested object/array updates
      }

      const savedUser = await user.save();
      console.log("✅ User updated successfully");
      return res.status(200).json({ message: "User updated", user: savedUser });
    }

    console.log("🆕 Creating new user:", uid);

    const newUser = await User.create({
      uid,
      phone: phone || "",
      address: address || [],
      vton_image: vton_image || "",
      type: type || "",
    });

    res.status(201).json({ message: "User created", user: newUser });
  } catch (error) {
    console.error("❌ Error in addUser:", error);
    res.status(500).json({ message: error.message });
  }
};

export const getUser = async (req, res) => {
  try {
    const { uid } = req.params;
    const user = await User.findOne({ uid });

    if (!user) return res.status(404).json({ message: "User not found" });

    res.status(200).json(user);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// export const uploadImage = async (req, res) => {
//     try {
//       if (!req.files || !req.files.image) {
//         return res.status(400).json({ message: "No image uploaded" });
//       }

//       const uid = req.body.uid;
//       if (!uid) {
//         return res.status(400).json({ message: "UID not provided" });
//       }

//       const imageFile = req.files.image;

//       // ✅ Make sure "images" folder exists
//       const uploadDir = path.join(__dirname, "../images");
//       if (!fs.existsSync(uploadDir)) fs.mkdirSync(uploadDir);

//       // ✅ Save as <uid>.png (or whatever extension)
//       const ext = path.extname(imageFile.name);
//       const fileName = `${uid}${ext}`;
//       const uploadPath = path.join(uploadDir, fileName);

//       // ✅ Move the file
//       await imageFile.mv(uploadPath);

//       // ✅ Respond with public path
//       res.status(200).json({
//         message: "✅ Image uploaded successfully",
//         file: `/images/${fileName}`,
//       });
//     } catch (error) {
//       console.error("Upload error:", error);
//       res.status(500).json({ message: error.message });
//     }
//   };
export const uploadImage = async (req, res) => {
  try {
    if (!req.files || !req.files.image)
      return res.status(400).json({ message: "No image uploaded" });

    const uid = req.body.uid;
    if (!uid) return res.status(400).json({ message: "UID not provided" });

    const imageFile = req.files.image;

    const uploadDir = path.join(__dirname, "../images");
    if (!fs.existsSync(uploadDir)) fs.mkdirSync(uploadDir);

    const ext = path.extname(imageFile.name);
    // const fileName = `${uid}${ext}`;
    const fileName = `${uid}_${Date.now()}${ext}`;
    const uploadPath = path.join(uploadDir, fileName);

    // ❌ Delete existing image
    if (fs.existsSync(uploadPath)) {
      fs.unlinkSync(uploadPath);
    }

    // 📌 Save new image
    await imageFile.mv(uploadPath);

    res.status(200).json({
      message: "Image replaced successfully",
      file: `/images/${fileName}`,
    });
  } catch (error) {
    console.error("Upload error:", error);
    res.status(500).json({ message: error.message });
  }
};
export const updateUserType = async (req, res) => {
  try {
    const { uid } = req.params;
    const { type } = req.body;

    if (!type) return res.status(400).json({ message: "New user type is required" });

    const user = await User.findOne({ uid });

    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }
    const allowedTypes = ["normal", "seller"];
    if (!allowedTypes.includes(type)) {
      return res.status(400).json({ message: "Invalid user type" });
    }

    // 🔄 Update type only
    user.type = type;
    await user.save();

    res.status(200).json({
      message: "User type updated successfully",
      user
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};
export const allusers = async (req, res) => {
  try {
    const users = await User.find({});
    res.status(200).json(users);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};