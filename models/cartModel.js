// import mongoose from "mongoose";

// const cartItemSchema = new mongoose.Schema({
//   product: {
//     type: mongoose.Schema.Types.ObjectId,
//     ref: "Product",
//     required: true,
//   },
//   quantity:{
//     type: Number,
//     required: true,
//     min: 1,
//     default: 1,
//   },
// });

// const cartSchema = new mongoose.Schema(
//   {
//     userId: {
//       type: mongoose.Schema.Types.ObjectId,
//       ref: "User",              // for authentication later
//       required: true,
//     },
//     items: [cartItemSchema],

//     totalPrice: {
//       type: Number,
//       default: 0,
//     },
//   },
//   { timestamps: true }
// );

// const Cart = mongoose.model("Cart", cartSchema);
// export default Cart;




import mongoose from "mongoose";

const cartSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    items: [
      {
        product: {
          type: mongoose.Schema.Types.ObjectId,
          ref: "Product",
          required: true,
        },
        size: { type: String, required: true },
        color: { type: String },
        quantity: { type: Number, required: true, min: 1 },
      },
    ],
    totalPrice: {
      type: Number,
      default: 0,
    },
  },
  { timestamps: true }
);

const Cart = mongoose.model("Cart", cartSchema);
export default Cart;
