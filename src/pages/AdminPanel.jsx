import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function AdminPanel() {
    const navigate = useNavigate();
    const { isAdmin } = useAuth();
    const [activeTab, setActiveTab] = useState('products');
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [users, setUsers] = useState([]);
    const [lineCommands, setLineCommands] = useState({});
    const [loading, setLoading] = useState(true);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [selectedUserOrder, setSelectedUserOrder] = useState(null);
    const [selectedProducts, setSelectedProducts] = useState({});
    const [selectedProductDetails, setSelectedProductDetails] = useState(null);
    const [orderStatusFilter, setOrderStatusFilter] = useState('ALL');
    const [reportHtml, setReportHtml] = useState('');
    const [showReport, setShowReport] = useState(false);
// Ajoutez après les autres états existants
    const [userForm, setUserForm] = useState({
        id: null,
        username: '',
        email: '',
        firstName: '',
        lastName: '',
        phoneNumber: '',
        address: ''
    });
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
            const [productsRes, ordersRes, usersRes] = await Promise.all([
                fetch('http://127.0.0.1:8080/api/products/all'),
                fetch('http://127.0.0.1:8080/api/commands'),
                fetch('http://127.0.0.1:8080/api/users')
            ]);

            const productsData = await productsRes.json();
            const ordersData = await ordersRes.json();
            const usersData = await usersRes.json();

            setProducts(productsData);
            setOrders(ordersData);
            setUsers(usersData);
        } catch (error) {
            toast.error('Error fetching data');
            console.error('Error:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchReport = async () => {
        try {
            const response = await fetch('http://127.0.0.1:8080/api/reports/monthly/products');
            const htmlContent = await response.text();
            setReportHtml(htmlContent);
            setShowReport(true);
        } catch (error) {
            toast.error('Error fetching report');
            console.error('Error:', error);
        }
    };

    // const fetchProductDetails = async (productId) => {
    //     try {
    //         const response = await fetch(`http://127.0.0.1:8080/api/products/${productId}`);
    //         const data = await response.json();
    //         setSelectedProducts(prev => ({
    //             ...prev,
    //             [productId]: data
    //         }));
    //     } catch (error) {
    //         toast.error('Error fetching product details');
    //         console.error('Error:', error);
    //     }
    // };

    // Ajouter cette fonction pour gérer l'affichage des détails
    const toggleProductDetails = async (productId) => {
        if (selectedProductDetails === productId) {
            setSelectedProductDetails(null); // Cache les détails si on clique à nouveau
        } else {
            try {
                const response = await fetch(`http://127.0.0.1:8080/api/products/${productId}`);
                const data = await response.json();
                setSelectedProducts(prev => ({
                    ...prev,
                    [productId]: data
                }));
                setSelectedProductDetails(productId); // Affiche les détails du nouveau produit
            } catch (error) {
                toast.error('Error fetching product details');
                console.error('Error:', error);
            }
        }
    };

    const handleExportExcel = async (type) => {
        try {
            const response = await fetch(`http://localhost:8080/api/export/${type}/excel`);
            if (!response.ok) throw new Error('Export failed');

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `${type}-report.xlsx`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);

            toast.success(`${type} exported successfully!`);
        } catch (error) {
            toast.error(`Error exporting ${type}`);
            console.error('Export error:', error);
        }
    };
    const getFilteredOrders = () => {
        if (orderStatusFilter === 'ALL') return orders;
        return orders.filter(order => order.status === orderStatusFilter);
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

    const handleUserSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('http://127.0.0.1:8080/api/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userForm)
            });

            if (!response.ok) throw new Error('Failed to save user');

            toast.success(userForm.id ? 'User updated successfully!' : 'User created successfully!');
            await fetchData();
            setUserForm({
                id: null,
                username: '',
                email: '',
                firstName: '',
                lastName: '',
                phoneNumber: '',
                address: ''
            });
        } catch (error) {
            toast.error('Error saving user');
            console.error('Error:', error);
        }
    };

    const handleEditUser = (user) => {
        setUserForm({
            id: user.id,
            username: user.username,
            email: user.email,
            firstName: user.firstName,
            lastName: user.lastName,
            phoneNumber: user.phoneNumber,
            address: user.address
        });
    };

    const handleDeleteUser = async (id) => {
        if (!window.confirm('Are you sure you want to delete this user?')) return;

        try {
            const response = await fetch(`http://127.0.0.1:8080/api/users/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Failed to delete user');

            toast.success('User deleted successfully!');
            await fetchData();
        } catch (error) {
            toast.error('Error deleting user');
            console.error('Error:', error);
        }
    };

    const handleProductSubmit = async (e) => {
        e.preventDefault();
        try {
            const productData = {
                ...(productForm.id && { id: productForm.id }),
                title: productForm.title,
                description: productForm.description,
                price: parseFloat(productForm.price),
                stockQuantity: parseInt(productForm.stockQuantity),
                imageUrl: productForm.imageUrl,
                category: productForm.category
            };

            const response = await fetch('http://127.0.0.1:8080/api/products', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(productData)
            });

            if (!response.ok) throw new Error('Failed to save product');

            toast.success(productForm.id ? 'Product updated successfully!' : 'Product created successfully!');
            await fetchData();
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
            await fetchData();
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

    const handleUpdateOrderStatus = async (order, newStatus) => {
        try {
            const updatedOrder = {
                id: order.id,
                status: newStatus,
                date: order.date,
                userId: order.userId
            };

            const response = await fetch('http://127.0.0.1:8080/api/commands', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(updatedOrder)
            });

            if (!response.ok) throw new Error('Failed to update order status');

            toast.success('Order status updated successfully!');
            await fetchData();
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
                {/* Ajoutez après le bouton Orders */}
                <button
                    onClick={() => setActiveTab('users')}
                    className={`px-4 py-2 rounded ${
                        activeTab === 'users' ? 'bg-blue-600 text-white' : 'bg-gray-200'
                    }`}
                >
                    Users
                </button>
                <button
                    onClick={() => setActiveTab('report')}
                    className={`px-4 py-2 rounded ${
                        activeTab === 'report' ? 'bg-blue-600 text-white' : 'bg-gray-200'
                    }`}
                >
                    Rapport
                </button>
            </div>

            {activeTab === 'products' && (
                <div className="space-y-6">
                    <form onSubmit={handleProductSubmit} className="bg-white p-6 rounded-lg shadow">
                        <h2 className="text-xl font-semibold mb-4">Add New Product</h2>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium mb-1">Title</label>
                                <input
                                    type="text"
                                    value={productForm.title}
                                    onChange={(e) => setProductForm(prev => ({...prev, title: e.target.value}))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Category</label>
                                <input
                                    type="text"
                                    value={productForm.category}
                                    onChange={(e) => setProductForm(prev => ({...prev, category: e.target.value}))}
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
                                    onChange={(e) => setProductForm(prev => ({...prev, price: e.target.value}))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Stock Quantity</label>
                                <input
                                    type="number"
                                    value={productForm.stockQuantity}
                                    onChange={(e) => setProductForm(prev => ({...prev, stockQuantity: e.target.value}))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div className="col-span-2">
                                <label className="block text-sm font-medium mb-1">Description</label>
                                <textarea
                                    value={productForm.description}
                                    onChange={(e) => setProductForm(prev => ({...prev, description: e.target.value}))}
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
                                    onChange={(e) => setProductForm(prev => ({...prev, imageUrl: e.target.value}))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>
                        </div>

                        <div className="mt-4 flex gap-2">
                            <button
                                type="submit"
                                className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
                            >
                                {productForm.id ? 'Update Product' : 'Add Product'}
                            </button>
                            {productForm.id && (
                                <button
                                    type="button"
                                    onClick={() => {
                                        setProductForm({
                                            id: null,
                                            title: '',
                                            description: '',
                                            price: '',
                                            stockQuantity: '',
                                            imageUrl: '',
                                            category: ''
                                        });
                                    }}
                                    className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                                >
                                    Cancel
                                </button>
                            )}
                        </div>
                    </form>
                    <div className="bg-white p-6 rounded-lg shadow">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-xl font-semibold">Products List</h2>
                            <div className="flex gap-2">
                                <button
                                    onClick={() => handleExportExcel('products')}
                                    className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 flex items-center gap-2"
                                >
                                    <span>Export Excel</span>
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20"
                                         fill="currentColor">
                                        <path fillRule="evenodd"
                                              d="M6 2a2 2 0 00-2 2v12a2 2 0 002 2h8a2 2 0 002-2V7.414A2 2 0 0015.414 6L12 2.586A2 2 0 0010.586 2H6zm5 6a1 1 0 10-2 0v3.586L7.707 10.293a1 1 0 10-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 11.586V8z"
                                              clipRule="evenodd"/>
                                    </svg>
                                </button>
                            </div>
                        </div>
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
                </div>
            )}
            {activeTab === 'orders' && (
                <div className="space-y-6">
                    <div className="bg-white p-6 rounded-lg shadow">
                        <div className="flex items-center justify-between mb-6">
                            <div className="flex items-center gap-4">
                                <h2 className="text-xl font-semibold">Orders List</h2>
                                <button
                                    onClick={() => handleExportExcel('commands')}
                                    className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 flex items-center gap-2"
                                >
                                    <span>Export Excel</span>
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20"
                                         fill="currentColor">
                                        <path fillRule="evenodd"
                                              d="M6 2a2 2 0 00-2 2v12a2 2 0 002 2h8a2 2 0 002-2V7.414A2 2 0 0015.414 6L12 2.586A2 2 0 0010.586 2H6zm5 6a1 1 0 10-2 0v3.586L7.707 10.293a1 1 0 10-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 11.586V8z"
                                              clipRule="evenodd"/>
                                    </svg>
                                </button>
                            </div>
                            <div className="flex gap-2">
                                <button
                                    onClick={() => setOrderStatusFilter('ALL')}
                                    className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                                        orderStatusFilter === 'ALL'
                                            ? 'bg-gray-900 text-white'
                                            : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                    }`}
                                >
                                    All
                                </button>
                                <button
                                    onClick={() => setOrderStatusFilter('Pending')}
                                    className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                                        orderStatusFilter === 'Pending'
                                            ? 'bg-yellow-500 text-white'
                                            : 'bg-yellow-50 text-yellow-600 hover:bg-yellow-100'
                                    }`}
                                >
                                    Pending
                                </button>
                                <button
                                    onClick={() => setOrderStatusFilter('Shipped')}
                                    className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                                        orderStatusFilter === 'Shipped'
                                            ? 'bg-blue-500 text-white'
                                            : 'bg-blue-50 text-blue-600 hover:bg-blue-100'
                                    }`}
                                >
                                    Shipped
                                </button>
                                <button
                                    onClick={() => setOrderStatusFilter('Delivered')}
                                    className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                                        orderStatusFilter === 'Delivered'
                                            ? 'bg-green-500 text-white'
                                            : 'bg-green-50 text-green-600 hover:bg-green-100'
                                    }`}
                                >
                                    Delivered
                                </button>
                                <button
                                    onClick={() => setOrderStatusFilter('Cancelled')}
                                    className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                                        orderStatusFilter === 'Cancelled'
                                            ? 'bg-red-500 text-white'
                                            : 'bg-red-50 text-red-600 hover:bg-red-100'
                                    }`}
                                >
                                    Cancelled
                                </button>
                            </div>
                        </div>
                        <div className="grid gap-4">
                            {getFilteredOrders().map(order => (
                                <div
                                    key={order.id}
                                    className={`border rounded p-4 ${
                                        order.status === 'Pending' ? 'border-yellow-200 bg-yellow-50' :
                                            order.status === 'Shipped' ? 'border-blue-200 bg-blue-50' :
                                                order.status === 'Delivered' ? 'border-green-200 bg-green-50' :
                                                    order.status === 'Cancelled' ? 'border-red-200 bg-red-50' :
                                                        'border-gray-200'
                                    }`}
                                >
                                    <div className="flex justify-between items-center mb-4">
                                        <div>
                                            <h3 className="font-semibold">Order #{order.id}</h3>
                                            <p className="text-sm text-gray-600">
                                                Date: {order.date} |
                                                <span className={`ml-2 px-2 py-1 rounded-full text-sm font-medium ${
                                                    order.status === 'Pending' ? 'bg-yellow-100 text-yellow-800' :
                                                        order.status === 'Shipped' ? 'bg-blue-100 text-blue-800' :
                                                            order.status === 'Delivered' ? 'bg-green-100 text-green-800' :
                                                                order.status === 'Cancelled' ? 'bg-red-100 text-red-800' :
                                                                    'bg-gray-100 text-gray-800'
                                                }`}>
                                        Status: {order.status}
                                    </span>
                                            </p>
                                        </div>
                                        <div className="flex gap-2 items-center">
                                            <select
                                                value={order.status}
                                                onChange={(e) => handleUpdateOrderStatus(order, e.target.value)}
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
                                                className="text-blue-600 hover:text-blue-700 px-3 py-1 border rounded"
                                            >
                                                {selectedOrder === order.id ? 'Hide Details' : 'Show Details'}
                                            </button>
                                            <button
                                                onClick={() => setSelectedUserOrder(selectedUserOrder === order.id ? null : order.id)}
                                                className="text-green-600 hover:text-green-700 px-3 py-1 border rounded"
                                            >
                                                {selectedUserOrder === order.id ? 'Hide User' : 'Show User'}
                                            </button>
                                        </div>
                                    </div>

                                    {selectedUserOrder === order.id && (
                                        <div className="mt-4 pl-4 border-l bg-gray-50 p-4 rounded">
                                            <h4 className="font-medium mb-2">User Information:</h4>
                                            {users.find(user => user.id === order.userId) ? (
                                                <div className="grid grid-cols-2 gap-4">
                                                    <div>
                                                        <p className="text-sm">
                                                            <span className="font-medium">Username:</span> {users.find(user => user.id === order.userId).username}
                                                        </p>
                                                        <p className="text-sm">
                                                            <span className="font-medium">Email:</span> {users.find(user => user.id === order.userId).email}
                                                        </p>
                                                    </div>
                                                    <div>
                                                        <p className="text-sm">
                                                            <span className="font-medium">Name:</span> {users.find(user => user.id === order.userId).firstName} {users.find(user => user.id === order.userId).lastName}
                                                        </p>
                                                        <p className="text-sm">
                                                            <span className="font-medium">Phone:</span> {users.find(user => user.id === order.userId).phoneNumber}
                                                        </p>
                                                        <p className="text-sm">
                                                            <span className="font-medium">Address:</span> {users.find(user => user.id === order.userId).address}
                                                        </p>
                                                    </div>
                                                </div>
                                            ) : (
                                                <p className="text-sm text-gray-500">User information not found</p>
                                            )}
                                        </div>
                                    )}

                                    {selectedOrder === order.id && lineCommands[order.id] && (
                                        <div className="mt-4 pl-4 border-l">
                                            <h4 className="font-medium mb-2">Order Details:</h4>
                                            <div className="space-y-4">
                                                {lineCommands[order.id].map(line => (
                                                    <div key={line.id} className="border rounded-lg overflow-hidden">
                                                        <button
                                                            onClick={() => toggleProductDetails(line.productId)}
                                                            className="w-full p-3 bg-gray-50 hover:bg-gray-100 flex justify-between items-center transition-colors duration-200"
                                                        >
                                                            <div className="flex items-center gap-2">
                                                    <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">
                                                        Product #{line.productId}
                                                    </span>
                                                            </div>
                                                            <div className="flex items-center gap-4">
                                                    <span className="bg-gray-100 px-3 py-1 rounded-full text-sm">
                                                        Quantity: {line.quantity}
                                                    </span>
                                                                <svg
                                                                    className={`w-5 h-5 transition-transform duration-200 ${
                                                                        selectedProductDetails === line.productId ? 'transform rotate-180' : ''
                                                                    }`}
                                                                    fill="none"
                                                                    viewBox="0 0 24 24"
                                                                    stroke="currentColor"
                                                                >
                                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                                                </svg>
                                                            </div>
                                                        </button>

                                                        {selectedProductDetails === line.productId && selectedProducts[line.productId] && (
                                                            <div className="p-4 border-t">
                                                                <div className="flex gap-4">
                                                                    <img
                                                                        src={selectedProducts[line.productId].imageUrl}
                                                                        alt={selectedProducts[line.productId].title}
                                                                        className="w-24 h-24 object-cover rounded"
                                                                        onError={(e) => {
                                                                            e.target.src = '/placeholder-image.jpg';
                                                                        }}
                                                                    />
                                                                    <div className="flex-1">
                                                                        <h5 className="font-semibold text-lg mb-1">
                                                                            {selectedProducts[line.productId].title}
                                                                        </h5>
                                                                        <p className="text-gray-600 text-sm mb-2">
                                                                            {selectedProducts[line.productId].description}
                                                                        </p>
                                                                        <div className="flex gap-4 text-sm">
                                                                <span className="bg-green-100 text-green-800 px-2 py-1 rounded-full">
                                                                    ${selectedProducts[line.productId].price}
                                                                </span>
                                                                            <span className="bg-purple-100 text-purple-800 px-2 py-1 rounded-full">
                                                                    {selectedProducts[line.productId].category}
                                                                </span>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        )}
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
            {activeTab === 'users' && (
                <div className="space-y-6">
                    {/* Formulaire de gestion des utilisateurs */}
                    <form onSubmit={handleUserSubmit} className="bg-white p-6 rounded-lg shadow">
                        <h2 className="text-xl font-semibold mb-4">
                            {userForm.id ? 'Edit User' : 'Add New User'}
                        </h2>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium mb-1">Username</label>
                                <input
                                    type="text"
                                    value={userForm.username}
                                    onChange={(e) => setUserForm(prev => ({ ...prev, username: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Email</label>
                                <input
                                    type="email"
                                    value={userForm.email}
                                    onChange={(e) => setUserForm(prev => ({ ...prev, email: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">First Name</label>
                                <input
                                    type="text"
                                    value={userForm.firstName}
                                    onChange={(e) => setUserForm(prev => ({ ...prev, firstName: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Last Name</label>
                                <input
                                    type="text"
                                    value={userForm.lastName}
                                    onChange={(e) => setUserForm(prev => ({ ...prev, lastName: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium mb-1">Phone Number</label>
                                <input
                                    type="tel"
                                    value={userForm.phoneNumber}
                                    onChange={(e) => setUserForm(prev => ({ ...prev, phoneNumber: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    required
                                />
                            </div>

                            <div className="col-span-2">
                                <label className="block text-sm font-medium mb-1">Address</label>
                                <textarea
                                    value={userForm.address}
                                    onChange={(e) => setUserForm(prev => ({ ...prev, address: e.target.value }))}
                                    className="w-full p-2 border rounded"
                                    rows="2"
                                    required
                                />
                            </div>
                        </div>

                        <div className="mt-4 flex gap-2">
                            <button
                                type="submit"
                                className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
                            >
                                {userForm.id ? 'Update User' : 'Add User'}
                            </button>
                            {userForm.id && (
                                <button
                                    type="button"
                                    onClick={() => {
                                        setUserForm({
                                            id: null,
                                            username: '',
                                            email: '',
                                            firstName: '',
                                            lastName: '',
                                            phoneNumber: '',
                                            address: ''
                                        });
                                    }}
                                    className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                                >
                                    Cancel
                                </button>
                            )}
                        </div>
                    </form>

                    {/* Liste des utilisateurs */}
                    <div className="bg-white p-6 rounded-lg shadow">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-xl font-semibold">Users List</h2>
                            <button
                                onClick={() => handleExportExcel('users')}
                                className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 flex items-center gap-2"
                            >
                                <span>Export Excel</span>
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20"
                                     fill="currentColor">
                                    <path fillRule="evenodd"
                                          d="M6 2a2 2 0 00-2 2v12a2 2 0 002 2h8a2 2 0 002-2V7.414A2 2 0 0015.414 6L12 2.586A2 2 0 0010.586 2H6zm5 6a1 1 0 10-2 0v3.586L7.707 10.293a1 1 0 10-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 11.586V8z"
                                          clipRule="evenodd"/>
                                </svg>
                            </button>
                        </div>
                        <div className="grid gap-4">
                            {users.map(user => (
                                <div key={user.id} className="border rounded p-4 hover:bg-gray-50 transition-colors">
                                    <div className="flex justify-between items-start">
                                        <div className="space-y-2">
                                            <div className="flex items-center gap-2">
                                                <h3 className="font-semibold text-lg">
                                                    {user.firstName} {user.lastName}
                                                </h3>
                                                <span className="text-sm text-gray-500">({user.username})</span>
                                            </div>
                                            <p className="text-gray-600">{user.email}</p>
                                            <p className="text-gray-600">{user.phoneNumber}</p>
                                            <p className="text-gray-600">{user.address}</p>
                                        </div>
                                        <div className="flex gap-2">
                                            <button
                                                onClick={() => handleEditUser(user)}
                                                className="text-blue-600 hover:text-blue-700 px-3 py-1 border rounded"
                                            >
                                                Edit
                                            </button>
                                            <button
                                                onClick={() => handleDeleteUser(user.id)}
                                                className="text-red-600 hover:text-red-700 px-3 py-1 border rounded"
                                            >
                                                Delete
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}
            {activeTab === 'report' && (
                <div className="space-y-6">
                    <div className="flex justify-between items-center">
                        <h2 className="text-xl font-semibold">Monthly Report</h2>
                        <button
                            onClick={fetchReport}
                            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                        >
                            Generate Report
                        </button>
                    </div>

                    {showReport && (
                        <div className="bg-white rounded-lg shadow">
                            <div className="p-4">
                                <button
                                    onClick={() => setShowReport(false)}
                                    className="mb-4 text-gray-600 hover:text-gray-800"
                                >
                                    Close Report
                                </button>
                                <div
                                    className="report-container"
                                    dangerouslySetInnerHTML={{ __html: reportHtml }}
                                />
                            </div>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}