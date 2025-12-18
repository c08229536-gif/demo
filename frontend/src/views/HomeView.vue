<template>
  <div class="common-layout">
    <el-container direction="horizontal" style="height: 100vh;">
      <el-aside width="200px" class="aside">
        <div class="logo">在线教育平台</div>
        <el-menu :default-active="$route.path" router class="el-menu-vertical" background-color="#304156" text-color="#fff">
          <el-menu-item index="/home/profile"><el-icon><User /></el-icon><span>个人中心</span></el-menu-item>
          <el-menu-item index="/home/courses"><el-icon><Reading /></el-icon><span>课程中心</span></el-menu-item>
          
          <el-menu-item index="/home/my-courses" v-if="userRole === 'student'"><el-icon><Collection /></el-icon><span>我的课程</span></el-menu-item>
          <el-menu-item index="/home/my-assignment" v-if="userRole === 'student'"><el-icon><EditPen /></el-icon><span>我的作业</span></el-menu-item>
          <el-menu-item index="/home/feedback" v-if="userRole === 'student' || userRole === 'teacher'"><el-icon><ChatLineSquare /></el-icon><span>意见反馈</span></el-menu-item>
          
          <el-menu-item index="/home/grade-assignment" v-if="userRole === 'teacher'"><el-icon><Edit /></el-icon><span>作业批改</span></el-menu-item>
          
          <el-menu-item index="/home/user-manage" v-if="userRole === 'admin'"><el-icon><UserFilled /></el-icon><span>用户管理</span></el-menu-item>
          <el-menu-item index="/home/admin-audit" v-if="userRole === 'admin'"><el-icon><List /></el-icon><span>课程审核</span></el-menu-item>
          <el-menu-item index="/home/admin-feedback" v-if="userRole === 'admin'"><el-icon><Service /></el-icon><span>反馈处理</span></el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="breadcrumb">
            </div>
          
          <div class="header-right">
            <el-popover placement="bottom" :width="300" trigger="click">
              <template #reference>
                <div class="bell-wrapper">
                  <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="item">
                    <el-icon size="22" style="cursor: pointer; color: #666;"><Bell /></el-icon>
                  </el-badge>
                </div>
              </template>
              
              <div class="msg-list">
                <h4 style="margin: 0 0 10px 0; border-bottom: 1px solid #eee; padding-bottom: 5px;">系统消息</h4>
                <el-empty v-if="messages.length === 0" description="暂无消息" :image-size="50" />
                <div v-else class="msg-scroll">
                  <div v-for="msg in messages" :key="msg.id" class="msg-item" @click="readMessage(msg)">
                    <div class="msg-title">
                      <span v-if="msg.isRead===0" class="dot">●</span> {{ msg.title }}
                    </div>
                    <div class="msg-content">{{ msg.content }}</div>
                    <div class="msg-time">{{ msg.createTime ? msg.createTime.substring(5, 16).replace('T', ' ') : '' }}</div>
                  </div>
                </div>
              </div>
            </el-popover>

            <span style="margin: 0 15px;">你好，{{ realName }}</span>
            <el-button type="danger" size="small" @click="logout">退出</el-button>
          </div>
        </el-header>
        
        <el-main style="background: #f0f2f5;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
// 👇 引入需要的图标
import { User, Reading, Collection, EditPen, Edit, UserFilled, List, ChatLineSquare, Service, Bell } from '@element-plus/icons-vue'

const router = useRouter()
const userRole = ref('')
const realName = ref('')
const unreadCount = ref(0)
const messages = ref([])

const fetchUserInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    userRole.value = res.data.role
    realName.value = res.data.realName
  } catch (error) {
    router.push('/')
  }
}

// 👇 新增：获取消息
const fetchMessages = async () => {
  try {
    const res = await axios.get('/api/message/my')
    messages.value = res.data
    unreadCount.value = messages.value.filter(m => m.isRead === 0).length
  } catch(e) {}
}

// 👇 新增：点击已读
const readMessage = async (msg) => {
  if (msg.isRead === 0) {
    await axios.post(`/api/message/read/${msg.id}`)
    msg.isRead = 1
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
}

const logout = () => {
  localStorage.removeItem('token')
  router.push('/')
}

onMounted(() => {
  fetchUserInfo()
  fetchMessages() // 加载消息
})
</script>

<style scoped>
.common-layout { height: 100vh; }
.aside { background-color: #304156; color: white; }
.logo { height: 60px; line-height: 60px; text-align: center; font-size: 20px; font-weight: bold; background: #2b3649; }
.header { background: #fff; border-bottom: 1px solid #ddd; display: flex; align-items: center; justify-content: space-between; }
.header-right { display: flex; align-items: center; }

/* 消息样式 */
.bell-wrapper { margin-right: 20px; display: flex; align-items: center; height: 100%; }
.msg-scroll { max-height: 300px; overflow-y: auto; }
.msg-item { padding: 10px; border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: background 0.2s; }
.msg-item:hover { background: #f9f9f9; }
.msg-title { font-size: 14px; font-weight: bold; color: #333; display: flex; align-items: center; }
.msg-content { font-size: 13px; color: #666; margin-top: 5px; line-height: 1.4; }
.msg-time { font-size: 12px; color: #999; margin-top: 5px; text-align: right; }
.dot { color: #f56c6c; margin-right: 5px; font-size: 12px; }
</style>