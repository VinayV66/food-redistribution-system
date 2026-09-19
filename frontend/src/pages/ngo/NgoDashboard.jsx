import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ngoApi } from '../../api/apiService';
import { useAuth } from '../../context/AuthContext';

const NgoDashboard = () => {
  const { user } = useAuth();
  const [pickups, setPickups] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    ngoApi.getMyPickups({ size: 5 })
      .then(res => setPickups(res.data.content))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const stats = {
    total: pickups.length,
    pending: pickups.filter(p => p.status === 'PENDING').length,
    delivered: pickups.filter(p => p.status === 'DELIVERED').length,
  };

  return (
    <div className="page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 28 }}>
        <div>
          <h1 className="page-title">👋 Welcome, {user?.fullName}!</h1>
          <p className="page-subtitle">NGO Dashboard — find and accept food donations</p>
        </div>
        <Link to="/ngo/available-donations" className="btn btn-primary btn-lg">🔍 Find Food</Link>
      </div>

      <div className="stats-grid">
        <div className="stat-card"><div className="stat-value">{stats.total}</div><div className="stat-label">Total Pickups</div></div>
        <div className="stat-card warning"><div className="stat-value">{stats.pending}</div><div className="stat-label">Pending</div></div>
        <div className="stat-card success"><div className="stat-value">{stats.delivered}</div><div className="stat-label">Delivered</div></div>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title">Recent Pickups</span>
          <Link to="/ngo/my-pickups" className="btn btn-outline btn-sm">View All →</Link>
        </div>
        {loading ? (
          <div className="spinner-center"><div className="spinner"></div></div>
        ) : pickups.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 40, color: '#6c757d' }}>
            <p>No pickups yet. Accept a donation to get started!</p>
            <Link to="/ngo/available-donations" className="btn btn-primary" style={{ marginTop: 16 }}>Browse Donations</Link>
          </div>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead><tr><th>Food</th><th>Donor</th><th>Status</th><th>Scheduled</th></tr></thead>
              <tbody>
                {pickups.map(p => (
                  <tr key={p.id}>
                    <td><strong>{p.donationFoodName}</strong></td>
                    <td>{p.pickupAddress}</td>
                    <td><span className={`badge badge-${p.status.toLowerCase()}`}>{p.status}</span></td>
                    <td style={{ fontSize: 12 }}>{p.scheduledPickupTime ? new Date(p.scheduledPickupTime).toLocaleString() : '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default NgoDashboard;
