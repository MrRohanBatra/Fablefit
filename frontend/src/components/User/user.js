// export class User {
//   constructor({
//     user,
//     phone = "",
//     vton_image = "",
//     address = [],
//     type = "normal",
//   }) {
//     this.firebaseUser = user;
//     this.phone = phone;
//     this.vton_image = vton_image;
//     this.address = address;
//     this.type = type;
//   }

//   toJson() {
//     return {
//       uid: this.firebaseUser.uid,
//       phone: this.phone,
//       address: this.address,
//       vton_image: this.vton_image, // ✅ match backend key
//       type: this.type,
//     };
//   }

//   getAddress(addressType) {
//     if (!Array.isArray(this.address)) return null;
//     const found = this.address.find((addr) => addr[addressType]);
//     return found ? found[addressType] : null;
//   }

//   getPhoneNumber() {
//     return this.phone;
//   }
//     async updatePhoneNumber(newNumber) {
//         const oldNumber = this.phone;
//         if (this.phone != newNumber) {
//             this.phone = newNumber;
//             const update = await this.userUpdated();
//             if (update) {
//                 return true;
//             }
//             else {
//                 this.phone = oldNumber;
//                 return false;
//             }
//         }
//         return false;
//   }
//   getVtonImageUrl() {
//     return this.vton_image === "" ? null : this.vton_image;
//   }

//   async userUpdated() {
//     try {
//       console.log("sending req to backend ");
//       const response = await fetch("/api/users/add", {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify(this.toJson()),
//       });

//       const data = await response.json();

//       if (!response.ok) {
//         console.error("❌ Failed to update user:", data.message);
//         throw new Error(data.message || "Failed to update user");
//       }

//       console.log("✅ User updated successfully:", data.user);
//       return data.user;
//     } catch (error) {
//       console.error("⚠️ Error updating user:", error);
//     }
//   }
//   async updateAddress(newAddress) {
//     // ✅ define oldAddress outside try and make sure it's always an array
//     let oldAddress = Array.isArray(this.address) ? [...this.address] : [];

//     try {
//       if (!Array.isArray(newAddress)) {
//         throw new Error("Address must be an array of address objects");
//       }

//       // ✅ temporarily update local copy
//       this.address = newAddress;

//       const updatedUser = await this.userUpdated();

//       if (updatedUser) {
//         console.log("✅ Address updated successfully!");
//         return true;
//       } else {
//         // ❌ Rollback local change
//         this.address = oldAddress;
//         console.warn("⚠️ Backend update failed, address reverted.");
//         return false;
//       }
//     } catch (err) {
//       // ❌ Rollback local change on any exception
//       console.error("❌ Error updating address:", err.message);
//       this.address = oldAddress;
//       return false;
//     }
//   }

//   static refreshUser(oldUser) {
//     if (!oldUser) return null;

//     return new User({
//       user: oldUser.firebaseUser,
//       phone: oldUser.phone,
//       vton_image: oldUser.vton_image,
//       address: [...oldUser.address],
//       type: oldUser.type,
//     });
//   }
// }
export class User {
  constructor({
    user,
    phone = "",
    vton_image = "",
    address = [],
    type = "normal",
  }) {
    this.firebaseUser = user;
    this.phone = phone;
    this.vton_image = vton_image;
    this.address = address;
    this.type = type;
  }

  // 🔁 Converts this user into backend-friendly JSON
  toJson() {
    return {
      uid: this.firebaseUser?.uid,
      phone: this.phone || "",
      address: this.address || [],
      vton_image: this.vton_image || "",
      type: this.type || "normal",
    };
  }

  // 📞 Getters
  getAddress(addressType) {
    if (!Array.isArray(this.address)) return null;
    const found = this.address.find((addr) => addr[addressType]);
    return found ? found[addressType] : null;
  }

  getPhoneNumber() {
    return this.phone;
  }

  getVtonImageUrl() {
    return this.vton_image;
  }

  // 🧩 Generic backend sync
  async userUpdated() {
    try {
      console.log("sending req to backend...");
      const response = await fetch("/api/users/add", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(this.toJson()),
      });

      const data = await response.json();
      if (!response.ok) throw new Error(data.message || "Failed to update user");

      console.log("✅ User updated successfully:", data.user);
      return data.user;
    } catch (error) {
      console.error("⚠️ Error updating user:", error);
      return null;
    }
  }

  // 🏠 Update Address
  // async updateAddress(newAddress) {
  //   const oldAddress = Array.isArray(this.address) ? [...this.address] : [];

  //   try {
  //     if (!Array.isArray(newAddress)) throw new Error("Address must be an array");

  //     this.address = newAddress;
  //     const updatedUser = await this.userUpdated();
  //     console.log("Updated user");
  //     if (updatedUser) {
  //       const refreshUser = User.refreshUser(updatedUser);
  //       console.log("✅ Address updated successfully!");

  //       return true;
  //     } else {
  //       this.address = oldAddress;
  //       console.warn("⚠️ Backend update failed, address reverted.");
  //       return false;
  //     }
  //   } catch (err) {
  //     console.error("❌ Error updating address:", err.message);
  //     this.address = oldAddress;
  //     return false;
  //   }
  // }
  async updateAddress(newAddress) {
    const oldAddress = Array.isArray(this.address) ? [...this.address] : [];

    try {
      if (!Array.isArray(newAddress))
        throw new Error("Address must be an array");

      this.address = newAddress;

      const backendUpdated = await this.userUpdated();

      if (!backendUpdated) {
        this.address = oldAddress;
        return null;  // return null on failure
      }

      return backendUpdated; // return updated backend data
    } catch (err) {
      console.error("❌ Error updating address:", err.message);
      this.address = oldAddress;
      return null;
    }
  }


  // 📞 Update Phone Number
  // async updatePhoneNumber(newNumber) {
  //   const oldNumber = this.phone;
  //   if (this.phone === newNumber) return false;

  //   this.phone = newNumber;
  //   const updatedUser = await this.userUpdated();

  //   if (updatedUser) {
  //     Object.assign(this, User.refreshUser(updatedUser, this.firebaseUser));
  //     return true;
  //   } else {
  //     this.phone = oldNumber;
  //     return false;
  //   }
  // }
  async updatePhoneNumber(newNumber) {
    const oldNumber = this.phone;
    if (this.phone === newNumber) return null;

    this.phone = newNumber;
    const backendUser = await this.userUpdated();

    if (!backendUser) {
      this.phone = oldNumber;
      return null;
    }

    return backendUser; // <-- return backend user data
  }
  // 🏷️ Update User Type
  async updateType(newType) {
    const oldType = this.type;

    try {
      if (!newType) throw new Error("Invalid user type");
      if (this.type === newType) return null; // nothing to update

      this.type = newType; // update locally

      const response = await fetch(`/api/users/updatetype/${this.firebaseUser?.uid}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ type: newType }),
      });

      const data = await response.json();
      if (!response.ok) throw new Error(data.message);

      return data.user; // return backend object
    } catch (error) {
      console.error("⚠️ Error updating user type:", error.message);
      this.type = oldType; // revert on failure
      return null;
    }
  }


  // 🖼️ Update VTON Image URL
  // async updateVtonImage(newImageUrl) {
  //   const oldImage = this.vton_image;
  //   try {
  //     if (!newImageUrl) throw new Error("Invalid image URL");
  //     this.vton_image = newImageUrl;

  //     const updatedUser = await this.userUpdated();

  //     if (updatedUser) {
  //       Object.assign(this, User.refreshUser(updatedUser, this.firebaseUser));
  //       console.log("🖼️ VTON image updated successfully!");
  //       return true;
  //     } else {
  //       this.vton_image = oldImage;
  //       console.warn("⚠️ Backend update failed, image reverted.");
  //       return false;
  //     }
  //   } catch (err) {
  //     console.error("❌ Error updating vton_image:", err.message);
  //     this.vton_image = oldImage;
  //     return false;
  //   }
  // }
  async updateVtonImage(newImageUrl) {
    const oldImage = this.vton_image;

    try {
      if (!newImageUrl) throw new Error("Invalid image URL");

      this.vton_image = newImageUrl;

      const backendUpdated = await this.userUpdated();

      if (!backendUpdated) {
        this.vton_image = oldImage;
        return null;
      }

      return backendUpdated; // IMPORTANT
    } catch (err) {
      this.vton_image = oldImage;
      return null;
    }
  }


  // // ♻️ Rebuild user instance safely
  //   static refreshUser(oldUser) {
  //   if (!oldUser) return null;

  //   return new User({
  //     user: oldUser.firebaseUser,
  //     phone: oldUser.phone,
  //     vton_image: oldUser.vton_image ||"",
  //     address: [...oldUser.address],
  //     type: oldUser.type,
  //   });
  // }
  static refreshUser(fromBackend, prevUser) {
    if (!fromBackend || !prevUser) return null;

    return new User({
      user: prevUser.firebaseUser,     // keep firebase user
      phone: fromBackend.phone,
      vton_image: fromBackend.vton_image || "",
      address: [...fromBackend.address],
      type: fromBackend.type,
    });
  }

}
