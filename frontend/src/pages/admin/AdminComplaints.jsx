import { useState, useEffect } from 'react';
import { adminApi } from '../../api/apiService';

const AdminComplaints = () => {
  const [data, setData]       = useState({ content: [], totalPages: 0 });
  const [page, setPage]       = useState(0);
  const [status, setStatus]   = useState('OPEN');
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  const fetchComplaints = () => {
    setLoading(true);
    adminApi.getComplaints({ page, size: 10, ...(status ? { status } : {}) })
      .then(res => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchComplaints(); }, [page, status]);

  const handleResolve = async (id) => {
    const note = prompt('Enter resolution note:');
    if (note === null) return;
    try {
      await adminApi.resolveComplaint(id, note);
      setMessage('✅ Complaint resolved.');
      fetchComplaints();
    } catch (err) {
      setMessage('Failed: ' + (err.response?.data?.message || err.message));
    }
  };

  const statuses = ['', 'OPEN', 'UNDER_REVIEW', 'RESOLVED', 'DISMISSED'];

  return (
    <div className="page">
      <h1 className="page-title">⚠️ Complaints</h1>
      <p className="page-subtitle">Review and resolve user complaints</p>

      {message && <div className="alert alert-info">{message}</div>}

      <div className="filters-bar">
        <select className="form-control" style={{ width: 'auto' }} value={status}
          onChange={e => { setStatus(e.target.value); setPage(0); }}>
          {statuses.map(s => <option key={s} value={s}>{s || 'All'}</option>)}
        </select>
      </div>

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : (
        <div className="card">
          {data.content.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 40, color: '#6c757d' }}>No complaints found.</div>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr><th>#</th><th>Type</th><th>Reporter</th><th>Description</th><th>Status</th><th>Actions</th></tr>
                </thead>
                <tbody>
                  {data.content.map(c => (
                    <tr key={c.id}>
                      <td>{c.id}</td>
                      <td>{c.type?.replace('_',' ')}</td>
                      <td>{c.reporter?.fullName}</td>
                      <td style={{ fontSize: 13, maxWidth: 200 }}>{c.description}</td>
                      <td><span className={`badge badge-${c.status?.toLowerCase()}`}>{c.status}</span></td>
                      <td>
                        {c.status === 'OPEN' && (
                          <button className="btn btn-primary btn-sm" onClick={() => handleResolve(c.id)}>
                            Resolve
                          </button>
                        )}
                        {c.resolutionNote && (
                          <span style={{ fontSize: 12, color: '#6c757d' }}>📝 {c.resolutionNote}</span>
                        )}
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

export default AdminComplaints;
