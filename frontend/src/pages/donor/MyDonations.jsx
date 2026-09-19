import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { donationApi } from '../../api/apiService';

const statusBadge = (s) => <span className={`badge badge-${s.toLowerCase()}`}>{s}</span>;

const MyDonations = () => {
  const [data, setData]       = useState({ content: [], totalPages: 0 });
  const [page, setPage]       = useState(0);
  const [status, setStatus]   = useState('');
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  const fetchDonations = async () => {
    setLoading(true);
    try {
      const params = { page, size: 10, ...(status ? { status } : {}) };
      const res = await donationApi.getMy(params);
      setData(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchDonations(); }, [page, status]);

  const handleCancel = async (id) => {
    if (!confirm('Are you sure you want to cancel this donation?')) return;
    try {
      await donationApi.cancel(id);
      setMessage('Donation cancelled.');
      fetchDonations();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Failed to cancel.');
    }
  };

  const statuses = ['', 'AVAILABLE', 'ACCEPTED', 'PICKUP_ASSIGNED', 'COLLECTED', 'COMPLETED', 'EXPIRED', 'CANCELLED'];

  return (
    <div className="page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 className="page-title">My Donations</h1>
        <Link to="/donor/create-donation" className="btn btn-primary">+ New Donation</Link>
      </div>

      {message && <div className="alert alert-info">{message}</div>}

      {/* Filters */}
      <div className="filters-bar">
        <label style={{ fontSize: 14, fontWeight: 500 }}>Filter by status:</label>
        <select className="form-control" style={{ width: 'auto' }} value={status} onChange={e => { setStatus(e.target.value); setPage(0); }}>
          {statuses.map(s => <option key={s} value={s}>{s || 'All'}</option>)}
        </select>
      </div>

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : data.content.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: 48 }}>
          <p style={{ color: '#6c757d', marginBottom: 16 }}>No donations found.</p>
          <Link to="/donor/create-donation" className="btn btn-primary">Make First Donation</Link>
        </div>
      ) : (
        <div className="card">
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Food Name</th><th>Category</th><th>Quantity</th>
                  <th>Expiry</th><th>Status</th><th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {data.content.map(d => (
                  <tr key={d.id}>
                    <td><strong>{d.foodName}</strong></td>
                    <td>{d.foodCategory?.replace('_',' ')}</td>
                    <td>{d.quantity} {d.quantityUnit}</td>
                    <td style={{ fontSize: 12 }}>{new Date(d.expiryTime).toLocaleString()}</td>
                    <td>{statusBadge(d.status)}</td>
                    <td>
                      <div style={{ display: 'flex', gap: 6 }}>
                        <Link to={`/donor/donations/${d.id}`} className="btn btn-outline btn-sm">View</Link>
                        {d.status === 'AVAILABLE' && (
                          <button className="btn btn-danger btn-sm" onClick={() => handleCancel(d.id)}>Cancel</button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {/* Pagination */}
          {data.totalPages > 1 && (
            <div className="pagination">
              {Array.from({ length: data.totalPages }, (_, i) => (
                <button key={i} className={`page-btn${page === i ? ' active' : ''}`} onClick={() => setPage(i)}>{i + 1}</button>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default MyDonations;
