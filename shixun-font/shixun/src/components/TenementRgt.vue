<template>
  <el-container>
    <el-header>
      <h2>注册租户</h2>
    </el-header>
    <el-main>
      <el-steps :active="activeStep" finish-status="success" style="margin-bottom: 20px;">
        <el-step title="租户信息"></el-step>
        <el-step title="用户信息"></el-step>
      </el-steps>

      <el-form v-if="activeStep === 0" :model="tenantForm" :rules="tenantRules" ref="tenantFormRef" label-width="100px" class="register-form">
        <el-form-item label="租户名称" prop="name">
          <el-input v-model="tenantForm.name"></el-input>
        </el-form-item>
        <el-form-item label="联系人姓名" prop="linkName">
          <el-input v-model="tenantForm.linkName"></el-input>
        </el-form-item>
        <el-form-item label="电话号码" prop="phoneNum">
          <el-input v-model="tenantForm.phoneNum"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input type="email" v-model="tenantForm.email"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmitTenant">下一步</el-button>
        </el-form-item>
      </el-form>

      <el-form v-if="activeStep === 1" :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px" class="register-form">
        <el-form-item label="用户名" prop="name">
          <el-input v-model="userForm.name"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="userForm.password"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="userForm.department"></el-input>
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8088/files/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :on-error="handleAvatarError"
            :before-upload="beforeAvatarUpload"
          >
            <el-button type="primary">上传头像</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmitUser">注册</el-button>
        </el-form-item>
      </el-form>

      <el-alert v-if="error" title="错误" type="error" :description="error" show-icon></el-alert>
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import router from '@/router';

export default defineComponent({
  name: 'TenementRgt',
  setup() {
    const activeStep = ref(0);

    const tenantForm = ref({
      name: '',
      linkName: '',
      phoneNum: '',
      email: '',
      num: 0,
      state: true,
      createTime: '',
    });


    const tenantRules = ref({
      tenementName: [{ required: true, message: '请输入租户名称', trigger: 'blur' }],
      linkName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
      phoneNum: [{ required: true, message: '请输入电话号码', trigger: 'blur' }],
      email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
    });

    const userForm = ref({
      name: '',
      password: '',
      department: '',
      avatar: '',
    });

    const userRules = ref({
      name: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      department: [{ required: true, message: '请输入部门', trigger: 'blur' }],
    });

    const error = ref<string | null>(null);
    const tenantFormRef = ref();
    const userFormRef = ref();
    const formatDateTime = (date: Date): string => {
        return date.toISOString().slice(0, 19).replace('T', ' ');
      };

    let tenantId: number | null = null;

    const handleAvatarSuccess = (response: any) => {
      if (response && response.code === '0') {
        userForm.value.avatar = response.data;
      } else {
        error.value = '上传头像失败';
      }
    };

    const handleAvatarError = (err: any) => {
      if (err instanceof Error) {
        error.value = '上传失败: ' + err.message;
      } else {
        error.value = '上传失败 due to unknown error';
      }
    };

    const beforeAvatarUpload = (file: File) => {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        ElMessage.error('头像图片必须是 JPG 或 PNG 格式!');
      }
      if (!isLt2M) {
        ElMessage.error('头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    };

    const onSubmitTenant = async () => {
      tenantForm.value.createTime = formatDateTime(new Date()); // 当前时间

      (tenantFormRef.value as any).validate(async (valid: boolean) => {
        if (valid) {
          request.post('/tenement/regist', tenantForm.value).then((res: any) => {
            if (res.code === '0') {
              tenantId = res.data;
              alert(tenantId);
              activeStep.value = 1;
              ElMessage.success('租户信息已注册成功，请继续注册用户信息');
            } else {
              error.value = '租户注册失败: ' + res.msg;
            }
          });
        }
      });
    };
    const onSubmitUser = async () => {
      (userFormRef.value as any).validate(async (valid: boolean) => {
            if (valid && tenantId !== null) {
                const userData = {
                  name: userForm.value.name,
                  password: userForm.value.password,
                  department: userForm.value.department,
                  avatar: userForm.value.avatar,
                  tenementId: tenantId,
                  createTime: new Date(),
                };
                request.post('/user/register',userData).then((res:any)=>{
                  if (res.code === '0') {
                  ElMessage.success('用户注册成功!');
                  userForm.value = {
                    name: '',
                    password: '',
                    department: '',
                    avatar: '',
                  };
                  router.replace("/login");
                } else {
                  error.value = res.msg;
                }
          });  
        }
      });
    };

    return {
      activeStep,
      tenantForm,
      tenantRules,
      userForm,
      userRules,
      error,
      tenantFormRef,
      userFormRef,
      handleAvatarSuccess,
      handleAvatarError,
      beforeAvatarUpload,
      onSubmitTenant,
      onSubmitUser,
    };
  },
});
</script>


<style scoped>
.container {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
}
.register-form {
  max-width: 400px;
  margin: 0 auto;
}
</style>

