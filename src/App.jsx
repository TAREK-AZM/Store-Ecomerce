import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import ProductDetails from './pages/ProductDetails'
import Cart from './pages/Cart'
import Checkout from './pages/Checkout'
import AdminLogin from './pages/AdminLogin'
import AdminPanel from './pages/AdminPanel'
import { CartProvider } from './context/CartContext'
import { AuthProvider } from './context/AuthContext'

function App() {
    return (
        <AuthProvider>
            <CartProvider>
                <Router>
                    <div className="min-h-screen bg-gray-100">
                        <Navbar />
                        <main className="container mx-auto px-4 py-8">
                            <Routes>
                                <Route path="/" element={<Home />} />
                                <Route path="/product/:id" element={<ProductDetails />} />
                                <Route path="/cart" element={<Cart />} />
                                <Route path="/checkout" element={<Checkout />} />
                                <Route path="/admin/login" element={<AdminLogin />} />
                                <Route path="/admin/panel" element={<AdminPanel />} />
                            </Routes>
                        </main>
                        <Toaster position="bottom-right" />
                    </div>
                </Router>
            </CartProvider>
        </AuthProvider>
    )
}

export default App

