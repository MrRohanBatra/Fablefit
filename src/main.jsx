import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import Appname from './components/NameContext.js'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Login from './components/login.jsx'
import Profile from './components/Profile.jsx'
createRoot(document.getElementById('root')).render(
  <StrictMode>
    {/* <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path='/search' element={<Login></Login>}></Route>
        <Route path='/profile' element={<Profile></Profile>}></Route>
      </Routes>
    </BrowserRouter> */}
    <BrowserRouter>
    <App></App>
    </BrowserRouter>
    
  </StrictMode>
)
