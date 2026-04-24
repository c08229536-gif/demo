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
  /* 高级动态流光渐变背景 */
  background: linear-gradient(-45deg, #1e3c72, #2a5298, #0f2027, #203a43);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  overflow: hidden;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.login-box {
  width: 420px;
  padding: 50px 40px;
  /* 毛玻璃效果核心参数 */
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3);
  /* 优雅的入场动画 */
  animation: fadeInUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

.title { 
  text-align: center; 
  margin-bottom: 30px; 
  color: #ffffff; 
  font-weight: 700;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.role-tabs { margin-bottom: 25px; }

/* 覆盖 Element Plus Tabs 默认样式，适配透明背景 */
:deep(.el-tabs__item) {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
  transition: all 0.3s;
}
:deep(.el-tabs__item.is-active) {
  color: #ffffff;
  font-weight: 700;
}
:deep(.el-tabs__active-bar) {
  background-color: #ffffff;
  height: 3px;
  border-radius: 3px;
}
:deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(255, 255, 255, 0.2);
}

/* 覆盖输入框样式，匹配毛玻璃质感 */
:deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.15) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 10px;
  transition: all 0.3s;
}
:deep(.el-input__wrapper:hover), :deep(.el-input__wrapper.is-focus) {
  background-color: rgba(255, 255, 255, 0.25) !important;
  border-color: rgba(255, 255, 255, 0.4) !important;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1) !important;
}
:deep(.el-input__inner) {
  color: #ffffff !important;
}
:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.6) !important;
}
/* 修改输入框前置图标颜色 */
:deep(.el-input__prefix-inner) {
  color: rgba(255, 255, 255, 0.8) !important;
}

/* 渐变质感登录按钮 */
.action-btn { 
  width: 100%; 
  border-radius: 10px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border: none;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 1px;
  height: 48px;
  transition: transform 0.2s, box-shadow 0.2s;
  color: #fff;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(118, 75, 162, 0.4);
}
.action-btn:active {
  transform: translateY(0);
}

.toggle-link { text-align: right; margin-top: 15px; }
.toggle-link .el-link {
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  transition: color 0.3s;
}
.toggle-link .el-link:hover {
  color: #ffffff;
}
</style>