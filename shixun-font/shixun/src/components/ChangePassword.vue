<template>
  <div>
    <el-form :model="form" ref="formRef" label-width="100px">
      <el-form-item label="当前密码" prop="currentPassword">
        <el-input type="password" v-model="form.currentPassword"></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input type="password" v-model="form.newPassword"></el-input>
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmNewPassword">
        <el-input type="password" v-model="form.confirmNewPassword"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">修改密码</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';
import { useRouter } from 'vue-router';

export default {
  name: 'ChangePassword',
  setup() {
    const form = reactive({
      currentPassword: '',
      newPassword: '',
      confirmNewPassword: ''
    });

    const formRef = ref(null);
    const user = JSON.parse(window.sessionStorage.getItem('user') || '{}');
    const identity = window.sessionStorage.getItem('identity');

    const onSubmit = () => {
      if (form.newPassword !== form.confirmNewPassword) {
        ElMessage.error('新密码和确认密码不一致');
        return;
      }
 

      if (formRef.value) {
        formRef.value.validate((valid) => {
          if (valid) {
            const url = identity === 'admin' ? '/admin/changePassword' : '/user/changePassword';
            request.put(url, {
              userId: user.id,
              currentPassword: form.currentPassword,
              newPassword: form.newPassword,
              confirmNewPassword:form.confirmNewPassword
            }).then((res) => {
              console.log('ChangePassword response:', res); // 调试信息
              if (res.code === '0') {
                ElMessage.success('密码修改成功');
               
              } else {
                ElMessage.error('密码修改失败: 原密码不正确');
              }
            }).catch((error) => {
              ElMessage.error('密码修改失败: ' + error.message);
            });
          }
        });
      }
    };

    return {
      form,
      formRef,
      onSubmit
    };
  }
};
</script>
