import request from '@/utils/request';

export function listUsers(keyword) {
  return request.get('/api/users', { params: { keyword } });
}

export function listRoles() {
  return request.get('/api/roles');
}

export function createUser(data) {
  return request.post('/api/users', data);
}

export function updateUser(id, data) {
  return request.put(`/api/users/${id}`, data);
}

export function createTag(data) {
  return request.post('/api/tags', data);
}

export function updateTag(id, data) {
  return request.put(`/api/tags/${id}`, data);
}

export function deleteTag(id) {
  return request.delete(`/api/tags/${id}`);
}

export function listCategories(includeDisabled = true) {
  return request.get('/api/categories/tree', { params: { includeDisabled } });
}

export function createCategory(data) {
  return request.post('/api/categories', data);
}

export function updateCategory(id, data) {
  return request.put(`/api/categories/${id}`, data);
}

export function deleteCategory(id) {
  return request.delete(`/api/categories/${id}`);
}
