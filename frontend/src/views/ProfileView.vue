<template>
  <div class="profile-container">
    <h2>👤 个人中心</h2>

    <el-row :gutter="20" style="width: 100%; max-width: 900px;">
      <el-col :span="10">
        <el-card class="profile-card">
          <div class="user-header">
            <el-upload
              class="avatar-uploader"
              action="/api/upload/file"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <div class="avatar-wrapper">
                <el-avatar 
                  :key="userInfo.avatar"
                  :size="100" 
                  :src="userInfo.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" 
                />
                <div class="avatar-mask">
                  <el-icon><Camera /></el-icon>
                  <span>更换头像</span>
                </div>
              </div>
            </el-upload>
            
            <h3 class="username">{{ userInfo.realName }}</h3>
            
            <p class="role">
              <el-tag v-if="userInfo.role === 'teacher'" type="warning" effect="dark">教师</el-tag>
              <el-tag v-else-if="userInfo.role === 'admin'" type="danger" effect="dark">管理员</el-tag>
              <el-tag v-else type="success" effect="dark">学生</el-tag>
            </p>
          </div>
          
          <el-divider />
          
          <el-descriptions :column="1" border>
            <el-descriptions-item :label="accountLabel">
              {{ userInfo.username }}
            </el-descriptions-item>

            <el-descriptions-item label="手机号">
              {{ userInfo.phone || '未填写' }}
            </el-descriptions-item>
            
            <el-descriptions-item label="邮箱">
              {{ userInfo.email || '未填写' }}
            </el-descriptions-item>
            
            <el-descriptions-item label="注册时间">
              {{ userInfo.createTime ? userInfo.createTime.substring(0, 10) : '-' }}
            </el-descriptions-item>
          </el-descriptions>

          <div style="margin-top: 20px; text-align: center; display: flex; justify-content: center; gap: 10px;">
             <el-button type="primary" @click="openEditDialog">编辑资料</el-button>
             <el-button type="warning" @click="openPasswordDialog">修改密码</el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card class="stat-card">
          <template #header><h3>📊 学习数据</h3></template>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="stat-item">
                <div class="stat-value">{{ stats.courseCount }}</div>
                <div class="stat-label">加入课程</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="stat-item">
                <div class="stat-value">{{ stats.assignmentCount }}</div>
                <div class="stat-label">提交作业</div>
              </div>
            </el-col>
          </el-row>

          <el-divider />
          
          <div v-if="userInfo.role === 'student'" style="text-align: center; color: #666;">
            <p>“学习是最好的投资。”</p>
            <el-button type="primary" plain round @click="$router.push('/home/courses')">去选课</el-button>
          </div>
          <div v-else style="text-align: center; color: #666;">
            <p>感谢您的辛勤付出！</p>
          </div>

        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="编辑个人资料" width="400px">
      <el-form label-width="60px">
        <el-form-item label="姓名">
          <el-input v-model="editForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        
        <el-form-item label="手机">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form :model="passwordForm" ref="passwordFormRef" :rules="passwordRules" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确认修改</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'

const router = useRouter()

const userInfo = ref({})
const stats = ref({ courseCount: 0, assignmentCount: 0 })
const dialogVisible = ref(false)

// 编辑表单数据 (只包含允许修改的字段)
const editForm = reactive({
  realName: '',
  phone: '',
  email: ''
})

// --- 修改密码相关 ---
const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error("两次输入不一致!"))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }]
}

const openPasswordDialog = () => {
  passwordDialogVisible.value = true
  // 重置表单状态
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await axios.post('/api/auth/update-password', {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        passwordDialogVisible.value = false
        // 强制退出
        localStorage.removeItem('token')
        localStorage.removeItem('userRole')
        router.push('/')
      } catch (error) {
        ElMessage.error(error.response?.data?.message || error.response?.data || '密码修改失败，请检查旧密码是否正确')
      }
    }
  })
}

// 👇 计算属性：根据角色决定显示“学号”还是“工号”
const accountLabel = computed(() => {
  if (userInfo.value.role === 'student') return '学号'
  if (userInfo.value.role === 'teacher') return '工号'
  return '账号'
})

// 获取个人信息
const fetchMyInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    userInfo.value = res.data
    
    // 如果不是管理员，顺便查一下统计数据
    if (res.data.role !== 'admin') {
      fetchStats()
    }
  } catch (error) {
    console.error('获取信息失败', error)
  }
}

// 获取统计数据
const fetchStats = async () => {
  try {
    // 并行请求两个接口
    const [cRes, aRes] = await Promise.all([
      axios.get('/api/course/my-courses'),
      axios.get('/api/assignment/my-list')
    ])
    stats.value.courseCount = cRes.data.length
    stats.value.assignmentCount = aRes.data.filter(a => a.status === '已提交').length
  } catch (error) {
    console.error('统计获取失败', error)
  }
}

// 打开编辑弹窗
const openEditDialog = () => {
  // 回填现有数据
  editForm.realName = userInfo.value.realName
  editForm.phone = userInfo.value.phone
  editForm.email = userInfo.value.email
  dialogVisible.value = true
}

// 提交修改
const handleUpdate = async () => {
  try {
    // 调用更新接口
    await axios.post('/api/auth/update', editForm)
    ElMessage.success('资料保存成功')
    dialogVisible.value = false
    fetchMyInfo() // 刷新页面显示
    // 通知全局更新（例如同步右上角名字）
    window.dispatchEvent(new CustomEvent('user-info-updated'))
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// ==== 头像上传逻辑 ====
const handleAvatarSuccess = async (response) => {
  const avatarUrl = response
  try {
    await axios.post('/api/auth/update', { avatar: avatarUrl })
    // 强制触发响应式更新
    userInfo.value.avatar = ''
    setTimeout(() => {
      userInfo.value.avatar = avatarUrl
      ElMessage.success('头像修改成功')
      // 通知全局更新（例如同步右上角头像，如果有的话）
      window.dispatchEvent(new CustomEvent('user-info-updated'))
    }, 50)
  } catch (error) {
    ElMessage.error('头像保存失败')
  }
}

const beforeAvatarUpload = (rawFile) => {
  const isTypeValid = ['image/jpeg', 'image/png', 'image/gif'].includes(rawFile.type)
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isTypeValid) {
    ElMessage.error('头像只能是 JPG/PNG/GIF 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

onMounted(() => {
  fetchMyInfo()
})
</script>

<style scoped>
.profile-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 100px);
}
.profile-container h2 {
  margin-bottom: 30px;
  align-self: flex-start;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
  width: 100%;
}
.profile-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.user-header {
  text-align: center;
  padding: 20px 0;
}
.avatar-uploader {
  display: inline-block;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
}
.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 12px;
}
.avatar-wrapper:hover .avatar-mask {
  opacity: 1;
}
.username {
  margin: 15px 0 8px;
  font-size: 22px;
  font-weight: 600;
}
.role {
  margin-bottom: 0;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.stat-item {
  text-align: center;
  padding: 25px 0;
  background: #fdfdfd;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  transition: all 0.3s;
}
.stat-item:hover {
  background: #fff;
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64,158,255,0.1);
  transform: translateY(-3px);
}
.stat-value {
  font-size: 42px;
  font-weight: 800;
  color: #409EFF;
}
.stat-label {
  color: #606266;
  font-size: 15px;
  margin-top: 8px;
}
</style>