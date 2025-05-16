import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import toast from 'react-hot-toast'

const Checkout = () => {
    const navigate = useNavigate()
    const { cart, getCartTotal, clearCart } = useCart()
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        firstName: '',
        lastName: '',
        phoneNumber: '',
        address: ''
    })

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Formatons les données exactement comme attendu par le backend
        const orderData = {
            user: {
                id: 0, // ID fixé à 0 comme dans votre exemple
                username: formData.username,
                email: formData.email,
                firstName: formData.firstName,
                lastName: formData.lastName,
                phoneNumber: formData.phoneNumber,
                address: formData.address
            },
            lineCommands: cart.map(item => ({
                id: Math.floor(Math.random() * 10000), // Générons un ID aléatoire comme dans votre exemple
                quantity: parseInt(item.quantity), // Assurons-nous que quantity est un nombre
                productId: parseInt(item.productId) // Assurons-nous que productId est un nombre
            }))
        };

        try {
            const response = await fetch('http://127.0.0.1:8080/api/commands/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(orderData)
            });

            // Vérifions la réponse avant de la traiter
            if (!response.ok) {
                const errorText = await response.text();
                console.error('Erreur serveur:', errorText);
                throw new Error(`Error creating order: ${errorText}`);
            }

            // Si tout va bien, nous traitons la réponse
            const responseData = await response.blob();
            const pdfUrl = window.URL.createObjectURL(responseData);

            // Téléchargeons le PDF
            const link = document.createElement('a');
            link.href = pdfUrl;
            link.download = 'order.pdf';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

            // Nettoyons le tout
            window.URL.revokeObjectURL(pdfUrl);
            clearCart();
            toast.success('Order placed successfully!');
            navigate('/');

        } catch (error) {
            console.error('Error details:', error);
            toast.error(`Error creating order: ${error.message}`);
        }
    };

    const handleChange = (e) => {
        setFormData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }))
    }

    if (cart.length === 0) {
        return (
            <div className="text-center py-12">
                <h2 className="text-2xl font-bold mb-4">Your cart is empty</h2>
                <button
                    onClick={() => navigate('/')}
                    className="text-blue-600 hover:underline"
                >
                    Continue Shopping
                </button>
            </div>
        )
    }

    return (
        <div className="max-w-4xl mx-auto p-4">
            <h1 className="text-2xl font-bold mb-6">Checkout</h1>

            <div className="grid md:grid-cols-2 gap-8">
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium mb-1">Username</label>
                        <input
                            type="text"
                            name="username"
                            required
                            value={formData.username}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Email</label>
                        <input
                            type="email"
                            name="email"
                            required
                            value={formData.email}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">First Name</label>
                        <input
                            type="text"
                            name="firstName"
                            required
                            value={formData.firstName}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Last Name</label>
                        <input
                            type="text"
                            name="lastName"
                            required
                            value={formData.lastName}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Phone Number</label>
                        <input
                            type="tel"
                            name="phoneNumber"
                            required
                            value={formData.phoneNumber}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Address</label>
                        <textarea
                            name="address"
                            required
                            value={formData.address}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                            rows="3"
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700"
                    >
                        Place Order
                    </button>
                </form>

                <div className="bg-gray-50 p-6 rounded-lg">
                    <h2 className="text-lg font-semibold mb-4">Order Summary</h2>

                    <div className="space-y-2">
                        {cart.map(item => (
                            <div key={item.id} className="flex justify-between">
                                <span>{item.title} x {item.quantity}</span>
                                <span>${(item.price * item.quantity).toFixed(2)}</span>
                            </div>
                        ))}
                    </div>

                    <div className="mt-4 pt-4 border-t">
                        <div className="flex justify-between font-bold">
                            <span>Total</span>
                            <span>${getCartTotal().toFixed(2)}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Checkout