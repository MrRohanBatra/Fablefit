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
export{
    getPhoneNumber,
    getAddress,
}