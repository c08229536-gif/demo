<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="220px" class="aside-menu">
        <div class="logo">🎓 在线教育平台</div>
        <el-menu
          active-text-color="#ffd04b"
          background-color="#545c64"
          class="el-menu-vertical-demo"
          default-active="/home/courses" 
          text-color="#fff"
          router
        >
          <el-menu-item index="/home/courses">
            <el-icon><Reading /></el-icon>
            <span>课程中心</span>
          </el-menu-item>
          <el-menu-item index="/home/my-courses">
            <el-icon><DataLine /></el-icon>
            <span>学习进度</span>
          </el-menu-item>
          <el-menu-item index="/home/my-assignments">
            <el-icon><EditPen /></el-icon>
            <span>我的作业</span>
          </el-menu-item>
          <el-menu-item index="/home/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="/home/grade" v-if="userRole === 'teacher' || userRole === 'admin'">
            <el-icon><Monitor /></el-icon>
            <span>教师批改</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="main-header">
          <div class="header-left">
            <span>欢迎回来，{{ realName || '同学' }}</span>
          </div>
          <div class="header-right">
            <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
          </div>
        </el-header>

        <el-main>
          <RouterView />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue' // 必须导入 ref 和 onMounted
import { Reading, DataLine, EditPen, User, Monitor } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()

// 定义响应式变量，用来存用户信息
const userRole = ref('')
const realName = ref('')

// 获取当前用户信息 (和 ProfileView 里一样的逻辑)
const fetchUserInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    // 把后台返回的角色赋值给变量，前端菜单就会自动刷新
    userRole.value = res.data.role 
    realName.value = res.data.realName
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

// 页面一加载，就去调接口
onMounted(() => {
  fetchUserInfo()
})

// 退出登录逻辑
const handleLogout = async () => {
  try {
    await axios.post('/api/logout')
    ElMessage.success('已退出登录')
    router.push('/')
  } catch (error) {
    router.push('/')
  }
}
</script>

<style scoped>
.common-layout, .el-container {
  height: 100vh; /* 全屏高度 */
}

.aside-menu {
  background-color: #545c64;
  color: white;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  background-color: #434a50;
}

.main-header {
  background-color: #fff;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between; /* 左右撑开 */
  align-items: center;
}

.el-menu {
  border-right: none; /* 去掉菜单右边的白线 */
}
</style>