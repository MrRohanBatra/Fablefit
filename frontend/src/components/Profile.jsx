import { useState, useEffect, useContext, useRef, use } from "react";
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
  ProgressBar
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
import { User } from "./User/user";
import Product from "./Product/Product";
import Order from "./Product/Order";
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
// function ProfileDetails() {
//   const [user, setUser, handleSignOut] = useContext(UserContext);

//   // ✅ Hooks always at top
//   const [showAddAdressModal, setAddAddressModal] = useState(false);
//   const [editMode, setEditMode] = useState(false);
//   const [saving, setSaving] = useState(false);
//   const [toast, setToast] = useState({
//     show: false,
//     message: "",
//     variant: "info",
//   });

//   // ✅ Safe optional chaining
//   const fullName = user?.firebaseUser?.displayName || "";
//   const nameParts = fullName.trim().split(" ").filter(Boolean);
//   const firstNameInit = nameParts[0] || "";
//   const lastNameInit = nameParts.slice(1).join(" ") || "";

//   const [firstName, setFirstName] = useState(firstNameInit);
//   const [lastName, setLastName] = useState(lastNameInit);
//   const [phoneNumber, setPhoneNumber] = useState(
//     user?.getPhoneNumber?.() || ""
//   );
//   const [addressList, setAddressList] = useState([]);
//   const [editAdress, setEditAdress] = useState(false);
//   const [homeAddress, setHomeAddress] = useState("");
//   const [workAddress, setWorkAddress] = useState("");
//   const [vtonUrl, setVtonUrl] = useState(user?.getVtonImageUrl() || "");
//   const [loadingVton, setLoadingVton] = useState(true);
//   const [showUploadModal, setShowUploadModal] = useState(false);
//   const [loading, setLoading] = useState(true);

//   // ✅ Effects always safe
//   useEffect(() => {
//     if (!user) return;
//     const fullName = user?.firebaseUser?.displayName || "";
//     const parts = fullName.trim().split(" ").filter(Boolean);
//     setFirstName(parts[0] || "");
//     setLastName(parts.slice(1).join(" ") || "");
//     setPhoneNumber(user?.getPhoneNumber?.() || "");
//   }, [user]);

//   useEffect(() => {
//     if (!user) return;
//     const userAddresses = user.address || [];
//     setAddressList(userAddresses);

//     if (userAddresses.length > 0) {
//       const home = userAddresses.find((a) => a.home)?.home || "";
//       const work = userAddresses.find((a) => a.work)?.work || "";

//       setHomeAddress(home);
//       setWorkAddress(work);
//     }
//   }, [user]);

//   // useEffect(() => {
//   //   if (!user) return;
//   //   console.log(user);
//   //   setLoadingVton(true);
//   //   const url = user.vton_image;
//   //   console.log(url);
//   //   setVtonUrl(url);
//   //   console.log(vtonUrl);
//   //   setLoadingVton(false);
//   // }, [user]);
//   useEffect(() => {
//   if (!user) return;

//   const newUrl = user.vton_image || "";

//   // Only update if the URL actually changed
//   if (newUrl !== vtonUrl) {
//     setLoadingVton(!newUrl);  // show spinner only when URL is empty
//     setVtonUrl(newUrl);
//   }
// }, [user?.vton_image]);  // NOTICE: only track vton_image, not full user


//   // ✅ Keep all hooks above this line
//   if (!user.firebaseUser) {
//     return (
//       <Container className="m-4 text-center">
//         <Spinner animation="border" role="status">
//           <span className="visually-hidden">Loading...</span>
//         </Spinner>
//         <p className="mt-2">Loading user data...</p>
//       </Container>
//     );
//   }

//   const showToast = (message, variant = "info", duration = 2500) => {
//     setToast({ show: true, message, variant });
//     setTimeout(() => setToast((prev) => ({ ...prev, show: false })), duration);
//   };

//   const handleSave = async () => {
//     const newFullName = `${firstName} ${lastName}`.trim();
//     const oldFullName = user.firebaseUser.displayName || "";
//     const oldPhoneNumber = user.getPhoneNumber() || "";
//     const newPhoneNumber = phoneNumber;
//     if (newFullName === oldFullName && oldPhoneNumber === newPhoneNumber) {
//       setEditMode(false);
//       return;
//     }

//     try {
//       setSaving(true);
//       showToast("Saving your changes...", "info", 4000);

//       await updateProfile(user.firebaseUser, { displayName: newFullName });
//       await user.updatePhoneNumber(newPhoneNumber);
//       const updatedUser = User.refreshUser(user);
//       setUser(updatedUser);
//       showToast("Profile updated successfully!", "success");
//     } catch (error) {
//       console.error("Error updating profile:", error);
//       showToast("Failed to update profile. Please try again.", "danger");
//     } finally {
//       setSaving(false);
//       setEditMode(false);
//     }
//   };

//   return (
//     <>
//       {/* === ANIMATED TOAST === */}
//       <div
//         className="position-fixed top-0 end-0 p-3"
//         style={{ marginTop: "70px", zIndex: 1060 }}
//       >
//         <AnimatePresence>
//           {toast.show && (
//             <motion.div
//               key="toast"
//               initial={{ opacity: 0, x: 50, y: -10 }}
//               animate={{ opacity: 1, x: 0, y: 0 }}
//               exit={{ opacity: 0, x: 50, y: -10 }}
//               transition={{ duration: 0.3 }}
//             >
//               <Toast
//                 bg={
//                   toast.variant === "danger"
//                     ? "danger"
//                     : toast.variant === "success"
//                       ? "success"
//                       : "light"
//                 }
//                 onClose={() => setToast({ ...toast, show: false })}
//                 className="shadow-lg rounded-3"
//               >
//                 <Toast.Header className="d-flex justify-content-between align-items-center">
//                   <div className="d-flex align-items-center">
//                     <Image
//                       src="/react.svg"
//                       width={20}
//                       className="rounded me-2"
//                     />
//                     <strong className="me-auto">FableFit</strong>
//                   </div>
//                 </Toast.Header>
//                 <Toast.Body
//                   className={`fw-semibold ${toast.variant === "danger" ? "text-white" : ""
//                     }`}
//                 >
//                   {toast.message}
//                   {saving && toast.variant === "info" && (
//                     <Spinner
//                       animation="border"
//                       size="sm"
//                       variant="primary"
//                       className="ms-2"
//                     />
//                   )}
//                 </Toast.Body>
//               </Toast>
//             </motion.div>
//           )}
//         </AnimatePresence>
//       </div>

//       {/* === MAIN PROFILE FORM === */}
//       <motion.div
//         initial={{ opacity: 0, y: 10 }}
//         animate={{ opacity: 1, y: 0 }}
//         transition={{ duration: 0.4 }}
//       >
//         <Container className="mt-4">
//           <Form className="ms-4">
//             <h5 className="mt-2">Personal Information</h5>
//             <Row className="mt-3">
//               <Col md={6}>
//                 <Form.Group className="mb-3">
//                   <Form.Label>First Name</Form.Label>
//                   <Form.Control
//                     type="text"
//                     placeholder="Enter your first name"
//                     value={firstName}
//                     onChange={(e) => setFirstName(e.target.value)}
//                     readOnly={!editMode}
//                   />
//                 </Form.Group>
//               </Col>
//               <Col md={6}>
//                 <Form.Group className="mb-3">
//                   <Form.Label>Last Name</Form.Label>
//                   <Form.Control
//                     type="text"
//                     placeholder="Enter your last name"
//                     value={lastName}
//                     onChange={(e) => setLastName(e.target.value)}
//                     readOnly={!editMode}
//                   />
//                 </Form.Group>
//               </Col>

//               <Col md={12}>
//                 <Form.Group className="mb-3">
//                   <Form.Label>Email</Form.Label>
//                   <InputGroup>
//                     <Form.Control
//                       type="email"
//                       value={user?.firebaseUser.email || ""}
//                       readOnly
//                     />
//                     {user?.firebaseUser.emailVerified ? (
//                       <InputGroup.Text className="text-success">
//                         Verified
//                       </InputGroup.Text>
//                     ) : (
//                       <InputGroup.Text
//                         className="text-danger"
//                         style={{ cursor: "pointer" }}
//                         onClick={() => {
//                           sendEmailVerification(user).then(() =>
//                             showToast("Verification email sent!", "info")
//                           );
//                         }}
//                       >
//                         Not Verified
//                       </InputGroup.Text>
//                     )}
//                   </InputGroup>
//                 </Form.Group>
//               </Col>

//               <Col md={6}>
//                 <Form.Group className="mb-3">
//                   <Form.Label>Phone Number</Form.Label>
//                   <InputGroup>
//                     <InputGroup.Text>+91</InputGroup.Text>
//                     <Form.Control
//                       type="number"
//                       value={phoneNumber}
//                       onChange={(e) => {
//                         setPhoneNumber(e.target.value);
//                       }}
//                       readOnly={!editMode}
//                     />
//                   </InputGroup>
//                 </Form.Group>
//               </Col>
//             </Row>
//             {/* === VTON STATUS SECTION === */}
//             <Row className="mt-4">
//               <Col md={12}>
//                 <Card className="shadow-sm border-0 rounded-3 p-3">
//                   <Card.Body>
//                     <div className="d-flex justify-content-between align-items-center">
//                       <div>
//                         <h6 className="mb-1">Virtual Try-On Image</h6>
//                         {loadingVton ? (
//                           <Spinner animation="border" size="sm" />
//                         ) : vtonUrl ? (
//                           <small
//                             className="text-success fw-semibold"
//                             style={{ cursor: "pointer" }}
//                             onClick={() => window.open(vtonUrl, "_blank")}
//                           >
//                             ✅ Your VTON image is uploaded.
//                           </small>

//                         ) : (
//                           <small className="text-danger fw-semibold">
//                             ⚠️ No VTON image found.
//                           </small>
//                         )}
//                       </div>

//                       <div>
//                         {vtonUrl ? (
//                           <Button
//                             variant="outline-primary"
//                             onClick={() => {
//                               setShowUploadModal(true);
//                             }}
//                           >
//                             Re-Upload
//                           </Button>
//                         ) : (
//                           <Button
//                             variant="primary"
//                             onClick={() => {
//                               setShowUploadModal(true);
//                             }}
//                           >
//                             Upload
//                           </Button>
//                         )}
//                       </div>
//                     </div>
//                   </Card.Body>
//                 </Card>
//               </Col>
//             </Row>

//             <motion.div className="mt-3" layout>
//               <motion.div
//                 whileHover={{ scale: 1.05 }}
//                 whileTap={{ scale: 0.95 }}
//                 style={{ display: "inline-block" }}
//               >
//                 <Button variant="danger" onClick={handleSignOut}>
//                   Sign Out
//                 </Button>
//               </motion.div>

//               {!editMode ? (
//                 <motion.div
//                   whileHover={{ scale: 1.05 }}
//                   whileTap={{ scale: 0.95 }}
//                   style={{ display: "inline-block" }}
//                 >
//                   <Button
//                     variant="primary"
//                     className="ms-4"
//                     onClick={() => {
//                       setEditMode(true);
//                       showToast("Email cannot be changed", "info", 3000);
//                     }}
//                   >
//                     Edit Details
//                   </Button>
//                 </motion.div>
//               ) : (
//                 <motion.div
//                   whileHover={{ scale: 1.05 }}
//                   whileTap={{ scale: 0.95 }}
//                   style={{ display: "inline-block" }}
//                 >
//                   <Button
//                     variant="success"
//                     className="ms-4"
//                     onClick={handleSave}
//                     disabled={saving}
//                   >
//                     Save Changes
//                   </Button>
//                 </motion.div>
//               )}
//             </motion.div>

//             {/* === ADDRESS SECTION ===
//             <Row className="mt-3">
//               {addressList.length > 0 && (
//                 <>
//                   {addressList.map((obj, index) => {
//                     const [type, addr] = Object.entries(obj)[0];
//                     return (
//                       <AddressCard
//                         type={type}
//                         addr={addr}
//                         index={index}
//                         user={user}
//                         key={index}
//                         showToast={showToast}
//                       ></AddressCard>
//                     );
//                   })}
//                 </>
//               )}
//             </Row> */}
//             {/* === ADDRESS SECTION === */}
//             <Row className="mt-3">
//               {addressList.length > 0 ? (
//                 <>
//                   {/* {addressList.map((obj, index) => {
//                     const [type, addr] = Object.entries(obj)[0];
//                     return (
//                       <AddressCard
//                         type={type}
//                         addr={addr}
//                         index={index}
//                         user={user}
//                         key={index}
//                         showToast={showToast}
//                       />
//                     );
//                   })} */}
//                   <AddressCard
//                     type={"home"}
//                     address={homeAddress}
//                     onAddressChange={setHomeAddress}
//                     isEditing={editAdress}
//                   ></AddressCard>
//                   <AddressCard
//                     type={"work"}
//                     address={workAddress}
//                     onAddressChange={setWorkAddress}
//                     isEditing={editAdress}
//                   ></AddressCard>
//                 </>
//               ) : (
//                 <Col md={12}>
//                   <Card className="shadow-sm border-0 rounded-3 p-3 text-center">
//                     <Card.Body>
//                       <h6 className="text-muted mb-0">No address found.</h6>
//                       <p className="text-secondary small mb-3">
//                         Add a delivery address to complete your profile.
//                       </p>
//                       <Button
//                         variant="outline-primary"
//                         onClick={() => {
//                           // example: open address add modal if you have one
//                           setAddAddressModal(true);
//                         }}
//                       >
//                         Add Address
//                       </Button>
//                     </Card.Body>
//                   </Card>
//                 </Col>
//               )}
//             </Row>
//             {addressList.length < 2 ? (
//               <Button
//                 onClick={() => {
//                   setAddAddressModal(true);
//                 }}
//               >
//                 Add Address
//               </Button>
//             ) : (
//               <Button
//                 onClick={() => {
//                   setAddAddressModal(true);
//                 }}
//               >
//                 Edit Address
//               </Button>
//             )}
//           </Form>
//           <UploadModal
//             show={showUploadModal}
//             showToast={showToast}
//             onHide={() => setShowUploadModal(false)}
//             onUploadComplete={async (url) => {
//               setVtonUrl(url);
//               const success = await user.updateVtonImage(url);
//               if (success) {
//                 const refresdUser = User.refreshUser(user);
//                 setUser(refresdUser);
//               }
//             }}
//             user={user}
//           ></UploadModal>
//           <AddAddressModal
//             show={showAddAdressModal}
//             user={user}
//             setUser={setUser}
//             showToast={showToast}
//             onHide={() => {
//               setAddAddressModal(false);
//             }}
//           ></AddAddressModal>
//         </Container>
//       </motion.div>
//     </>
//   );
// }
function ProfileDetails() {
  const [user, setUser, handleSignOut] = useContext(UserContext);

  // ========================= STATE =========================
  const [showAddAdressModal, setAddAddressModal] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [saving, setSaving] = useState(false);
  const [toast, setToast] = useState({
    show: false,
    message: "",
    variant: "info",
  });

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");

  const [addressList, setAddressList] = useState([]);
  const [editAdress, setEditAdress] = useState(false);
  const [homeAddress, setHomeAddress] = useState("");
  const [workAddress, setWorkAddress] = useState("");

  const [vtonUrl, setVtonUrl] = useState("");
  const [loadingVton, setLoadingVton] = useState(true);

  const [showUploadModal, setShowUploadModal] = useState(false);
  const [loading, setLoading] = useState(true);

  // ===================== INITIAL LOAD EFFECT =====================
  // useEffect(() => {
  //   if (!user || !loading) return;

  //   // name
  //   const fullName = user?.firebaseUser?.displayName || "";
  //   const parts = fullName.trim().split(" ").filter(Boolean);
  //   setFirstName(parts[0] || "");
  //   setLastName(parts.slice(1).join(" ") || "");

  //   // phone
  //   setPhoneNumber(user?.getPhoneNumber?.() || "");

  //   // addresses
  //   const userAddresses = user.address || [];
  //   setAddressList(userAddresses);

  //   if (userAddresses.length > 0) {
  //     const home = userAddresses.find((a) => a.home)?.home || "";
  //     const work = userAddresses.find((a) => a.work)?.work || "";

  //     setHomeAddress(home);
  //     setWorkAddress(work);
  //   }

  //   // VTON
  //   const url = user?.vton_image || "";
  //   setVtonUrl(url);
  //   setLoadingVton(!url);

  //   // finish initial load
  //   setLoading(false);
  // }, [user, loading]);

  // // ===================== VTON UPDATE EFFECT =====================
  // useEffect(() => {
  //   if (!user || loading) return;

  //   const newUrl = user.vton_image || "";

  //   if (newUrl !== vtonUrl) {
  //     setLoadingVton(!newUrl);
  //     setVtonUrl(newUrl);
  //     setLoading(false)
  //   }
  // }, [user?.vton_image]);
  const lastVtonUrlRef = useRef(null);

  useEffect(() => {
    console.log("🔄 useEffect RUN");

    console.log("➡️ user object:", user);
    if (!user || !user.firebaseUser) {
      console.log("❌ No user or firebaseUser — skipping effect");
      return;
    }

    // --- NAME ---
    const fullName = user.firebaseUser.displayName || "";
    console.log("👤 fullName from firebase:", fullName);

    const parts = fullName.trim().split(" ").filter(Boolean);
    const f = parts[0] || "";
    const l = parts.slice(1).join(" ") || "";

    console.log("🟦 Calculated name:", f, l);
    console.log("🟧 Current state name:", firstName, lastName);

    if (f !== firstName) {
      console.log("📝 Updating firstName state");
      setFirstName(f);
    }
    if (l !== lastName) {
      console.log("📝 Updating lastName state");
      setLastName(l);
    }

    // --- PHONE ---
    const phone = user.getPhoneNumber?.() || "";
    console.log("📞 phone from backend:", phone);
    console.log("📞 phoneNumber state:", phoneNumber);

    if (phone !== phoneNumber) {
      console.log("📝 Updating phoneNumber");
      setPhoneNumber(phone);
    }

    // --- ADDRESS ---
    const addrs = user.address || [];
    console.log("🏠 backend addresses:", addrs);
    console.log("🏠 current addressList state:", addressList);

    if (JSON.stringify(addrs) !== JSON.stringify(addressList)) {
      console.log("📝 Updating addressList");
      setAddressList(addrs);

      const home = addrs.find((a) => a.home)?.home || "";
      const work = addrs.find((a) => a.work)?.work || "";

      console.log("🏡 home:", home, " | 🏢 work:", work);
      console.log("🏡 homeAddress state:", homeAddress);
      console.log("🏢 workAddress state:", workAddress);

      if (home !== homeAddress) {
        console.log("📝 Updating homeAddress");
        setHomeAddress(home);
      }
      if (work !== workAddress) {
        console.log("📝 Updating workAddress");
        setWorkAddress(work);
      }
    }

    // --- VTON ---
    const newUrl = user.vton_image || "";
    console.log("🖼️ backend VTON:", newUrl);
    console.log("🖼️ current vtonUrl state:", vtonUrl);
    console.log("🖼️ lastVtonUrlRef:", lastVtonUrlRef.current);

    // ⛔ STOP if URL value didn't actually change
    if (lastVtonUrlRef.current === newUrl) {
      console.log("⛔ VTON unchanged — not updating spinner/state");
      return;
    }

    console.log("🟢 VTON changed — updating...");
    lastVtonUrlRef.current = newUrl;

    setLoadingVton(false);
    setVtonUrl(newUrl);

  }, [user]);

  // ===================== BLOCK WHILE LOADING ====================
  if (!user || !user.firebaseUser) {
    return (
      <Container className="m-4 text-center">
        <Spinner animation="border" />
        <p className="mt-2">Loading user data...</p>
      </Container>
    );
  }

  // ===================== TOAST HANDLER =====================
  const showToast = (message, variant = "info", duration = 2500) => {
    setToast({ show: true, message, variant });
    setTimeout(() => setToast((prev) => ({ ...prev, show: false })), duration);
  };

  // ===================== SAVE HANDLER =====================
  const handleSave = async () => {
    const newFullName = `${firstName} ${lastName}`.trim();
    const oldFullName = user.firebaseUser.displayName || "";
    const oldPhone = user.getPhoneNumber() || "";

    if (newFullName === oldFullName && oldPhone === phoneNumber) {
      setEditMode(false);
      return;
    }

    try {
      setSaving(true);
      showToast("Saving your changes...", "info", 4000);

      await updateProfile(user.firebaseUser, { displayName: newFullName });
      const backendUpdated = await user.updatePhoneNumber(phoneNumber);

      if (backendUpdated) {
        const refreshed = User.refreshUser(backendUpdated, user);
        setUser(refreshed);
      }
      // const updatedUser = User.refreshUser(user);
      // setUser(updatedUser);

      showToast("Profile updated successfully!", "success");
    } catch (error) {
      console.error("Error updating profile:", error);
      showToast("Failed to update profile. Please try again.", "danger");
    } finally {
      setSaving(false);
      setEditMode(false);
      setLoading(true)
    }
  };

  // ===================== RENDER UI =====================
  return <>
    {loading && !user && !user?.firebaseUser ? <><Container className="m-4 text-center">
      <Spinner animation="border" role="status">
        <span className="visually-hidden">Loading...</span>
      </Spinner>
      <p className="mt-2">Loading user data...</p>
    </Container></> : (<>
      <>
        {/* === TOAST === */}
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
                        : "warning"
                  }
                  onClose={() => setToast({ ...toast, show: false })}
                  className={`shadow-lg rounded-3 ${toast.variant === "danger" || toast.variant==="success" ? "text-white" : "text-dark"}`}
                >
                  <Toast.Header>
                    <Image src="/react.svg" width={20} className="rounded me-2" />
                    <strong className="me-auto text-success">FableFit</strong>
                  </Toast.Header>
                  <Toast.Body
                    className={`fw-semibold ${toast.variant === "danger" || toast.variant==="success" ? "text-white" : "text-dark"
                      }`}
                  >
                    {toast.message}
                    {saving && toast.variant === "info" && (
                      <Spinner
                        animation="border"
                        size="sm"
                        className="ms-2"
                      />
                    )}
                  </Toast.Body>
                </Toast>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        {/* === MAIN === */}
        <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }}>
          <Container className="mt-4">
            <Form className="ms-4">
              {/* Personal Info */}
              <h5 className="mt-2">Personal Information</h5>
              <Row className="mt-3">
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>First Name</Form.Label>
                    <Form.Control
                      type="text"
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
                        value={user?.firebaseUser?.email || ""}
                        readOnly
                      />
                      {user?.firebaseUser?.emailVerified ? (
                        <InputGroup.Text className="text-success">
                          Verified
                        </InputGroup.Text>
                      ) : (
                        <InputGroup.Text
                          className="text-danger"
                          style={{ cursor: "pointer" }}
                          onClick={() =>
                            sendEmailVerification(user).then(() =>
                              showToast("Verification email sent!", "info")
                            )
                          }
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
                        onChange={(e) => setPhoneNumber(e.target.value)}
                        readOnly={!editMode}
                      />
                    </InputGroup>
                  </Form.Group>
                </Col>
              </Row>

              {/* VTON */}
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
                              style={{ cursor: "pointer" }}
                              onClick={() => window.open(vtonUrl, "_blank")}
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
                          <Button
                            variant={vtonUrl ? "outline-primary" : "primary"}
                            onClick={() => setShowUploadModal(true)}
                          >
                            {vtonUrl ? "Re-Upload" : "Upload"}
                          </Button>
                        </div>
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>

              {/* Buttons */}
              <motion.div className="mt-3" layout>
                <Button variant="danger" onClick={handleSignOut}>
                  Sign Out
                </Button>

                {!editMode ? (
                  <Button
                    variant="primary"
                    className="ms-4"
                    onClick={() => {
                      setEditMode(true);
                      showToast("Email cannot be changed", "info");
                    }}
                  >
                    Edit Details
                  </Button>
                ) : (
                  <Button
                    variant="success"
                    className="ms-4"
                    onClick={handleSave}
                    disabled={saving}
                  >
                    Save Changes
                  </Button>
                )}
              </motion.div>

              {/* Address Cards */}
              <Row className="mt-3">
                {addressList.length > 0 ? (
                  <>
                    <AddressCard
                      type="home"
                      address={homeAddress}
                      onAddressChange={setHomeAddress}
                      isEditing={editAdress}
                    />
                    <AddressCard
                      type="work"
                      address={workAddress}
                      onAddressChange={setWorkAddress}
                      isEditing={editAdress}
                    />
                  </>
                ) : (
                  <Col md={12}>
                    <Card className="shadow-sm border-0 rounded-3 p-3 text-center">
                      <Card.Body>
                        <h6 className="text-muted mb-0">No address found.</h6>
                        <p className="text-secondary small mb-3">
                          Add a delivery address to complete your profile.
                        </p>
                        <Button
                          variant="outline-primary"
                          onClick={() => setAddAddressModal(true)}
                        >
                          Add Address
                        </Button>
                      </Card.Body>
                    </Card>
                  </Col>
                )}
              </Row>

              {addressList.length < 2 ? (
                <Button onClick={() => setAddAddressModal(true)}>
                  Add Address
                </Button>
              ) : (
                <Button onClick={() => setAddAddressModal(true)}>
                  Edit Address
                </Button>
              )}
            </Form>

            {/* Upload Modal */}
            <UploadModal
              show={showUploadModal}
              showToast={showToast}
              onHide={() => setShowUploadModal(false)}
              onUploadComplete={async (url) => {
                setVtonUrl(url);

                const backendUpdated = await user.updateVtonImage(url);

                if (backendUpdated) {
                  const refreshedUser = User.refreshUser(backendUpdated, user);
                  setUser(refreshedUser);
                }
              }}

              user={user}
            />
              {/* Address Modal */}
            <AddAddressModal
              show={showAddAdressModal}
              user={user}
              setUser={setUser}
              showToast={showToast}
              onHide={() => setAddAddressModal(false)}
            />
          </Container>
        </motion.div>
      </>
    </>)}
  </>;
}

function UploadModal({ show, onHide, onUploadComplete, showToast, user }) {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [preview,setPreview]=useState(null);
  useEffect(()=>{
    if(!file){
      setPreview(null);
      return;
    }
    const objectUrl=URL.createObjectURL(file);
    setPreview(objectUrl);
    return ()=>URL.revokeObjectURL(objectUrl);
  },[file]);

  const handleUpload = async () => {
    if (!file) {
      showToast("Please select an image first", "danger");
      return;
    }

    setUploading(true);
    try {
      const formData = new FormData();
      formData.append("image", file);
      formData.append("uid", user.firebaseUser.uid);

      const response = await fetch(
        "/api/users/uploadimage",
        {
          method: "POST",
          body: formData,
        }
      );

      if (response.ok) {
        const data = await response.json();
        console.log(data);
        showToast("VTON image uploaded successfully!", "success");
        onUploadComplete(`${data.file}`);
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
          <><p className="text-muted small mb-0">
            Selected: <strong>{file.name}</strong>
          </p>
          <Image src={preview} alt="Preview" fluid rounded className="mt-3" width="120"
                      height="120"/>
        </>
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

function AddressCard({ type, address, onAddressChange, isEditing }) {
  if (address == "") {
    return <></>;
  }
  return (
    <Col md={6}>
      <Card className="shadow-sm mb-3 rounded-4">
        <Card.Body>
          <Card.Title className="d-flex justify-content-between align-items-center">
            <span className="text-capitalize fw-bold">{type} Address</span>
          </Card.Title>
          <Card.Text>
            <Form.Control
              value={address}
              readOnly={!isEditing}
              onChange={(e) => onAddressChange(e.target.value)}
              as={isEditing ? "input" : "textarea"}
              rows={3}
            />
          </Card.Text>
        </Card.Body>
      </Card>
    </Col>
  );
}

function AddAddressModal({ show, onHide, user, setUser, showToast }) {
  const [addressType, setAddressType] = useState("home");
  const [addressValue, setAddressValue] = useState("");
  const [loading, setLoading] = useState(false);

const handleSave = async () => {
  if (!addressValue.trim()) {
    showToast("Please enter a valid address.", "danger");
    return;
  }

  try {
    setLoading(true);

    const existingAddresses = Array.isArray(user.address)
      ? [...user.address]
      : [];

    // build new address object
    const updatedAddresses = [...existingAddresses];
    const index = updatedAddresses.findIndex((a) => a[addressType]);

    if (index !== -1) {
      updatedAddresses[index][addressType] = addressValue;
    } else {
      updatedAddresses.push({ [addressType]: addressValue });
    }

    // 1️⃣ Update backend → get backend updated user
    const backendUpdated = await user.updateAddress(updatedAddresses);

    // 2️⃣ If backend failed
    if (!backendUpdated) {
      showToast("Failed to save address. Try again.", "danger");
      return;
    }

    // 3️⃣ Merge backend + firebase user → correct new User instance
    const refreshedUser = User.refreshUser(backendUpdated, user);

    // 4️⃣ Save into context (react)
    setUser(refreshedUser);

    showToast("Address saved successfully!", "success");
    onHide();

  } catch (err) {
    console.error(err);
    showToast("Something went wrong!", "danger");
  } finally {
    setLoading(false);
  }
};

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Add / Update Address</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <Form.Group className="mb-3">
            <Form.Label>Address Type</Form.Label>
            <Form.Select
              value={addressType}
              onChange={(e) => setAddressType(e.target.value)}
            >
              <option value="home">Home</option>
              <option value="work">Work</option>
            </Form.Select>
          </Form.Group>

          <Form.Group>
            <Form.Label>Address</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              placeholder="Enter full address"
              value={addressValue}
              onChange={(e) => setAddressValue(e.target.value)}
            />
          </Form.Group>
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Cancel
        </Button>
        <Button variant="primary" onClick={handleSave} disabled={loading}>
          {loading ? "Saving..." : "Save"}
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
function updateUserDetails(user, dispName) {
  updateProfile(user, {
    displayName: dispName,
  });
}
function ProfileCart() {
  const [cart, setCart] = useContext(cartContext);
  const [detailedItems, setDetailedItems] = useState([]); // holds { item, product }
  const [user] = useContext(UserContext);
  const [showModal, setShowModal] = useState(false);
  const [deliveryCharge, setDeliveryCharge] = useState(0);
  const [finalAmount, setFinalAmount] = useState(cart.totalPrice);
  const [selectedAddress, setSelectedAddress] = useState("home");


  const navigate = useNavigate();
  // 🧠 Fetch product details for each cart item
  useEffect(() => {
    const loadProducts = async () => {
      try {
        console.log("🧾 Cart items:", cart.items);
        if (!cart?.items?.length) {
          setDetailedItems([]);
          return;
        }

        // Fetch all product details in parallel
        const productFetches = await Promise.all(
          cart.items.map(async (item) => {
            try {
              // Get product ID (could be string or object)
              const productId =
                typeof item.product === "object"
                  ? item.product._id
                  : item.product;

              if (!productId) throw new Error("Missing product ID");

              const res = await fetch(
                `/api/products/id/${productId}`
              );

              if (!res.ok) throw new Error(`Product ${productId} not found`);
              const data = await res.json();

              // Wrap as Product instance for uniformity
              const product = new Product(data);
              return { item, product };
            } catch (err) {
              console.warn("⚠️ Failed to load product:", item, err);
              // fallback empty product
              return { item, product: new Product({}) };
            }
          })
        );

        setDetailedItems(productFetches);
      } catch (err) {
        console.error("❌ Error loading cart product details:", err);
        setDetailedItems([]);
      }
    };

    loadProducts();
  }, [cart]);

  // 🗑️ Remove an item
  const handleRemove = async (productId, size, color) => {
    try {
      const updated = await cart.removeProduct(productId, size, color);
      setCart(updated);
    } catch (err) {
      console.error("❌ Failed to remove product:", err);
    }
  };
  const computeDeliveryCharge = () => {
    const itemCount = cart.items.reduce((sum, i) => sum + i.quantity, 0);

    let charge = 0;

    if (cart.totalPrice < 999) {
      charge = 49 + itemCount * 10; // example: ₹49 base + ₹10 per item
    }

    setDeliveryCharge(charge);
    setFinalAmount(cart.totalPrice + charge);
  };


  // 🔢 Update item quantity
  const handleQuantityChange = async (productId, size, color, newQty) => {
    const quantity = parseInt(newQty, 10);
    if (quantity > 0) {
      try {
        const updated = await cart.updateQuantity(
          productId,
          size,
          color,
          quantity
        );
        setCart(updated);
      } catch (err) {
        console.error("❌ Failed to update quantity:", err);
      }
    }
  };

  const handleConfirmOrder = async () => {
    try {
      const res = await fetch("/api/orders/place", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          uid: user?.firebaseUser?.uid,
          address: user?.getAddress(selectedAddress) || "",
          paymentMethod: "cod",
          cart: {
            ...cart,
            totalPrice: finalAmount  // include delivery cost
          },
          deliveryCharge
        })
      });

      const data = await res.json();

      if (!res.ok) {
        alert(data.message || "Order failed");
        return;
      }

      setCart({ items: [], totalPrice: 0 });
      setShowModal(false);
      navigate("/profile/orders");
    } catch (err) {
      console.error("Order error:", err);
    }
  };


  // ⛔ Empty cart
  if (!cart || cart.items.length === 0) {
    return (
      <Container className="mt-4">
        <h4>Your Cart</h4>
        <Card className="p-3 mt-3 text-center">
          <p className="text-muted mb-0">🛒 No items in your cart yet.</p>
        </Card>
      </Container>
    );
  }

  // ⏳ Waiting for product details
  if (detailedItems.length === 0) {
    return (
      <Container className="mt-4 d-flex justify-content-center align-items-center p-5">
        <Spinner animation="border" variant="primary" />
      </Container>
    );
  }

  // ✅ Display loaded cart
  return (
    <Container className="mt-4">
      <h4 className="fw-bold">Your Cart</h4>

      <Card className="p-3 mt-3">
        {detailedItems.map(({ item, product }, index) => (
          <Row key={index} className="align-items-center mb-3 border-bottom pb-3">
            <Col xs={3} md={2}>
              <Image
                src={product.firstImage()}
                alt={product.name}
                fluid
                rounded
                style={{ "cursor": "pointer" }}
                onClick={() => { navigate(`/product/${product._id}`) }}
              />
            </Col>

            <Col xs={9} md={4} >
              <h6 className="mb-1">{product.name}</h6>
              <small className="text-muted">{product.companyName}</small>
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
                    product._id,
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
                ₹{(product.price * item.quantity).toFixed(2)}
              </p>
            </Col>

            <Col xs={12} md={1} className="text-md-end mt-2 mt-md-0">
              <Button
                variant="outline-danger"
                size="sm"
                onClick={() =>
                  handleRemove(product._id, item.size, item.color)
                }
              >
                Remove
              </Button>
            </Col>
          </Row>
        ))}

        <div className="text-end mt-3">
          <h5>Total: ₹{cart.totalPrice.toFixed(2)}</h5>
          <Button variant="success" className="mt-2"
            onClick={() => {
              computeDeliveryCharge();
              setShowModal(true);
            }}>
            Proceed to Checkout
          </Button>
        </div>
      </Card>
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Confirm Your Order</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {/* ADDRESS SELECTOR */}
          <Form.Group className="mb-3">
            <Form.Label>Select Delivery Address</Form.Label>
            <Form.Select
              value={selectedAddress}
              onChange={(e) => setSelectedAddress(e.target.value)}
            >
              <option value="home">Home</option>
              <option value="work">Work</option>
            </Form.Select>
          </Form.Group>

          {/* DISPLAY SELECTED ADDRESS */}
          <Card className="p-2 mb-3">
            <small className="text-muted">Deliver To:</small>
            <div className="fw-semibold">
              {user?.getAddress(selectedAddress) || "Address not available"}
            </div>
          </Card>

          {/* PRICE SECTION */}
          <p>Items Total: <b>₹{cart.totalPrice.toFixed(2)}</b></p>
          <p>Delivery Charge: <b>₹{deliveryCharge}</b></p>
          <hr />
          <h5>Final Amount: ₹{finalAmount.toFixed(2)}</h5>
          <small className="text-muted">Free delivery above ₹999</small>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Cancel
          </Button>
          <Button variant="success" onClick={handleConfirmOrder}>
            Confirm Order
          </Button>
        </Modal.Footer>
      </Modal>

    </Container>
  );
}
function ProfileOrders() {
  const [user] = useContext(UserContext);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    if (!user?.firebaseUser?.uid) return;

    const loadOrders = async () => {
      try {
        const res = await fetch(
          `/api/orders/user/${user.firebaseUser.uid}`
        );

        const data = await res.json();

        // Convert each backend order to frontend Order class
        const converted = data.map((o) => new Order(o));

        setOrders(converted);
      } catch (err) {
        console.error("Failed to load orders:", err);
      } finally {
        setLoading(false);
      }
    };

    loadOrders();
  }, [user]);

  // ⏳ Loading state
  if (loading) {
    return (
      <Container className="mt-4 d-flex justify-content-center">
        <Spinner animation="border" variant="primary" />
      </Container>
    );
  }

  // 🛒 Empty state
  if (!orders.length) {
    return (
      <Container className="mt-4">
        <h4>Your Orders</h4>
        <Card className="p-3 mt-3 text-center">
          <p className="text-muted">You have not placed any orders yet.</p>
        </Card>
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <h4>Your Orders</h4>

      {orders.map((order) => (
        <Card key={order._id} className="p-3 mt-3 shadow-sm">
          <h5 className="mb-1">
            Order #{order._id.slice(-6)}
          </h5>
          <small className="text-muted">
            Ordered on {new Date(order.createdAt).toDateString()}
          </small>

          <Row className="mt-3">
            {order.items.map((it, i) => {
              const prod = new Product(it.product);
              return (
                <Col xs={12} md={6} key={i} className="d-flex align-items-center mb-3">
                  <Image
                    src={prod.firstImage()}
                    alt={prod.name}
                    width={70}
                    height={70}
                    rounded
                    className="me-3"
                    style={{ cursor: "pointer", objectFit: "cover" }}
                    onClick={() => navigate(`/product/${prod._id}`)}
                  />
                  <div>
                    <strong>{prod.name}</strong>
                    <br />
                    Size: {it.size} | Color: {it.color}
                    <br />
                    Qty: {it.quantity}
                    <br />
                    Price: ₹{it.price}
                  </div>
                </Col>
              );
            })}
          </Row>

          {/* Delivery progress */}
          <ProgressBar
            now={order.statusProgress()}
            label={order.statusLabel()}
            className="my-3"
            animated
          />

          <p className="mb-0">
            <b>Total Paid:</b> ₹{order.totalPrice}
          </p>

          <p className="text-muted">
            Expected Delivery: {new Date(order.deliveryDate).toDateString()}
          </p>
        </Card>
      ))}
    </Container>
  );
}
export default Profile;
