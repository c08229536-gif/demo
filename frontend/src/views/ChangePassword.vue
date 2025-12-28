<template>
  <div class="change-password-container">
    <div class="change-password-box">
      <h2 class="title">修改初始密码</h2>
      <p class="subtitle">为了您的账户安全，首次登录请修改密码。</p>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="margin-top: 30px;">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码"></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入新密码"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="loading" style="width: 100%;">确认修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (form.confirmPassword !== '') {
      formRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致!'))
  } else {
    callback()
  }
}

const rules = reactive({
  newPassword: [{ validator: validatePass, trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
})

const handleChangePassword = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await axios.post('/api/auth/change-password', { newPassword: form.newPassword })
        ElMessage.success('密码修改成功！请重新登录。')
        // 清除可能存在的旧登录信息并跳转到登录页
        localStorage.removeItem('userRole')
        window.location.href = '/' // 强制刷新跳转
      } catch (error) {
        ElMessage.error(error.response?.data || '密码修改失败')
      } finally {
        loading.value = false
      }
    } else {
      return false
    }
  })
}

</script>

<style scoped>
.change-password-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f0f2f5;
}

.change-password-box {
  width: 450px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 10px;
}

.subtitle {
  text-align: center;
  color: #888;
  margin-bottom: 30px;
}
</style>