import React, { useState } from "react";
import { Form, Button, Container, Row, Col, Card, Spinner } from "react-bootstrap";
import Product from "./Product/Product"; // your product class

export default function AddProduct() {
  const [formData, setFormData] = useState(new Product());
  const [preview, setPreview] = useState([]);
  const [imageFiles, setImageFiles] = useState([]); // actual files
  const [loading, setLoading] = useState(false);

  // 🧠 Handle input fields
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  // 🧠 Handle comma-separated array fields (sizes, etc.)
  const handleArrayChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value.split(",").map((v) => v.trim()) });
  };

  // 🧠 Handle image file selection
  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    const imageURLs = files.map((file) => URL.createObjectURL(file));

    setPreview(imageURLs);
    setImageFiles(files);
  };

  // 🧩 Upload each image → get URLs → send product data
  const uploadImagesAndSubmit = async (data, images) => {
    try {
      const imageUrls = [];

      // Upload each image individually
      for (const file of images) {
        const imgData = new FormData();
        imgData.append("abcd", file);

        const res = await fetch("/api/products/upload", {
          method: "POST",
          body: imgData,
        });

        const result = await res.json();

        if (res.ok && result.url) {
          const imgURL = `${result.url}`;
          imageUrls.push(imgURL);
        } else {
          throw new Error(result.message || "Failed to upload image");
        }
      }

      // Combine URLs into product data
      const productData = { ...data, images: imageUrls };

      // Send product data to backend
      const response = await fetch("/api/products/add", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(productData),
      });

      const result = await response.json();

      if (!response.ok) throw new Error(result.message);
      return result;
    } catch (error) {
      throw error;
    }
  };

  // 🧩 Handle submit
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await uploadImagesAndSubmit(formData, imageFiles);
      alert("✅ Product added successfully!");
      setFormData(new Product());
      setPreview([]);
      setImageFiles([]);
    } catch (error) {
      console.error("Upload failed:", error);
      alert("❌ Failed to add product. Check console for details.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="py-4">
      <Row className="justify-content-center">
        <Col md={8}>
          <Card className="shadow-sm p-4">
            <h3 className="mb-4 text-center">Add New Product</h3>

            <Form onSubmit={handleSubmit}>
              <Form.Group className="mb-3">
                <Form.Label>Product Name</Form.Label>
                <Form.Control
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="Enter product name"
                  required
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Description</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="Enter product description"
                />
              </Form.Group>

              <Row>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Category</Form.Label>
                    <Form.Select
                      name="category"
                      value={formData.category}
                      onChange={handleChange}
                      required
                    >
                      <option value="">Select Category</option>
                      <option value="Women">Women</option>
                      <option value="Men">Men</option>
                      <option value="Kids">Kids</option>
                      <option value="Unisex">Unisex</option>
                    </Form.Select>
                  </Form.Group>
                </Col>

                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Price (₹)</Form.Label>
                    <Form.Control
                      type="number"
                      name="price"
                      value={formData.price}
                      onChange={handleChange}
                      placeholder="Enter price"
                      min="0"
                      required
                    />
                  </Form.Group>
                </Col>
              </Row>

              <Row>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Sizes (comma separated)</Form.Label>
                    <Form.Control
                      type="text"
                      name="sizes"
                      value={formData.sizes.join(", ")}
                      onChange={handleArrayChange}
                      placeholder="e.g., S, M, L, XL"
                    />
                  </Form.Group>
                </Col>

                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Color</Form.Label>
                    <Form.Control
                      type="text"
                      name="color"
                      value={formData.color}
                      onChange={handleChange}
                      placeholder="Enter color"
                    />
                  </Form.Group>
                </Col>
              </Row>

              <Form.Group className="mb-3">
                <Form.Label>Stock</Form.Label>
                <Form.Control
                  type="number"
                  name="stock"
                  value={formData.stock}
                  onChange={handleChange}
                  placeholder="Enter stock quantity"
                  min="0"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Company Name</Form.Label>
                <Form.Control
                  type="text"
                  name="companyName"
                  value={formData.companyName}
                  onChange={handleChange}
                  placeholder="Enter company name"
                  required
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>VTON Category</Form.Label>
                <Form.Select
                  name="vton_category"
                  value={formData.vton_category}
                  onChange={handleChange}
                >
                  <option value="">Select Category</option>
                  <option value="upper_body">Upper</option>
                  <option value="lower_body">Lower</option>
                  <option value="dresses">Dress</option>
                </Form.Select>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Upload Images</Form.Label>
                <Form.Control
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={handleImageChange}
                />
                <div className="mt-3 d-flex flex-wrap gap-2">
                  {preview.map((img, i) => (
                    <img
                      key={i}
                      src={img}
                      alt="preview"
                      width="80"
                      height="80"
                      className="rounded border"
                    />
                  ))}
                </div>
              </Form.Group>

              <Button
                type="submit"
                variant="primary"
                className="w-100 mt-3"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <Spinner
                      as="span"
                      animation="border"
                      size="sm"
                      role="status"
                      aria-hidden="true"
                    />{" "}
                    Uploading...
                  </>
                ) : (
                  "Add Product"
                )}
              </Button>
            </Form>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}
