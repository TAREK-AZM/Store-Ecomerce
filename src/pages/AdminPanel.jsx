import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

function AdminPanel() {
    const navigate = useNavigate();
    const { isAdmin } = useAuth();
    const [activeTab, setActiveTab] = useState('products');
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [lineCommands, setLineCommands] = useState({});
    const [loading, setLoading] = useState(true);
    const [selectedOrder, setSelectedOrder] = useState(null);

    const [productForm, setProductForm] = useState({
        id: null,
        title: '',
        description: '',
        price: '',
        stockQuantity: '',
        imageUrl: '',
        category: ''
    });

    useEffect(() => {
        if (!isAdmin) {
            navigate('/admin/login');
            return;
        }
        fetchData();
    }, [isAdmin, navigate]);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [productsRes, ordersRes] = await Promise.all([
                fetch('http://127.0.0.1:8080/api/products/all'),
                fetch('http://127.0.0.1:8080/api/commands')
            ]);

            const productsData = await productsRes.json();
            const ordersData = await ordersRes.json();

            setProducts(productsData);
            setOrders(ordersData);
        } catch (error) {
            toast.error('Error fetching data');
            console.error('Error:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchLineCommands = async (commandId) => {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/line-commands/lines/${commandId}`);
            const data = await response.json();
            setLineCommands(prev => ({ ...prev, [commandId]: data }));
        } catch (error) {
            toast.error('Error fetching line commands');
            console.error('Error:', error);
        }
    };

    const handleProductSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('http://127.0.0.1:8080/api/products', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    ...productForm,
                    price: parseFloat(productForm.price),
                    stockQuantity: parseInt(productForm.stockQuantity)
                })
            });

            if (!response.ok) throw new Error('Failed to save product');

            toast.success('Product saved successfully!');
            fetchData();
            setProductForm({
                id: null,
                title: '',
                description: '',
                price: '',
                stockQuantity: '',
                imageUrl: '',
                category: ''
            });
        } catch (error) {
            toast.error('Error saving product');
            console.error('Error:', error);
        }
    };

    const handleDeleteProduct = async (id) => {
        if (!window.confirm('Are you sure you want to delete this product?')) return;

        try {
            const response = await fetch(`http://127.0.0.1:8080/api/products/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Failed to delete product');

            toast.success('Product deleted successfully!');
            fetchData();
        } catch (error) {
            toast.error('Error deleting product');
            console.error('Error:', error);
        }
    };

    const handleEditProduct = (product) => {
        setProductForm({
            id: product.id,
            title: product.title,
            description: product.description,
            price: product.price,
            stockQuantity: product.stockQuantity,
            imageUrl: product.imageUrl,
            category: product.category
        });
    };

    const handleUpdateOrderStatus = async (id, newStatus) => {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/commands/${id}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ status: newStatus })
            });

            if (!response.ok) throw new Error('Failed to update order status');

            toast.success('Order status updated successfully!');
            fetchData();
        } catch (error) {
            toast.error('Error updating order status');
            console.error('Error:', error);
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center py-8">
                <div className="animate-spin h-8 w-8 border-4 border-blue-500 rounded-full border-t-transparent"></div>
            </div>
        );
    }

    return (
        <div className="max-w-6xl mx-auto p-4">
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
                    <form onSubmit={handleProductSubmit} className="bg-white p-6 rounded-lg shadow">
                        <h2 className="text-xl font-semibold mb-4">
                            {productForm.id ? 'Edit Product' : 'Add New Product'}
                        </h2>
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
                                    step="0.01"
                                    value={productForm.price}
                                    onChange={(e) => setProductForm(prev => ({ ...prev, price: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Stock Quantity</label>
                                <input
                                    type="number"
                                    value={productForm.stockQuantity}
                                    onChange={(e) => setProductForm(prev => ({ ...prev, stockQuantity: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div className="col-span-2">
                                <label className="block text-sm font-medium mb-1">Description</label>
                                <textarea
                                    value={productForm.description}
                                    onChange={(e) => setProductForm(prev => ({ ...prev, description: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    rows="3"
                                    required
                                />
                            </div>

                            <div className="col-span-2">
                                <label className="block text-sm font-medium mb-1">Image URL</label>
                                <input
                                    type="text"
                                    value={productForm.imageUrl}
                                    onChange={(e) => setProductForm(prev => ({ ...prev, imageUrl: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>
                        </div>

                        <div className="mt-4 flex gap-4">
                            <button
                                type="submit"
                                className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
                            >
                                {productForm.id ? 'Update Product' : 'Add Product'}
                            </button>
                            {productForm.id && (
                                <button
                                    type="button"
                                    onClick={() => setProductForm({
                                        id: null,
                                        title: '',
                                        description: '',
                                        price: '',
                                        stockQuantity: '',
                                        imageUrl: '',
                                        category: ''
                                    })}
                                    className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                                >
                                    Cancel Edit
                                </button>
                            )}
                        </div>
                    </form>

                    <div className="bg-white p-6 rounded-lg shadow">
                        <h2 className="text-xl font-semibold mb-4">Products List</h2>
                        <div className="grid gap-4">
                            {products.map(product => (
                                <div key={product.id} className="flex items-center gap-4 p-4 border rounded">
                                    <img
                                        src={product.imageUrl}
                                        alt={product.title}
                                        className="w-16 h-16 object-cover rounded"
                                        onError={(e) => {
                                            e.target.src = '/placeholder-image.jpg';
                                        }}
                                    />
                                    <div className="flex-1">
                                        <h3 className="font-semibold">{product.title}</h3>
                                        <p className="text-sm text-gray-600">
                                            ${product.price} - {product.stockQuantity} in stock - {product.category}
                                        </p>
                                        <p className="text-sm text-gray-500">{product.description}</p>
                                    </div>
                                    <div className="flex gap-2">
                                        <button
                                            onClick={() => handleEditProduct(product)}
                                            className="text-blue-600 hover:text-blue-700"
                                        >
                                            Edit
                                        </button>
                                        <button
                                            onClick={() => handleDeleteProduct(product.id)}
                                            className="text-red-600 hover:text-red-700"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            ) : (
                <div className="space-y-6">
                    <div className="bg-white p-6 rounded-lg shadow">
                        <h2 className="text-xl font-semibold mb-4">Orders List</h2>
                        <div className="grid gap-4">
                            {orders.map(order => (
                                <div key={order.id} className="border rounded p-4">
                                    <div className="flex justify-between items-center mb-4">
                                        <div>
                                            <h3 className="font-semibold">Order #{order.id}</h3>
                                            <p className="text-sm text-gray-600">
                                                Date: {order.date} | Status: {order.status}
                                            </p>
                                        </div>
                                        <div className="flex gap-2 items-center">
                                            <select
                                                value={order.status}
                                                onChange={(e) => handleUpdateOrderStatus(order.id, e.target.value)}
                                                className="p-2 border rounded"
                                            >
                                                <option value="Pending">Pending</option>
                                                <option value="Shipped">Shipped</option>
                                                <option value="Delivered">Delivered</option>
                                                <option value="Cancelled">Cancelled</option>
                                            </select>
                                            <button
                                                onClick={() => {
                                                    setSelectedOrder(selectedOrder === order.id ? null : order.id);
                                                    if (selectedOrder !== order.id) {
                                                        fetchLineCommands(order.id);
                                                    }
                                                }}
                                                className="text-blue-600 hover:text-blue-700"
                                            >
                                                {selectedOrder === order.id ? 'Hide Details' : 'Show Details'}
                                            </button>
                                        </div>
                                    </div>

                                    {selectedOrder === order.id && lineCommands[order.id] && (
                                        <div className="mt-4 pl-4 border-l">
                                            <h4 className="font-medium mb-2">Order Details:</h4>
                                            <div className="space-y-2">
                                                {lineCommands[order.id].map(line => (
                                                    <div key={line.id} className="flex justify-between text-sm">
                                                        <span>Product ID: {line.productId}</span>
                                                        <span>Quantity: {line.quantity}</span>
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default AdminPanel;