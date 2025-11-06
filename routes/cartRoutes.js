// import express from "express";
// import{
//   addToCart,
//   removeFromCart,
//   getCart,
//   clearCart,
// }from "../controllers/cartController.js";

// const router = express.Router();

// router.post("/add", addToCart);
// router.post("/remove", removeFromCart);
// router.get("/:userId", getCart);
// router.delete("/clear", clearCart);

// export default router;



import express from "express";
import {
  addToCart,
  removeFromCart,
  updateCartQuantity,
  getCart,
} from "../controllers/cartController.js";

const router = express.Router();

router.post("/add", addToCart);
router.post("/remove", removeFromCart);
router.post("/update", updateCartQuantity);
router.get("/:uid", getCart);

export default router;




