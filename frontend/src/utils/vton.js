export async function runVtonProcess({
  apiURL = "https://api.rohan.org.in",
  humanImageUrl,
  garmentImageUrl,
  category = "upper_body",
  denoise_steps = 30,
  seed = 42,
  onProgress = () => {}, // optional callback for UI progress
  onStatus = () => {}, // optional callback for UI status text
}) {
  // Helper: fetch URL → File
  async function getImageFile(url, name = "image") {
    const response = await fetch(url);
    if (!response.ok)
      throw new Error(`Failed to fetch image: ${response.statusText}`);
    const contentType = response.headers.get("content-type") || "image/jpeg";
    const ext = contentType.split("/")[1] || "jpg";
    const blob = await response.blob();
    return new File([blob], `${name}.${ext}`, { type: contentType });
  }

    try {
        console.log(`Human URL: ${humanImageUrl}`);
        console.log(`Garment URL: ${garmentImageUrl}`);
        console.log(`Category: ${category}`);

    onStatus("Preparing images...");
    const humanImage = await getImageFile(humanImageUrl, "human");
    const garmentImage = await getImageFile(garmentImageUrl, "garment");

    const formData = new FormData();
    formData.append("human_image", humanImage);
    formData.append("garment_image", garmentImage);
    formData.append("description", "");
    formData.append("denoise_steps", denoise_steps);
    formData.append("seed", seed);
    formData.append("category", category);

    onProgress(0);
    onStatus("Uploading to server...");
    const response = await fetch(`${apiURL}/tryon`, {
      method: "POST",
      body: formData,
    });
      if (!response.ok) throw new Error("Failed to start try-on job.");
      onProgress(5);
    const data = await response.json();
    if (data.error) throw new Error(data.error);

    const { task_id, position_in_queue } = data;
    if (!task_id) throw new Error("No task_id returned by backend.");

    // 🧮 Estimate time (10s per job in queue)
    const estimatedSeconds = Math.max(position_in_queue * 10, 5);
    console.log(`Task queued at position #${position_in_queue}`);
      onStatus("Doing the magic");
      onProgress(10);
    // Countdown progress
    let elapsed = 0;
    const progressTimer = setInterval(() => {
      elapsed += 1;
      const progress = Math.min((elapsed / estimatedSeconds) * 100, 100);
      onProgress(progress);
    }, 1000);

    // Wait for estimated time before starting polling
    await new Promise((resolve) =>
      setTimeout(resolve, estimatedSeconds * 1000)
    );
    clearInterval(progressTimer);
    onProgress(100);

    // 🕒 Start polling until done
    onStatus("Checking status...");
    while (true) {
      const res = await fetch(`${apiURL}/status/${task_id}`);
      const statusData = await res.json();

      if (statusData.error) throw new Error(statusData.error);
      const { status } = statusData;

      if (status === "queued" || status === "running") {
        onStatus(status === "queued" ? "Still in queue..." : "Processing...");
        await new Promise((r) => setTimeout(r, 3000));
        continue;
      }

      if (status === "done") {
        onStatus("✅ Done!");
        return `${apiURL}/result/${task_id}`;
      }

      if (status === "failed" || status === "error") {
        throw new Error(statusData.error || "Processing failed.");
      }
    }
  } catch (error) {
    console.error("❌ VTON Error:", error);
    onStatus(`❌ ${error.message}`);
    throw error;
  }
}

export function filterCategory(category) {
  if (!category) return null;

  const input = category.trim().toLowerCase();

  // 🧩 Mapping of possible user/product values → backend values
  const map = {
    upper: "upper_body",
    top: "upper_body",
    tshirt: "upper_body",
    shirt: "upper_body",
    hoodie: "upper_body",
    jacket: "upper_body",
    sweater: "upper_body",
    upper_body: "upper_body",

    pant: "lower_body",
    lower: "lower_body",
    trouser: "lower_body",
    jeans: "lower_body",
    skirt: "lower_body",
    shorts: "lower_body",
    lower_body: "lower_body",

    dress: "dresses",
    dresses: "dresses",
    gown: "dresses",
  };

  // Find a matching normalized key
  const normalized = Object.keys(map).find((key) => input.includes(key));

  return normalized ? map[normalized] : null;
}
