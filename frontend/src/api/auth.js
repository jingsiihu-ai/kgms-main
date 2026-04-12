import request from '@/utils/request';

export function login(data) {
  return request.post('/api/auth/login', data);
}

export function getMe() {
  return request.get('/api/auth/me');
}

export function logout() {
  return request.post('/api/auth/logout');
}
