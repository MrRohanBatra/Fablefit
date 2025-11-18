import { createContext, useEffect, useState } from "react";
import { Navigate, useLocation, useNavigate } from "react-router-dom";
import NavbarFinal from "./components/navbar";
import LoginPage from "./components/login";

import "bootstrap-icons/font/bootstrap-icons.css";
import Appname from "./components/NameContext";
import { auth, UserContext } from "./components/FirebaseAuth";
import { onAuthStateChanged, signOut } from "firebase/auth";
import Home from "./components/Home";
import Search from "./components/Search";
export const themeContext = createContext(null);
export const cartContext = createContext([]);
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AnimatePresence, motion } from "framer-motion";
import ProductDisplay from "./components/ProductDisplay";
import Profile from "./components/Profile";
import Vton from "./components/vton";
import { loadCart } from "./utils/util";
import Cart from "./components/Product/Cart";
import { User } from "./components/User/user";
import AddProduct from "./components/Seller";
function App_home() {
  const [theme, setTheme] = useState(
    document.documentElement.getAttribute("data-bs-theme")
  );
  const [user, setUser] = useState(null);
  const [cart, setCart] = useState(null);
  const [name, setName] = useState("FableFit");
  // useEffect(() => {
  //   const unsubscribe = onAuthStateChanged(auth, async (currentUser) => {
  //     if (currentUser) {
  //       try {
  //         const appUser = new User(currentUser);
  //         const response = await fetch(
  //           `/api/users/${currentUser.uid}`
  //         );
  //         if (response.ok) {
  //           const existingUser = await res.json();
  //           appUser.phone = existingUser.phone || "";
  //           appUser.address = existingUser.address || [];
  //           appUser.vton_img = existingUser.vton_image || "";
  //           appUser.type = existingUser.type || "normal";
  //         }
  //         await appUser.userUpdated();

  //         // 4️⃣ Save in React context
  //         setUser(appUser);
  //       } catch (error) {
  //         console.error("❌ Error initializing user:", error);
  //       }
  //     }
  //     else {
  //       setUser(null);
  //     }

  //     return () => unsubscribe();
  //   })}, []);
  useEffect(() => {
  const unsubscribe = onAuthStateChanged(auth, async (currentUser) => {
    if (currentUser) {
      try {
        // Create base app user
        const appUser = new User({ user: currentUser });

        // Try to get user data from backend
        const response = await fetch(
          `/api/users/${currentUser.uid}`
        );

        if (response.ok) {
          // ✅ Found existing user — fill info
          const existingUser = await response.json();
          appUser.phone = existingUser.phone || "";
          appUser.address = existingUser.address || [];
          appUser.vton_image = existingUser.vton_image || "";
          appUser.type = existingUser.type || "normal";
        } else if (response.status === 404) {
          // ⚡ Not found → backend will create via userUpdated()
          console.warn("User not found, creating new user...");
        } else {
          throw new Error(`Unexpected backend response: ${response.status}`);
        }

        // 🧠 This will create/update in backend automatically
        await appUser.userUpdated();

        // 🔄 Save to React context
        setUser(appUser);

      } catch (error) {
        console.error("❌ Error initializing user:", error);
      }
    } else {
      setUser(null);
    }
  });

  return () => unsubscribe();
}, []);

  const handleSignOut = () => {
    signOut(auth);
    setUser(null);
  };
  useEffect(() => {
    if (user ) {
      loadCart(user?.firebaseUser?.uid || "").then((data) => {
        if (data) {
          setCart(data);
        } else {
          setCart(new Cart({uid:user?.firebaseUser.uid,items:[]}));
        }
        console.log(data);
      });
    } else {
      setCart(new Cart({uid:user?.firebaseUser.uid,items:[]}));
    }
  }, [user]);
  const location = useLocation();
  return (
    <UserContext.Provider value={[user, setUser, handleSignOut]}>
      <cartContext.Provider value={[cart, setCart]}>
        <themeContext.Provider value={[theme, setTheme]}>
          <Appname.Provider value={[name, setName]}>
            <NavbarFinal></NavbarFinal>
            <AnimatePresence mode="wait">
              <Routes location={location} key={location.pathname}>
                <Route
                  path="/"
                  element={
                    <motion.div
                      initial={{ opacity: 0, y: 15 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -15 }}
                      transition={{ duration: 0.2, ease: "easeInOut" }}
                    >
                      <Home />
                    </motion.div>
                  }
                />
                <Route
                  path="/product/:id"
                  element={
                    <motion.div
                      initial={{ opacity: 0, y: 15 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -15 }}
                      transition={{ duration: 0.2, ease: "easeInOut" }}
                    >
                      <ProductDisplay />
                    </motion.div>
                  }
                />
                <Route
                  path="/profile/"
                  element={<Navigate to={"/profile/details"}></Navigate>}
                />
                <Route
                  path="/profile/:page"
                  element={
                    <motion.div
                      initial={{ opacity: 0, y: 15 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -15 }}
                      transition={{ duration: 0.2, ease: "easeInOut" }}
                    >
                      <Profile />
                    </motion.div>
                  }
                />
                <Route
                  path="/search"
                  element={
                    <motion.div
                      initial={{ opacity: 0, y: 40 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -40 }}
                      transition={{ duration: 0.2, ease: "easeInOut" }}
                    >
                      <Search />
                    </motion.div>
                  }
                />
                <Route
                  path="/seller"
                  element={
                    <motion.div
                      initial={{ opacity: 0, y: 40 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -40 }}
                      transition={{ duration: 0.2, ease: "easeInOut" }}
                    >
                      <AddProduct/>
                    </motion.div>
                  }
                />
                <Route path="/vton" element={<Vton></Vton>}></Route>
              </Routes>
            </AnimatePresence>
          </Appname.Provider>
        </themeContext.Provider>
      </cartContext.Provider>
    </UserContext.Provider>
  );
}

export default App_home;
