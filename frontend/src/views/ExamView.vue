<template>
  <div class="exam-container" style="padding: 20px; background-color: #f5f7fa; min-height: 90vh;">
    <el-card shadow="never" style="max-width: 900px; margin: 0 auto;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <h2 style="margin: 0;">{{ examInfo.title || '在线测验' }}</h2>
            <div v-if="examInfo.wordUrl" style="margin-top: 8px;">
              <el-link
                type="primary"
                @click="handleDownload(examInfo.wordUrl)"
              >
                下载Word版试卷
              </el-link>
            </div>
          </div>
          <div style="display: flex; align-items: center; gap: 15px;">
             <el-button v-if="!isStarted" type="primary" size="large" @click="startExam">开始考试</el-button>
             <el-tag v-else type="danger" size="large" effect="dark">
               剩余时间：{{ formatTime(timeLeft) }}
             </el-tag>
          </div>
        </div>
      </template>

      <div v-if="isStarted">
        <div v-for="(q, index) in questions" :key="q.id" style="margin-bottom: 40px; border-bottom: 1px solid #eee; padding-bottom: 20px;">
          <p style="font-size: 16px; font-weight: bold;">
            {{ index + 1 }}. {{ q.content }} 
            <span style="color: #909399; font-weight: normal; font-size: 14px;">({{ q.score }}分)</span>
          </p>
          
          <el-radio-group v-if="q.type === '单选' || q.type === 'single'" v-model="answers[q.id]" style="display: flex; flex-direction: column; gap: 15px; margin-top: 15px; align-items: flex-start;">
            <el-radio v-for="(val, key) in parseOptions(q.options)" :key="key" :label="key" style="margin-left: 0;">
              {{ key }}. {{ val }}
            </el-radio>
          </el-radio-group>

          <el-radio-group v-else-if="q.type === '判断' || q.type === 'judge'" v-model="answers[q.id]" style="margin-top: 15px;">
            <el-radio label="正确">正确</el-radio>
            <el-radio label="错误">错误</el-radio>
          </el-radio-group>

          <div v-else-if="q.type === '简答' || q.type === 'text'" style="margin-top: 15px;">
             <el-input
               v-model="answers[q.id]"
               type="textarea"
               :rows="4"
               placeholder="请输入您的答案..."
             />
          </div>
        </div>

        <div style="text-align: center; margin-top: 30px;">
          <el-button type="primary" size="large" @click="confirmSubmit" :loading="submitting">提交试卷</el-button>
        </div>
      </div>
      
      <div v-else style="padding: 40px; text-align: center; color: #666;">
         <el-icon size="60"><Warning /></el-icon>
         <p>请点击右上角“开始考试”按钮开始作答，倒计时将在点击后开始。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const examInfo = ref({})
const questions = ref([])
const answers = ref({})
const timeLeft = ref(0)
const submitting = ref(false)
const isStarted = ref(false) // 新增：是否已开始
let timer = null

// 加载考试数据
const fetchExamData = async () => {
  try {
    const examId = route.params.id || 1 // 默认取ID为1的考试
    const res = await axios.get(`/api/exam/${examId}`)
    examInfo.value = res.data.info
    questions.value = res.data.questions
    
    // 初始化答案对象，防止 v-model 绑定问题
    questions.value.forEach(q => {
      if (!answers.value[q.id]) {
        answers.value[q.id] = ''
      }
    })

    // 这里不直接倒计时，等用户点击
    // timeLeft.value = (res.data.info.duration || 60) * 60
    
    // 检查是否已经在考试中
    autoStartIfResuming()
  } catch (error) {
    ElMessage.error('获取考试信息失败')
  }
}

const startExam = async () => {
  // 如果已经开始过（继续考试），直接进入
  if (isStarted.value) return

  // 否则确认开始
  ElMessageBox.confirm('点击开始后将立即开始倒计时，确定现在开始吗？', '开始考试', {
     confirmButtonText: '立即开始',
     cancelButtonText: '稍后再来',
     type: 'info'
  }).then(async () => {
     try {
       // 调用后端开始接口，获取真实倒计时
       const res = await axios.post('/api/exam/start', { examId: examInfo.value.id })
       const { remainingSeconds, status } = res.data
       
       if (status === 1) {
         ElMessage.warning('您已提交过该试卷')
         router.push('/home/my-exams')
         return
       }
       
       timeLeft.value = remainingSeconds
       isStarted.value = true
       startTimer()
       startAntiCheat() // 开启防作弊
     } catch (e) {
       ElMessage.error('开始考试失败')
     }
  })
}

// 自动进入（如果是继续考试）
const autoStartIfResuming = async () => {
    try {
       const res = await axios.post('/api/exam/start', { examId: examInfo.value.id })
       const { remainingSeconds, status } = res.data
       if (status === 0 && remainingSeconds < (examInfo.value.duration * 60)) {
           // 说明已经在进行中了，自动继续
           timeLeft.value = remainingSeconds
           isStarted.value = true
           startTimer()
           startAntiCheat()
       }
    } catch(e){}
}

const handleDownload = (wordUrl) => {
  if (!wordUrl) return
  const fileName = wordUrl.substring(wordUrl.lastIndexOf('/') + 1)
  const downloadUrl = `/api/upload/download?fileName=${fileName}`
  window.open(downloadUrl, '_self')
}

// 防作弊逻辑
const startAntiCheat = () => {
  document.addEventListener('visibilitychange', handleVisibilityChange)
}
const handleVisibilityChange = async () => {
  if (document.hidden && isStarted.value && !submitting.value) {
     try {
       await axios.post('/api/exam/switch-blur', { examId: examInfo.value.id })
       ElMessage.warning({
         message: '检测到切出考试界面，系统已记录！累计3次将自动交卷！',
         duration: 5000
       })
       // 也可以在这里查询最新次数，如果 >=3 前端主动提交
       // 但为了安全，最好是下一次心跳或者提交时后端拒绝，这里简单点前端判断
       // 实际生产中建议后端通过WebSocket推送或者下一次请求返回强制提交指令
       // 这里我们简单再查一次状态
       const res = await axios.post('/api/exam/start', { examId: examInfo.value.id })
       if (res.data.switchCount >= 3) {
           ElMessageBox.alert('您切出界面次数过多，系统已自动交卷！', '强制交卷', {
               confirmButtonText: '确定',
               callback: () => autoSubmit()
           })
       }
     } catch(e) {}
  }
}

// 格式化时间
const formatTime = (seconds) => {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}分${s < 10 ? '0' + s : s}秒`
}

// 倒计时逻辑
const startTimer = () => {
  timer = setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value--
    } else {
      clearInterval(timer)
      autoSubmit()
    }
  }, 1000)
}

// 解析JSON选项
const parseOptions = (optStr) => {
  if (!optStr) return {}
  try { return JSON.parse(optStr) } catch (e) { return {} }
}

const confirmSubmit = () => {
  ElMessageBox.confirm('确认现在提交试卷吗？', '提示', { type: 'warning' }).then(() => {
    autoSubmit()
  })
}

const autoSubmit = async () => {
  submitting.value = true
  try {
    const res = await axios.post('/api/exam/submit', {
      examId: examInfo.value.id,
      answers: answers.value
    })
    ElMessage.success(`考试结束！得分：${res.data.score}`)
    router.push('/home/my-courses')
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
    clearInterval(timer)
    document.removeEventListener('visibilitychange', handleVisibilityChange)
  }
}

onMounted(() => fetchExamData())
onUnmounted(() => {
    clearInterval(timer)
    document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>
