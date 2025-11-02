import { useState, useEffect, useContext, use } from "react";
import { useParams, useNavigate } from "react-router";
import {
  Container,
  Row,
  Col,
  Nav,
  Form,
  Button,
  Card,
  InputGroup,
  Modal,
  Spinner,
  Toast,
  Image,
} from "react-bootstrap";
import { motion, AnimatePresence } from "framer-motion";
import { UserContext } from "./FirebaseAuth";
import {
  getAddress,
  getPhoneNumber,
  getVtonImageUrl,
  updateAddress,
} from "../utils/util";
import { sendEmailVerification, updateProfile } from "firebase/auth";
import { cartContext } from "../App";
import { Cart } from "./Product/Cart";

function Profile() {
  const { page } = useParams();
  const navigate = useNavigate();
  const [tab, setTab] = useState("details");

  useEffect(() => {
    if (page) setTab(page);
  }, [page]);

  const handleSelect = (key) => {
    setTab(key);
    // navigate(`/profile/${key}`);
  };

  return (
    <Container className="mt-5 mb-5">
      <h2 className="fw-bold mb-4">My Account</h2>

      <Nav
        variant="tabs"
        className="text-primary"
        activeKey={tab}
        onSelect={handleSelect}
      >
        <Nav.Item>
          <Nav.Link eventKey="details">Account Details</Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link eventKey="cart">Cart</Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link eventKey="orders">Orders</Nav.Link>
        </Nav.Item>
      </Nav>

      {/* Animate tab content */}
      <AnimatePresence mode="wait">
        {tab === "details" && (
          <motion.div
            key="details"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.3 }}
          >
            <ProfileDetails />
          </motion.div>
        )}
        {tab === "cart" && (
          <motion.div
            key="cart"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.3 }}
          >
            <ProfileCart />
          </motion.div>
        )}
        {tab === "orders" && (
          <motion.div
            key="orders"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.3 }}
          >
            <ProfileOrders />
          </motion.div>
        )}
      </AnimatePresence>
    </Container>
  );
}

function ProfileDetails() {
  const [user, setUser, handleSignOut] = useContext(UserContext);
  const [editMode, setEditMode] = useState(false);
  const [saving, setSaving] = useState(false);
  const [toast, setToast] = useState({
    show: false,
    message: "",
    variant: "info", // "info" | "success" | "danger"
  });

  // Initial name split
  const fullName = user?.displayName || "";
  const nameParts = fullName.trim().split(" ").filter(Boolean);
  const firstNameInit = nameParts[0] || "";
  const lastNameInit = nameParts.slice(1).join(" ") || "";

  const [firstName, setFirstName] = useState(firstNameInit);
  const [lastName, setLastName] = useState(lastNameInit);
  const [phoneNumber, setPhoneNumber] = useState("0");
  const [addressList, setAddress] = useState([]);
  const [vtonUrl, setVtonUrl] = useState(null);
  const [loadingVton, setLoadingVton] = useState(true);
  const [showUploadModal, setShowUploadModal] = useState(false);

  useEffect(() => {
    setFirstName(firstNameInit);
    setLastName(lastNameInit);
    setPhoneNumber(getPhoneNumber(user));
  }, [user]);
  useEffect(() => {
    getAddress(user).then((data) => {
      setAddress(data);
    });
  }, []);
  useEffect(() => {
    const fetchVton = async () => {
      setLoadingVton(true);
      const url = await getVtonImageUrl(user?.uid);
      setVtonUrl(url);
      setLoadingVton(false);
    };
    if (user) fetchVton();
  }, [user]);

  const showToast = (message, variant = "info", duration = 2500) => {
    setToast({ show: true, message, variant });
    setTimeout(() => setToast((prev) => ({ ...prev, show: false })), duration);
  };

  const handleSave = async () => {
    const newFullName = `${firstName} ${lastName}`.trim();
    const oldFullName = user.displayName || "";

    if (newFullName === oldFullName) {
      setEditMode(false);
      return;
    }

    try {
      setSaving(true);
      showToast("Saving your changes...", "info", 4000);

      await updateProfile(user, { displayName: newFullName });
      setUser({ ...user, displayName: newFullName });

      await new Promise((r) => setTimeout(r, 1000));

      showToast("Profile updated successfully!", "success");
    } catch (error) {
      console.error("Error updating profile:", error);
      showToast("Failed to update profile. Please try again.", "danger");
    } finally {
      setSaving(false);
      setEditMode(false);
    }
  };

  return (
    <>
      {/* === ANIMATED TOAST === */}
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
                  {saving && toast.variant === "info" && (
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

      {/* === MAIN PROFILE FORM === */}
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
      >
        <Container className="mt-4">
          <Form className="ms-4">
            <h5 className="mt-2">Personal Information</h5>
            <Row className="mt-3">
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>First Name</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Enter your first name"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    readOnly={!editMode}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Last Name</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Enter your last name"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    readOnly={!editMode}
                  />
                </Form.Group>
              </Col>

              <Col md={12}>
                <Form.Group className="mb-3">
                  <Form.Label>Email</Form.Label>
                  <InputGroup>
                    <Form.Control
                      type="email"
                      value={user?.email || ""}
                      readOnly
                    />
                    {user?.emailVerified ? (
                      <InputGroup.Text className="text-success">
                        Verified
                      </InputGroup.Text>
                    ) : (
                      <InputGroup.Text
                        className="text-danger"
                        style={{ cursor: "pointer" }}
                        onClick={() => {
                          sendEmailVerification(user).then(() =>
                            showToast("Verification email sent!", "info")
                          );
                        }}
                      >
                        Not Verified
                      </InputGroup.Text>
                    )}
                  </InputGroup>
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Phone Number</Form.Label>
                  <InputGroup>
                    <InputGroup.Text>+91</InputGroup.Text>
                    <Form.Control
                      type="number"
                      value={phoneNumber}
                      onChange={(e)=>{setPhoneNumber(e.target.value)}}
                      readOnly={!editMode}
                    />
                  </InputGroup>
                </Form.Group>
              </Col>
            </Row>
            {/* === VTON STATUS SECTION === */}
            <Row className="mt-4">
              <Col md={12}>
                <Card className="shadow-sm border-0 rounded-3 p-3">
                  <Card.Body>
                    <div className="d-flex justify-content-between align-items-center">
                      <div>
                        <h6 className="mb-1">Virtual Try-On Image</h6>
                        {loadingVton ? (
                          <Spinner animation="border" size="sm" />
                        ) : vtonUrl ? (
                          <small
                            className="text-success fw-semibold"
                            onClick={() => {
                              window.location.href = vtonUrl;
                            }}
                          >
                            ✅ Your VTON image is uploaded.
                          </small>
                        ) : (
                          <small className="text-danger fw-semibold">
                            ⚠️ No VTON image found.
                          </small>
                        )}
                      </div>

                      <div>
                        {vtonUrl ? (
                          <Button
                            variant="outline-primary"
                            onClick={() => {
                              setShowUploadModal(true);
                            }}
                          >
                            Re-Upload
                          </Button>
                        ) : (
                          <Button
                            variant="primary"
                            onClick={() => {
                              setShowUploadModal(true);
                            }}
                          >
                            Upload
                          </Button>
                        )}
                      </div>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            </Row>

            <motion.div className="mt-3" layout>
              <motion.div
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                style={{ display: "inline-block" }}
              >
                <Button variant="danger" onClick={handleSignOut}>
                  Sign Out
                </Button>
              </motion.div>

              {!editMode ? (
                <motion.div
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  style={{ display: "inline-block" }}
                >
                  <Button
                    variant="primary"
                    className="ms-4"
                    onClick={() => { setEditMode(true);showToast("Email cannot be changed","info",3000) }}
                  >
                    Edit Details
                  </Button>
                </motion.div>
              ) : (
                <motion.div
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  style={{ display: "inline-block" }}
                >
                  <Button
                    variant="success"
                    className="ms-4"
                    onClick={handleSave}
                    disabled={saving}
                  >
                    Save Changes
                  </Button>
                </motion.div>
              )}
            </motion.div>

            {/* === ADDRESS SECTION === */}
            <Row className="mt-3">
              {addressList.length > 0 && (
                <>
                  {addressList.map((obj, index) => {
                    const [type, addr] = Object.entries(obj)[0];
                    return (
                      <AddressCard
                        type={type}
                        addr={addr}
                        index={index}
                        user={user}
                        key={index}
                        showToast={showToast}
                      ></AddressCard>
                    );
                  })}
                </>
              )}
            </Row>
          </Form>
          <UploadModal
            show={showUploadModal}
            showToast={showToast}
            onHide={() => setShowUploadModal(false)}
            onUploadComplete={(url) => {
              setVtonUrl(url);
            }}
          ></UploadModal>
        </Container>
      </motion.div>
    </>
  );
}
function UploadModal({ show, onHide, onUploadComplete, showToast }) {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  const handleUpload = async () => {
    if (!file) {
      showToast("Please select an image first", "danger");
      return;
    }

    setUploading(true);
    try {
      const formData = new FormData();
      formData.append("vton_image", file);

      // ✅ send to backend Flask endpoint
      const response = await fetch("https://api.rohan.org.in/upload_vton", {
        method: "POST",
        body: formData,
      });

      if (response.ok) {
        const data = await response.json();
        showToast("VTON image uploaded successfully!", "success");
        onUploadComplete(data.url); // pass back uploaded URL
        onHide();
      } else {
        showToast("Upload failed. Try again.", "danger");
      }
    } catch (error) {
      console.error(error);
      showToast("Error during upload.", "danger");
    } finally {
      setUploading(false);
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Upload Virtual Try-On Image</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form.Group controlId="formFile" className="mb-3">
          <Form.Label>Select an image (JPG, PNG)</Form.Label>
          <Form.Control
            type="file"
            accept="image/*"
            onChange={(e) => setFile(e.target.files[0])}
          />
        </Form.Group>

        {file && (
          <p className="text-muted small mb-0">
            Selected: <strong>{file.name}</strong>
          </p>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide} disabled={uploading}>
          Cancel
        </Button>
        <Button
          variant="primary"
          onClick={handleUpload}
          disabled={!file || uploading}
        >
          {uploading ? (
            <>
              <Spinner animation="border" size="sm" /> Uploading...
            </>
          ) : (
            "Upload"
          )}
        </Button>
      </Modal.Footer>
    </Modal>
  );
}

function AddressCard({ type, addr, user, index, showToast }) {
  const [isEditing, setIsEditing] = useState(false);
  const [address, setAddress] = useState(addr);
  return (
    <Col md={6} key={index}>
      <Card className="shadow-sm mb-3 rounded-4">
        <Card.Body>
          <Card.Title className="d-flex justify-content-between align-items-center">
            <span className="text-capitalize fw-bold">{type} Address</span>
            {!isEditing ? (
              <Button
                variant="outline-primary"
                size="sm"
                onClick={() => setIsEditing(!isEditing)}
              >
                Edit
              </Button>
            ) : (
              <Button
                variant="outline-primary"
                size="sm"
                onClick={() => {
                  showToast(`Updating ${type} Address`, "info", 1500);
                  updateAddress(user, type, address)
                    .then(() => {
                      setIsEditing(!isEditing);
                      showToast(`Updated ${type} Address`, "success", 3000);
                    })
                    .catch((e) => {
                      showToast(
                        `Error in Updating ${type}Address`,
                        "danger",
                        3000
                      );
                    });
                }}
              >
                Save
              </Button>
            )}
          </Card.Title>
          <Card.Text>
            <Form.Control
              value={address}
              readOnly={!isEditing}
              onChange={(e) => {
                setAddress(e.target.value);
              }}
            ></Form.Control>
          </Card.Text>
        </Card.Body>
      </Card>
    </Col>
  );
}
function updateUserDetails(user, dispName) {
  updateProfile(user, {
    displayName: dispName,
  });
}
function ProfileCart() {
  const [cart, setCart] = useContext(cartContext);

  const handleRemove = (productId, size, color) => {
    setCart((prev) => {
      prev.removeProduct(productId, size, color);
      return new Cart(prev.userId, [...prev.items], prev.totalPrice);
    });
  };

  const handleQuantityChange = (productId, size, color, newQty) => {
    const quantity = parseInt(newQty, 10);
    if (quantity > 0) {
      setCart((prev) => {
        prev.updateQuantity(productId, size, color, quantity);
        return new Cart(prev.userId, [...prev.items], prev.totalPrice);
      });
    }
  };

  if (cart.length() === 0) {
    return (
      <Container className="mt-4">
        <h4>Your Cart</h4>
        <Card className="p-3 mt-3 text-center">
          <p className="text-muted mb-0">🛒 No items in your cart yet.</p>
        </Card>
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <h4 className="fw-bold">Your Cart</h4>

      <Card className="p-3 mt-3">
        {cart.items.map((item, index) => (
          <Row
            key={index}
            className="align-items-center mb-3 border-bottom pb-3"
          >
            <Col xs={3} md={2}>
              <Image
                src={item.product.images?.[0] || "/placeholder.png"}
                alt={item.product.name}
                fluid
                rounded
              />
            </Col>

            <Col xs={9} md={4}>
              <h6 className="mb-1">{item.product.name}</h6>
              <small className="text-muted">{item.product.companyName}</small>
              <br />
              <small>
                Size: <b>{item.size}</b> | Color: <b>{item.color}</b>
              </small>
            </Col>

            <Col xs={12} md={3} className="mt-2 mt-md-0">
              <Form.Select
                size="sm"
                value={item.quantity}
                onChange={(e) =>
                  handleQuantityChange(
                    item.product._id,
                    item.size,
                    item.color,
                    e.target.value
                  )
                }
              >
                {[...Array(10).keys()].map((n) => (
                  <option key={n + 1} value={n + 1}>
                    {n + 1}
                  </option>
                ))}
              </Form.Select>
            </Col>

            <Col xs={12} md={2} className="text-md-end mt-2 mt-md-0">
              <p className="mb-1 fw-semibold">
                ₹{(item.product.price * item.quantity).toFixed(2)}
              </p>
            </Col>

            <Col xs={12} md={1} className="text-md-end mt-2 mt-md-0">
              <Button
                variant="outline-danger"
                size="sm"
                onClick={() =>
                  handleRemove(item.product._id, item.size, item.color)
                }
              >
                Remove
              </Button>
            </Col>
          </Row>
        ))}

        <div className="text-end mt-3">
          <h5>Total: ₹{cart.totalPrice.toFixed(2)}</h5>
          <Button variant="success" className="mt-2">
            Proceed to Checkout
          </Button>
        </div>
      </Card>
    </Container>
  );
}
function ProfileOrders() {
  return (
    <Container className="mt-4">
      <h4>Your Orders</h4>
      <Card className="p-3 mt-3">
        <p>You have not placed any orders yet.</p>
      </Card>
    </Container>
  );
}

export default Profile;
