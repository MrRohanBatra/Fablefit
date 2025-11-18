// src/models/Product.js

export default class Product {
  constructor(data = {}) {
    console.log(data);
    this._id = data._id;
    this.name = data.name || "";
    this.description = data.description || "";
    this.category = data.category || "Unisex";
    this.price = data.price || 0;
    this.sizes = data.sizes || [];
    this.color = data.color || "";
    this.stock = data.stock || 0;
    this.companyId = data.companyId || null;
    this.companyName = data.companyName || "";
    this.images = data.images || [];
    this.vton_category = data.vton_category || "";
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
  }

  isInStock() {
    return this.stock > 0;
  }

  formattedPrice() {
    return `₹ ${this.price.toFixed(2)}`;
  }

  firstImage() {
    return this.images[0] || "/default-product.png";
  }

  hasSize(size) {
    return this.sizes.includes(size);
  }

  availableSizes() {
    return this.sizes.length ? this.sizes : ["Free Size"];
  }

  toJSON() {
    return { ...this };
  }
}

export function stripLocalhost(url) {
  try {
    const u = new URL(url);
    
    // If hostname is localhost, return only the pathname
    if (u.hostname === "localhost") {
      console.log(`returning ${u.pathname} `)
      return u.pathname; // includes leading '/'
    }
    
    // Otherwise return original URL
    return url;
  } catch (e) {
    console.error("Invalid URL:", e);
    return url;
  }
}
