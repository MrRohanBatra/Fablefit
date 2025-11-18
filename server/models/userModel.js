import mongoose from "mongoose";

const userSchema = new mongoose.Schema(
  {
    uid: {
      type: String,
      required: true,
      unique: true, // Firebase UID
    },
    phone: {
      type: String,
      default: null,
    },
    // address: {
    //   home: {
    //     type: String,
    //     default: null,
    //   },
    //   work: {
    //     type: String,
    //     default: null,
    //   },
    // },
    address: {
      type: [mongoose.Schema.Types.Mixed],
      default: [],
    },
    vton_image: {
      type: String,
      default: null, // URL or file path
    },
    type: {
      type: String,
      enum: ["normal", "seller"],
      default: "normal",
    },
  },
  { timestamps: true }
);
const User = mongoose.model("User", userSchema);
export default User;
