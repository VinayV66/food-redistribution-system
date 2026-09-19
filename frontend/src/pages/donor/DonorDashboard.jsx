import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { donationApi } from '../../api/apiService';
import { useAuth } from '../../context/AuthContext';

const statusBadge = (s) => <span className={`badge badge-${s.toLowerCase()}`}>{s}</span>;

const DonorDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({ total: 0, available: 0, completed: 0, expired: 0 });
  const [recent, setRecent] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await donationApi.getMy({ size: 5 });
        const donations = res.data.content;
        setRecent(donations);
        const all = await donationApi.getMy({ size: 1000 });
        const all_d = all.data.content;
        setStats({
          total: all.data.totalElements,
          available: all_d.filter(d => d.status === 'AVAILABLE').length,
          completed: all_d.filter(d => d.status === 'COMPLETED').length,
          expired: all_d.filter(d => d.status === 'EXPIRED').length,
        });
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  return (
    <div className="page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 28 }}>
        <div>
          <h1 className="page-title">👋 Welcome, {user?.fullName}!</h1>
          <p className="page-subtitle">Donor Dashboard — manage your food donations</p>
        </div>
        <Link to="/donor/create-donation" className="btn btn-primary btn-lg">+ Donate Food</Link>
      </div>

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : (
        <>
          <div className="stats-grid">
            <div className="stat-card"><div className="stat-value">{stats.total}</div><div className="stat-label">Total Donations</div></div>
            <div className="stat-card secondary"><div className="stat-value">{stats.available}</div><div className="stat-label">Available</div></div>
            <div className="stat-card success"><div className="stat-value">{stats.completed}</div><div className="stat-label">Completed</div></div>
            <div className="stat-card danger"><div className="stat-value">{stats.expired}</div><div className="stat-label">Expired</div></div>
          </div>

          <div className="card">
            <div className="card-header">
              <span className="card-title">Recent Donations</span>
              <Link to="/donor/my-donations" className="btn btn-outline btn-sm">View All →</Link>
            </div>
            {recent.length === 0 ? (
              <div style={{ textAlign: 'center', padding: 40, color: '#6c757d' }}>
                <p>No donations yet.</p>
                <Link to="/donor/create-donation" className="btn btn-primary" style={{ marginTop: 16 }}>Make Your First Donation</Link>
              </div>
            ) : (
              <div className="table-wrapper">
                <table>
                  <thead><tr><th>Food Name</th><th>Category</th><th>Quantity</th><th>Expiry</th><th>Status</th><th></th></tr></thead>
                  <tbody>
                    {recent.map(d => (
                      <tr key={d.id}>
                        <td><strong>{d.foodName}</strong></td>
                        <td>{d.foodCategory}</td>
                        <td>{d.quantity} {d.quantityUnit}</td>
                        <td>{new Date(d.expiryTime).toLocaleString()}</td>
                        <td>{statusBadge(d.status)}</td>
                        <td><Link to={`/donor/donations/${d.id}`} className="btn btn-outline btn-sm">View</Link></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default DonorDashboard;
