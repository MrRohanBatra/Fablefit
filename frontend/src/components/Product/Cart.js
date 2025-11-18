import Product from "./Product.js";

class Cart {
  constructor(data = {}) {
    this._id = data._id || null;
    this.uid = data.uid || null;

    // ✅ Keep items intact but normalized
    this.items = Array.isArray(data.items)
      ? data.items.map((item) => ({
          product: item.product || null, // may be string (id) or object
          size: item.size || null,
          color: item.color || null,
          quantity: item.quantity || 1,
        }))
      : [];

    this.totalPrice = data.totalPrice || 0;
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
  }

  /** 🧾 Convert to backend format */
  toJSON() {
    return {
      uid: this.uid,
      totalPrice: this.totalPrice,
      items: this.items.map((item) => ({
        product:
          typeof item.product === "object"
            ? item.product._id
            : item.product, // keep ID if already string
        size: item.size,
        color: item.color,
        quantity: item.quantity,
      })),
    };
  }

  /** 💰 Local total recalculation */
  recalcTotal() {
    this.totalPrice = this.items.reduce((sum, item) => {
      const price =
        typeof item.product === "object" ? item.product.price || 0 : 0;
      return sum + price * item.quantity;
    }, 0);
  }

  /** 🛒 Add a product (and sync to backend) */
  async addProduct(product, size, color, quantity = 1) {
    const payload = {
      uid: this.uid,
      productId:
        typeof product === "object" ? product._id : product, // allow passing id or object
      size,
      color,
      quantity,
    };

    const res = await fetch("/api/cart/add", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (!res.ok) throw new Error("Failed to add to cart");
    const data = await res.json();
    return new Cart(data.cart);
  }

  /** ❌ Remove an item */
  async removeProduct(productId, size, color) {
    const res = await fetch("/api/cart/remove", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ uid: this.uid, productId, size, color }),
    });

    if (!res.ok) throw new Error("Failed to remove from cart");
    const data = await res.json();
    return new Cart(data.cart);
  }

  /** 🔢 Update quantity */
  async updateQuantity(productId, size, color, quantity) {
    const res = await fetch("/api/cart/update", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ uid: this.uid, productId, size, color, quantity }),
    });

    if (!res.ok) throw new Error("Failed to update quantity");
    const data = await res.json();
    return new Cart(data.cart);
  }

  /** 🔄 Load cart for a user */
  static async fetchForUser(uid) {
    const res = await fetch(`/api/cart/${uid}`);
    if (!res.ok) throw new Error("Failed to load cart");
    const data = await res.json();
    return new Cart(data);
  }

  /** 🧮 Get total items count */
  get itemCount() {
    return this.items.reduce((sum, i) => sum + i.quantity, 0);
  }

  length() {
    return this.itemCount;
  }
}

export { Cart };
export default Cart;
