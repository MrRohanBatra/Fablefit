// src/models/Product.js

export default class Product {
  constructor(data = {}) {
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
    this.vtonCategory = data.model_3d_url || "";
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
