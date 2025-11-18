import { initializeApp } from "firebase/app";
import {
  GoogleAuthProvider,
  getAuth,
  signInWithEmailAndPassword, // <-- Changed import
  signInWithPopup,
  onAuthStateChanged,
  signOut,
} from "firebase/auth";
import { createContext, useEffect, useState } from "react"; // Unused 'Children' removed

/*const firebaseConfig = {
  apiKey: "AIzaSyAI7OwMffnqF7zIzMJOaUEZ6GM64raH31Y",
  authDomain: "ecomapp-fd4cc.firebaseapp.com",
  projectId: "ecomapp-fd4cc",
  storageBucket: "ecomapp-fd4cc.firebasestorage.app",
  messagingSenderId: "1072032319999",
  appId: "1:1072032319999:web:55fc9ed03c6d4942a6bfca",
};
*/
const firebaseConfig = {
  apiKey: "AIzaSyAc7GUsZbBO2FD0DzxT2U3EWdXXL5wfSqs",
  authDomain: "fablefit-81e44.firebaseapp.com",
  projectId: "fablefit-81e44",
  storageBucket: "fablefit-81e44.firebasestorage.app",
  messagingSenderId: "567522622987",
  appId: "1:567522622987:web:32b6150206acd9e864510e"
};

// Export the context so other components can use it
const UserContext = createContext(null);
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);

export {
  UserContext,
  app,
  auth
}