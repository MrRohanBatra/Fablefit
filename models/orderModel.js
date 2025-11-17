import mongoose from "mongoose";

const orderItemSchema = new mongoose.Schema({
  product: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Product",
    required: true,
  },
  size: { type: String, required: true },
  color: { type: String },
  quantity: { type: Number, required: true, min: 1 },
  price: { type: Number, required: true }, // snapshot of product price
});

const orderSchema = new mongoose.Schema(
  {
    // 🔥 FIX: Firebase UID is a string, NOT ObjectId
    userId: {
      type: String,
      required: true,
    },

    items: [orderItemSchema],

    totalPrice: {
      type: Number,
      required: true,
    },

    address: {
      type: String,
      required: true,
    },

    paymentMethod: {
      type: String,
      enum: ["cod", "online"],
      default: "cod",
    },

    status: {
      type: String,
      enum: ["placed", "shipped", "delivered", "cancelled","out-for-delivery"],
      default: "placed",
    },

    isPaid: {
      type: Boolean,
      default: false,
    },

    paidAt: {
      type: Date,
    },

    deliveryDate: {
      type: Date,
      default: function () {
        return new Date(Date.now() + 8 * 24 * 60 * 60 * 1000);
      },
    },
  },
  { timestamps: true }
);

const Order = mongoose.model("Order", orderSchema);
export default Order;
