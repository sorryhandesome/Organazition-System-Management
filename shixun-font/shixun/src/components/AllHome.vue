<template>
  <el-container>
    <!-- 顶部导航栏 -->
    <el-header style="background-color: #409EFF; color: white; display: flex; justify-content: space-between; align-items: center;">
      <div style="font-weight: bold; font-size: 24px;">测盟汇</div>
      <div>
        <el-button icon="el-icon-user" @click="goToSelfInfo" circle></el-button>
        <el-button icon="el-icon-switch-button" @click="logout" circle style="margin-left: 10px;">退出</el-button>
      </div>
    </el-header>

    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px" style="background-color: #D3DCE6;">
        <el-menu
          default-active="1"
          class="el-menu-vertical-demo"
          background-color="#D3DCE6"
          text-color="#333"
          active-text-color="#409EFF"
          @select="handleSelect"
        >
          <el-menu-item index="branchManagement">
            <template v-slot:title>
              <i class="el-icon-s-management"></i>
              <span>部门管理</span>
            </template>
          </el-menu-item>
          <!-- 其他菜单项可以在这里添加 -->
        </el-menu>
      </el-aside>

      <!-- 主要内容 -->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { useRouter } from 'vue-router';

export default {
  setup() {
    const router = useRouter();

    const handleSelect = (index) => {
      router.push(`/${index}`);
    };

    const goToSelfInfo = () => {
      router.push('/selfInfo');
    };

    const logout = () => {
      // 处理退出登录逻辑，例如清除用户信息、跳转到登录页等
      // 假设使用localStorage存储用户信息
      localStorage.removeItem('user');
      router.push('/login');
    };

    return {
      handleSelect,
      goToSelfInfo,
      logout
    };
  }
};
</script>

<style scoped>
.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: #409EFF;
  color: white;
}

.el-aside {
  background-color: #D3DCE6;
}

.el-main {
  padding: 20px;
}
</style>
