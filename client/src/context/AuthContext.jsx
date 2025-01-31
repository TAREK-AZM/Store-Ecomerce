import { createContext, useContext, useState } from 'react'

const AuthContext = createContext()

export function AuthProvider({ children }) {
    const [isAdmin, setIsAdmin] = useState(false)

    const login = (username, password) => {
        // In a real app, this would make an API call
        if (username === 'admin' && password === 'admin123') {
            setIsAdmin(true)
            return true
        }
        return false
    }

    const logout = () => {
        setIsAdmin(false)
    }

    return (
        <AuthContext.Provider value={{ isAdmin, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext)