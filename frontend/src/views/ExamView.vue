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
          
          <el-radio-group v-if="q.type === '单选'" v-model="answers[q.id]" style="display: flex; flex-direction: column; gap: 15px; margin-top: 15px;">
            <el-radio v-for="(val, key) in parseOptions(q.options)" :key="key" :label="key">
              {{ key }}. {{ val }}
            </el-radio>
          </el-radio-group>

          <el-radio-group v-else-if="q.type === '判断'" v-model="answers[q.id]" style="margin-top: 15px;">
            <el-radio label="正确">正确</el-radio>
            <el-radio label="错误">错误</el-radio>
          </el-radio-group>
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
    // 这里不直接倒计时，等用户点击
    timeLeft.value = (res.data.info.duration || 60) * 60
  } catch (error) {
    ElMessage.error('获取考试信息失败')
  }
}

const startExam = () => {
  ElMessageBox.confirm('点击开始后将立即开始倒计时，确定现在开始吗？', '开始考试', {
     confirmButtonText: '立即开始',
     cancelButtonText: '稍后再来',
     type: 'info'
  }).then(() => {
     isStarted.value = true
     startTimer()
  })
}

const handleDownload = (wordUrl) => {
  if (!wordUrl) return
  const fileName = wordUrl.substring(wordUrl.lastIndexOf('/') + 1)
  const downloadUrl = `/api/upload/download?fileName=${fileName}`
  window.open(downloadUrl, '_self')
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
  }
}

onMounted(() => fetchExamData())
onUnmounted(() => clearInterval(timer))
</script>
