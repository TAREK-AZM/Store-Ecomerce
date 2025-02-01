import { Link } from 'react-router-dom'
import { ShoppingCartIcon, UserIcon } from '@heroicons/react/24/outline'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
    const { cart } = useCart()
    const { isAdmin, logout } = useAuth()

    return (
        <nav className="bg-white shadow-lg">
            <div className="container mx-auto px-4">
                <div className="flex justify-between items-center h-16">
                    <Link to="/" className="text-xl font-bold">
                        E-Commerce
                    </Link>

                    <div className="flex items-center gap-4">
                        <Link to="/cart" className="relative">
                            <ShoppingCartIcon className="h-6 w-6" />
                            {cart.length > 0 && (
                                <span className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs">
                  {cart.length}
                </span>
                            )}
                        </Link>

                        {isAdmin ? (
                            <div className="flex items-center gap-4">
                                <Link to="/admin/panel" className="text-sm">
                                    Admin Panel
                                </Link>
                                <button
                                    onClick={logout}
                                    className="text-sm text-red-600"
                                >
                                    Logout
                                </button>
                            </div>
                        ) : (
                            <Link to="/admin/login">
                                <UserIcon className="h-6 w-6" />
                            </Link>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    )
}