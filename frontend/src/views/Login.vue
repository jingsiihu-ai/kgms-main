<template>
  <div class="login-page">
    <div class="login-card page-card">
      <div class="left-panel">
        <h1>幼儿园资源管理与检索系统</h1>
        <p>支持目录树归档、多条件检索、资源上传、收藏与后台管理。</p>
        <ul>
          <li>目录树 + 标签的资源管理方式</li>
          <li>与手机端共享 users / resources / tags 等基础表</li>
          <li>支持 Word / PDF / PPT / 图片 / 视频</li>
        </ul>
      </div>
      <div class="right-panel">
        <h2>账号登录</h2>
        <el-form ref="form" :model="form" :rules="rules" label-width="70px" @keyup.enter.native="submit">
          <el-form-item label="用户名" prop="username"><el-input v-model="form.username" placeholder="请输入用户名" /></el-form-item>
          <el-form-item label="密码" prop="password"><el-input v-model="form.password" type="password" show-password placeholder="请输入密码" /></el-form-item>
          <el-form-item><el-button type="primary" style="width:100%;" :loading="loading" @click="submit">登录</el-button></el-form-item>
        </el-form>
        <div class="tip">演示账号：admin / teacher1 / care1，密码均为 123456</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginView',
  data() {
    return {
      loading: false,
      form: { username: 'admin', password: '123456' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    };
  },
  methods: {
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return;
        this.loading = true;
        try {
          await this.$store.dispatch('doLogin', this.form);
          this.$router.replace('/dashboard');
        } finally {
          this.loading = false;
        }
      });
    }
  }
};
</script>

<style scoped>
.login-page { min-height:100vh; display:flex; align-items:center; justify-content:center; padding:24px; background:linear-gradient(135deg,#f7efe0,#eef4ff); }
.login-card { width:960px; max-width:100%; display:grid; grid-template-columns:1.1fr 1fr; overflow:hidden; }
.left-panel { background:linear-gradient(160deg,#1e2a3a,#2a5ea7); color:#fff; padding:42px 38px; }
.left-panel h1 { margin:0 0 16px; font-size:30px; line-height:1.3; }
.left-panel p { margin:0 0 18px; opacity:.95; }
.left-panel ul { margin:0; padding-left:18px; line-height:1.9; }
.right-panel { padding:34px; }
.right-panel h2 { margin:0 0 14px; color:#22384f; }
.tip { margin-top:8px; color:#6a7f95; font-size:12px; }
@media (max-width: 900px) { .login-card { grid-template-columns:1fr; } }
</style>
