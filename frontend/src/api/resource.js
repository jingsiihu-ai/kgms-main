import axios from 'axios';
import request from '@/utils/request';

export function listTags() {
  return request.get('/api/tags');
}

export function listCategoryTree(includeDisabled = false) {
  return request.get('/api/categories/tree', { params: { includeDisabled } });
}

export function uploadResources(formData, onUploadProgress) {
  return request.post('/api/resources/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress
  });
}

export function searchResources(payload) {
  return request.post('/api/resources/search', payload);
}

export function getResourceDetail(id) {
  return request.get(`/api/resources/${id}`);
}

export function getRecent(limit = 8) {
  return request.get('/api/resources/recent', { params: { limit } });
}

export function getHotTags(limit = 10) {
  return request.get('/api/resources/hot-tags', { params: { limit } });
}

export function toggleFavorite(id, favorite) {
  return favorite ? request.post(`/api/resources/${id}/favorite`) : request.delete(`/api/resources/${id}/favorite`);
}

export function getMyFavorites(pageNo = 1, pageSize = 10) {
  return request.get('/api/users/me/favorites', { params: { pageNo, pageSize } });
}

export function deleteResource(id) {
  return request.delete(`/api/resources/${id}`);
}

export function updateResource(id, data) {
  return request.put(`/api/resources/${id}`, data);
}

export async function downloadResourceFile(id, fallbackName = 'download-file') {
  const token = localStorage.getItem('kgms_token');

  const response = await axios.get(`/api/resources/${id}/download`, {
    responseType: 'blob',
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  });

  const blob = new Blob([response.data]);
  const disposition = response.headers['content-disposition'] || '';
  let fileName = fallbackName;

  const utf8Match = disposition.match(/filename\*=UTF-8''([^;]+)/i);
  const normalMatch = disposition.match(/filename="?([^"]+)"?/i);

  if (utf8Match && utf8Match[1]) {
    fileName = decodeURIComponent(utf8Match[1]);
  } else if (normalMatch && normalMatch[1]) {
    fileName = decodeURIComponent(normalMatch[1]);
  }

  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
}