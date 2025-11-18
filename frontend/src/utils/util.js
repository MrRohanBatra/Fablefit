import { Cart } from "../components/Product/Cart";
const getPhoneNumber = (user) => {
  return "9818888495";
};
const getAddress = async (user) => {
  console.log("fetching address");
  const resp = await fetch("/address.json");
  const data = await resp.json();
  console.log("adrress ", data.length);
  return data;
};
const updateAddress = (user, type, addr) => {
  console.log(`Updating ${type} to ${addr} for user ${user?.displayName}...`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!addr || addr.trim() === "") {
        reject(new Error("Address cannot be empty"));
      } else if (addr.includes("error")) {
        reject(new Error("Invalid address content"));
      } else {
        console.log(
          `✅ Updated ${type} to ${addr} for user ${user?.displayName}`
        );
        resolve();
      }
    }, 1000);
  });
};
const getVtonImageUrl = async (user) => {
  const response = await fetch("/vton.json");
  if (response.ok) {
    const data = await response.json();
    const { url } = data;
    if (url) {
      return url;
    }
  }
  return null;
}
const loadCart = async (uid) => {
  try {
    if (!uid) return new Cart({ uid: null, items: [] });
    const cart = await Cart.fetchForUser(uid);
    return cart;
  } catch (err) {
    console.error("❌ Error loading cart:", err);
    return new Cart({ uid, items: [] });
  }
};

export { getPhoneNumber, getAddress, updateAddress, loadCart,getVtonImageUrl };
