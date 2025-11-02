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
      default:[],
      required: true,

    },

    // size: {
    //       type: String,
    //       required: true, // 👈 this makes 'size' mandatory
    //     },
    color: {
      type: String,
      default:[],
    },
    stock: {
      type: Number,
      default: 0,
    },
    companyId: {
      type: mongoose.Schema.Types.ObjectId,
      default: () => new mongoose.Types.ObjectId(),
      ref: "Company"
    },
    companyName: {
      type: String,
      required: true,
    },

    images: {
      type: [String], 
      default: [],
    },
    model_3d_url: {
      type: String, // URL for virtual try-on
    },
  },
  { timestamps: true } // automatically adds createdAt & updatedAt
);

// const Product = mongoose.model("Product", productSchema);
// export default Product;
const Product = mongoose.models.Product || mongoose.model("Product", productSchema);
export default Product;






