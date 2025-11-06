import React, { useState, useEffect } from "react";
import ProgressBar from "react-bootstrap/ProgressBar";

function Vton() {
  const [humanImage, setHumanImage] = useState(null);
  const [garmentImage, setGarmentImage] = useState(null);
  const [category, setCategory] = useState("upper_body");
  const [steps, setSteps] = useState(30);
  const [seed, setSeed] = useState(42);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState("");
  const [vtonAccepted, setVtonAccepted] = useState(false);
  const [estTime, setEstTime] = useState(0); // in seconds
  const [progress, setProgress] = useState(0);
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

    try {
      const response = await fetch(`${apiURL}/tryon`, {
        method: "POST",
        body: formData,
      });

      if (!response.ok) throw new Error("Failed to start task");
      const data = await response.json();

      const { task_id, position_in_queue } = data;
      console.log("Task created:", data);
      setVtonAccepted(true);
      setStatus(`Task queued at position #${position_in_queue}`);
      const estimatedSeconds = position_in_queue * 10; // assume 10 sec/image
      setEstTime(estimatedSeconds);

      // Start progress bar countdown
      let elapsed = 0;
      const progressTimer = setInterval(() => {
        elapsed += 1;
        setProgress(Math.min((elapsed / estimatedSeconds) * 100, 100));
      }, 1000);

      // Wait for estimated time before starting polling
      setTimeout(() => {
        clearInterval(progressTimer);
        setProgress(100);
        pollStatus(task_id);
      }, estimatedSeconds * 1000);
    } catch (error) {
      console.error("Error:", error);
      setStatus("❌ Failed to start task");
    } finally {
      setLoading(false);
    }
  }

  async function pollStatus(task_id) {
    setStatus("Checking status...");
    const intervalId = setInterval(async () => {
      try {
        const res = await fetch(`${apiURL}/status/${task_id}`);
        const statusData = await res.json();
        const { status, error } = statusData;

        if (error) throw new Error(error);

        if (status === "queued") {
          setStatus("Still in queue...");
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
        setVtonAccepted(false);
      } catch (err) {
        console.error("Polling error:", err);
        setStatus("❌ Error fetching status");
        clearInterval(intervalId);
      }
    }, 3000);
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
      {!vtonAccepted ? (
        <button
          onClick={handleSubmit}
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
      ) : null}

      {/* Status */}
      {status && <p style={{ marginTop: "15px" }}>{status}</p>}

      {/* Progress Bar */}
      {vtonAccepted && estTime > 0 && (
        <div className="mt-3 px-4">
          <p>
            Estimated time: <b>{estTime}s</b>
          </p>
          <ProgressBar
            now={progress}
            label={`${Math.round(progress)}%`}
            animated
            striped
            variant="info"
          />
        </div>
      )}

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
