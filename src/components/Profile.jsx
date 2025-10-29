import { useState, useEffect, useContext, use } from "react";
import { useParams, useNavigate } from "react-router";
import { Container, Row, Col, Nav, Form, Button, Card, InputGroup, Modal, Spinner, Toast, Image } from "react-bootstrap";
import { motion, AnimatePresence } from "framer-motion";
import { UserContext } from "./FirebaseAuth";
import { getAddress, getPhoneNumber } from "../utils/util";
import { sendEmailVerification, updateProfile } from "firebase/auth";

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

      <Nav variant="tabs" className="text-primary" activeKey={tab} onSelect={handleSelect}>
        <Nav.Item><Nav.Link eventKey="details">Account Details</Nav.Link></Nav.Item>
        <Nav.Item><Nav.Link eventKey="cart">Cart</Nav.Link></Nav.Item>
        <Nav.Item><Nav.Link eventKey="orders">Orders</Nav.Link></Nav.Item>
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
  const handleAddAddress = () => {
    // TODO: Open modal or navigate to address form
    console.log("Add new address clicked");
  };
  const [editAddress,setEditAddress]=useState(false);

  const handleEditAddress = (index) => {
    // TODO: Allow editing the address at given index
    setEditAddress(true);
    console.log("Edit address at index:", index);
  };

  // Toast state
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
  const [addressList,setAddress]=useState([]);
  
  useEffect(() => {
    setFirstName(firstNameInit);
    setLastName(lastNameInit);
  }, [user]);
  useEffect(()=>{
    getAddress(user).then((data)=>{setAddress(data)});
  },[])
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
      <div className="position-fixed top-0 end-0 p-3" style={{ marginTop: "70px", zIndex: 1060 }}>
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
                    <Image src="/react.svg" width={20} className="rounded me-2" />
                    <strong className="me-auto">FableFit</strong>
                  </div>
                  {/* <Button
                    variant="link"
                    size="sm"
                    className="p-0 text-muted"
                    onClick={() => setToast({ ...toast, show: false })}
                  >
                    <XCircle></XCircle>
                  </Button> */}
                </Toast.Header>
                <Toast.Body
                  className={`fw-semibold ${toast.variant === "danger" ? "text-white" : ""
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
                    <Form.Control type="email" value={user?.email || ""} readOnly />
                    {user?.emailVerified ? (
                      <InputGroup.Text className="text-success">Verified</InputGroup.Text>
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
                      value={getPhoneNumber(user)}
                      readOnly
                    />
                  </InputGroup>
                </Form.Group>
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
                    onClick={() => setEditMode(true)}
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
              {addressList.length > 0 ? (
                addressList.map((addrObj, index) => {
                  const [label, value] = Object.entries(addrObj)[0];
                  return (
                    <Col md={6} key={index}>
                      <Card className="mb-3 shadow-sm rounded-4 border-0">
                        <Card.Body>
                          <div className="d-flex justify-content-between align-items-center mb-2">
                            <strong className="text-capitalize">{label} Address</strong>
                            <Button
                              variant="outline-primary"
                              size="sm"
                              onClick={() => handleEditAddress(index)}
                            >
                              Edit
                            </Button>
                          </div>
                          <Form.Control
                            as="textarea"
                            rows={2}
                            value={value}
                            readOnly={!editAddress}
                          />
                        </Card.Body>
                      </Card>
                    </Col>
                  );
                })
              ) : (
                <Col>
                  <p className="text-muted fst-italic">No addresses added yet.</p>
                </Col>
              )}

              <Col xs={12}>
                <Button
                  variant="outline-success"
                  className="rounded-pill mt-2"
                  onClick={handleAddAddress}
                >
                  + Add New Address
                </Button>
              </Col>
            </Row>

          </Form>
        </Container>
      </motion.div>
    </>
  );
}




function updateUserDetails(user, dispName) {
  updateProfile(user, {
    displayName: dispName,
  })
}
function ProfileCart() {
  return (
    <Container className="mt-4">
      <h4>Your Cart</h4>
      <Card className="p-3 mt-3">
        <p>No items in your cart yet.</p>
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
