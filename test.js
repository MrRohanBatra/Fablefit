// test.js
import fetch from "node-fetch";
import FormData from "form-data";
import fs from "fs";

const API_URL = "https://api.rohan.org.in/tryon"; // your endpoint
const NUM_REQUESTS = 10; // number of concurrent users

async function sendRequest(id) {
  try {
    // check that files exist
    if (!fs.existsSync("./test_human.jpg") || !fs.existsSync("./test_garment.jpg")) {
      throw new Error("Missing test_human.jpg or test_garment.jpg in current folder");
    }

    // create form
    const formData = new FormData();
    formData.append("human_image", fs.createReadStream("./test_human.jpg"), {
      filename: "test_human.jpg",
      contentType: "image/jpeg",
    });
    formData.append("garment_image", fs.createReadStream("./test_garment.jpg"), {
      filename: "test_garment.jpg",
      contentType: "image/jpeg",
    });
    formData.append("denoise_steps", "30");
    formData.append("seed", "42");
    formData.append("category", "upper_body");

    // send POST request
    const res = await fetch(API_URL, {
      method: "POST",
      body: formData,
      headers: formData.getHeaders(),
    });

    const text = await res.text();
    let data;
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }

    console.log(`✅ Request #${id}`, data);
  } catch (e) {
    console.error(`❌ Request #${id}`, e.message);
  }
}

(async () => {
  console.log(`🚀 Starting ${NUM_REQUESTS} concurrent try-on requests...\n`);
  const tasks = [];
  for (let i = 0; i < NUM_REQUESTS; i++) tasks.push(sendRequest(i + 1));
  await Promise.all(tasks);
  console.log("\n✅ All requests completed.");
})();
