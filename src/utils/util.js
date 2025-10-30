const getPhoneNumber=(user)=>{
    return "9818888495";
}
const getAddress=async(user)=>{
    console.log("fetching address");
    const resp=await fetch("/address.json");
    const data=await resp.json();
    console.log("adrress ",data.length);
    return data;
}
const updateAddress = (user, type, addr) => {
  console.log(`Updating ${type} to ${addr} for user ${user?.displayName}...`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!addr || addr.trim() === "") {
        reject(new Error("Address cannot be empty"));
      } else if (addr.includes("error")) {
        reject(new Error("Invalid address content"));
      } else {
        console.log(`✅ Updated ${type} to ${addr} for user ${user?.displayName}`);
        resolve();
      }
    }, 1000);
  });
};

export{
    getPhoneNumber,
    getAddress,
    updateAddress,
}