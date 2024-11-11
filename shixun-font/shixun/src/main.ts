// src/main.ts
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import request from '@/utils/request';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'; // 使用最新的样式导入路径
const app = createApp(App);

// 挂载 Axios 实例到全局
app.config.globalProperties.$request = request;

app.use(router);
app.use(ElementPlus);

app.mount('#app');
