<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">在线教育平台</h2>

      <el-tabs v-model="activeRole" stretch class="role-tabs">
        <el-tab-pane label="我是学生" name="student"></el-tab-pane>
        <el-tab-pane label="我是老师" name="teacher"></el-tab-pane>
        <el-tab-pane label="管理员" name="admin"></el-tab-pane>
      </el-tabs>

      <el-form :model="form" label-width="0" style="margin-top: 20px;">
        <el-form-item>
          <el-input 
            v-model="form.username" 
            prefix-icon="User" 
            :placeholder="accountPlaceholder" 
            size="large" 
          />
        </el-form-item>

        <el-form-item>
          <el-input v-model="form.password" prefix-icon="Lock" type="password" placeholder="请输入密码" show-password size="large" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="action-btn" size="large" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>

        <div class="toggle-link">
          <el-link type="danger" :underline="false" @click="handleForgotPassword">
            忘记密码?
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue' // 记得引入 computed
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const activeRole = ref('student')

const form = reactive({
  username: '',
  password: ''
})

// 👇 计算属性：动态改变输入框提示
const accountPlaceholder = computed(() => {
  if (activeRole.value === 'student') return '请输入学号'
  if (activeRole.value === 'teacher') return '请输入工号'
  return '请输入管理员账号'
})

// 登录逻辑 (保持不变)
const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入完整')
    return
  }
  loading.value = true

  try {
    const params = new URLSearchParams()
    params.append('username', form.username)
    params.append('password', form.password)
    
    const loginRes = await axios.post('/api/login', params)
    const userInfo = loginRes.data
    const realRole = userInfo.role

    // 检查是否首次登录
    if (userInfo.firstLogin) {
        ElMessage.info('首次登录，请修改您的密码。')
        localStorage.setItem('userRole', realRole) // 即使是首次登录，也要先存好角色信息
        router.push('/change-password')
        return; // 中断后续代码执行
    }

    // 校验角色匹配
    if (realRole !== activeRole.value) {
      ElMessage.error(`登录失败：账号角色不匹配！`)
      loading.value = false
      return
    }

    localStorage.setItem('userRole', realRole) 
    ElMessage.success('登录成功！')
    router.push('/home/courses')

  } catch (error) {
    console.error(error)
    ElMessage.error('账号或密码错误')
  } finally {
    loading.value = false
  }
}

const handleForgotPassword = () => {
  ElMessageBox.alert('请联系系统管理员重置密码。<br>邮箱：admin@edu.com', '忘记密码', { confirmButtonText: '知道了', dangerouslyUseHTMLString: true })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #74ebd5 0%, #ACB6E5 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}
.title { text-align: center; margin-bottom: 20px; color: #333; }
.role-tabs { margin-bottom: 20px; }
.action-btn { width: 100%; }
.toggle-link { text-align: right; margin-top: 10px; }
</style>