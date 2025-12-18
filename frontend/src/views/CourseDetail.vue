<template>
  <div class="course-detail">
    <el-breadcrumb separator="/" style="margin-bottom: 20px;">
      <el-breadcrumb-item :to="{ path: '/home/courses' }">课程中心</el-breadcrumb-item>
      <el-breadcrumb-item>{{ course.title }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="20">
      <el-col :span="16">
        <div class="video-player">
          <video v-if="currentVideoUrl" :src="currentVideoUrl" controls autoplay class="real-video" @ended="handleVideoEnded"></video>
          <div v-else class="placeholder">
            <el-icon size="60"><VideoPlay /></el-icon>
            <p>请点击右侧目录选择章节播放</p>
          </div>
        </div>
        <h1>{{ course.title }}</h1>
        <p class="desc">{{ course.description }}</p>

        <el-tabs v-model="activeTab" class="course-tabs" style="margin-top: 20px;">
          
          <el-tab-pane label="📂 课件资料" name="resources">
             <div v-if="userRole === 'teacher' || userRole === 'admin'" style="margin-bottom: 15px;">
               <el-button type="primary" size="small" @click="resourceDialogVisible = true">
                 <el-icon style="margin-right: 5px"><Upload /></el-icon> 上传资料
               </el-button>
             </div>

             <el-table :data="resourceList" stripe style="width: 100%" empty-text="暂无资料">
               <el-table-column prop="title" label="资料名称">
                 <template #default="scope">
                   <div style="display: flex; align-items: center; gap: 5px;">
                     <el-icon color="#409EFF"><Document /></el-icon>
                     <span style="font-weight: bold;">{{ scope.row.title }}</span>
                   </div>
                 </template>
               </el-table-column>
               <el-table-column prop="createTime" label="上传时间" width="160">
                  <template #default="scope">{{ scope.row.createTime ? scope.row.createTime.substring(0, 10) : '' }}</template>
               </el-table-column>
               <el-table-column label="操作" width="180">
                 <template #default="scope">
                   <a :href="scope.row.url" target="_blank" style="text-decoration: none; margin-right: 10px;">
                     <el-button type="info" link size="small">预览</el-button>
                   </a>

                   <el-button type="primary" link size="small" @click="handleDownload(scope.row)">
                     下载
                   </el-button>

                   <el-button 
                      v-if="userRole === 'teacher' || userRole === 'admin'"
                      type="danger" link size="small" 
                      style="margin-left: 10px;"
                      @click="deleteResource(scope.row.id)">删除</el-button>
                 </template>
               </el-table-column>
             </el-table>
          </el-tab-pane>

          <el-tab-pane label="💬 课程问答" name="qa">
            <div class="ask-box" v-if="userRole !== 'teacher'">
              <el-input v-model="newQuestion" placeholder="有什么不懂的？快向老师提问吧..." class="input-with-select">
                <template #append>
                  <el-button type="primary" @click="handleAsk">提问</el-button>
                </template>
              </el-input>
            </div>
            <div class="qa-list">
              <el-empty v-if="qaList.length === 0" description="暂无提问" />
              <div v-for="qa in qaList" :key="qa.id" class="qa-item">
                <div class="qa-header">
                  <el-avatar :size="30" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                  <span class="student-name">{{ qa.studentName }}</span>
                  <span class="time">{{ qa.createTime ? qa.createTime.replace('T', ' ') : '' }}</span>
                </div>
                <div class="qa-content">{{ qa.content }}</div>
                <div class="qa-reply" v-if="qa.reply">
                  <span class="teacher-tag">老师回复：</span>{{ qa.reply }}
                </div>
                <div v-if="userRole === 'teacher' && !qa.reply" style="margin-top: 10px; display: flex; gap: 10px;">
                  <el-input v-model="qa.tempReply" size="small" placeholder="输入回复内容..." />
                  <el-button type="primary" size="small" @click="handleReply(qa)">回复</el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="🎓 学员进度" name="students" v-if="userRole === 'teacher' || userRole === 'admin'">
            <el-table :data="studentList" stripe style="width: 100%">
              <el-table-column prop="studentName" label="姓名" width="120" />
              <el-table-column prop="username" label="学号" width="120" />
              <el-table-column label="进度">
                <template #default="scope">
                  <el-progress :percentage="scope.row.progress" :status="scope.row.progress === 100 ? 'success' : ''" />
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="⭐ 课程评价" name="reviews">
            <div v-if="userRole === 'student'" class="review-box">
               <div style="margin-bottom: 10px;">
                 <span style="font-weight: bold; margin-right: 10px;">给课程打分:</span>
                 <el-rate v-model="myRating" />
               </div>
               <el-input 
                 v-model="myComment" 
                 type="textarea" 
                 :rows="2" 
                 placeholder="写下你的听课感受..." 
               />
               <div style="text-align: right; margin-top: 10px;">
                 <el-button type="primary" @click="submitReview">发表评价</el-button>
               </div>
               <el-divider />
            </div>

            <div class="review-list">
              <el-empty v-if="reviewList.length === 0" description="暂无评价，快来抢沙发" />
              <div v-for="review in reviewList" :key="review.id" class="review-item">
                 <div class="review-header">
                    <el-avatar :size="30" :src="review.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                    <span class="student-name">{{ review.studentName }}</span>
                    <el-rate v-model="review.rating" disabled size="small" />
                    <span class="time">{{ review.createTime ? review.createTime.substring(0, 10) : '' }}</span>
                 </div>
                 <div class="review-content">{{ review.comment }}</div>
              </div>
            </div>
          </el-tab-pane>

        </el-tabs>
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
          <div v-if="userRole === 'student'">
            <el-button v-if="!isEnrolled" type="primary" size="large" style="width: 100%" @click="paymentDialogVisible = true">
              立即购买 (￥{{ course.price || '0.00' }})
            </el-button>
            <el-button v-else type="success" size="large" style="width: 100%" disabled>
              <el-icon><Check /></el-icon> 已加入学习
            </el-button>
            <div style="margin-top: 15px; padding: 10px; background: #f8f9fa; border-radius: 4px; display: flex; justify-content: space-between; align-items: center;">
               <span style="font-size: 14px; color: #666;">
                 当前余额: <strong style="color: #E6A23C;">￥{{ myBalance }}</strong>
               </span>
               <el-button type="primary" link size="small" @click="handleQuickRecharge">
                 充值
               </el-button>
            </div>
          </div>
          <div v-else style="display: flex; gap: 10px; flex-direction: column;">
            <el-button type="success" @click="assignmentDialogVisible = true">
              <el-icon style="margin-right: 5px"><Edit /></el-icon> 发布作业
            </el-button>
            <el-button type="warning" @click="chapterDialogVisible = true">
              <el-icon style="margin-right: 5px"><VideoCamera /></el-icon> 添加章节
            </el-button>
          </div>
        </el-card>

        <el-card style="margin-top: 20px;">
          <h3>课程目录</h3>
          <div v-if="chapters.length > 0" class="chapter-list">
            <div v-for="(item, index) in chapters" :key="item.chapterId" class="chapter-item" :class="{ 'active': currentVideoUrl === item.videoUrl }">
              <div class="chapter-info" @click="playVideo(item.videoUrl)">
                <el-icon v-if="finishedChapterIds.includes(item.chapterId)" color="#67C23A" size="20"><Check /></el-icon>
                <el-icon v-else><VideoPlay /></el-icon>
                <span style="margin-left: 8px;">第{{ index + 1 }}章：{{ item.title }}</span>
              </div>
              <el-button v-if="userRole === 'student' && isEnrolled && !finishedChapterIds.includes(item.chapterId)" type="primary" link size="small" @click.stop="markAsFinished(item)">标记学完</el-button>
              <span v-else-if="finishedChapterIds.includes(item.chapterId)" style="font-size: 12px; color: #67C23A;">已完成</span>
              <el-icon v-if="userRole === 'student' && !isEnrolled" color="#909399"><Lock /></el-icon>
            </div>
          </div>
          <el-empty v-else description="暂无章节" image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="paymentDialogVisible" title="确认购买" width="400px" center>
      <div style="text-align: center; padding: 10px 0;">
        <p>您正在购买：<strong>{{ course.title }}</strong></p>
        <h2 style="color: #f56c6c; margin: 20px 0;">￥{{ course.price }}</h2>
        <el-divider />
        <div style="display: flex; justify-content: space-between; font-size: 14px;">
          <span>您的钱包余额：</span>
          <span style="font-weight: bold;">￥{{ myBalance }}</span>
        </div>
        
        <div v-if="parseFloat(myBalance) < parseFloat(course.price)" style="margin-top: 15px; padding: 10px; background-color: #fef0f0; border-radius: 4px;">
           <p style="color: #f56c6c; font-size: 12px; margin-bottom: 8px;">
             <el-icon style="vertical-align: middle; margin-right: 3px;"><Warning /></el-icon>
             <span>余额不足 (还差 ￥{{ (parseFloat(course.price) - parseFloat(myBalance)).toFixed(2) }})</span>
           </p>
           <el-button type="warning" size="small" @click="handleQuickRecharge">
             🚀 立即充值
           </el-button>
        </div>
        </div>
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :disabled="parseFloat(myBalance) < parseFloat(course.price)" 
          :loading="paying"
          @click="handlePay"
        >
          确认支付
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resourceDialogVisible" title="添加课件资料" width="500px">
      <el-form label-width="80px">
        <el-form-item label="上传文件">
          <el-upload
            class="upload-demo"
            drag
            action="/api/upload/file"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <div v-if="!newResource.url">
               <el-icon class="el-icon--upload"><upload-filled /></el-icon>
               <div class="el-upload__text">
                 拖拽文件到此处 或 <em>点击上传</em>
               </div>
            </div>
            <div v-else style="padding: 20px 0;">
               <el-icon color="#67C23A" size="40"><CircleCheckFilled /></el-icon>
               <p style="color: #67C23A; margin-top: 10px;">文件已上传就绪</p>
               <p style="font-size: 12px; color: #999;">{{ newResource.url }}</p>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="资料名称">
          <el-input v-model="newResource.title" placeholder="上传后自动生成，也可手动修改" />
        </el-form-item>
        
        <el-form-item label="类型">
          <el-radio-group v-model="newResource.fileType">
            <el-radio label="ppt">PPT</el-radio>
            <el-radio label="pdf">PDF</el-radio>
            <el-radio label="code">代码</el-radio>
            <el-radio label="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resourceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddResource">确认添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignmentDialogVisible" title="布置新作业" width="500px">
      <el-form label-width="80px">
        <el-form-item label="标题"><el-input v-model="newAssignment.title" /></el-form-item>
        <el-form-item label="要求"><el-input v-model="newAssignment.description" type="textarea" /></el-form-item>
        <el-form-item label="截止"><el-date-picker v-model="newAssignment.deadline" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="assignmentDialogVisible=false">取消</el-button><el-button type="primary" @click="handlePublishAssignment">确认</el-button></template>
    </el-dialog>

    <el-dialog v-model="chapterDialogVisible" title="添加章节" width="500px">
      <el-form label-width="80px">
        <el-form-item label="标题"><el-input v-model="newChapter.title" /></el-form-item>
        <el-form-item label="链接"><el-input v-model="newChapter.videoUrl" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="newChapter.sortOrder" :min="1" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="chapterDialogVisible=false">取消</el-button><el-button type="primary" @click="handleAddChapter">确认</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
// 👇 修改点2：添加了 Warning 图标的引入
import { VideoPlay, Edit, VideoCamera, Check, Upload, Document, UploadFilled, CircleCheckFilled, Lock, Warning } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const course = ref({})
const chapters = ref([])
const resourceList = ref([]) // 资料列表
const studentList = ref([])
const qaList = ref([])
const userRole = ref('')
const currentVideoUrl = ref('')
const finishedChapterIds = ref([])
const activeTab = ref('resources') // 默认看资料

// 👇 新增：支付相关变量
const isEnrolled = ref(false)
const paymentDialogVisible = ref(false)
const paying = ref(false)
const myBalance = ref('0.00')
const myUserId = ref(null)

// 👇 新增：评价相关变量
const reviewList = ref([])
const myRating = ref(5)
const myComment = ref('')

// 弹窗控制
const assignmentDialogVisible = ref(false)
const chapterDialogVisible = ref(false)
const resourceDialogVisible = ref(false)

const newAssignment = ref({ title: '', description: '', deadline: '', courseId: null })
const newChapter = ref({ title: '', videoUrl: '', sortOrder: 1, courseId: null })
const newResource = ref({ title: '', url: '', fileType: 'ppt', courseId: null })

const newQuestion = ref('')

// 初始化
const init = async () => {
  await fetchUserInfo()
  const courseId = route.params.id
  if (!courseId) return
  
  // 设置 ID
  newAssignment.value.courseId = parseInt(courseId)
  newChapter.value.courseId = parseInt(courseId)
  newResource.value.courseId = parseInt(courseId)

  // 并行加载数据
  await fetchCourseDetail(courseId)
  fetchChapters(courseId)
  fetchResources(courseId)
  fetchQA(courseId)
  fetchReviews(courseId) // 👈 新增：加载评价
  
  if (userRole.value === 'student') {
    fetchProgress(courseId)
    checkEnrollStatus(courseId) // 👈 新增：检查是否已购买
    fetchMyBalance()            // 👈 新增：获取最新余额
  } else {
    activeTab.value = 'students' // 老师默认看学员
    fetchCourseStudents(courseId)
  }
}

// 核心数据获取
const fetchUserInfo = async () => {
  try { 
    const res = await axios.get('/api/auth/me')
    userRole.value = res.data.role 
    myUserId.value = res.data.userId || res.data.id
  } catch(e){}
}
const fetchCourseDetail = async (cid) => {
  const res = await axios.get(`/api/course/${cid}`); course.value = res.data
}
const fetchChapters = async (cid) => {
  const res = await axios.get(`/api/course/${cid}/chapters`); chapters.value = res.data
}

// 👇 新增：检查购买状态
const checkEnrollStatus = async (cid) => {
  try {
    const res = await axios.get(`/api/course/${cid}/is-enrolled`)
    isEnrolled.value = res.data
  } catch(e) { isEnrolled.value = false }
}

// 👇 新增：获取我的余额
const fetchMyBalance = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    myBalance.value = res.data.balance || '0.00'
  } catch(e) {}
}

// 👇 修改点3：新增充值功能函数
const handleQuickRecharge = () => {
  ElMessageBox.prompt('请输入充值金额', '钱包充值', {
    confirmButtonText: '充值',
    cancelButtonText: '取消',
    inputType: 'number',
    inputPattern: /^[0-9]+(\.[0-9]{1,2})?$/,
    inputErrorMessage: '金额格式不正确'
  }).then(async ({ value }) => {
    try {
      // 假设充值接口为 /api/payment/recharge
      await axios.post('/api/payment/recharge', {
        userId: myUserId.value,
        amount: parseFloat(value)
      })
      ElMessage.success(`成功充值 ${value} 元！`)
      fetchMyBalance() // 充值成功后立即刷新余额
    } catch (error) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '充值失败，请检查接口')
    }
  }).catch(() => {
    // 取消充值不做处理
  })
}

// 👇 新增：处理支付逻辑
const handlePay = async () => {
  paying.value = true
  try {
    await axios.post('/api/payment/buy', {
      userId: myUserId.value,
      courseId: course.value.courseId
    })
    ElMessage.success('支付成功，已为您开通课程！')
    paymentDialogVisible.value = false
    isEnrolled.value = true
    fetchMyBalance()
    init() // 重新刷新数据
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '支付失败')
  } finally {
    paying.value = false
  }
}

// 资源管理相关函数
const fetchResources = async (cid) => {
  const res = await axios.get(`/api/resource/course/${cid}`)
  resourceList.value = res.data
}
const handleAddResource = async () => {
  if (!newResource.value.title || !newResource.value.url) return ElMessage.warning('请填写完整')
  try {
    await axios.post('/api/resource/add', newResource.value)
    ElMessage.success('上传成功')
    resourceDialogVisible.value = false
    fetchResources(course.value.courseId)
  } catch(e) { ElMessage.error('上传失败') }
}
const deleteResource = (id) => {
  ElMessageBox.confirm('确定删除该资料吗？', '提示', { type: 'warning' }).then(async () => {
    await axios.delete(`/api/resource/${id}`)
    ElMessage.success('已删除')
    fetchResources(course.value.courseId)
  })
}
const handleUploadSuccess = (response, uploadFile) => {
  newResource.value.url = response
  ElMessage.success('文件上传成功！')
  if (!newResource.value.title) {
     const name = uploadFile.name
     newResource.value.title = name.substring(0, name.lastIndexOf('.'))
  }
}
const beforeUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 50) {
    ElMessage.error('文件大小不能超过 50MB!')
    return false
  }
  return true
}
const handleDownload = (row) => {
  const fileName = row.url.substring(row.url.lastIndexOf('/') + 1)
  const downloadUrl = `/api/upload/download?fileName=${fileName}`
  window.open(downloadUrl, '_self')
}

// 👇 新增：评价相关函数
const fetchReviews = async (cid) => {
  try {
    const res = await axios.get(`/api/review/course/${cid}`)
    reviewList.value = res.data
  } catch (e) {}
}

const submitReview = async () => {
  if (myRating.value === 0) return ElMessage.warning('请先打分')
  if (!myComment.value) return ElMessage.warning('请输入评论内容')
  
  try {
    await axios.post('/api/review/add', {
      courseId: course.value.courseId,
      rating: myRating.value,
      comment: myComment.value
    })
    ElMessage.success('评价成功')
    myComment.value = '' 
    fetchReviews(course.value.courseId) 
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '评价失败 (可能已评价过)')
  }
}

// 其他辅助函数
const fetchQA = async (cid) => { const res = await axios.get(`/api/feedback/course/${cid}`); qaList.value = res.data }
const handleAsk = async () => {
  if (!newQuestion.value) return ElMessage.warning('请输入问题')
  try {
    await axios.post('/api/feedback/add', { courseId: course.value.courseId, content: newQuestion.value })
    ElMessage.success('提问成功')
    newQuestion.value = ''
    fetchQA(course.value.courseId)
  } catch(e) { ElMessage.error('提问失败') }
}
const handleReply = async (qa) => {
  if (!qa.tempReply) return ElMessage.warning('请输入回复')
  try {
    await axios.post('/api/feedback/reply', { id: qa.id, reply: qa.tempReply })
    ElMessage.success('回复成功')
    fetchQA(course.value.courseId)
  } catch(e) { ElMessage.error('回复失败') }
}
const fetchProgress = async (cid) => { const res = await axios.get(`/api/course/${cid}/progress`); finishedChapterIds.value = res.data }
const fetchCourseStudents = async (cid) => { const res = await axios.get(`/api/course/${cid}/students`); studentList.value = res.data }
const markAsFinished = async (chapter) => {
  try {
    await axios.post(`/api/course/chapter/${chapter.chapterId}/finish`)
    ElMessage.success('已记录进度')
    finishedChapterIds.value.push(chapter.chapterId)
  } catch (e) {}
}
const playVideo = (url) => { 
  if(!isEnrolled.value && userRole.value === 'student') return ElMessage.warning('请先购买课程再观看视频')
  if(url) currentVideoUrl.value = url 
}
const handleVideoEnded = () => {
  if (userRole.value === 'teacher') return
  const currentChapter = chapters.value.find(c => c.videoUrl === currentVideoUrl.value)
  if (currentChapter && !finishedChapterIds.value.includes(currentChapter.chapterId)) {
    markAsFinished(currentChapter)
    ElMessage.success('自动打卡成功')
  }
}
const handleEnroll = async () => {
  // 此方法已弃用，改为使用 handlePay 进行购买
  paymentDialogVisible.value = true
}
const handlePublishAssignment = async () => {
  if (!newAssignment.value.title) return ElMessage.warning('标题不能为空')
  try { await axios.post('/api/assignment/add', newAssignment.value); ElMessage.success('发布成功'); assignmentDialogVisible.value=false } 
  catch(e) { ElMessage.error('发布失败') }
}
const handleAddChapter = async () => {
  if (!newChapter.value.title) return ElMessage.warning('标题不能为空')
  try { await axios.post('/api/course/chapter/add', newChapter.value); ElMessage.success('添加成功'); chapterDialogVisible.value=false; fetchChapters(course.value.courseId) } 
  catch(e) { ElMessage.error('添加失败') }
}

onMounted(() => { init() })
</script>

<style scoped>
.video-player { width: 100%; height: 400px; background: #000; display: flex; justify-content: center; align-items: center; color: #fff; }
.real-video { width: 100%; height: 100%; }
.teacher-info { display: flex; align-items: center; gap: 15px; }
.chapter-item { padding: 12px; border-bottom: 1px solid #f0f0f0; display: flex; justify-content: space-between; align-items: center; cursor: pointer;}
.chapter-item:hover { background: #f5f7fa; }
.chapter-item.active { background: #e6f7ff; color: #409EFF; }
.chapter-info { display: flex; align-items: center; flex: 1; }

.qa-list { margin-top: 10px; }
.qa-item { padding: 15px 0; border-bottom: 1px solid #eee; }
.qa-reply { background: #f9f9f9; padding: 10px; border-radius: 4px; margin-top: 5px; font-size: 14px;}
.teacher-tag { color: #409EFF; font-weight: bold; }

/* 👇 新增评价样式 */
.review-box { background: #fdfdfd; padding: 15px; border: 1px solid #eee; border-radius: 4px; margin-bottom: 20px;}
.review-item { padding: 15px 0; border-bottom: 1px solid #eee; }
.review-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; font-size: 14px; color: #666; }
.review-content { font-size: 15px; color: #333; line-height: 1.5; padding-left: 40px;}
.student-name { font-weight: bold; color: #333; }
.time { margin-left: auto; font-size: 12px; color: #999; }
</style>