// src/models/Order.js
import Product from "./Product.js";

class Order {
  constructor(data = {}) {
    this._id = data._id || null;
    this.userId = data.userId || null;

    this.totalPrice = data.totalPrice || 0;
    this.address = data.address || "";
    this.paymentMethod = data.paymentMethod || "cod";
    this.status = data.status || "placed"; // backend computed
    this.isPaid = data.isPaid || false;
    this.paidAt = data.paidAt ? new Date(data.paidAt) : null;

    this.deliveryDate = data.deliveryDate
      ? new Date(data.deliveryDate)
      : null;

    this.createdAt = data.createdAt ? new Date(data.createdAt) : null;
    this.updatedAt = data.updatedAt ? new Date(data.updatedAt) : null;

    // Normalize items
    this.items = Array.isArray(data.items)
      ? data.items.map((item) => ({
          product:
            typeof item.product === "object"
              ? new Product(item.product)
              : item.product, // productId string
          size: item.size || "",
          color: item.color || "",
          quantity: item.quantity || 1,
          price: item.price || 0,
        }))
      : [];
  }

  /** 📦 Total items */
  get itemCount() {
    return this.items.reduce((sum, i) => sum + i.quantity, 0);
  }

  /** 📥 Fetch user's orders from backend */
  static async fetchForUser(uid) {
    const res = await fetch(
      `/api/orders/user/${uid}`
    );
    if (!res.ok) throw new Error("Failed to load orders");

    const data = await res.json();
    return data.map((o) => new Order(o));
  }
  statusProgress() {
  switch (this.status) {
    case "placed": return 25;
    case "shipped": return 50;
    case "out-for-delivery": return 75;
    case "delivered": return 100;
    default: return 0;
  }
}

statusLabel() {
  switch (this.status) {
    case "placed": return "Order Placed";
    case "shipped": return "Shipped";
    case "out-for-delivery": return "Out for Delivery";
    case "delivered": return "Delivered";
    default: return "Processing";
  }
}
}

export default Order;
