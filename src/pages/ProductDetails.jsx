import { useState } from 'react'
import { useParams } from 'react-router-dom'
import { products } from '../data/mockData'
import { useCart } from '../context/CartContext'
import toast from 'react-hot-toast'

export default function ProductDetails() {
    const { id } = useParams()
    const [quantity, setQuantity] = useState(1)
    const { addToCart } = useCart()

    const product = products.find(p => p.id === Number(id))

    if (!product) {
        return <div>Product not found</div>
    }

    const handleAddToCart = () => {
        addToCart(product, quantity)
        toast.success('Added to cart!')
    }

    return (
        <div className="max-w-4xl mx-auto">
            <div className="grid md:grid-cols-2 gap-8">
                <div>
                    <img
                        src={product.img_url}
                        alt={product.title}
                        className="w-full rounded-lg"
                    />
                </div>

                <div className="space-y-4">
                    <h1 className="text-3xl font-bold">{product.title}</h1>
                    <p className="text-gray-600">{product.description}</p>
                    <p className="text-2xl font-bold">${product.price}</p>
                    <p className="text-sm text-gray-500">
                        {product.quantity} items in stock
                    </p>

                    <div className="space-y-4">
                        <div className="flex items-center gap-4">
                            <label className="text-sm font-medium">Quantity:</label>
                            <input
                                type="number"
                                min="1"
                                max={product.quantity}
                                value={quantity}
                                onChange={(e) => setQuantity(Number(e.target.value))}
                                className="w-20 p-2 border rounded"
                            />
                        </div>

                        <button
                            onClick={handleAddToCart}
                            className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700"
                        >
                            Add to Cart
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}