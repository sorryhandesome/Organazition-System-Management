import axios, { AxiosInstance } from 'axios';

const request: AxiosInstance = axios.create({
  baseURL: "http://localhost:8088", // 确保这里的 baseURL 是正确的
  timeout: 5000,
});

request.interceptors.request.use(
  (config) => {
    const user = JSON.parse(window.sessionStorage.getItem('access-user') || '{}');
    if (user && user.token) {
      config.headers['token'] = user.token;
    }
    return config;
  },
  (error) => {
    console.info("error: ");
    console.info(error);
    return Promise.reject(error);
  }
);

request.interceptors.response.use(
  (response) => {
    let res = response.data;
    if (response.config.responseType === 'blob') {
      return res;
    }
    if (typeof res === 'string') {
      res = res ? JSON.parse(res) : res;
    }
    return res;
  },
  (error) => {
    console.log('err' + error); // for debug
    return Promise.reject(error);
  }
);

export default request;
