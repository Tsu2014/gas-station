<template>
  <div class="login-page">
    <el-card class="login-box">
      <div class="login-title">
        <i class="el-icon-s-shop"></i>
        <span>智慧加油站运营后台</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" prefix-icon="el-icon-user" placeholder="admin"/>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" prefix-icon="el-icon-lock" placeholder="123456"
                    @keyup.enter.native="handleLogin"/>
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form>
      <div class="tips">默认账号：admin / manager / staff，密码均为 123456</div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      form: { username: 'admin', password: '123456' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.loading = true
        this.$store.dispatch('user/login', this.form).then(() => {
          this.$message.success('登录成功')
          this.$router.push('/')
        }).finally(() => {
          this.loading = false
        })
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2b5876 0%, #4e4376 100%);
}

.login-box {
  width: 420px;
  padding: 10px 20px 20px;
}

.login-title {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.login-btn {
  width: 100%;
}

.tips {
  margin-top: 12px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}
</style>
