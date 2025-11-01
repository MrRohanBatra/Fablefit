import { number } from "framer-motion";
import React, { useState } from "react";

function Vton() {
  const [humanImage, setHumanImage] = useState(null);
  const [garmentImage, setGarmentImage] = useState(null);
  const [category, setCategory] = useState("upper_body"); // default value
  const [steps, setSteps] = useState(30);
  const [seed, setSeed] = useState(42);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const apiURL = "https://api.rohan.org.in/tryon";

  async function handleSubmit() {
    if (!humanImage || !garmentImage) {
      alert("Please upload both images!");
      return;
    }

    setLoading(true);
    const formData = new FormData();
    formData.append("human_image", humanImage);
    formData.append("garment_image", garmentImage);
    formData.append("description", "");
    formData.append("denoise_steps", 20);
    formData.append("seed", 42);
    formData.append("category", category); // ✅ send selected category

    try {
      const response = await fetch(apiURL, {
        method: "POST",
        body: formData,
      });

      if (!response.ok) throw new Error("Failed to generate result");

      const blob = await response.blob();
      const imgUrl = URL.createObjectURL(blob);
      setResult(imgUrl);
    } catch (err) {
      console.error(err);
      alert("Error: " + err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="d-flex flex-column mw-40 justift-content-center text-center">
      <h2>🧥 FableFit Try-On</h2>

      <div className="d-flex gap-2">
        <p>Upload human image:</p>
        <input
          type="file"
          accept="image/*"
          onChange={(e) => setHumanImage(e.target.files[0])}
        />
      </div>

      <div className="d-flex gap-2">
        <p>Upload garment image:</p>
        <input
          type="file"
          accept="image/*"
          onChange={(e) => setGarmentImage(e.target.files[0])}
        />
      </div>
      <div className="d-flex gap-2">
        <p>Denoising Steps:</p>
              <input type="number" value={ steps} onChange={(e) => setSteps(e.target.value)} />
          </div>
          <div>
        <p>seed:</p>
              <input type="number" value={seed } onChange={(e) => setSeed(e.target.value)} ></input></div>
      
      

      {/* ✅ Category Dropdown */}
      <div
        className="d-flex gap-2"
        style={{ marginTop: "10px", justifyContent: "center" }}
      >
        <select
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          style={{ padding: "8px", borderRadius: "6px" }}
        >
          <option value="upper_body">Upper (Tops, Shirts)</option>
          <option value="lower_body">Lower (Pants, Skirts)</option>
          <option value="dresses">Dress</option>
        </select>
      </div>

      <button
        onClick={handleSubmit}
        disabled={loading}
        style={{
          marginTop: "15px",
          padding: "10px 20px",
          background: "black",
          color: "white",
          border: "none",
          borderRadius: "8px",
          cursor: "pointer",
        }}
      >
        {loading ? "Processing..." : "Generate Try-On"}
      </button>

      {result && (
        <div style={{ marginTop: "20px" }}>
          <h3>Result:</h3>
          <img src={result} alt="Try-On Result" width="300" />
        </div>
      )}
    </div>
  );
}

export default Vton;
