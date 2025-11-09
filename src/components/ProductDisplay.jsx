// import { useContext, useEffect, useState } from "react";
// import {
//   Container,
//   Row,
//   Col,
//   Image,
//   Button,
//   Spinner,
//   Form,
//     Card,
//   Badge,
// } from "react-bootstrap";
// import { useParams, useNavigate } from "react-router-dom";
// import Product from "./Product/Product";
// import "../CSS/product-display.css";
// import { cartContext } from "../App";

// function ProductDisplay() {
//   const { id } = useParams();
//   const [cart, setCart] = useContext(cartContext);
//   const [product, setProduct] = useState(null);
//   const [similar, setSimilar] = useState([]);
//   const [heroImage, setHeroImage] = useState(0);
//   const [loading, setLoading] = useState(true);
//   const [tryOnLoading, setTryOnLoading] = useState(false);
//   const [selectedSize, setSelectedSize] = useState("");
//   const [quantity, setQuantity] = useState(1);
//   const navigate = useNavigate();
//   const [vtonStart, setVtonStart] = useState(false);
//   const [vtonEnd, setVtonEnd] = useState(false);
//   const [vtonProgress, setVtonProgress] = useState(0);
//   async function getProductWithID(pid) {
//     const response = await fetch("http://localhost:5500/api/products/");
//     const data = await response.json();
//     const products = data.map((p) => new Product(p));
//     const product = products.find((el) => el._id == pid);
//     return { product, products };
//   }

//   useEffect(() => {
//     getProductWithID(id)
//       .then(({ product, products }) => {
//         setProduct(product);
//         if (product) {
//           const related = products
//             .filter(
//               (p) =>
//                 p._id !== product._id &&
//                 (p.color === product.color ||
//                   p.category === product.category ||
//                   Math.abs(p.price - product.price) < 500)
//             )
//             .slice(0, 10);
//           setSimilar(related);
//         }
//       })
//       .finally(() => setLoading(false));
//   }, [id]);

//   const handleTryOn = async () => {
//     setTryOnLoading(true);
//     try {
//       const res = await fetch(`/api/vton/${id}`);
//       const data = await res.json();
//       const tryOnImage = data.imageUrl;
//       setProduct((prev) => ({
//         ...prev,
//         images: [...prev.images, tryOnImage],
//       }));
//       setHeroImage(product.images.length);
//     } catch (err) {
//       console.error("Try-on failed:", err);
//       alert("Virtual try-on unavailable at the moment.");
//     } finally {
//       setTryOnLoading(false);
//     }
//   };

//   const handleAddToCart = async () => {
//   if (!selectedSize) {
//     alert("Please select a size first.");
//     return;
//   }

//   try {
//     const color = product.color || "Default";
//     const quantityToAdd = quantity || 1;

//     // 🧾 Call your Cart class method — it syncs with backend automatically
//     const updatedCart = await cart.addProduct(
//       product,
//       selectedSize,
//       color,
//       quantityToAdd
//     );

//     setCart(updatedCart); // ✅ replace old cart with new one

//     alert(`${quantityToAdd} item(s) added to cart!`);
//   } catch (err) {
//     console.error("❌ Failed to add to cart:", err);
//     alert("Failed to add item to cart. Please try again.");
//   }
// };


//   if (loading) {
//     return (
//       <div className="d-flex justify-content-center align-items-center p-5">
//         <Spinner animation="border" />
//       </div>
//     );
//   }

//   if (!product) {
//     return <p className="text-center text-danger">Product not found.</p>;
//   }

//   return (
//     <Container className="py-4">
//       <Row className="justify-content-center">
//         <Col md={10} lg={9}>
//           <Card className="border-0 shadow-lg p-4 rounded-4 mb-5 bg-body-tertiary">
//             <Row>
//               {/* LEFT IMAGES */}
//               <Col md={6} className="text-center">
//                 <Image
//                   src={product.images[heroImage]}
//                   alt={product.name}
//                   fluid
//                   className="rounded-4 shadow-sm mb-3 hero-image"
//                   style={{
//                     maxHeight: "500px",
//                     objectFit: "contain",
//                     backgroundColor: "var(--bs-light-bg-subtle)",
//                   }}
//                 />
//                 <div className="d-flex justify-content-center gap-2 flex-wrap">
//                   {product.images.map((img, index) => (
//                     <Image
//                       key={index}
//                       src={img}
//                       alt={`thumb-${index}`}
//                       onClick={() => setHeroImage(index)}
//                       className={`rounded-3 thumb ${
//                         heroImage === index
//                           ? "border border-primary border-3"
//                           : ""
//                       }`}
//                       style={{
//                         height: "80px",
//                         width: "70px",
//                         objectFit: "cover",
//                         cursor: "pointer",
//                       }}
//                     />
//                   ))}
//                 </div>
//               </Col>

//               {/* RIGHT DETAILS */}
//               <Col md={6} className="ps-md-5 mt-4 mt-md-0">
//                 <div className="d-flex align-items-center flex-wrap gap-2">
//                   <h3 className="fw-bold mb-0">{product.name}</h3>
//                   <Badge bg="secondary" className="px-3 py-2 text-uppercase">
//                     {product.category}
//                   </Badge>
//                 </div>
//                 <p className="text-danger mt-2 mb-0">
//                   Sold by {product.companyName}
//                 </p>
//                 <h4 className="text-primary mt-3 mb-2">
//                   ₹{product.price.toLocaleString()}
//                 </h4>
//                 <p className="text-muted">{product.description}</p>

//                 {/* SIZE SELECTION */}
//                 <div className="mt-3">
//                   <strong>Size:</strong>
//                   <div className="mt-2 d-flex flex-wrap gap-2">
//                     {product.sizes.map((size, i) => (
//                       <Button
//                         key={i}
//                         variant={
//                           selectedSize === size
//                             ? "primary"
//                             : "outline-secondary"
//                         }
//                         onClick={() => setSelectedSize(size)}
//                         className="px-3 py-1 text-success"
//                       >
//                         {size}
//                       </Button>
//                     ))}
//                   </div>
//                 </div>

//                 {/* QUANTITY SELECTOR */}
//                 <div className="mt-4 d-flex align-items-center gap-2">
//                   <strong>Quantity:</strong>
//                   <Form.Control
//                     type="number"
//                     value={quantity}
//                     min={1}
//                     onChange={(e) => setQuantity(parseInt(e.target.value))}
//                     style={{ width: "90px" }}
//                   />
//                 </div>

//                 {/* BUTTONS */}
//                 <div className="d-flex flex-column flex-md-row gap-3 mt-4">
//                   <Button
//                     size="lg"
//                     variant="primary"
//                     className="flex-grow-1"
//                     onClick={handleAddToCart}
//                   >
//                     Add to Cart
//                   </Button>
//                   <Button
//                     size="lg"
//                     variant="outline-success"
//                     onClick={handleTryOn}
//                     disabled={tryOnLoading}
//                     className="flex-grow-1"
//                   >
//                     {tryOnLoading ? "Generating..." : "Try On Virtually"}
//                   </Button>
//                 </div>

//                 {/* TRY ON STATUS */}
//                 {product.images.length > 3 && (
//                   <p className="text-success mt-3 mb-0 fw-semibold">
//                     ✅ Virtual try-on image added to gallery!
//                   </p>
//                 )}
//               </Col>
//             </Row>
//           </Card>

//           {/* SIMILAR PRODUCTS */}
//           {similar.length > 0 && (
//             <div className="mt-5">
//               <h4 className="fw-bold mb-3 border-bottom pb-2">
//                 Similar Products
//               </h4>
//               <Row className="g-4">
//                 {similar.map((item) => (
//                   <Col xs={12} sm={6} md={4} key={item._id}>
//                     <Card
//                       className="h-100 shadow-sm border-0 hover-card"
//                       onClick={() => navigate(`/product/${item._id}`)}
//                       style={{ cursor: "pointer" }}
//                     >
//                       <Image
//                         src={item.firstImage()}
//                         alt={item.name}
//                         fluid
//                         className="rounded-top"
//                         style={{
//                           height: "220px",
//                           objectFit: "cover",
//                         }}
//                       />
//                       <Card.Body>
//                         <Card.Title className="fw-semibold">
//                           {item.name}
//                         </Card.Title>
//                         <Card.Text className="text-muted small">
//                           {item.color} — ₹{item.price}
//                         </Card.Text>
//                       </Card.Body>
//                     </Card>
//                   </Col>
//                 ))}
//               </Row>
//             </div>
//           )}
//         </Col>
//       </Row>
//     </Container>
//   );
// }

// export default ProductDisplay;
import { useContext, useEffect, useState } from "react";
import {
  Container,
  Row,
  Col,
  Image,
  Button,
  Spinner,
  Form,
  Card,
  Badge,
  ProgressBar,
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import Product from "./Product/Product";
import "../CSS/product-display.css";
import { cartContext } from "../App";
import { runVtonProcess,filterCategory } from "../utils/vton";
import { UserContext } from "./FirebaseAuth";

function ProductDisplay() {
  const { id } = useParams();
  const [cart, setCart] = useContext(cartContext);
  const [product, setProduct] = useState(null);
  const [similar, setSimilar] = useState([]);
  const [heroImage, setHeroImage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [tryOnLoading, setTryOnLoading] = useState(false);
  const [selectedSize, setSelectedSize] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [vtonProgress, setVtonProgress] = useState(0);
  const [vtonStatus, setVtonStatus] = useState("");
  const navigate = useNavigate();
  const [user, setUser] = useContext(UserContext);

  async function getProductWithID(pid) {
    const response = await fetch("http://localhost:5500/api/products/");
    const data = await response.json();
    const products = data.map((p) => new Product(p));
    const product = products.find((el) => el._id == pid);
    return { product, products };
  }

  useEffect(() => {
    getProductWithID(id)
      .then(({ product, products }) => {
        setProduct(product);
        if (product) {
          console.log(product);
          const related = products
            .filter(
              (p) =>
                p._id !== product._id &&
                (p.color === product.color ||
                  p.category === product.category ||
                  Math.abs(p.price - product.price) < 500)
            )
            .slice(0, 10);
          setSimilar(related);
        }
      })
      .finally(() => setLoading(false));
  }, [id]);

  // 🧠 Handle Virtual Try-On
  const handleTryOn = async () => {
    if (!product) return;
    setTryOnLoading(true);
    setVtonProgress(0);
    setVtonStatus("Starting...");

    try {
      console.log(`Server Product VTON Category: ${product.vtonCategory}`)
      const resultUrl = await runVtonProcess({
        apiURL: "https://api.rohan.org.in",
        humanImageUrl: user?.getVtonImageUrl() || "",// replace with actual user image
        garmentImageUrl: product.images[0],
        category: filterCategory(product.vtonCategory || "upper_body"),
        onProgress: setVtonProgress,
        onStatus: setVtonStatus,
      });

      // Add the result image to the product gallery
      setProduct((prev) => ({
        ...prev,
        images: [...prev.images, resultUrl],
      }));

      // Show new image as hero
      setHeroImage(product.images.length);
      setVtonStatus("✅ Done!");
    } catch (err) {
      console.error("❌ Try-on failed:", err);
      setVtonStatus("❌ Error during try-on");
      alert("Virtual try-on unavailable at the moment.");
    } finally {
      setTryOnLoading(false);
    }
  };

  const handleAddToCart = async () => {
    if (!selectedSize) {
      alert("Please select a size first.");
      return;
    }

    try {
      const color = product.color || "Default";
      const quantityToAdd = quantity || 1;
      const updatedCart = await cart.addProduct(
        product,
        selectedSize,
        color,
        quantityToAdd
      );
      setCart(updatedCart);
      alert(`${quantityToAdd} item(s) added to cart!`);
    } catch (err) {
      console.error("❌ Failed to add to cart:", err);
      alert("Failed to add item to cart. Please try again.");
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center p-5">
        <Spinner animation="border" />
      </div>
    );
  }

  if (!product) {
    return <p className="text-center text-danger">Product not found.</p>;
  }

  return (
    <Container className="py-4">
      <Row className="justify-content-center">
        <Col md={10} lg={9}>
          <Card className="border-0 shadow-lg p-4 rounded-4 mb-5 bg-body-tertiary">
            <Row>
              {/* LEFT IMAGES */}
              <Col md={6} className="text-center">
                <Image
                  src={product.images[heroImage]}
                  alt={product.name}
                  fluid
                  className="rounded-4 shadow-sm mb-3 hero-image"
                  style={{
                    maxHeight: "500px",
                    objectFit: "contain",
                    backgroundColor: "var(--bs-light-bg-subtle)",
                  }}
                />
                <div className="d-flex justify-content-center gap-2 flex-wrap">
                  {product.images.map((img, index) => (
                    <Image
                      key={index}
                      src={img}
                      alt={`thumb-${index}`}
                      onClick={() => setHeroImage(index)}
                      className={`rounded-3 thumb ${
                        heroImage === index
                          ? "border border-primary border-3"
                          : ""
                      }`}
                      style={{
                        height: "80px",
                        width: "70px",
                        objectFit: "cover",
                        cursor: "pointer",
                      }}
                    />
                  ))}
                </div>
              </Col>

              {/* RIGHT DETAILS */}
              <Col md={6} className="ps-md-5 mt-4 mt-md-0">
                <div className="d-flex align-items-center flex-wrap gap-2">
                  <h3 className="fw-bold mb-0">{product.name}</h3>
                  <Badge bg="secondary" className="px-3 py-2 text-uppercase">
                    {product.category}
                  </Badge>
                </div>
                <p className="text-danger mt-2 mb-0">
                  Sold by {product.companyName}
                </p>
                <h4 className="text-primary mt-3 mb-2">
                  ₹{product.price.toLocaleString()}
                </h4>
                <p className="text-muted">{product.description}</p>

                {/* SIZE SELECTION */}
                <div className="mt-3">
                  <strong>Size:</strong>
                  <div className="mt-2 d-flex flex-wrap gap-2">
                    {product.sizes.map((size, i) => (
                      <Button
                        key={i}
                        variant={
                          selectedSize === size
                            ? "primary"
                            : "outline-secondary"
                        }
                        onClick={() => setSelectedSize(size)}
                        className="px-3 py-1 text-success"
                      >
                        {size}
                      </Button>
                    ))}
                  </div>
                </div>

                {/* QUANTITY SELECTOR */}
                <div className="mt-4 d-flex align-items-center gap-2">
                  <strong>Quantity:</strong>
                  <Form.Control
                    type="number"
                    value={quantity}
                    min={1}
                    onChange={(e) => setQuantity(parseInt(e.target.value))}
                    style={{ width: "90px" }}
                  />
                </div>

                {/* BUTTONS */}
                <div className="d-flex flex-column flex-md-row gap-3 mt-4">
                  <Button
                    size="lg"
                    variant="primary"
                    className="flex-grow-1"
                    onClick={handleAddToCart}
                  >
                    Add to Cart
                  </Button>

                  {/* VTON BUTTON WITH INLINE PROGRESS */}
                  <Button
                    size="lg"
                    variant="outline-success"
                    disabled={tryOnLoading}
                    className="flex-grow-1 position-relative"
                    onClick={handleTryOn}
                    style={{ overflow: "hidden" }}
                  >
                    {tryOnLoading ? (
                      <div className="w-100">
                        <div>{vtonStatus || "Processing..."}</div>
                        <ProgressBar
                          now={vtonProgress}
                          style={{
                            height: "6px",
                            marginTop: "5px",
                          }}
                          variant="info"
                          animated
                          striped
                        />
                      </div>
                    ) : (
                      "Try On Virtually"
                    )}
                  </Button>
                </div>

                {/* TRY ON STATUS */}
                {product.images.length > 3 && (
                  <p className="text-success mt-3 mb-0 fw-semibold">
                    ✅ Virtual try-on image added to gallery!
                  </p>
                )}
              </Col>
            </Row>
          </Card>

          {/* SIMILAR PRODUCTS */}
          {similar.length > 0 && (
            <div className="mt-5">
              <h4 className="fw-bold mb-3 border-bottom pb-2">
                Similar Products
              </h4>
              <Row className="g-4">
                {similar.map((item) => (
                  <Col xs={12} sm={6} md={4} key={item._id}>
                    <Card
                      className="h-100 shadow-sm border-0 hover-card"
                      onClick={() => navigate(`/product/${item._id}`)}
                      style={{ cursor: "pointer" }}
                    >
                      <Image
                        src={item.firstImage()}
                        alt={item.name}
                        fluid
                        className="rounded-top"
                        style={{
                          height: "220px",
                          objectFit: "cover",
                        }}
                      />
                      <Card.Body>
                        <Card.Title className="fw-semibold">
                          {item.name}
                        </Card.Title>
                        <Card.Text className="text-muted small">
                          {item.color} — ₹{item.price}
                        </Card.Text>
                      </Card.Body>
                    </Card>
                  </Col>
                ))}
              </Row>
            </div>
          )}
        </Col>
      </Row>
    </Container>
  );
}

export default ProductDisplay;
