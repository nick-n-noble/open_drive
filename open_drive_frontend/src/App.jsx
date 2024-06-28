import { useState } from 'react'
import { Route, Routes } from 'react-router-dom';
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Register from './components/Register'
import Login from './components/Login'
import Users from './components/Users'

function App() {
  const [count, setCount] = useState(0)

  return (
    <Routes>
      <Route path='/register' element={<Register />} />
      <Route path='/login' element={<Login />} />
      <Route path='/test' element={<Users />} />
    </Routes>
  )
}

export default App
