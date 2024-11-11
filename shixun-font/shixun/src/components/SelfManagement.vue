<template>
  <el-container>
    <el-header>
      <h2>个人资料管理</h2>
    </el-header>
    <el-main>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="self-management-form">
        <el-form-item label="昵称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="电话号码" prop="phoneNum">
          <el-input v-if="identity === 'admin'" v-model="form.phoneNum"></el-input>
          <el-input v-else v-model="form.tenement.phoneNum"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-if="identity === 'admin'" v-model="form.email"></el-input>
          <el-input v-else v-model="form.tenement.email"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="form.department"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">提交</el-button>
        </el-form-item>
      </el-form>
    </el-main>
  </el-container>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

export default {
  name: 'SelfManagement',
  setup() {
    const identity = ref(window.sessionStorage.getItem('identity'));
    const form = reactive({
      name: '',
      phoneNum: '',
      email: '',
      department: '',
      tenement: {
        email: '',
        phoneNum: ''
      }
    });

    const rules = reactive({
      name: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
      department: [{ required: true, message: '请输入部门', trigger: 'blur' }],
    });

    const formRef = ref(null);
    const router = useRouter();

    const loadProfile = () => {
      const storedData = JSON.parse(window.sessionStorage.getItem('user') || '{}');
      Object.assign(form, storedData);
    };

    const onSubmit = () => {
      if (formRef.value) {
        formRef.value.validate((valid) => {
          if (valid) {
            const url = `/${identity.value === 'admin' ? 'admin' : 'user'}/update`;
            request.put(url, form).then((res) => {
              if (res.code === '0') {
                ElMessage.success('资料更新成功');
                window.sessionStorage.setItem('user', JSON.stringify(form));
                router.push('/selfInfo'); // 跳转到个人资料页面
              } else {
                ElMessage.error('资料更新失败: ' + res.msg);
              }
            });
          }
        });
      }
    };

    onMounted(loadProfile);

    return {
      form,
      rules,
      formRef,
      onSubmit,
      identity,
    };
  }
};
</script>

<style>
.self-management-form {
  max-width: 600px;
  margin: 0 auto;
}
</style>
