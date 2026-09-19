import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ProtectedRoute, RoleRoute } from './routes/ProtectedRoute';

// Public pages
import Home from './pages/public/Home';
import Login from './pages/public/Login';
import Register from './pages/public/Register';
import About from './pages/public/About';

// Donor pages
import DonorDashboard from './pages/donor/DonorDashboard';
import CreateDonation from './pages/donor/CreateDonation';
import MyDonations from './pages/donor/MyDonations';
import DonationDetail from './pages/donor/DonationDetail';

// NGO pages
import NgoDashboard from './pages/ngo/NgoDashboard';
import AvailableDonations from './pages/ngo/AvailableDonations';
import NgoMyPickups from './pages/ngo/NgoMyPickups';

// Volunteer pages
import VolunteerDashboard from './pages/volunteer/VolunteerDashboard';
import AvailableTasks from './pages/volunteer/AvailableTasks';
import MyTasks from './pages/volunteer/MyTasks';

// Admin pages
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminUsers from './pages/admin/AdminUsers';
import AdminDonations from './pages/admin/AdminDonations';
import AdminNgoApprovals from './pages/admin/AdminNgoApprovals';
import AdminComplaints from './pages/admin/AdminComplaints';
import AdminAuditLogs from './pages/admin/AdminAuditLogs';

import Navbar from './components/Navbar';
import './styles/global.css';

// Smart redirect after login based on role
const RoleBasedRedirect = () => {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" />;
  switch (user?.role) {
    case 'ADMIN':     return <Navigate to="/admin/dashboard" />;
    case 'DONOR':     return <Navigate to="/donor/dashboard" />;
    case 'NGO':       return <Navigate to="/ngo/dashboard" />;
    case 'VOLUNTEER': return <Navigate to="/volunteer/dashboard" />;
    default:          return <Navigate to="/" />;
  }
};

function App() {
  return (
    <Router>
      <AuthProvider>
        <Navbar />
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<Home />} />
          <Route path="/about" element={<About />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={<RoleBasedRedirect />} />
          <Route path="/unauthorized" element={<div className="page-center"><h2>Access Denied</h2><p>You don't have permission to view this page.</p></div>} />

          {/* Donor routes */}
          <Route path="/donor/dashboard" element={<RoleRoute role="DONOR"><DonorDashboard /></RoleRoute>} />
          <Route path="/donor/create-donation" element={<RoleRoute role="DONOR"><CreateDonation /></RoleRoute>} />
          <Route path="/donor/my-donations" element={<RoleRoute role="DONOR"><MyDonations /></RoleRoute>} />
          <Route path="/donor/donations/:id" element={<RoleRoute role="DONOR"><DonationDetail /></RoleRoute>} />

          {/* NGO routes */}
          <Route path="/ngo/dashboard" element={<RoleRoute role="NGO"><NgoDashboard /></RoleRoute>} />
          <Route path="/ngo/available-donations" element={<RoleRoute role="NGO"><AvailableDonations /></RoleRoute>} />
          <Route path="/ngo/my-pickups" element={<RoleRoute role="NGO"><NgoMyPickups /></RoleRoute>} />

          {/* Volunteer routes */}
          <Route path="/volunteer/dashboard" element={<RoleRoute role="VOLUNTEER"><VolunteerDashboard /></RoleRoute>} />
          <Route path="/volunteer/available-tasks" element={<RoleRoute role="VOLUNTEER"><AvailableTasks /></RoleRoute>} />
          <Route path="/volunteer/my-tasks" element={<RoleRoute role="VOLUNTEER"><MyTasks /></RoleRoute>} />

          {/* Admin routes */}
          <Route path="/admin/dashboard" element={<RoleRoute role="ADMIN"><AdminDashboard /></RoleRoute>} />
          <Route path="/admin/users" element={<RoleRoute role="ADMIN"><AdminUsers /></RoleRoute>} />
          <Route path="/admin/donations" element={<RoleRoute role="ADMIN"><AdminDonations /></RoleRoute>} />
          <Route path="/admin/ngo-approvals" element={<RoleRoute role="ADMIN"><AdminNgoApprovals /></RoleRoute>} />
          <Route path="/admin/complaints" element={<RoleRoute role="ADMIN"><AdminComplaints /></RoleRoute>} />
          <Route path="/admin/audit-logs" element={<RoleRoute role="ADMIN"><AdminAuditLogs /></RoleRoute>} />

          {/* 404 */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
