<template>
  <div>
    <div class="operation-bar">
      <el-input
        v-model="searchText"
        placeholder="搜索课程名称..."
        prefix-icon="Search"
        style="width: 300px; margin-right: 10px;"
      />
      <el-button type="primary" @click="fetchCourses">搜索</el-button>
      
      <el-button type="success" @click="dialogVisible = true"
       v-if="userRole === 'teacher' || userRole === 'admin'" >
         发布新课程
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="6" v-for="course in courses" :key="course.courseId" style="margin-bottom: 20px;">
        <el-card :body-style="{ padding: '0px' }" shadow="hover">
          <img :src="course.cover" class="image" />
          <div style="padding: 14px">
            <h3 class="course-title">{{ course.title }}</h3>
            <div class="bottom">
              <span class="teacher">讲师：{{ course.teacher }}</span>
              <el-button text class="button" @click="$router.push(`/home/course/${course.courseId}`)">
                查看详情
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="发布新课程" width="500px">
      <el-form :model="newCourse" label-width="80px">
        <el-form-item label="课程标题">
          <el-input v-model="newCourse.title" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="讲师姓名">
          <el-input v-model="newCourse.teacher" placeholder="请输入讲师" />
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input v-model="newCourse.description" type="textarea" placeholder="简单介绍一下课程" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-input v-model="newCourse.cover" placeholder="请输入图片URL (可选)" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePublish">确认发布</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const searchText = ref('')
const courses = ref([]) 
const dialogVisible = ref(false) // 控制弹窗显示

// 新课程的数据模型
const newCourse = ref({
  title: '',
  teacher: '',
  description: '',
  cover: ''
})

// 获取课程列表
const fetchCourses = async () => {
  try {
    const res = await axios.get('/api/course/list')
    // 简单的前端搜索过滤 (如果输入了字，就只显示匹配的)
    if (searchText.value) {
      courses.value = res.data.filter(c => c.title.includes(searchText.value))
    } else {
      courses.value = res.data
    }
  } catch (error) {
    console.error('获取课程失败:', error)
  }
}

// 👇 处理发布逻辑
const handlePublish = async () => {
  if (!newCourse.value.title) {
    ElMessage.warning('请填写课程标题')
    return
  }

  try {
    await axios.post('/api/course/add', newCourse.value)
    ElMessage.success('发布成功！')
    dialogVisible.value = false // 关弹窗
    
    // 清空表单
    newCourse.value = { title: '', teacher: '', description: '', cover: '' }
    
    // 刷新列表
    fetchCourses()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.operation-bar {
  margin-bottom: 20px;
}
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