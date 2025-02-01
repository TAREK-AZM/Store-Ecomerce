import { Link } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { TrashIcon } from '@heroicons/react/24/outline'

export default function Cart() {
    const { cart, removeFromCart, updateQuantity, getCartTotal } = useCart()

    if (cart.length === 0) {
        return (
            <div className="text-center py-12">
                <h2 className="text-2xl font-bold mb-4">Your cart is empty</h2>
                <Link to="/" className="text-blue-600 hover:underline">
                    Continue Shopping
                </Link>
            </div>
        )
    }

    return (
        <div className="max-w-4xl mx-auto">
            <h1 className="text-2xl font-bold mb-6">Shopping Cart</h1>

            <div className="space-y-4">
                {cart.map(item => (
                    <div key={item.id} className="flex items-center gap-4 bg-white p-4 rounded-lg shadow">
                        <img
                            src={item.img_url}
                            alt={item.title}
                            className="w-24 h-24 object-cover rounded"
                        />

                        <div className="flex-1">
                            <h3 className="font-semibold">{item.title}</h3>
                            <p className="text-gray-600">${item.price}</p>
                        </div>

                        <div className="flex items-center gap-4">
                            <input
                                type="number"
                                min="1"
                                value={item.quantity}
                                onChange={(e) => updateQuantity(item.id, Number(e.target.value))}
                                className="w-20 p-2 border rounded"
                            />

                            <button
                                onClick={() => removeFromCart(item.id)}
                                className="text-red-600"
                            >
                                <TrashIcon className="h-5 w-5" />
                            </button>
                        </div>

                        <div className="text-right">
                            <p className="font-semibold">${(item.price * item.quantity).toFixed(2)}</p>
                        </div>
                    </div>
                ))}
            </div>

            <div className="mt-8 flex justify-between items-center">
                <p className="text-xl font-bold">
                    Total: ${getCartTotal().toFixed(2)}
                </p>
                <Link
                    to="/checkout"
                    className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700"
                >
                    Proceed to Checkout
                </Link>
            </div>
        </div>
    )
}