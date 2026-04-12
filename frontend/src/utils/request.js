import axios from 'axios';
import { Message } from 'element-ui';

const instance = axios.create({
  timeout: 60000
});

instance.interceptors.request.use(config => {
  const token = localStorage.getItem('kgms_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

instance.interceptors.response.use(
  response => {
    const data = response.data;
    if (data && typeof data.code !== 'undefined') {
      if (data.code === 0) {
        return data.data;
      }
      Message.error(data.message || '请求失败');
      return Promise.reject(new Error(data.message || '请求失败'));
    }
    return response.data;
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('kgms_token');
      window.location.href = '/login';
    }
    Message.error(error.message || '网络异常');
    return Promise.reject(error);
  }
);

export default instance;
