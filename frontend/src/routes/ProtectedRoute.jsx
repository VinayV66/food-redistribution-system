import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * ProtectedRoute: redirects unauthenticated users to /login.
 * Usage: wrap any route that requires authentication.
 */
export const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) return <div className="loading-screen">Loading...</div>;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  
  return children;
};

/**
 * RoleRoute: restricts access to users with a specific role.
 * Usage: <RoleRoute role="ADMIN"><AdminDashboard /></RoleRoute>
 */
export const RoleRoute = ({ children, role }) => {
  const { isAuthenticated, role: userRole, loading } = useAuth();
  
  if (loading) return <div className="loading-screen">Loading...</div>;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (userRole !== role) return <Navigate to="/unauthorized" replace />;
  
  return children;
};
