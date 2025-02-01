import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { products } from '../data/mockData'
import toast from 'react-hot-toast'

export default function AdminPanel() {
    const navigate = useNavigate()
    const { isAdmin } = useAuth()
    const [activeTab, setActiveTab] = useState('products')
    const [productForm, setProductForm] = useState({
        title: '',
        description: '',
        price: '',
        category: '',
        quantity: '',
        img_url: ''
    })

    useEffect(() => {
        if (!isAdmin) {
            navigate('/admin/login')
        }
    }, [isAdmin, navigate])

    const handleProductSubmit = (e) => {
        e.preventDefault()
        // In a real app, this would make an API call
        toast.success('Product added successfully!')
        setProductForm({
            title: '',
            description: '',
            price: '',
            category: '',
            quantity: '',
            img_url: ''
        })
    }

    return (
        <div className="max-w-4xl mx-auto">
            <h1 className="text-2xl font-bold mb-6">Admin Panel</h1>

            <div className="flex gap-4 mb-6">
                <button
                    onClick={() => setActiveTab('products')}
                    className={`px-4 py-2 rounded ${
                        activeTab === 'products' ? 'bg-blue-600 text-white' : 'bg-gray-200'
                    }`}
                >
                    Products
                </button>
                <button
                    onClick={() => setActiveTab('orders')}
                    className={`px-4 py-2 rounded ${
                        activeTab === 'orders' ? 'bg-blue-600 text-white' : 'bg-gray-200'
                    }`}
                >
                    Orders
                </button>
            </div>

            {activeTab === 'products' ? (
                <div className="space-y-6">
                    <form onSubmit={handleProductSubmit} className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium mb-1">Title</label>
                                <input
                                    type="text"
                                    value={productForm.title}
                                    onChange={(e) => setProductForm(prev => ({ ...prev, title: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Category</label>
                                <input
                                    type="text"
                                    value={productForm.category}
                                    onChange={(e) => setProductForm(prev => ({ ...prev, category: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Price</label>
                                <input
                                    type="number"
                                    value={productForm.price}
                                    onChange={(e) => setProductForm(prev => ({ ...prev, price: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Quantity</label>
                                <input
                                    type="number"
                                    value={productForm.quantity}
                                    onChange={(e) => setProductForm(prev => ({ ...prev, quantity: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Description</label>
                            <textarea
                                value={productForm.description}
                                onChange={(e) => setProductForm(prev => ({ ...prev, description: e.target.value }))}
                                className="w-full p-2 border rounded"
                                rows="3"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Image URL</label>
                            <input
                                type="text"
                                value={productForm.img_url}
                                onChange={(e) => setProductForm(prev => ({ ...prev, img_url: e.target.value }))}
                                className="w-full p-2 border rounded"
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
                        >
                            Add Product
                        </button>
                    </form>

                    <div className="mt-8">
                        <h2 className="text-xl font-semibold mb-4">Current Products</h2>
                        <div className="grid gap-4">
                            {products.map(product => (
                                <div key={product.id} className="flex items-center gap-4 bg-white p-4 rounded-lg shadow">
                                    <img
                                        src={product.img_url}
                                        alt={product.title}
                                        className="w-16 h-16 object-cover rounded"
                                    />
                                    <div className="flex-1">
                                        <h3 className="font-semibold">{product.title}</h3>
                                        <p className="text-sm text-gray-600">${product.price} - {product.quantity} in stock</p>
                                    </div>
                                    <button
                                        onClick={() => toast.success('Product deleted!')}
                                        className="text-red-600 hover:text-red-700"
                                    >
                                        Delete
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            ) : (
                <div>
                    <h2 className="text-xl font-semibold mb-4">Orders</h2>
                    <p className="text-gray-600">No orders to display.</p>
                </div>
            )}
        </div>
    )
}

