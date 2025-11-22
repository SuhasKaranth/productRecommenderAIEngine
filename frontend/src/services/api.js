import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const stagingApi = {
  // Get all staging products
  getAllProducts: (pendingOnly = false) =>
    api.get(`/api/admin/staging?pendingOnly=${pendingOnly}`),

  // Get single staging product
  getProduct: (id) =>
    api.get(`/api/admin/staging/${id}`),

  // Update staging product
  updateProduct: (id, data) =>
    api.put(`/api/admin/staging/${id}`, data),

  // Approve single product
  approveProduct: (id, reviewedBy, reviewNotes) =>
    api.post(`/api/admin/staging/${id}/approve`, { reviewedBy, reviewNotes }),

  // Bulk approve products
  bulkApprove: (productIds, reviewedBy, reviewNotes) =>
    api.post('/api/admin/staging/bulk-approve', { productIds, reviewedBy, reviewNotes }),

  // Reject product
  rejectProduct: (id, reviewedBy, reviewNotes) =>
    api.post(`/api/admin/staging/${id}/reject`, { reviewedBy, reviewNotes }),

  // Delete product
  deleteProduct: (id) =>
    api.delete(`/api/admin/staging/${id}`),

  // Get statistics
  getStats: () =>
    api.get('/api/admin/staging/stats'),
};

export const scraperApi = {
  // Trigger scraping
  triggerScrape: (websiteId) =>
    api.post(`http://localhost:8081/api/scraper/trigger/${websiteId}`),

  // Get job status
  getJobStatus: (jobId) =>
    api.get(`http://localhost:8081/api/scraper/status/${jobId}`),

  // Get all sources
  getSources: () =>
    api.get('http://localhost:8081/api/scraper/sources'),

  // Get history
  getHistory: (websiteId) =>
    api.get(`http://localhost:8081/api/scraper/history/${websiteId}`),
};

export default api;
