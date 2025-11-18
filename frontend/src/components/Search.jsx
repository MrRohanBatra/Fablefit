import { useContext, useState, useEffect, use } from "react";
import { cartContext, themeContext } from "../App";
import { UserContext } from "./FirebaseAuth";
import {
  Col,
  Container,
  Row,
  Form,
  Button,
  Card,
  Image,
  Spinner,
  Pagination,
  Toast,
} from "react-bootstrap";
import { useNavigate, useLocation, Link } from "react-router-dom";
import Product, { stripLocalhost } from "./Product/Product";
import "../CSS/products-scroll.css";
import Cart from "./Product/Cart";
import { AnimatePresence, motion } from "framer-motion";

function Search() {
  const navigate = useNavigate();
  const location = useLocation();

  const [theme] = useContext(themeContext);
  const [cart, setCart] = useContext(cartContext);
  const [user] = useContext(UserContext);

  const [query, setQuery] = useState("");
  const [selectedColor, setSelectedColor] = useState("");
  const [selectedSort, setSelectedSort] = useState("");
  const [selectedRange, setSelectedRange] = useState("");
  const [selectedSize, setSelectedSize] = useState("");

  const [products, setProducts] = useState([]); // raw data
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 12;

    useEffect(() => {
    const param = new URLSearchParams(location.search);
    setQuery(param.get("s") || "");
    setSelectedColor(param.get("color") || "");
    setSelectedSort(param.get("sort") || "");
    setSelectedRange(param.get("pricerange") || "");
    setSelectedSize(param.get("size") || "");
  }, [location.search]);

  useEffect(() => {
    if (query != "") {
      const loadProducts = async () => {
      try {
        const response = await fetch(`/api/products/search?s=${encodeURIComponent(query)}`);
        const data = await response.json();
        const wrapped = data.map((p) => new Product(p));
        setProducts(wrapped);
      } catch (err) {
        console.error("Error loading product.json:", err);
      } finally {
        setLoading(false);
      }
    };
    loadProducts();
    }

    
  }, [query]);


  // const filteredProducts = products
  //   .filter((p) => {
  //     if (
  //       selectedColor &&
  //       p.color.toLowerCase() !== selectedColor.toLowerCase()
  //     )
  //       return false;
  //     if (selectedRange) {
  //       const [min, max] = selectedRange.split(",").map(Number);
  //       if (min && p.price < min) return false;
  //       if (max && p.price > max) return false;
  //     }
  //     if (selectedSize && !p.sizes.includes(selectedSize)) return false;
  //     if (
  //       query &&
  //       !(
  //         p.name.toLowerCase().includes(query.toLowerCase()) ||
  //         p.description?.toLowerCase().includes(query.toLowerCase()) ||
  //         new RegExp(`\\b${query.toLowerCase()}\\b`).test(
  //           p.category?.toLowerCase() || ""
  //         )
  //       )
  //     )
  //       return false;
  //     return true;
  //   })
  //   .sort((a, b) => {
  //     if (selectedSort === "lowtohigh") return a.price - b.price;
  //     if (selectedSort === "hightolow") return b.price - a.price;
  //     return 0;
  //   });
  const filteredProducts = products
  .filter((p) => {
    if (
      selectedColor &&
      p.color.toLowerCase() !== selectedColor.toLowerCase()
    )
      return false;

    if (selectedRange) {
      const [min, max] = selectedRange.split(",").map(Number);
      if (min && p.price < min) return false;
      if (max && p.price > max) return false;
    }

    if (selectedSize && !p.sizes.includes(selectedSize)) return false;

    if (query) {
      // Split query into individual words (e.g. "mens genz tshirt" → ["mens", "genz", "tshirt"])
      const words = query.toLowerCase().split(/\s+/).filter(Boolean);

      // Combine searchable fields into one string
      const text =
        `${p.name} ${p.description || ""} ${p.category || ""}`.toLowerCase();

      // Require all words to appear at least once
      const allWordsMatch = words.every((word) => text.includes(word));
      if (!allWordsMatch) return false;
    }

    return true;
  })
  .sort((a, b) => {
    if (selectedSort === "lowtohigh") return a.price - b.price;
    if (selectedSort === "hightolow") return b.price - a.price;
    return 0;
  });

  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = filteredProducts.slice(
    indexOfFirstProduct,
    indexOfLastProduct
  );
  const totalPages = Math.ceil(filteredProducts.length / productsPerPage);

  // Update URL when filters change
  const handleFilterChange = () => {
    const params = new URLSearchParams(location.search);

    if (query) params.set("s", query);
    else params.delete("s");

    if (selectedColor) params.set("color", selectedColor);
    else params.delete("color");

    if (selectedSort) params.set("sort", selectedSort);
    else params.delete("sort");

    if (selectedRange) params.set("pricerange", selectedRange);
    else params.delete("pricerange");
    if (selectedSize) params.set("size", selectedSize);
    else params.delete("size");

    navigate(`/search?${params.toString()}`, { replace: true });
  };
  const [isFirstRender, setIsFirstRender] = useState(true);

  useEffect(() => {
    if (isFirstRender) {
      setIsFirstRender(false);
      return; // ⛔ Skip the first run
    }

    handleFilterChange();
    setCurrentPage(1);
  }, [selectedColor, selectedSort, selectedRange, selectedSize]);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);

    const topElement = document.getElementById("top");
    if (topElement) {
      topElement.scrollIntoView({ behavior: "smooth", block: "start" });
    } else {
      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  };
  const showToast = (message, variant = "info", duration = 2500) => {
    setToast({ show: true, message, variant });
    setTimeout(() => setToast((prev) => ({ ...prev, show: false })), duration);
  };
  const [toast, setToast] = useState({
    show: false,
    message: "",
    variant: "info",
  });
  const navi = useNavigate();
  return (
    <>
      {/* ✅ Toast Notifications */}
      <div
        className="position-fixed top-0 end-0 p-3"
        style={{ marginTop: "70px", zIndex: 1060 }}
      >
        <AnimatePresence>
          {toast.show && (
            <motion.div
              key="toast"
              initial={{ opacity: 0, x: 50, y: -10 }}
              animate={{ opacity: 1, x: 0, y: 0 }}
              exit={{ opacity: 0, x: 50, y: -10 }}
              transition={{ duration: 0.3 }}
            >
              <Toast
                bg={
                  toast.variant === "danger"
                    ? "danger"
                    : toast.variant === "success"
                    ? "success"
                    : "light"
                }
                onClose={() => setToast({ ...toast, show: false })}
                className="shadow-lg rounded-3"
              >
                <Toast.Header className="d-flex justify-content-between align-items-center">
                  <div className="d-flex align-items-center">
                    <Image
                      src="/react.svg"
                      width={20}
                      className="rounded me-2"
                    />
                    <strong className="me-auto">FableFit</strong>
                  </div>
                </Toast.Header>

                <Toast.Body
                  className={`fw-semibold ${
                    toast.variant === "danger" ? "text-white" : ""
                  }`}
                >
                  {toast.message}
                  {toast.variant === "info" && (
                    <Spinner
                      animation="border"
                      size="sm"
                      variant="primary"
                      className="ms-2"
                    />
                  )}
                </Toast.Body>
              </Toast>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      <Container
        id="top"
        fluid
        className={`mt-4 ${theme === "dark" ? "bg-dark text-light" : ""}`}
      >
        <Row>
          {/* LEFT FILTERS */}
          <Col xs={12} md={3} className="border-end">
            <div className="d-flex align-items-center gap-3 mb-3">
              <i className="bi bi-sliders fs-4 text-primary"></i>
              <h3 className="m-0">Filters</h3>
            </div>

            <Form.Group controlId="colorFilter" className="mb-3">
              <Form.Label>Color</Form.Label>
              <Form.Select
                value={selectedColor}
                onChange={(e) => setSelectedColor(e.target.value)}
              >
                <option value="">All Colors</option>
                <option value="red">Red</option>
                <option value="blue">Blue</option>
                <option value="green">Green</option>
                <option value="black">Black</option>
                <option value="white">White</option>
                <option value="peach">Peach</option>
                <option value="navy">Navy</option>
                <option value="yellow">Yellow</option>
              </Form.Select>
            </Form.Group>

            <Form.Group controlId="priceRange" className="mb-3">
              <Form.Label>Price Range</Form.Label>
              <Form.Select
                value={selectedRange}
                onChange={(e) => setSelectedRange(e.target.value)}
              >
                <option value="">Any Price</option>
                <option value="0,1000">Below ₹1000</option>
                <option value="1000,2000">₹1000 - ₹2000</option>
                <option value="2000,">Above ₹2000</option>
              </Form.Select>
            </Form.Group>
            <Form.Group controlId="sizeFilter" className="mb-3">
              <Form.Label>Size</Form.Label>
              <Form.Select
                value={selectedSize}
                onChange={(e) => setSelectedSize(e.target.value)}
              >
                <option value="">All Sizes</option>
                <option value="S">S</option>
                <option value="M">M</option>
                <option value="L">L</option>
                <option value="XL">XL</option>
                <option value="XXL">XXL</option>
              </Form.Select>
            </Form.Group>

            <Form.Group controlId="sortFilter" className="mb-3">
              <Form.Label>Sort By</Form.Label>
              <Form.Select
                value={selectedSort}
                onChange={(e) => setSelectedSort(e.target.value)}
              >
                <option value="">Default</option>
                <option value="lowtohigh">Price: Low to High</option>
                <option value="hightolow">Price: High to Low</option>
              </Form.Select>
            </Form.Group>

            <div className="d-flex justify-content-end">
              <Button
                variant="secondary"
                onClick={() => {
                  setSelectedColor("");
                  setSelectedSort("");
                  setSelectedRange("");
                  setSelectedSize("");
                  navigate(`/search?s=${query}`);
                }}
              >
                Clear Filters
              </Button>
            </div>
          </Col>

          {/* RIGHT PRODUCTS */}
          <Col xs={12} md={9} className="products-section">
            <div className="products-scroll">
              <Row className="mb-3">
                <Col>
                  <h4 className="border-bottom pb-2">Products</h4>
                </Col>
              </Row>
              {loading ? (
                <div className="d-flex justify-content-center align-items-center p-5">
                  <Spinner animation="border" variant="primary" />
                </div>
              ) : (
                <>
                  <Row className="g-3">
                    {currentProducts.length > 0 ? (
                      currentProducts.map((product) => (
                        <Col xs={12} sm={6} lg={4} key={product._id} className>
                          <Card
                            className="h-100 card-hover ms-3"
                            onClick={() => {
                              navi(`/product/${product._id}`);
                            }}
                          >
                            <Image
                              src={stripLocalhost(product.firstImage())}
                              alt={product.name}
                              className="card-img-top object-fit-cover"
                              style={{ height: "200px" }}
                            />
                            <Card.Body>
                              <Card.Title>{product.name}
                                
                              </Card.Title>
                              <Card.Text>
                                <small className="text-muted">
                                  {product.companyName}
                                </small>
                                <br />
                                Color: {product.color} <br />
                                Price: {product.formattedPrice()} <br />
                                Sizes: {product.availableSizes().join(", ")}
                              </Card.Text>
                              <Button
                                variant="primary"
                                onClick={async (e) => {
                                  e.stopPropagation();
                                  try {
                                    const updated = await cart.addProduct(
                                      product,
                                      "M",
                                      product.color,
                                      1
                                    );
                                    setCart(updated);
                                    showToast("Added to Cart", "success", 1000); // ✅ new instance triggers re-render
                                  } catch (err) {
                                    console.error(
                                      "❌ Failed to add product:",
                                      err
                                    );
                                  }
                                }}
                              >
                                Add to Cart
                              </Button>
                            </Card.Body>
                          </Card>
                        </Col>
                      ))
                    ) : (
                      <Col>
                        <p>No products found matching your filters.</p>
                      </Col>
                    )}
                  </Row>

                  {/* 📄 Pagination */}
                  {!loading && totalPages > 1 && (
                    <div className="d-flex justify-content-center my-4">
                      <Pagination>
                        <Pagination.Prev
                          disabled={currentPage === 1}
                          onClick={() => handlePageChange(currentPage - 1)}
                        />
                        {Array.from({ length: totalPages }).map((_, i) => (
                          <Pagination.Item
                            key={i + 1}
                            active={i + 1 === currentPage}
                            onClick={() => handlePageChange(i + 1)}
                          >
                            {i + 1}
                          </Pagination.Item>
                        ))}
                        <Pagination.Next
                          disabled={currentPage === totalPages}
                          onClick={() => handlePageChange(currentPage + 1)}
                        />
                      </Pagination>
                    </div>
                  )}

                  {/* 📊 Product count info */}
                  <div className="text-center text-muted mb-3">
                    Showing{" "}
                    {filteredProducts.length === 0
                      ? "0"
                      : `${indexOfFirstProduct + 1}-${Math.min(
                          indexOfLastProduct,
                          filteredProducts.length
                        )}`}{" "}
                    of {filteredProducts.length} products
                  </div>
                </>
              )}
            </div>
          </Col>
        </Row>
      </Container>
    </>
  );
}

export default Search;
