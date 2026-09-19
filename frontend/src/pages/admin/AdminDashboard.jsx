import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { adminApi } from '../../api/apiService';

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi.getDashboard()
      .then(res => setStats(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="spinner-center"><div className="spinner"></div></div>;
  if (!stats) return null;

  const links = [
    { path: '/admin/users', label: '👥 Manage Users', desc: `${stats.totalUsers} total users` },
    { path: '/admin/ngo-approvals', label: '🏢 NGO Approvals', desc: `${stats.pendingNgoApprovals} pending` },
    { path: '/admin/donations', label: '🍱 All Donations', desc: `${stats.totalDonations} total` },
    { path: '/admin/complaints', label: '⚠️ Complaints', desc: 'Review & resolve' },
    { path: '/admin/audit-logs', label: '📋 Audit Logs', desc: 'System activity' },
  ];

  return (
    <div className="page">
      <h1 className="page-title">⚙️ Admin Dashboard</h1>
      <p className="page-subtitle">Platform overview and management</p>

      <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))' }}>
        <div className="stat-card"><div className="stat-value">{stats.totalUsers}</div><div className="stat-label">Total Users</div></div>
        <div className="stat-card secondary"><div className="stat-value">{stats.totalDonors}</div><div className="stat-label">Donors</div></div>
        <div className="stat-card"><div className="stat-value">{stats.totalNgos}</div><div className="stat-label">NGOs</div></div>
        <div className="stat-card warning"><div className="stat-value">{stats.totalVolunteers}</div><div className="stat-label">Volunteers</div></div>
        <div className="stat-card success"><div className="stat-value">{stats.completedDonations}</div><div className="stat-label">Completed</div></div>
        <div className="stat-card"><div className="stat-value">{stats.availableDonations}</div><div className="stat-label">Available</div></div>
        <div className="stat-card danger"><div className="stat-value">{stats.expiredDonations}</div><div className="stat-label">Expired</div></div>
        <div className="stat-card success">
          <div className="stat-value">{stats.totalFoodRescuedKg?.toFixed(1)}</div>
          <div className="stat-label">kg Food Rescued</div>
        </div>
      </div>

      {stats.pendingNgoApprovals > 0 && (
        <div className="alert alert-warning">
          ⚠️ <strong>{stats.pendingNgoApprovals}</strong> NGO(s) are waiting for approval.{' '}
          <Link to="/admin/ngo-approvals" style={{ color: 'var(--warning)', fontWeight: 600 }}>Review Now →</Link>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 16 }}>
        {links.map(l => (
          <Link key={l.path} to={l.path} className="card" style={{ textDecoration: 'none', color: 'inherit', display: 'block' }}>
            <div style={{ fontSize: 20, fontWeight: 600, marginBottom: 6 }}>{l.label}</div>
            <div style={{ color: '#6c757d', fontSize: 14 }}>{l.desc}</div>
          </Link>
        ))}
      </div>
    </div>
  );
};

export default AdminDashboard;
