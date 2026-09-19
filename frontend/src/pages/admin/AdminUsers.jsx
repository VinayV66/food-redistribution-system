import { useState, useEffect } from 'react';
import { adminApi } from '../../api/apiService';

const AdminUsers = () => {
  const [data, setData]       = useState({ content: [], totalPages: 0 });
  const [page, setPage]       = useState(0);
  const [search, setSearch]   = useState('');
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  const fetchUsers = () => {
    setLoading(true);
    adminApi.getUsers({ page, size: 15, ...(search ? { search } : {}) })
      .then(res => setData(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchUsers(); }, [page, search]);

  const handleBlock = async (id, blocked) => {
    try {
      if (blocked) { await adminApi.unblockUser(id); }
      else         { await adminApi.blockUser(id); }
      setMessage(blocked ? 'User unblocked.' : 'User blocked.');
      fetchUsers();
    } catch (err) {
      setMessage('Action failed: ' + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div className="page">
      <h1 className="page-title">👥 User Management</h1>
      <p className="page-subtitle">Manage all platform users</p>

      {message && <div className="alert alert-info">{message}</div>}

      <div className="filters-bar">
        <input type="text" className="form-control" placeholder="🔍 Search by name or email..."
          style={{ maxWidth: 320 }} value={search}
          onChange={e => { setSearch(e.target.value); setPage(0); }} />
      </div>

      {loading ? (
        <div className="spinner-center"><div className="spinner"></div></div>
      ) : (
        <div className="card">
          <div className="table-wrapper">
            <table>
              <thead>
                <tr><th>#</th><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Status</th><th>Actions</th></tr>
              </thead>
              <tbody>
                {data.content?.map(u => (
                  <tr key={u.id}>
                    <td>{u.id}</td>
                    <td><strong>{u.fullName}</strong></td>
                    <td style={{ fontSize: 13 }}>{u.email}</td>
                    <td style={{ fontSize: 13 }}>{u.phone}</td>
                    <td><span className={`badge badge-${u.role?.toLowerCase()}`}>{u.role}</span></td>
                    <td>
                      {u.blocked
                        ? <span className="badge badge-expired">BLOCKED</span>
                        : <span className="badge badge-available">ACTIVE</span>}
                    </td>
                    <td>
                      {u.role !== 'ADMIN' && (
                        <button
                          className={`btn btn-sm ${u.blocked ? 'btn-success' : 'btn-danger'}`}
                          onClick={() => handleBlock(u.id, u.blocked)}>
                          {u.blocked ? 'Unblock' : 'Block'}
                        </button>
                      )}
                    </td>
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

export default AdminUsers;
