import { createContext, useContext, useReducer } from 'react'

const CartContext = createContext()

const cartReducer = (state, action) => {
    switch (action.type) {
        case 'ADD_TO_CART':
            const lineCommand = {
                id: Math.floor(Math.random() * 10000), // ID temporaire
                quantity: action.payload.quantity,
                productId: action.payload.id,
                commandId: null, // Sera défini par le backend
                // Informations additionnelles pour l'affichage
                title: action.payload.title,
                price: action.payload.price,
                img_url: action.payload.imageUrl
            }

            const existingItem = state.items.find(item => item.productId === action.payload.id)
            if (existingItem) {
                return {
                    ...state,
                    items: state.items.map(item =>
                        item.productId === action.payload.id
                            ? { ...item, quantity: item.quantity + action.payload.quantity }
                            : item
                    )
                }
            }
            return {
                ...state,
                items: [...state.items, lineCommand]
            }

        case 'REMOVE_FROM_CART':
            return {
                ...state,
                items: state.items.filter(item => item.productId !== action.payload)
            }

        case 'UPDATE_QUANTITY':
            return {
                ...state,
                items: state.items.map(item =>
                    item.productId === action.payload.id
                        ? { ...item, quantity: action.payload.quantity }
                        : item
                )
            }

        case 'CLEAR_CART':
            return {
                ...state,
                items: []
            }

        default:
            return state
    }
}

export function CartProvider({ children }) {
    const [state, dispatch] = useReducer(cartReducer, { items: [] })

    const addToCart = (product, quantity = 1) => {
        dispatch({ type: 'ADD_TO_CART', payload: { ...product, quantity } })
    }

    const removeFromCart = (productId) => {
        dispatch({ type: 'REMOVE_FROM_CART', payload: productId })
    }

    const updateQuantity = (productId, quantity) => {
        dispatch({ type: 'UPDATE_QUANTITY', payload: { id: productId, quantity } })
    }

    const clearCart = () => {
        dispatch({ type: 'CLEAR_CART' })
    }

    const getCartTotal = () => {
        return state.items.reduce((total, item) => total + item.price * item.quantity, 0)
    }

    return (
        <CartContext.Provider value={{
            cart: state.items,
            addToCart,
            removeFromCart,
            updateQuantity,
            clearCart,
            getCartTotal
        }}>
            {children}
        </CartContext.Provider>
    )
}

export const useCart = () => useContext(CartContext)