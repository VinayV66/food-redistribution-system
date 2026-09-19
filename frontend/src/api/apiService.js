import api from './axios';

// Auth API calls
export const authApi = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

// User API calls
export const userApi = {
  getMe: () => api.get('/users/me'),
  getById: (id) => api.get(`/users/${id}`),
};

// Donation API calls
export const donationApi = {
  getAll: (params) => api.get('/donations', { params }),
  getById: (id) => api.get(`/donations/${id}`),
  getMy: (params) => api.get('/donations/my', { params }),
  create: (data) => api.post('/donations', data),
  update: (id, data) => api.put(`/donations/${id}`, data),
  cancel: (id) => api.delete(`/donations/${id}`),
};

// NGO API calls
export const ngoApi = {
  getAll: () => api.get('/ngo'),
  getAvailableDonations: (params) => api.get('/ngo/available-donations', { params }),
  acceptDonation: (donationId) => api.post(`/ngo/accept-donation/${donationId}`),
  getMyPickups: (params) => api.get('/ngo/my-pickups', { params }),
};

// Volunteer API calls
export const volunteerApi = {
  getAvailableTasks: (params) => api.get('/volunteer/tasks', { params }),
  getMyTasks: (params) => api.get('/volunteer/my-tasks', { params }),
  acceptTask: (id) => api.post(`/volunteer/tasks/${id}/accept`),
  collectFood: (id) => api.post(`/volunteer/tasks/${id}/collect`),
  deliverFood: (id) => api.post(`/volunteer/tasks/${id}/deliver`),
};

// Pickup API calls
export const pickupApi = {
  getById: (id) => api.get(`/pickups/${id}`),
  cancel: (id) => api.put(`/pickups/${id}/cancel`),
  assignVolunteer: (id, volunteerId) => api.post(`/pickups/${id}/assign`, null, { params: { volunteerId } }),
};

// Admin API calls
export const adminApi = {
  getDashboard: () => api.get('/admin/dashboard'),
  getUsers: (params) => api.get('/admin/users', { params }),
  blockUser: (id) => api.put(`/admin/users/${id}/block`),
  unblockUser: (id) => api.put(`/admin/users/${id}/unblock`),
  getPendingNgos: (params) => api.get('/admin/ngo-approvals', { params }),
  approveNgo: (userId) => api.put(`/admin/ngos/${userId}/approve`),
  rejectNgo: (userId, reason) => api.put(`/admin/ngos/${userId}/reject`, { reason }),
  getComplaints: (params) => api.get('/admin/complaints', { params }),
  resolveComplaint: (id, note) => api.put(`/admin/complaints/${id}/resolve`, { note }),
  getAuditLogs: (params) => api.get('/admin/audit-logs', { params }),
};

// Notification API calls
export const notificationApi = {
  getAll: (params) => api.get('/notifications', { params }),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  markAllRead: () => api.post('/notifications/mark-all-read'),
};

// Complaint API calls
export const complaintApi = {
  file: (data) => api.post('/complaints', data),
  getMy: (params) => api.get('/complaints/my', { params }),
};
