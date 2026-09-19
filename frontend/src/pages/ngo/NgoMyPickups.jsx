import { useState, useEffect } from 'react';
import { ngoApi, pickupApi } from '../../api/apiService';

const NgoMyPickups = () => {
  const [data, setData]       = useState({ content: [], totalPages: 0 });
  const [page, setPage]       = useState(0);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  const fetchPickups = () => {
    setLoading(true);
    ngoApi.getMyPickups({ page, size: 10 })
      .then(res => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchPickups(); }, [page]);

  const handleCancel = async (id) => {
    if (!confirm('Cancel this pickup? The donation will become available again.')) return;
    try {
      await pickupApi.cancel(id);
      setMessage('Pickup cancelled. Donation is available again.');
      fetchPickups();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Failed to cancel.');
    }
  };

  return (
    <div className="page">
      <h1 className="page-title">My Pickups</h1>
      <p className="page-subtitle">Track all your accepted donation pickups</p>

      {message && <div className="alert alert-info">{message}</div>}

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : (
        <div className="card">
          {data.content.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 40, color: '#6c757d' }}>
              <p>No pickups yet. Accept a donation to get started!</p>
            </div>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>#</th><th>Food</th><th>Pickup Address</th>
                    <th>Volunteer</th><th>Status</th><th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.content.map(p => (
                    <tr key={p.id}>
                      <td>{p.id}</td>
                      <td><strong>{p.donationFoodName}</strong></td>
                      <td style={{ fontSize: 13 }}>{p.pickupAddress}</td>
                      <td>{p.volunteerName || <span style={{ color: '#6c757d' }}>Unassigned</span>}</td>
                      <td><span className={`badge badge-${p.status.toLowerCase()}`}>{p.status}</span></td>
                      <td>
                        {(p.status === 'PENDING' || p.status === 'ASSIGNED') && (
                          <button className="btn btn-danger btn-sm" onClick={() => handleCancel(p.id)}>Cancel</button>
                        )}
                        {p.deliveredAt && <span style={{ fontSize: 12, color: 'var(--success)' }}>✅ {new Date(p.deliveredAt).toLocaleDateString()}</span>}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
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

export default NgoMyPickups;
