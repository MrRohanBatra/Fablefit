import { createContext, useEffect, useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import NavbarFinal from './components/navbar';
import LoginPage from './components/login';

import 'bootstrap-icons/font/bootstrap-icons.css';
import Appname from './components/NameContext';
import { auth, UserContext } from './components/FirebaseAuth';
import { onAuthStateChanged, signOut } from 'firebase/auth';
import Home from './components/Home';
import Search from './components/Search';
export const themeContext = createContext(null);
export const cartContext = createContext([]);
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AnimatePresence, motion } from "framer-motion";
import ProductDisplay from './components/ProductDisplay';
import Profile from './components/Profile';
import Vton from './components/vton';
import { loadCart } from './utils/util';
import Cart from './components/Product/Cart';
function App_home() {
  const [theme, setTheme] = useState(document.documentElement.getAttribute('data-bs-theme'));
  const [user, setUser] = useState(null);
  const [cart, setCart] = useState(new Cart(null));
  const [name, setName] = useState("FableFit")
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      setUser(currentUser);
    });

    return () => unsubscribe();
  }, []);
  const handleSignOut = () => {
    signOut(auth)
    setUser(null);
  }
  useEffect(() => {
    if (user) {
      loadCart(user?.uid).then((data) => {
        if (data) {
          setCart(data);
        }
        else {
          setCart(new Cart(user.uid, []));
        }
        console.log(data);
      })
    }
    else {
      setCart(new Cart(null, []));
    }
  },[user])
  const location = useLocation();
  return (
    
    <UserContext.Provider value={[user,setUser,handleSignOut]}>
    <cartContext.Provider value={[cart,setCart]}>
    <themeContext.Provider value={[theme,setTheme]}>
      <Appname.Provider value={[name,setName]}>
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
              <ProductDisplay/>
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
              <Profile/>
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
                <Route path='/vton' element={<Vton></Vton>}></Route>
      </Routes>
    </AnimatePresence>
    </Appname.Provider>
    </themeContext.Provider>
    </cartContext.Provider>
    </UserContext.Provider>

  );
}

export default App_home;