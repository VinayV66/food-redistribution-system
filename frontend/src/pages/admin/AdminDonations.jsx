import { useState, useEffect } from 'react';
import { donationApi } from '../../api/apiService';

const statusBadge = (s) => <span className={`badge badge-${s?.toLowerCase()}`}>{s}</span>;

const AdminDonations = () => {
  const [data, setData]     = useState({ content: [], totalPages: 0 });
  const [page, setPage]     = useState(0);
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    const params = { page, size: 15, ...(status ? { status } : {}) };
    donationApi.getAll(params)
      .then(res => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [page, status]);

  const statuses = ['','AVAILABLE','REQUESTED','ACCEPTED','PICKUP_ASSIGNED','COLLECTED','DELIVERED','COMPLETED','EXPIRED','CANCELLED'];

  return (
    <div className="page">
      <h1 className="page-title">🍱 All Donations</h1>
      <p className="page-subtitle">View all food donations on the platform</p>

      <div className="filters-bar">
        <label style={{ fontSize: 14, fontWeight: 500 }}>Filter by status:</label>
        <select className="form-control" style={{ width: 'auto' }} value={status}
          onChange={e => { setStatus(e.target.value); setPage(0); }}>
          {statuses.map(s => <option key={s} value={s}>{s || 'All'}</option>)}
        </select>
      </div>

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : (
        <div className="card">
          <div className="table-wrapper">
            <table>
              <thead>
                <tr><th>#</th><th>Food</th><th>Category</th><th>Donor</th><th>Qty</th><th>Expiry</th><th>Status</th></tr>
              </thead>
              <tbody>
                {data.content?.map(d => (
                  <tr key={d.id}>
                    <td>{d.id}</td>
                    <td><strong>{d.foodName}</strong></td>
                    <td>{d.foodCategory?.replace('_',' ')}</td>
                    <td>{d.donorName}</td>
                    <td>{d.quantity} {d.quantityUnit}</td>
                    <td style={{ fontSize: 12 }}>{new Date(d.expiryTime).toLocaleString()}</td>
                    <td>{statusBadge(d.status)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
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

export default AdminDonations;
