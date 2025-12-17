<template>
  <div>
    <div class="header-actions">
      <el-input
        v-model="searchText"
        placeholder="搜索课程名称..."
        prefix-icon="Search"
        class="search-input"
        clearable
        @clear="filterCourses"
        @input="filterCourses"
      />
      
      <el-button 
        type="success" 
        @click="dialogVisible = true"
        v-if="userRole === 'teacher' || userRole === 'admin'"
      >
         <el-icon style="margin-right: 5px"><Plus /></el-icon> 发布新课程
      </el-button>
    </div>

    <div class="category-tabs">
      <span class="label">课程方向：</span>
      <el-radio-group v-model="currentCategory" @change="filterCourses">
        <el-radio-button label="全部" />
        <el-radio-button label="后端" />
        <el-radio-button label="前端" />
        <el-radio-button label="AI" />
        <el-radio-button label="移动端" />
        <el-radio-button label="测试" />
        <el-radio-button label="其他" />
      </el-radio-group>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6" v-for="course in filteredList" :key="course.courseId" style="margin-bottom: 20px;">
        <el-card :body-style="{ padding: '0px' }" shadow="hover" class="course-card">
          <div class="img-wrapper">
             <img :src="course.cover" class="image" />
             <span class="category-tag">{{ course.category || '其他' }}</span>
          </div>
          
          <div style="padding: 14px">
            <h3 class="course-title" :title="course.title">{{ course.title }}</h3>
            
            <div style="margin-top: 8px; display: flex; align-items: center;">
               <el-rate
                 v-model="course.rating"
                 disabled
                 show-score
                 text-color="#ff9900"
                 score-template="{value} 分"
                 size="small"
               />
               <span v-if="!course.rating" style="font-size: 12px; color: #ccc; margin-left: 5px;">(暂无评分)</span>
            </div>

            <div class="bottom">
              <span class="teacher">
                <el-icon><User /></el-icon> {{ course.teacher }}
              </span>
              <el-button type="primary" plain size="small" @click="$router.push(`/home/course/${course.courseId}`)">
                进入学习
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="filteredList.length === 0 && !loading" description="该分类下暂无课程" />

    <el-dialog v-model="dialogVisible" title="发布新课程" width="550px">
      <el-form :model="newCourse" label-width="80px">
        <el-form-item label="课程标题">
          <el-input v-model="newCourse.title" placeholder="请输入课程名称" />
        </el-form-item>
        
        <el-form-item label="课程分类">
           <el-select v-model="newCourse.category" placeholder="请选择分类" style="width: 100%;">
             <el-option label="后端开发" value="后端" />
             <el-option label="前端开发" value="前端" />
             <el-option label="人工智能" value="AI" />
             <el-option label="移动开发" value="移动端" />
             <el-option label="软件测试" value="测试" />
             <el-option label="其他" value="其他" />
           </el-select>
        </el-form-item>

        <el-form-item label="讲师姓名">
          <el-input v-model="newCourse.teacher" placeholder="请输入讲师" />
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input v-model="newCourse.description" type="textarea" :rows="3" placeholder="简单介绍一下课程" />
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
import { ref, onMounted, computed } from 'vue'
import { Search, Plus, User } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const searchText = ref('')
const currentCategory = ref('全部') // 当前选中的分类
const allCourses = ref([]) // 存所有课程
const filteredList = ref([]) // 存筛选后的课程
const loading = ref(false)

const dialogVisible = ref(false)
const userRole = ref('') 

const newCourse = ref({
  title: '',
  teacher: '',
  description: '',
  cover: '',
  category: '后端' // 默认值
})

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    userRole.value = res.data.role
  } catch (error) {}
}

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/course/list')
    allCourses.value = res.data
    // 初始执行一次筛选
    filterCourses()
  } catch (error) {
    console.error('获取课程失败:', error)
  } finally {
    loading.value = false
  }
}

// 👇 核心筛选逻辑：同时根据【搜索词】和【分类】过滤
const filterCourses = () => {
  let result = allCourses.value

  // 1. 先按分类筛
  if (currentCategory.value !== '全部') {
    result = result.filter(c => c.category === currentCategory.value)
  }

  // 2. 再按关键词筛
  if (searchText.value) {
    result = result.filter(c => c.title.toLowerCase().includes(searchText.value.toLowerCase()))
  }

  filteredList.value = result
}

// 发布课程
const handlePublish = async () => {
  if (!newCourse.value.title) return ElMessage.warning('请填写课程标题')
  
  try {
    await axios.post('/api/course/add', newCourse.value)
    ElMessage.success('发布申请已提交，等待审核！') 
    dialogVisible.value = false 
    // 重置表单
    newCourse.value = { title: '', teacher: '', description: '', cover: '', category: '后端' }
    fetchCourses()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchCourses()
})
</script>

<style scoped>
/* 顶部样式优化 */
.header-actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.search-input { width: 300px; }

/* 分类标签栏 */
.category-tabs {
  margin-bottom: 25px;
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
}
.label { font-weight: bold; color: #333; margin-right: 15px; }

/* 课程卡片优化 */
.course-card {
  transition: all 0.3s;
  border: none;
  border-radius: 8px;
  overflow: hidden;
}
.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
}

.img-wrapper {
  position: relative;
  height: 160px;
  overflow: hidden;
}
.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}
.course-card:hover .image {
  transform: scale(1.05); /* 图片微放大效果 */
}

/* 分类标签悬浮 */
.category-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0,0,0,0.6);
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.course-title {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333;
}

.bottom {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.teacher {
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>