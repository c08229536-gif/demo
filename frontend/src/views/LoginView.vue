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
          <el-input v-model="form.username" prefix-icon="User" placeholder="请输入账号" size="large" />
        </el-form-item>

        <el-form-item>
          <el-input v-model="form.password" prefix-icon="Lock" type="password" placeholder="请输入密码" show-password size="large" />
        </el-form-item>

        <el-form-item v-if="!isLoginMode">
           <el-input v-model="form.realName" prefix-icon="Postcard" placeholder="请输入真实姓名" size="large" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="action-btn" size="large" @click="handleSubmit" :loading="loading">
            {{ isLoginMode ? '登录' : '注册' }}
          </el-button>
        </el-form-item>

        <div class="toggle-link" v-if="activeRole !== 'admin'">
          <el-link type="primary" @click="toggleMode">
            {{ isLoginMode ? '没有账号？去注册' : '已有账号？去登录' }}
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Postcard } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const isLoginMode = ref(true) 
const loading = ref(false)
const activeRole = ref('student') // 默认选中学生标签

const form = reactive({
  username: '',
  password: '',
  realName: ''
})

const toggleMode = () => {
  isLoginMode.value = !isLoginMode.value
  form.username = ''
  form.password = ''
  form.realName = ''
}

const handleSubmit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true

  try {
    if (isLoginMode.value) {
      // === 登录逻辑 ===
      const params = new URLSearchParams()
      params.append('username', form.username)
      params.append('password', form.password)
      
      // 1. 先验证密码
      await axios.post('/api/login', params)
      
      // 2. 密码对后，查一下这个人的真实身份
      const meRes = await axios.get('/api/auth/me')
      const realRole = meRes.data.role // 数据库里的角色

      // 3. 🚨 关键校验：如果你在“教师”页签，登录了“学生”账号，报错！
      if (realRole !== activeRole.value) {
        ElMessage.error(`登录失败：该账号不是${getRoleName(activeRole.value)}账号！`)
        loading.value = false
        return
      }

      // 4. 身份匹配成功，存起来
      localStorage.setItem('userRole', realRole) 
      
      ElMessage.success('登录成功！')
      router.push('/home/courses')

    } else {
      // === 注册逻辑 ===
      // 注册时，直接使用当前选中的 tab 作为角色
      const res = await axios.post('/api/auth/register', {
        username: form.username,
        password: form.password,
        realName: form.realName || '新用户',
        role: activeRole.value // 👈 关键：注册为当前选中的角色
      })

      if (res.data.includes('成功')) {
        ElMessage.success(res.data)
        toggleMode()
      } else {
        ElMessage.error(res.data)
      }
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}

// 辅助函数：把英文角色转成中文提示
const getRoleName = (role) => {
  if (role === 'student') return '学生'
  if (role === 'teacher') return '教师'
  if (role === 'admin') return '管理员'
  return ''
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
.title {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
.role-tabs {
  margin-bottom: 20px;
}
.action-btn {
  width: 100%;
}
.toggle-link {
  text-align: right;
  margin-top: 10px;
}
</style>