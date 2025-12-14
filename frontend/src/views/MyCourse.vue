<template>
  <div>
    <h2>📚 我的学习进度</h2>
    
    <el-empty v-if="courses.length === 0" description="你还没有加入任何课程，快去选课吧！">
      <el-button type="primary" @click="$router.push('/home/courses')">去选课</el-button>
    </el-empty>

    <el-row :gutter="20" v-else>
      <el-col :span="6" v-for="course in courses" :key="course.courseId" style="margin-bottom: 20px;">
        <el-card :body-style="{ padding: '0px' }" shadow="hover">
          <img :src="course.cover" class="image" />
          <div style="padding: 14px">
            <h3 class="course-title">{{ course.title }}</h3>
            <div class="bottom">
              <span class="teacher">讲师：{{ course.teacher }}</span>
              <el-button type="primary" size="small" @click="$router.push(`/home/course/${course.courseId}`)">
                继续学习
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const courses = ref([])

// 获取“我的”课程
const fetchMyCourses = async () => {
  try {
    // 请求刚才写的那个新接口
    const res = await axios.get('/api/course/my-courses')
    courses.value = res.data
  } catch (error) {
    console.error('获取我的课程失败', error)
  }
}

onMounted(() => {
  fetchMyCourses()
})
</script>

<style scoped>
.image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  display: block;
}
.course-title {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}
.bottom {
  margin-top: 13px;
  line-height: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.teacher {
  font-size: 13px;
  color: #999;
}
</style>