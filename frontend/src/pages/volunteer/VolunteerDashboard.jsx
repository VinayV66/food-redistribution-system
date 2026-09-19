import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { volunteerApi } from '../../api/apiService';
import { useAuth } from '../../context/AuthContext';

const VolunteerDashboard = () => {
  const { user } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    volunteerApi.getMyTasks({ size: 5 })
      .then(res => setTasks(res.data.content))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const stats = {
    total: tasks.length,
    active: tasks.filter(t => ['ASSIGNED','ACCEPTED','COLLECTED'].includes(t.status)).length,
    delivered: tasks.filter(t => t.status === 'DELIVERED').length,
  };

  return (
    <div className="page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 28 }}>
        <div>
          <h1 className="page-title">👋 Welcome, {user?.fullName}!</h1>
          <p className="page-subtitle">Volunteer Dashboard — pick up and deliver food</p>
        </div>
        <Link to="/volunteer/available-tasks" className="btn btn-primary btn-lg">🔍 Find Tasks</Link>
      </div>

      <div className="stats-grid">
        <div className="stat-card"><div className="stat-value">{stats.total}</div><div className="stat-label">Total Tasks</div></div>
        <div className="stat-card warning"><div className="stat-value">{stats.active}</div><div className="stat-label">Active</div></div>
        <div className="stat-card success"><div className="stat-value">{stats.delivered}</div><div className="stat-label">Delivered</div></div>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title">My Recent Tasks</span>
          <Link to="/volunteer/my-tasks" className="btn btn-outline btn-sm">View All →</Link>
        </div>
        {loading ? (
          <div className="spinner-center"><div className="spinner"></div></div>
        ) : tasks.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 40, color: '#6c757d' }}>
            <p>No tasks yet. Find available pickups!</p>
            <Link to="/volunteer/available-tasks" className="btn btn-primary" style={{ marginTop: 16 }}>Browse Tasks</Link>
          </div>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead><tr><th>Food</th><th>Pickup Address</th><th>NGO</th><th>Status</th></tr></thead>
              <tbody>
                {tasks.map(t => (
                  <tr key={t.id}>
                    <td><strong>{t.donationFoodName}</strong></td>
                    <td style={{ fontSize: 13 }}>{t.pickupAddress}</td>
                    <td>{t.ngoName}</td>
                    <td><span className={`badge badge-${t.status.toLowerCase()}`}>{t.status}</span></td>
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

export default VolunteerDashboard;
