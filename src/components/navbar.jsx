import { useContext, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";

import {
  Navbar,
  Nav,
  Container,
  Form,
  FormControl,
  Button,
  Image,
  Badge,
  InputGroup,
  Dropdown,
  Offcanvas,
  ListGroup,
} from "react-bootstrap";
import Appname from "./NameContext";
import logo from "../assets/react.svg";

import {
  Search,
  Cart3,
  MoonStarsFill,
  SunFill,
  Truck,
  Sun,
  Moon,
} from "react-bootstrap-icons";
import { auth, UserContext } from "./FirebaseAuth";
import {
  EmailAuthProvider,
  GoogleAuthProvider,
  signInWithPopup,
} from "firebase/auth";
import { cartContext, themeContext } from "../App";
import { Link, useLocation, useNavigate } from "react-router";
const searchRecommendations = [
  // 👔 Men
  "Men GenZ T-Shirts",
  "Men Old School Shirts",
  "Men Simple Polo Shirts",
  "Men Casual Hoodies",
  "Men Formal Shirts",
  "Men Party Jackets",
  "Men Ethnic Kurta",
  "Men Sporty Track Pants",
  "Men GenZ Jeans",
  "Men Old School Trousers",
  "Men Casual Shorts",

  // 👗 Women
  "Women GenZ Tops",
  "Women Old School Blouses",
  "Women Simple Kurtis",
  "Women Casual Dresses",
  "Women Formal Sarees",
  "Women Party Gowns",
  "Women Ethnic Suits & Sets",
  "Women GenZ Skirts",
  "Women Old School Jeans",
  "Women Casual Leggings",

  // 🧒 Kids
  "Kids GenZ T-Shirts",
  "Kids Old School Frocks",
  "Kids Simple Dresses",
  "Kids Casual Shirts",
  "Kids Party Jackets",
  "Kids Ethnic Wear",
  "Kids Sporty Shorts",
  "Kids GenZ Jeans",
  "Kids Old School Skirts",
  "Kids Casual Nightwear",

  // 🎨 Universal fashion vibes
  "GenZ Streetwear",
  "Old School Vintage Wear",
  "Simple Everyday Wear",
  "Casual Summer Collection",
  "Formal Office Wear",
  "Party Wear Collection",
  "Ethnic Festive Wear",
  "Sporty Activewear",
  "Winter Jackets",
  "Summer Essentials",
];
function getTrendingRecommendations(count = 5) {
  const shuffled = [...searchRecommendations].sort(() => 0.5 - Math.random());
  return shuffled.slice(0, count);
}
const trendingRecommendations = getTrendingRecommendations(5);

function NavbarFinal() {
  const [name] = useContext(Appname);
  const [user, setUser, handleSignOut] = useContext(UserContext);
  const [theme, setTheme] = useContext(themeContext);
  const [showSearchCanvas, setSearchCanvas] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const [cart, setCart] = useContext(cartContext);
  const [cartItemCount, setCartItemCount] = useState(cart?.itemCount && 0);
  const loadTheme = () => {
    const data = localStorage.getItem("theme") || "light";
    setTheme(data);
    document.documentElement.setAttribute("data-bs-theme", data);
  };
  const location = useLocation();
  const updateTheme = (th) => {
    setTheme(th);
    localStorage.setItem("theme", th);
    document.documentElement.setAttribute("data-bs-theme", th);
  };
  useEffect(() => {
    setCartItemCount(cart?.itemCount);
  }, [cart]);
  useEffect(() => {
    loadTheme();
  }, []);
  const renderProfileDropdown = () => {
    if (!user) {
      return (
        <Button
          variant="primary"
          /*as={Link} to="/login?redirect=/"*/ onClick={() => {
            const provider = new GoogleAuthProvider();
            signInWithPopup(auth, provider).then((cred) => {
              setUser(cred.user);
            });
          }}
          className="ms-2"
        >
          Sign In
        </Button>
      );
    }

    return (
      <Dropdown as={Nav.Item} align="end">
        <Dropdown.Toggle
          as={Nav.Link}
          id="dropdown-profile"
          className="d-flex align-items-center"
        >
          <Image
            src={user?.firebaseUser?.photoURL || "/pp.png"/*"/pp.png"`https://avatar.iran.liara.run/username?username=${user.displayName}`*/}
            alt={user?.firebaseUser?.displayName || ""}
            roundedCircle
            width="32"
            height="32"
            className="me-2"
          />

          {/*<span className="d-none d-lg-inline">{user.displayName}</span> */}
        </Dropdown.Toggle>
        <Dropdown.Menu>
          <Dropdown.Header>
            Signed in as
            <br />
            <strong>{user?.firebaseUser?.email || "example@mail.com"}</strong>
          </Dropdown.Header>
          <Dropdown.Divider />
          <Dropdown.Item as={Link} to="/profile/details">
            <i className="bi-person-circle me-2"></i>My Profile
          </Dropdown.Item>
          <Dropdown.Item as={Link} to="/profile/orders">
            <i className="bi-box-seam-fill me-2"></i>My Orders
          </Dropdown.Item>
          <Dropdown.Item as={Link} to="/profile/wishlist">
            <i className="bi-heart-fill me-2"></i>Wishlist
          </Dropdown.Item>
          <Dropdown.Divider />
          <Dropdown.Item
            onClick={() => {
              handleSignOut();
            }}
            className="text-danger"
          >
            <i className="bi-box-arrow-right me-2"></i>Sign Out
          </Dropdown.Item>
        </Dropdown.Menu>
      </Dropdown>
    );
  };
  const handleSearch = (e) => {
    e.preventDefault();
    const val = searchValue.trim();
    if (val.length > 3) {
      console.log("Searching for:", searchValue);
    }
  };
  const navi = useNavigate();
  const searchNavigate = (addr) => {
    setSearchCanvas(false);
    navi(addr);
  };
  return (
    <Navbar
      collapseOnSelect
      expand="lg"
      bg={theme}
      variant={theme}
      className="shadow-sm py-3"
      sticky="top"
    >
      <Container fluid className="px-4">
        {/* Brand/Logo */}
        <Navbar.Brand
          as={Link}
          to={"/"}
          className="d-flex align-items-center fw-bold fs-4 me-lg-4"
        >
          <Image
            src={logo}
            width="35"
            height="35"
            className="d-inline-block align-top me-2"
            alt="Logo"
          />
          {name}
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="responsive-navbar-nav" />

        <Navbar.Collapse id="responsive-navbar-nav">
          {/* Centered navigation links */}
          <Nav className="ms-5 gap-3">
            <Nav.Link
              as={Link}
              to="/search?s=men"
              className="fw-semibold text-uppercase "
            >
              Men
            </Nav.Link>
            <Nav.Link
              as={Link}
              to="/search?s=women"
              className="fw-semibold text-uppercase "
            >
              Women
            </Nav.Link>
            <Nav.Link
              as={Link}
              to="/search?s=kids"
              className="fw-semibold text-uppercase "
            >
              Kids
            </Nav.Link>
            <Nav.Link
              as={Link}
              to="/seller"
              className="fw-semibold text-uppercase "
            >
              Seller
            </Nav.Link>
          </Nav>
          <Nav className="mx-auto d-none d-lg-flex">
            <marquee className="rounded-pill small">
              <div
                className="d-flex align-items-center gap-2 bg-body-tertiary px-3 py-1 rounded-pill small"
                style={{ width: "300px" }}
              >
                <Truck size={20} className="text-primary" />
                <span className="fw-semibold">
                  Free Shipping on Orders Over ₹999
                </span>
              </div>
            </marquee>
          </Nav>
          {/* Right-aligned content */}
          <Nav className="ms-auto align-items-center gap-3 flex-row">
            {/* 2. Modern Search Bar using InputGroup */}
            <Form className="d-none d-lg-flex" onSubmit={handleSearch}>
              <InputGroup>
                <Button
                  variant="outline-primary"
                  type="submit"
                  onClick={(e) => {
                    e.preventDefault();
                    setSearchCanvas(true);
                  }}
                >
                  <Search />
                </Button>
              </InputGroup>
            </Form>

            {/* 3. Account and Cart Icons */}
            <Nav.Link
              as={Link}
              to="/profile/cart"
              className="position-relative"
              title="Shopping Cart"
            >
              <Cart3 size={24} />
              {cartItemCount > 0 && (
                <Badge
                  bg="danger"
                  pill
                  className="position-absolute top-0 start-100 translate-middle"
                  style={{ fontSize: "0.6em", padding: "0.4em 0.6em" }}
                >
                  {cartItemCount > 20 ? "20+" : cartItemCount}
                </Badge>
              )}
            </Nav.Link>
            {/* <Nav.Link className="postion-relative">
              {theme == "light" ? <>
              <Moon onClick={() => {
                updateTheme("dark")
              }}></Moon>
              </> : <>
              <Sun onClick={() => {
                updateTheme("light")
                }}></Sun>
              </>}
            </Nav.Link> */}
            <Nav.Link className="position-relative">
              <AnimatePresence mode="wait" initial={false}>
                {theme === "light" ? (
                  <motion.div
                    key="moon"
                    initial={{ rotate: 90, opacity: 0 }}
                    animate={{ rotate: 0, opacity: 1 }}
                    exit={{ rotate: -90, opacity: 0 }}
                    transition={{ duration: 0.4, ease: "easeInOut" }}
                  >
                    <Moon
                      size={22}
                      onClick={() => updateTheme("dark")}
                      style={{ cursor: "pointer" }}
                    />
                  </motion.div>
                ) : (
                  <motion.div
                    key="sun"
                    initial={{ rotate: -90, opacity: 0 }}
                    animate={{ rotate: 0, opacity: 1 }}
                    exit={{ rotate: 90, opacity: 0 }}
                    transition={{ duration: 0.4, ease: "easeInOut" }}
                  >
                    <Sun
                      size={22}
                      onClick={() => updateTheme("light")}
                      style={{ cursor: "pointer" }}
                    />
                  </motion.div>
                )}
              </AnimatePresence>
            </Nav.Link>

            {renderProfileDropdown()}
          </Nav>
        </Navbar.Collapse>
      </Container>
      <Offcanvas
        show={showSearchCanvas}
        onHide={() => setSearchCanvas(false)}
        placement="end"
        aria-labelledby="search-offcanvas-title"
      >
        <Offcanvas.Header closeButton>
          <Offcanvas.Title id="search-offcanvas-title">
            Search Products
          </Offcanvas.Title>
        </Offcanvas.Header>

        <Offcanvas.Body>
          {/* A form to handle the search input */}
          <Form
            className="d-flex"
            onSubmit={(e) => {
              e.preventDefault();
              if (searchValue) {
                searchNavigate(`/search?s=${searchValue}`);
              }
            }}
          >
            <InputGroup>
              <FormControl
                type="search"
                placeholder="What are you looking for?"
                aria-label="Search"
                autoFocus
                value={searchValue}
                // The handler is now simpler
                onChange={(e) => setSearchValue(e.target.value)}
              />
              <Button variant="primary" type="submit">
                {/* as={Link} to={`/search?s=${searchValue}`} */}
                Search
              </Button>
            </InputGroup>
          </Form>
          {searchValue.trim() === "" ? (
            // If the search bar is empty, show "Trending"
            <Container className="mt-4">
              <p className="fw-bold">Trending Now</p>
              <ListGroup>
                {trendingRecommendations.map((p) => (
                  <Button
                    as={ListGroup.Item}
                    key={p}
                    action
                    // href={`/search?s=${p}`}
                    onClick={() => {
                      searchNavigate(`/search?s=${p}`);
                    }}
                  >
                    {p}
                  </Button>
                ))}
              </ListGroup>
            </Container>
          ) : (
            <Container className="mt-4">
              <p className="fw-bold">Search Results</p>
              <ListGroup>
                {searchRecommendations
                  // FIX: Made the search fully case-insensitive
                  .filter((p) =>
                    p.toLowerCase().includes(searchValue.trim().toLowerCase())
                  )
                  // FIX: Correctly returned the JSX by using parentheses () instead of braces {}
                  .map((p) => (
                    <Button
                      as={ListGroup.Item}
                      key={p}
                      action
                      onClick={() => {
                        searchNavigate(`/search?s=${p}`);
                      }}
                    >
                      {p}
                    </Button>
                  ))}
              </ListGroup>
            </Container>
          )}
        </Offcanvas.Body>
      </Offcanvas>
    </Navbar>
  );
}

export default NavbarFinal;
