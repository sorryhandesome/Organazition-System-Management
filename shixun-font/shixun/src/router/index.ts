import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router';
import UserLogin from '../components/UserLogin.vue';
import TenementRgt from '../components/TenementRgt.vue';
import AllHome from '@/components/AllHome.vue';
import SelfInfo from '@/components/SelfInfo.vue';
import SelfManagement from '@/components/SelfManagement.vue';
import ChangePassword from '@/components/ChangePassword.vue';
import BranchManagement from '@/components/BranchManagement.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: UserLogin
  },
  {
    path: '/register',
    name: 'TenementRgt',
    component: TenementRgt
  },
  {
    path: '/allHome',
    name: 'AllHome',
    component: AllHome,
    children: [
      {
        path: '/branchManagement',
        name: 'BranchManagement',
        component: BranchManagement
      }
      // 其他子路由可以在这里添加
    ]
  },
  {
    path: '/selfInfo',
    name: 'SelfInfo',
    component: SelfInfo
  },
  {
    path: '/selfManagement',
    name: 'SelfManagement',
    component: SelfManagement
  },
  {
    path: '/changePassword',
    name: 'ChangePassword',
    component: ChangePassword
  }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const user = window.sessionStorage.getItem('user');
  if (to.path === '/login' || to.path === '/register') {
    return next();
  }
  if (!user) {
    return next('/login');
  }
  if (to.path === '/' && user) {
    return next('/allHome');
  }
  next();
});

export default router;
