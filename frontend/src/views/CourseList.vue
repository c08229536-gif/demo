<template>
  <div class="course-list-page" style="padding: 20px;">
    
    <div v-if="bannerList.length > 0" class="carousel-container">
      <el-carousel :interval="5000" type="card" height="300px">
        <el-carousel-item v-for="item in bannerList" :key="item.id">
          <div class="banner-item" @click="handleBannerClick(item.linkUrl)">
            <el-image :src="item.imageUrl" fit="cover" class="banner-image">
              <template #placeholder>
                <div class="image-slot">加载中<span class="dot">...</span></div>
              </template>
            </el-image>
            <div class="banner-info">
              <h3>{{ item.title }}</h3>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="header-actions">
      <el-input
        v-model="searchText"
        placeholder="搜索课程名称..."
        :prefix-icon="Search"
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
             <img :src="course.cover || 'https://via.placeholder.com/300x160?text=No+Cover'" class="image" />
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
                <el-icon><UserIcon /></el-icon> {{ course.teacher }}
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
import { ref, onMounted } from 'vue'
import { Search, Plus, User as UserIcon } from '@element-plus/icons-vue' // 👈 注意：这里重命名 User 图标防止冲突
import axios from 'axios'
import { ElMessage } from 'element-plus'

// --- 数据响应式变量 ---
const searchText = ref('')
const currentCategory = ref('全部')
const allCourses = ref([])
const filteredList = ref([])
const bannerList = ref([]) // 存储轮播图数据
const loading = ref(false)
const dialogVisible = ref(false)
const userRole = ref('') 

const newCourse = ref({
  title: '',
  teacher: '',
  description: '',
  cover: '',
  category: '后端'
})

// --- 逻辑函数 ---

// 1. 获取轮播图列表
const fetchBanners = async () => {
  try {
    const res = await axios.get('/api/banners') // 对应后端的公共查询接口
    bannerList.value = res.data.filter(b => b.isActive === 1)
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

// 2. 获取当前登录用户信息 (用于角色权限控制)
const fetchUserInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    userRole.value = res.data.role
  } catch (error) {
    console.error('获取用户信息失败')
  }
}

// 3. 获取所有课程列表
const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/course/list') // 请确保后端该接口路径正确
    allCourses.value = res.data
    filterCourses() // 获取后立即执行一次筛选逻辑
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 4. 核心筛选逻辑：根据分类按钮和搜索关键词同时过滤
const filterCourses = () => {
  let result = allCourses.value

  // 分类过滤
  if (currentCategory.value !== '全部') {
    result = result.filter(c => c.category === currentCategory.value)
  }

  // 关键词过滤
  if (searchText.value) {
    const key = searchText.value.toLowerCase()
    result = result.filter(c => c.title.toLowerCase().includes(key))
  }

  filteredList.value = result
}

// 5. 教师发布课程
const handlePublish = async () => {
  if (!newCourse.value.title) return ElMessage.warning('请填写课程标题')
  
  try {
    await axios.post('/api/course/add', newCourse.value)
    ElMessage.success('发布申请已提交，等待审核！') 
    dialogVisible.value = false 
    // 重置表单
    newCourse.value = { title: '', teacher: '', description: '', cover: '', category: '后端' }
    fetchCourses() // 重新加载数据
  } catch (error) {
    ElMessage.error('发布失败，请检查网络')
  }
}

// 6. 轮播图跳转
const handleBannerClick = (url) => {
  if (url) window.open(url, '_blank')
}

// --- 生命周期挂载 ---
onMounted(() => {
  fetchUserInfo()
  fetchCourses()
  fetchBanners()
})
</script>

<style scoped>
/* 轮播图区域 */
.carousel-container {
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  border-radius: 8px;
  overflow: hidden;
}
.banner-item {
  width: 100%;
  height: 100%;
  position: relative;
  cursor: pointer;
}
.banner-image {
  width: 100%;
  height: 100%;
}
.banner-info {
  position: absolute;
  bottom: 0;
  width: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 10px 20px;
}
.banner-info h3 { margin: 0; font-size: 18px; }

/* 顶部操作区 */
.header-actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.search-input { width: 320px; }

/* 课程分类标签 */
.category-tabs {
  margin-bottom: 25px;
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
}
.label { font-weight: bold; color: #333; margin-right: 15px; }

/* 课程卡片 */
.course-card {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: none;
  border-radius: 10px;
  overflow: hidden;
}
.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.12);
}

.img-wrapper {
  position: relative;
  height: 160px;
  background: #f0f2f5;
}
.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.category-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(64, 158, 255, 0.9);
  color: #fff;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 11px;
}

.course-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  height: 22px;
  line-height: 22px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #2c3e50;
}

.bottom {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.teacher {
  font-size: 13px;
  color: #7f8c8d;
  display: flex;
  align-items: center;
  gap: 4px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}
</style>