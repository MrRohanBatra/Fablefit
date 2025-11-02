export class User {
  constructor({
    user,
    phone = "",
    vton_img = "",
    address = [],
    type = "normal",
  }) {
    this.firebaseUser = user;
    this.phone = phone;
    this.vton_img = vton_img;
    this.address = address;
    this.type = type;
  }

  toJson() {
    return {
      uid: this.firebaseUser.uid,
      phone: this.phone,
      address: this.address,
      vton_image: this.vton_img, // ✅ match backend key
      type: this.type,
    };
  }

  getAddress(addressType) {
    if (!Array.isArray(this.address)) return null;
    const found = this.address.find((addr) => addr[addressType]);
    return found ? found[addressType] : null;
  }

  getPhone() {
    return this.phone;
  }

  getImageUrl() {
    return this.vton_img;
  }

  async userUpdated() {
    try {
      const response = await fetch("http://localhost:5000/api/users/add", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(this.toJson()),
      });

      const data = await response.json();

      if (!response.ok) {
        console.error("❌ Failed to update user:", data.message);
        throw new Error(data.message || "Failed to update user");
      }

      console.log("✅ User updated successfully:", data.user);
      return data.user;
    } catch (error) {
      console.error("⚠️ Error updating user:", error);
    }
  }
  async updateAddress(newAddress) {
    try {
      if (!Array.isArray(newAddress)) {
        throw new Error("Address must be an array of address objects");
      }

      const oldAddress = [...this.address]; // keep a copy
      this.address = newAddress;

      const updatedUser = await this.userUpdated();

      if (updatedUser) {
        console.log("✅ Address updated successfully!");
        return true;
      } else {
        // ❌ Rollback local change
        this.address = oldAddress;
        console.warn("⚠️ Backend update failed, address reverted.");
        return false;
      }
    } catch (err) {
      // ❌ Rollback local change on any exception
      console.error("❌ Error updating address:", err.message);
      this.address = oldAddress;
      return false;
    }
  }
}
