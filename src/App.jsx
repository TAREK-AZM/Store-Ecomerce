import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import ProductDetails from './pages/ProductDetails';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import AdminLogin from './pages/AdminLogin';
import AdminPanel from './pages/AdminPanel';
import { CartProvider } from './context/CartContext';
import { AuthProvider } from './context/AuthContext';

function App() {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);

                // Fetch both products and categories in parallel
                const [productsResponse, categoriesResponse] = await Promise.all([
                    fetch('http://127.0.0.1:8080/api/products/all'),
                    fetch('http://127.0.0.1:8080/api/categories')
                ]);

                if (!productsResponse.ok) {
                    throw new Error('Failed to fetch products');
                }
                if (!categoriesResponse.ok) {
                    throw new Error('Failed to fetch categories');
                }

                const productsData = await productsResponse.json();
                const categoriesData = await categoriesResponse.json();

                setProducts(productsData);
                setCategories(categoriesData);
            } catch (error) {
                console.error('Error fetching data:', error);
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (error) {
        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <div className="text-center text-red-600">
                    <h2 className="text-xl font-bold mb-2">Error Loading Data</h2>
                    <p>{error}</p>
                    <button
                        onClick={() => window.location.reload()}
                        className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Retry
                    </button>
                </div>
            </div>
        );
    }

    return (
        <AuthProvider>
            <CartProvider>
                <Router>
                    <div className="min-h-screen bg-gray-100">
                        <Navbar />
                        <main className="container mx-auto px-4 py-8">
                            {loading ? (
                                <div className="flex justify-center items-center py-8">
                                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                                </div>
                            ) : (
                                <Routes>
                                    <Route path="/" element={
                                        <Home
                                            products={products}
                                            categories={categories}
                                        />
                                    } />
                                    <Route
                                        path="/product/:id"
                                        element={<ProductDetails products={products} />}
                                    />
                                    <Route path="/cart" element={<Cart />} />
                                    <Route path="/checkout" element={<Checkout />} />
                                    <Route path="/admin/login" element={<AdminLogin />} />
                                    <Route
                                        path="/admin/panel"
                                        element={
                                            <AdminPanel
                                                products={products}
                                                categories={categories}
                                            />
                                        }
                                    />
                                </Routes>
                            )}
                        </main>
                        <Toaster position="bottom-right" />
                    </div>
                </Router>
            </CartProvider>
        </AuthProvider>
    );
}

export default App;