import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Sticky top navigation bar.
 * Shows different links based on user role.
 */
const Navbar = () => {
  const { user, isAuthenticated, logout, role } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">
        🍱 Food<span>Rescue</span>
      </Link>

      <div className="navbar-links">
        <Link to="/">Home</Link>
        <Link to="/about">About</Link>

        {!isAuthenticated ? (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register" style={{ background: 'var(--secondary)', color: 'white', borderRadius: '6px' }}>
              Register
            </Link>
          </>
        ) : (
          <>
            {/* Donor-specific links */}
            {role === 'DONOR' && (
              <>
                <Link to="/donor/dashboard">Dashboard</Link>
                <Link to="/donor/create-donation">Donate Food</Link>
                <Link to="/donor/my-donations">My Donations</Link>
              </>
            )}

            {/* NGO-specific links */}
            {role === 'NGO' && (
              <>
                <Link to="/ngo/dashboard">Dashboard</Link>
                <Link to="/ngo/available-donations">Find Food</Link>
                <Link to="/ngo/my-pickups">My Pickups</Link>
              </>
            )}

            {/* Volunteer-specific links */}
            {role === 'VOLUNTEER' && (
              <>
                <Link to="/volunteer/dashboard">Dashboard</Link>
                <Link to="/volunteer/available-tasks">Find Tasks</Link>
                <Link to="/volunteer/my-tasks">My Tasks</Link>
              </>
            )}

            {/* Admin-specific links */}
            {role === 'ADMIN' && (
              <>
                <Link to="/admin/dashboard">Dashboard</Link>
                <Link to="/admin/users">Users</Link>
                <Link to="/admin/ngo-approvals">NGO Approvals</Link>
              </>
            )}

            <span style={{ color: 'rgba(255,255,255,0.7)', fontSize: 13 }}>
              👤 {user?.fullName}
            </span>
            <button className="btn-logout" onClick={handleLogout}>Logout</button>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
