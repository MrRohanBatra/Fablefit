class User {
  constructor(uid, phone = null, address = {}, vton_image = null, type = "normal") {
    this.uid = uid;
    this.phone = phone;
    this.address = {
      home: address.home || null,
      work: address.work || null,
    };
    this.vton_image = vton_image;
    this.type = type;
  }

  // Create User object from backend JSON
  static fromJson(data) {
    return new User(
      data.uid,
      data.phone,
      data.address || {},
      data.vton_image,
      data.type || "normal"
    );
  }

  // Convert User object to JSON for sending to backend
  toJSON() {
    return {
      uid: this.uid,
      phone: this.phone,
      address: this.address,
      vton_image: this.vton_image,
      type: this.type,
    };
  }

  // Update home or work address
  setAddress(type, value) {
    if (type === "home" || type === "work") {
      this.address[type] = value;
    }
  }

  setVtonImage(imageUrl) {
    this.vton_image = imageUrl;
  }

  isSeller() {
    return this.type === "seller";
  }
}

export default User;
