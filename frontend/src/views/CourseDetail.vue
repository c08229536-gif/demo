<template>
  <div class="course-detail">
    <el-breadcrumb separator="/" style="margin-bottom: 20px;">
      <el-breadcrumb-item :to="{ path: '/home/courses' }">课程中心</el-breadcrumb-item>
      <el-breadcrumb-item>{{ course.title }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="20">
      <el-col :span="16">
        <div class="video-player">
          <div class="placeholder">
            <el-icon size="60"><VideoPlay /></el-icon>
            <p>正在播放：{{ course.title }}</p>
          </div>
        </div>
        <h1>{{ course.title }}</h1>
        <p class="desc">{{ course.description }}</p>
      </el-col>

      <el-col :span="8">
        <el-card class="teacher-card">
          <h3>讲师介绍</h3>
          <div class="teacher-info">
            <el-avatar :size="50" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
            <span class="name">{{ course.teacher }}</span>
          </div>
        </el-card>

        <el-card style="margin-top: 20px; text-align: center;">
          <el-button type="primary" size="large" style="width: 100%" @click="handleEnroll">
            立即加入学习
          </el-button>
        </el-card>

        <el-card style="margin-top: 20px;">
          <h3>课程目录</h3>
          <el-steps direction="vertical" :active="1">
            <el-step title="第一章：课程介绍" description="免费试看" />
            <el-step title="第二章：环境搭建" />
            <el-step title="第三章：核心语法" />
          </el-steps>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { VideoPlay } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus' // 引入消息提示

const route = useRoute()
const course = ref({})

// 获取课程详情
const fetchCourseDetail = async () => {
  const courseId = route.params.id
  if (!courseId) return;

  try {
    const res = await axios.get(`/api/course/${courseId}`)
    course.value = res.data
  } catch (error) {
    console.error('详情加载失败:', error)
  }
}

// 👇 新增：处理加入课程逻辑
const handleEnroll = async () => {
  try {
    // 发送请求给后端：我要报名这门课
    // 注意：这里用的是 course.value.courseId，确保你的实体类里有这个字段
    const res = await axios.post(`/api/course/enroll/${course.value.courseId}`)
    
    // 弹出后端返回的提示文字 (例如："加入成功！")
    ElMessage.success(res.data)
  } catch (error) {
    console.error(error)
    ElMessage.error('加入失败，请检查登录状态')
  }
}

// 页面加载时执行
onMounted(() => {
  fetchCourseDetail()
})
</script>

<style scoped>
.video-player {
  width: 100%;
  height: 400px;
  background-color: #000;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
}
.teacher-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
.name {
  font-size: 18px;
  font-weight: bold;
}
.desc {
  color: #666;
  line-height: 1.6;
  margin-top: 20px;
}
</style>