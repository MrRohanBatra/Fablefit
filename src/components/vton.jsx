import React, { useState } from "react";

function Vton() {
  const [humanImage, setHumanImage] = useState(null);
  const [garmentImage, setGarmentImage] = useState(null);
  const [category, setCategory] = useState("upper_body");
  const [steps, setSteps] = useState(30);
  const [seed, setSeed] = useState(42);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState("");

  // ✅ Flask API base URL (no trailing slash)
  const apiURL = "https://api.rohan.org.in";

  async function handleSubmit() {
    if (!humanImage || !garmentImage) {
      alert("Please upload both images!");
      return;
    }

    setLoading(true);
    setResult(null);
    setStatus("Uploading images...");

    const formData = new FormData();
    formData.append("human_image", humanImage);
    formData.append("garment_image", garmentImage);
    formData.append("description", "");
    formData.append("denoise_steps", steps);
    formData.append("seed", seed);
    formData.append("category", category);

    // try {
    //   const response = await fetch(`${apiURL}/tryon`, {
    //     method: "POST",
    //     body: formData,
    //   });

    //   if (!response.ok) throw new Error("Failed to start task");
    //   const data = await response.json();

    //   const { task_id, position } = data;
    //   console.log("Task created:", data);
    //   setStatus(`Task queued (position #${position})...`);

    //   let currentStatus = "queued";
    //   let resultUrl = null;

    //   while (true) {
    //     await new Promise((res) => setTimeout(res, 3000)); // wait 3 sec
    //     const res = await fetch(`${apiURL}/task_status/${task_id}`);
    //     const statusData = await res.json();

    //     if (statusData.error) throw new Error(statusData.error);

    //     currentStatus = statusData.status;
    //     resultUrl = statusData.result_url;

    //     if (currentStatus === "queued") {
    //       setStatus(`Waiting in queue (#${position})...`);
    //     } else if (currentStatus === "processing") {
    //       setStatus("Processing...");
    //     } else if (currentStatus === "done") {
    //       setStatus("✅ Done!");
    //       break; // ✅ exit loop
    //     } else if (currentStatus === "error") {
    //       throw new Error(statusData.error || "Processing failed");
    //     }
    //   }

    //   // 3️⃣ Display result
    //   if (resultUrl) {
    //     setResult(resultUrl);
    //   } else {
    //     throw new Error("No result URL received");
    //   }
    // } catch (e) {
    //   console.error(e);
    //   alert("Error: " + e.message);
    //   setStatus("❌ Error occurred");
    // } finally {
    //   setLoading(false);
    // }
    try {
      const response = await fetch(`${apiURL}/tryon`, {
        method: "POST",
        body: formData,
      });

      if (!response.ok) throw new Error("Failed to start task");

      const data = await response.json();
      const { task_id ,position_in_queue} = data;

      console.log("Task created:", data);
      setStatus(`Task queued #${position_in_queue}...`);

      const intervalId = setInterval(async () => {
        try {
          const res = await fetch(`${apiURL}/status/${task_id}`);
          const statusData = await res.json();

          const { status, error, result_path,mask_path } = statusData;

          if (error) {
            throw new Error(error);
          }

          if (status === "queued") {
            setStatus("Waiting in queue...");
          } else if (status === "processing") {
            setStatus("Processing...");
          } else if (status === "done") {
            setStatus("✅ Done!");
            setResult(`${apiURL}/result/${task_id}`);
            clearInterval(intervalId);
          } else if (status === "error") {
            setStatus("❌ Error during processing");
            clearInterval(intervalId);
          }
        } catch (err) {
          console.error("Polling error:", err);
          setStatus("❌ Error fetching status");
          clearInterval(intervalId);
        }
      }, 3000); 
    } catch (error) {
      console.error("Error:", error);
      setStatus("❌ Failed to start task");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="d-flex flex-column mw-40 justify-content-center text-center mt-5">
      <h2>🧥 FableFit Try-On</h2>

      {/* Upload Inputs */}
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

      {/* Parameters */}
      <div className="d-flex gap-2">
        <p>Denoising Steps:</p>
        <input
          type="number"
          value={steps}
          onChange={(e) => setSteps(e.target.value)}
        />
      </div>

      <div className="d-flex gap-2">
        <p>Seed:</p>
        <input
          type="number"
          value={seed}
          onChange={(e) => setSeed(e.target.value)}
        />
      </div>

      {/* Category Dropdown */}
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

      {/* Submit Button */}
      <button
        onClick={() => {
          for (let i = 0; i < 10; i++){
            handleSubmit()
          }
        }}
        disabled={loading}
        style={{
          marginTop: "15px",
          padding: "10px 20px",
          background: loading ? "gray" : "black",
          color: "white",
          border: "none",
          borderRadius: "8px",
          cursor: "pointer",
        }}
      >
        {loading ? "Processing..." : "Generate Try-On"}
      </button>

      {/* Status */}
      {status && <p style={{ marginTop: "15px" }}>{status}</p>}

      {/* Result */}
      {result && (
        <div style={{ marginTop: "20px" }}>
          <h3>Result:</h3>
          <img
            src={result}
            alt="Try-On Result"
            width="300"
            style={{
              borderRadius: "8px",
              boxShadow: "0 4px 8px rgba(0,0,0,0.2)",
            }}
          />
        </div>
        
      )}
    </div>
  );
}

export default Vton;
