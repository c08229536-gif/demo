<template>
  <div class="profile-container">
    <h2>👤 个人中心</h2>

    <el-card class="profile-card">
      <div class="user-header">
        <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <h3 class="username">{{ userInfo.realName }}</h3>
        
        <p class="role">
          角色：
          <el-tag v-if="userInfo.role === 'teacher' || userInfo.role === 'TEACHER'" type="warning">教师</el-tag>
          <el-tag v-else-if="userInfo.role === 'admin' || userInfo.role === 'ADMIN'" type="danger">管理员</el-tag>
          <el-tag v-else type="success">学生</el-tag>
        </p>
        </div>

      <el-divider />
      
      <el-descriptions title="详细资料" :column="1" border>
        <el-descriptions-item label="用户名">
          {{ userInfo.username }}
        </el-descriptions-item>
        <el-descriptions-item label="真实姓名">
          {{ userInfo.realName }}
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag type="success">正常使用中</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ userInfo.createTime ? userInfo.createTime.replace('T', ' ') : '暂无数据' }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div style="margin-top: 30px; text-align: center;">
         <el-button type="warning" disabled>修改密码 (暂未开放)</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const userInfo = ref({})

// 获取我的信息
const fetchMyInfo = async () => {
  try {
    // 调用刚才写的 /auth/me 接口
    const res = await axios.get('/api/auth/me')
    userInfo.value = res.data
  } catch (error) {
    console.error('获取个人信息失败', error)
  }
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
  padding: 20px;
}
.profile-card {
  width: 600px;
  margin-top: 20px;
}
.user-header {
  text-align: center;
  margin-bottom: 20px;
}
.username {
  margin: 10px 0 5px;
  font-size: 24px;
}
.role {
  color: #999;
  font-size: 14px;
}
</style>