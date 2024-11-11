<template>
  <div class="self-info-container">
    <el-header class="header-bar">
      <div class="header-title">个人信息管理</div>
      <div class="header-actions">
        <el-button @click="goToEditProfile">修改个人资料</el-button>
        <el-button @click="goToChangePassword">修改密码</el-button>
      </div>
    </el-header>
    <el-card class="box-card" shadow="hover">
      <template v-slot:header>
        <div class="clearfix">
          <span>个人信息</span>
        </div>
      </template>
      <div v-if="identity === 'admin'">
        <div class="info-item">
          <el-avatar :src="avatarUrl" size="large"></el-avatar>
        </div>
        <div class="info-item">
          <span class="label">用户名:</span>
          <span>{{ form.name }}</span>
        </div>
        <div class="info-item">
          <span class="label">电话号码:</span>
          <span>{{ form.phoneNum }}</span>
        </div>
        <div class="info-item">
          <span class="label">邮箱:</span>
          <span>{{ form.email }}</span>
        </div>
        <div class="info-item">
          <span class="label">部门:</span>
          <span>{{ form.department }}</span>
        </div>
        <div class="info-item">
          <span class="label">创建时间:</span>
          <span>{{ formatDate(form.createTime) }}</span>
        </div>
      </div>
      <div v-else>
        <div class="info-item">
          <el-avatar :src="avatarUrl" size="large"></el-avatar>
        </div>
        <div class="info-item">
          <span class="label">用户名:</span>
          <span>{{ form.name }}</span>
        </div>
        <div class="info-item">
          <span class="label">租户名称:</span>
          <span>{{ form.tenement.tenementName }}</span>
        </div>
        <div class="info-item">
          <span class="label">租户联系人:</span>
          <span>{{ form.tenement.linkName }}</span>
        </div>
        <div class="info-item">
          <span class="label">租户电话:</span>
          <span>{{ form.tenement.phoneNum }}</span>
        </div>
        <div class="info-item">
          <span class="label">租户邮箱:</span>
          <span>{{ form.tenement.email }}</span>
        </div>
        <div class="info-item">
          <span class="label">部门:</span>
          <span>{{ form.department }}</span>
        </div>
        <div class="info-item">
          <span class="label">创建时间:</span>
          <span>{{ formatDate(form.createTime) }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'SelfInfo',
  setup() {
    const identity = ref(window.sessionStorage.getItem('identity').replace(/"/g, ''));
    const form = reactive({
      name: '',
      phoneNum: '',
      email: '',
      department: '',
      createTime: '',
      avatar: '',
      tenement: {
        tenementName: '',
        linkName: '',
        phoneNum: '',
        email: ''
      }
    }); 
    const avatarUrl = ref('');

    const loadProfile = () => {
      const storedData = JSON.parse(window.sessionStorage.getItem('user') || '{}');
      if (storedData.avatar) {
        avatarUrl.value = `http://localhost:8088/static/image/${storedData.avatar}`;
      }
      Object.assign(form, storedData);
    };

    const formatDate = (date) => {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
    };

    const router = useRouter();

    const goToEditProfile = () => {
      router.push('/selfManagement');
    };

    const goToChangePassword = () => {
      router.push('/changePassword');
    };

    onMounted(loadProfile);

    return {
      form,
      avatarUrl,
      identity,
      formatDate,
      goToEditProfile,
      goToChangePassword
    };
  }
};
</script>

<style scoped>
.self-info-container {
  width: 60%;
  margin: 20px auto;
}
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #dcdcdc;
}
.header-title {
  font-size: 20px;
  font-weight: bold;
}
.header-actions {
  display: flex;
  gap: 10px;
}
.info-item {
  display: flex;
  justify-content: space-between;
  margin: 10px 0;
}
.label {
  font-weight: bold;
}
</style>
