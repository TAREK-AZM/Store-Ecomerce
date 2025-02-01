import { useState } from 'react'
import { Link } from 'react-router-dom'
import { products, categories } from '../data/mockData'
import { useCart } from '../context/CartContext'
import toast from 'react-hot-toast'

export default function Home() {
    const [searchTerm, setSearchTerm] = useState('')
    const [selectedCategory, setSelectedCategory] = useState('')
    const [priceRange, setPriceRange] = useState({ min: '', max: '' })
    const { addToCart } = useCart()

    const filteredProducts = products.filter(product => {
        const matchesSearch = product.title.toLowerCase().includes(searchTerm.toLowerCase())
        const matchesCategory = !selectedCategory || product.category === selectedCategory
        const matchesPrice = (!priceRange.min || product.price >= Number(priceRange.min)) &&
            (!priceRange.max || product.price <= Number(priceRange.max))
        return matchesSearch && matchesCategory && matchesPrice
    })

    const handleAddToCart = (product) => {
        addToCart(product)
        toast.success('Added to cart!')
    }

    return (
        <div className="space-y-6">
            <div className="flex flex-col md:flex-row gap-4">
                <input
                    type="text"
                    placeholder="Search products..."
                    className="flex-1 p-2 border rounded"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />

                <select
                    className="p-2 border rounded"
                    value={selectedCategory}
                    onChange={(e) => setSelectedCategory(e.target.value)}
                >
                    <option value="">All Categories</option>
                    {categories.map(category => (
                        <option key={category.id} value={category.title}>
                            {category.title}
                        </option>
                    ))}
                </select>

                <div className="flex gap-2">
                    <input
                        type="number"
                        placeholder="Min Price"
                        className="w-24 p-2 border rounded"
                        value={priceRange.min}
                        onChange={(e) => setPriceRange(prev => ({ ...prev, min: e.target.value }))}
                    />
                    <input
                        type="number"
                        placeholder="Max Price"
                        className="w-24 p-2 border rounded"
                        value={priceRange.max}
                        onChange={(e) => setPriceRange(prev => ({ ...prev, max: e.target.value }))}
                    />
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {filteredProducts.map(product => (
                    <div key={product.id} className="bg-white rounded-lg shadow-md overflow-hidden">
                        <Link to={`/product/${product.id}`}>
                            <img
                                src={product.img_url}
                                alt={product.title}
                                className="w-full h-48 object-cover"
                            />
                        </Link>
                        <div className="p-4">
                            <Link to={`/product/${product.id}`}>
                                <h3 className="text-lg font-semibold">{product.title}</h3>
                            </Link>
                            <p className="text-gray-600">{product.category}</p>
                            <div className="mt-2 flex justify-between items-center">
                                <span className="text-lg font-bold">${product.price}</span>
                                <button
                                    onClick={() => handleAddToCart(product)}
                                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                                >
                                    Add to Cart
                                </button>
                            </div>
                        </div>NON
                    </div>
                ))}
            </div>
        </div>
    )
}