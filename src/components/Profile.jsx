import { useState, useEffect, useContext } from "react";
import { useParams, useNavigate } from "react-router";
import { Container, Row, Col, Nav, Form, Button, Card, InputGroup ,Modal,Spinner} from "react-bootstrap";
import { motion, AnimatePresence } from "framer-motion";
import { UserContext } from "./FirebaseAuth";
import { getPhoneNumber } from "../utils/util";
import { updateProfile } from "firebase/auth";

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
  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);

  
  const fullName = user?.displayName || "";
  const nameParts = fullName.trim().split(" ").filter(Boolean);
  const firstNameInit = nameParts[0] || "";
  const lastNameInit = nameParts.slice(1).join(" ") || "";


  const [firstName, setFirstName] = useState(firstNameInit);
  const [lastName, setLastName] = useState(lastNameInit);

  useEffect(() => {
    setFirstName(firstNameInit);
    setLastName(lastNameInit);
  }, [user]);

  const handleSave = async () => {
    const newFullName = `${firstName} ${lastName}`.trim();
    const oldFullName = user.displayName || "";

    if (newFullName === oldFullName) {
      setEditMode(false);
      return;
    }

    try {
      setShowModal(true);
      setSaving(true);
      await updateProfile(user, { displayName: newFullName });
      setUser({ ...user, displayName: newFullName });
      await new Promise((r) => setTimeout(r, 1000)); 
      setSaving(false);
      setShowModal(false);
      alert("Profile updated successfully!");
    } catch (error) {
      console.error("Error updating profile:", error);
      setSaving(false);
      setShowModal(false);
      alert("Failed to update profile. Please try again.");
    }

    setEditMode(false);
  };

  return (
    <>
      {/* MODAL WITH SPINNER */}
      <Modal show={showModal} centered backdrop="static" keyboard={false}>
        <Modal.Body className="text-center py-4">
          {saving ? (
            <>
              <Spinner animation="border" role="status" variant="primary" />
              <p className="mt-3 mb-0 fw-semibold">Saving your changes...</p>
            </>
          ) : (
            <p className="mb-0">Done!</p>
          )}
        </Modal.Body>
      </Modal>

      {/* MAIN FORM */}
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
                  <Form.Control type="email" value={user?.email || ""} readOnly />
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Phone Number</Form.Label>
                  <InputGroup>
                    <InputGroup.Text>{"+91"}</InputGroup.Text>
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
              <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} style={{ display: "inline-block" }}>
                <Button variant="danger" onClick={handleSignOut}>
                  Sign Out
                </Button>
              </motion.div>

              {!editMode ? (
                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} style={{ display: "inline-block" }}>
                  <Button
                    variant="primary"
                    className="ms-4"
                    onClick={() => setEditMode(true)}
                  >
                    Edit Details
                  </Button>
                </motion.div>
              ) : (
                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} style={{ display: "inline-block" }}>
                  <Button
                    variant="success"
                    className="ms-4"
                    onClick={handleSave}
                  >
                    Save Changes
                  </Button>
                </motion.div>
              )}
            </motion.div>
          </Form>
        </Container>
      </motion.div>
    </>
  );
}



function updateUserDetails(user,dispName){
   updateProfile(user,{
    displayName:dispName,
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
