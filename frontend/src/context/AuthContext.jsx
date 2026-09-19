import { createContext, useContext, useState, useEffect } from 'react';
import { authApi, userApi } from '../api/apiService';

// Create the Auth Context
const AuthContext = createContext(null);

/**
 * AuthProvider wraps the entire app and provides:
 *  - user: the logged-in user object (or null)
 *  - token: the JWT token
 *  - login(), register(), logout() functions
 *  - isAuthenticated, role
 */
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  // On app load: if a token is stored, fetch the user profile
  useEffect(() => {
    const initAuth = async () => {
      const savedToken = localStorage.getItem('token');
      if (savedToken) {
        try {
          const savedUser = JSON.parse(localStorage.getItem('user') || 'null');
          setUser(savedUser);
          setToken(savedToken);
        } catch {
          localStorage.clear();
        }
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  const login = async (email, password) => {
    const response = await authApi.login({ email, password });
    const data = response.data;
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(data));
    setToken(data.token);
    setUser(data);
    return data;
  };

  const register = async (formData) => {
    const response = await authApi.register(formData);
    const data = response.data;
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(data));
    setToken(data.token);
    setUser(data);
    return data;
  };

  const logout = () => {
    localStorage.clear();
    setToken(null);
    setUser(null);
  };

  const isAuthenticated = !!token && !!user;
  const role = user?.role;

  return (
    <AuthContext.Provider value={{
      user, token, loading, isAuthenticated, role,
      login, register, logout
    }}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook — use this in any component to access auth
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
