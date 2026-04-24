<template>
  <div class="common-layout">
    <el-container direction="horizontal" style="height: 100vh;">
      <el-aside width="200px" class="aside">
        <div class="logo">在线教育平台</div>
        <el-menu :default-active="$route.path" router class="el-menu-vertical" background-color="#304156" text-color="#fff">
          <el-menu-item index="/home/courses"><el-icon><Reading /></el-icon><span>课程中心</span></el-menu-item>
          
          <template v-if="userRole === 'student'">
            <el-menu-item index="/home/my-courses"><el-icon><Collection /></el-icon><span>我的课程</span></el-menu-item>
            <el-menu-item index="/home/my-exams"><el-icon><Postcard /></el-icon><span>在线考试</span></el-menu-item>
            <el-menu-item index="/home/my-assignment"><el-icon><EditPen /></el-icon><span>我的作业</span></el-menu-item>
          </template>

          <el-menu-item index="/home/feedback" v-if="userRole === 'student' || userRole === 'teacher'"><el-icon><ChatLineSquare /></el-icon><span>意见反馈</span></el-menu-item>
          
          <el-menu-item index="/home/grade-assignment" v-if="userRole === 'teacher'"><el-icon><Edit /></el-icon><span>作业批改</span></el-menu-item>
          <el-menu-item index="/home/teacher-stats" v-if="userRole === 'teacher'"><el-icon><DataAnalysis /></el-icon><span>学情统计</span></el-menu-item>
          
          <template v-if="userRole === 'admin'">
            <el-menu-item index="/home/user-manage"><el-icon><UserFilled /></el-icon><span>用户管理</span></el-menu-item>
            <el-menu-item index="/home/admin-audit"><el-icon><List /></el-icon><span>课程审核</span></el-menu-item>
            <el-menu-item index="/home/admin-feedback"><el-icon><Service /></el-icon><span>反馈处理</span></el-menu-item>
            <el-menu-item index="/home/banner-manage"><el-icon><Picture /></el-icon><span>首页运营</span></el-menu-item>
            <el-menu-item index="/home/log-manage"><el-icon><Monitor /></el-icon><span>系统日志</span></el-menu-item>
          </template>
          <el-menu-item index="/home/profile"><el-icon><User /></el-icon><span>个人中心</span></el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="breadcrumb"></div>
          
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
                <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 5px; margin-bottom: 10px;">
                  <h4 style="margin: 0;">系统消息</h4>
                  <el-button 
                    v-if="userRole === 'admin'" 
                    type="primary" 
                    link 
                    size="small" 
                    @click="announcementDialogVisible = true"
                  >发布公告</el-button>
                </div>
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
        
        <el-main class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>

    <!-- 发布公告对话框 -->
    <el-dialog v-model="announcementDialogVisible" title="发布系统公告" width="500px">
      <el-form :model="announcementForm" label-width="80px">
        <el-form-item label="公告标题">
          <el-input v-model="announcementForm.title" placeholder="例如：维护公告、新功能上线" />
        </el-form-item>
        <el-form-item label="公告内容">
          <el-input v-model="announcementForm.content" type="textarea" :rows="5" placeholder="请输入具体的公告内容..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="announcementDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePublishAnnouncement">立即发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
// 导入所有需要的图标
import { 
  User, Reading, Collection, EditPen, Edit, 
  UserFilled, List, ChatLineSquare, Service, 
  Bell, Picture, Monitor, Postcard, DataAnalysis 
} from '@element-plus/icons-vue'

const router = useRouter()
const realName = ref('')
const unreadCount = ref(0)
const messages = ref([])

const userRole = ref('')
const announcementDialogVisible = ref(false)
const announcementForm = reactive({
  title: '',
  content: ''
})

const fetchUserInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    userRole.value = res.data.role
    realName.value = res.data.realName
    // 同步到本地供路由守卫使用
    localStorage.setItem('userRole', res.data.role)
  } catch (error) {
    router.push('/')
  }
}

const fetchMessages = async () => {
  try {
    const res = await axios.get('/api/message/my')
    messages.value = res.data
    unreadCount.value = messages.value.filter(m => m.isRead === 0).length
  } catch(e) {}
}

const readMessage = async (msg) => {
  if (msg.isRead === 0) {
    await axios.post(`/api/message/read/${msg.id}`)
    msg.isRead = 1
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
}

// 发布公告
const handlePublishAnnouncement = async () => {
  if (!announcementForm.title || !announcementForm.content) {
    return ElMessage.warning('请填写完整的标题和内容')
  }
  try {
    await axios.post('/api/message/send-all', announcementForm)
    ElMessage.success('公告已全量发布')
    announcementDialogVisible.value = false
    announcementForm.title = ''
    announcementForm.content = ''
    fetchMessages() // 刷新列表
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  router.push('/')
}

onMounted(() => {
  fetchUserInfo()
  fetchMessages()
  // 监听用户信息更新通知
  window.addEventListener('user-info-updated', fetchUserInfo)
})

onUnmounted(() => {
  // 页面销毁时记得移除监听，防止内存泄漏
  window.removeEventListener('user-info-updated', fetchUserInfo)
})
</script>

<style scoped>
@keyframes slideIn {
  from { opacity: 0; transform: translateX(-20px); }
  to { opacity: 1; transform: translateX(0); }
}

.common-layout { height: 100vh; overflow: hidden; background: var(--bg-color); }

/* 侧边栏：深色高质感 */
.aside { 
  background-color: #111827; /* 现代深灰 */
  color: white; 
  box-shadow: 2px 0 10px rgba(0,0,0,0.1);
  z-index: 10;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo { 
  height: 64px; 
  line-height: 64px; 
  text-align: center; 
  font-size: 20px; 
  font-weight: 700; 
  letter-spacing: 1px;
  background: #1f2937;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  animation: slideIn 0.5s ease-out;
}

/* 侧边菜单深度覆盖，实现圆角、悬浮和活动状态高亮 */
:deep(.el-menu) {
  border-right: none !important;
  background: transparent !important;
}
:deep(.el-menu-item) {
  margin: 4px 12px;
  border-radius: 8px;
  height: 48px;
  line-height: 48px;
  transition: all 0.3s;
  color: #d1d5db !important; /* 字体默认为浅灰 */
}
:deep(.el-menu-item:hover) {
  background-color: rgba(255,255,255,0.08) !important;
  transform: translateX(4px);
  color: #fff !important;
}
:deep(.el-menu-item.is-active) {
  background-color: var(--primary-color) !important;
  color: #fff !important;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

/* 顶部导航：玻璃态与微阴影 */
.header { 
  background: rgba(255, 255, 255, 0.85); 
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(229, 231, 235, 0.5); 
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);
  display: flex; 
  align-items: center; 
  justify-content: space-between; 
  padding: 0 24px; 
  height: 64px;
  z-index: 5;
}
.header-right { display: flex; align-items: center; font-weight: 500; font-size: 15px;}

/* 消息铃铛悬停动效 */
.bell-wrapper { 
  margin-right: 24px; 
  display: flex; 
  align-items: center; 
  height: 100%; 
  transition: transform 0.2s; 
}
.bell-wrapper:hover { transform: scale(1.1); }

/* 主区域背景 */
.main-content {
  background: var(--bg-color);
  padding: 24px;
}

/* 下拉消息弹窗美化 */
.msg-scroll { max-height: 320px; overflow-y: auto; padding: 0 8px; }
.msg-item { 
  padding: 12px; 
  border-bottom: 1px solid #f3f4f6; 
  cursor: pointer; 
  border-radius: 8px;
  transition: all 0.2s; 
  margin-bottom: 4px;
}
.msg-item:last-child { border-bottom: none; }
.msg-item:hover { 
  background: #f8fafc; 
  transform: translateY(-1px); 
  box-shadow: 0 2px 5px rgba(0,0,0,0.02);
}
.msg-title { font-size: 14px; font-weight: 600; color: #1e293b; display: flex; align-items: center; }
.msg-content { font-size: 13px; color: #64748b; margin-top: 6px; line-height: 1.5; }
.msg-time { font-size: 12px; color: #94a3b8; margin-top: 8px; text-align: right; }
.dot { color: #ef4444; margin-right: 6px; font-size: 12px; }
</style>