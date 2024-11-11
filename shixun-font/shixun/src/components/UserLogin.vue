<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" class="login-form">
        <el-form-item label="用户名" prop="name">
          <el-input v-model="form.name" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="身份" prop="identity">
          <el-select v-model="form.identity" placeholder="请选择身份">
            <el-option label="超级管理员" value="admin"></el-option>
            <el-option label="企业租户" value="user"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button class="button1" style="width: 45%"  @click="login" :disabled="!isFormValid">登录</el-button>
          <el-button class="button2" style="width: 45%"  @click="regist" >注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import 'element-plus/dist/index.css';
import request from '@/utils/request';

export default defineComponent({
  name: 'UserLogin',
  setup() {
    const form = reactive({
      name: '',
      password: '',
      identity: ''
    });

    const rules = reactive({
      name: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ],
      identity: [
        { required: true, message: '请选择身份', trigger: 'blur' }
      ]
    });

    const formRef = ref();
    const router = useRouter();
``
    const isFormValid = computed(() => {
      const { name, password, identity } = form;
      return Boolean(name && password && identity);
    });

    const login = () => {
      if (formRef.value) {
        formRef.value.validate((valid: boolean) => {
          if (valid) {
            request.post(`/${form.identity}/login`, form).then((res: any) => {
              if (res.code === '0') {
                ElMessage({
                  message: '登陆成功',
                  type: 'success'
                });
                window.sessionStorage.setItem('user', JSON.stringify(res.data));
                window.sessionStorage.setItem('identity', form.identity);
                router.replace({ path: '/allHome' });
              } else {
                ElMessage({
                  message: '无此用户',
                  type: 'error'
                });
              }
            });
          }
        });
      }
    };
    const regist =() =>{
      router.replace({path:'/register'})
    }

    return {
      form,
      rules,
      formRef,
      isFormValid,
      login,
      regist
    };
  }
});
</script>



<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    position: fixed;
    height: 100%;
    width: 100%;
    top: 0;
    left: 0;
    background: linear-gradient(
        
            135deg,
            hsl(170deg, 80%, 70%),
            hsl(190deg, 80%, 70%),
            hsl(250deg, 80%, 70%),
            hsl(320deg, 80%, 70%),
            hsl(320deg, 80%, 70%),
            hsl(250deg, 80%, 70%),
            hsl(190deg, 80%, 70%),
            hsl(190deg, 80%, 70%),
            hsl(170deg, 80%, 70%)
    );
    background-size: 600%;
    animation: myanimation 15s linear infinite;
}

@keyframes myanimation {
    0% {
        background-position: 0 0;
    }
    100% {
        background-position: 100% 100%;
    }
}

.login-card {
  width: 100%;
  max-width: 400px; 
  padding: 20px;
}

.login-form {
  width: 100%;
}
.button1{
  float: inline-start;
}
.button2{
  float: inline-end;
}
</style>
