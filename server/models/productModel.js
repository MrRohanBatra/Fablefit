import mongoose from "mongoose";

const productSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: [true, "Product name"],
    },
    description: {
      type: String,
      required: [true, "Product description"],
    },
    category: {
      type: String,
      enum: ["Men", "Women", "Kids", "Unisex"],
      required: true,
    },
    price: {
      type: Number,
      required: [true, "Product price"],
    },
    sizes: {
      type: [String],    // ["S", "M", "L", "XL"]
      default: [],
      required: true,

    },

    // size: {
    //       type: String,
    //       required: true, // 👈 this makes 'size' mandatory
    //     },
    color: {
      type: String,
      default: "unknown"
    },
    stock: {
      type: Number,
      default: 0,
    },
    companyName: {
      type: String,
      required: true,
    },

    images: {
      type: [String],
      default: [],
    },
    vton_category: {
      type: String,
    },
  },
  { timestamps: true } // automatically adds createdAt & updatedAt
);

// const Product = mongoose.model("Product", productSchema);
// export default Product;
const Product = mongoose.models.Product || mongoose.model("Product", productSchema);
export default Product;






