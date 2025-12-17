<template>
  <div class="grade-container">
    <h2>🍎 教师批改台</h2>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <h3>作业任务</h3>
          <el-menu @select="handleSelectAssignment" class="assignment-menu">
            <el-menu-item v-for="item in assignmentList" :key="item.id" :index="item.id.toString()">
              <el-icon><Document /></el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card v-if="currentAssignmentId">
          <template #header>
             <div style="display: flex; justify-content: space-between; align-items: center;">
               <h3>提交记录 ({{ submissionList.length }} 人已交)</h3>
               <el-button type="primary" link @click="fetchSubmissions(currentAssignmentId)">刷新</el-button>
             </div>
          </template>
          
          <el-table :data="submissionList" stripe>
            <el-table-column prop="studentId" label="学生ID" width="80" />
            
            <el-table-column label="作业概览">
              <template #default="scope">
                <div class="content-preview">
                   {{ scope.row.content ? scope.row.content.substring(0, 20) + (scope.row.content.length > 20 ? '...' : '') : '[纯图片提交]' }}
                </div>
                <el-tag v-if="scope.row.imageUrl" size="small" type="warning">含图片</el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="submitTime" label="提交时间" width="170">
               <template #default="scope">
                 {{ scope.row.submitTime.replace('T', ' ') }}
               </template>
            </el-table-column>
            
            <el-table-column label="操作/打分" width="220">
              <template #default="scope">
                <div style="display: flex; align-items: center; gap: 10px;">
                  <el-button size="small" @click="viewDetail(scope.row)">批阅</el-button>

                  <div v-if="scope.row.score !== null" style="color: green; font-weight: bold;">
                    {{ scope.row.score }} 分
                  </div>
                  <div v-else style="color: #999; font-size: 12px;">(未打分)</div>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        
        <el-empty v-else description="请在左侧选择一个作业来批改" />
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="作业详情与打分" width="600px">
      <div v-if="currentSubmission">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学生ID">{{ currentSubmission.studentId }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentSubmission.submitTime.replace('T', ' ') }}</el-descriptions-item>
          
          <el-descriptions-item label="文本内容">
            <div style="white-space: pre-wrap;">{{ currentSubmission.content || '无文本内容' }}</div>
          </el-descriptions-item>
          
          <el-descriptions-item label="图片附件">
            <div v-if="currentSubmission.imageUrl">
              <el-image 
                style="width: 100%; max-height: 400px;" 
                :src="currentSubmission.imageUrl" 
                :preview-src-list="[currentSubmission.imageUrl]"
                fit="contain" 
              />
            </div>
            <div v-else>无图片</div>
          </el-descriptions-item>
        </el-descriptions>

        <div class="grade-section">
          <span>给分：</span>
          <el-input-number v-model="currentSubmission.tempScore" :min="0" :max="100" />
          <el-button type="primary" @click="handleGrade">确认打分</el-button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'

const assignmentList = ref([])
const submissionList = ref([])
const currentAssignmentId = ref(null)

// 弹窗控制
const dialogVisible = ref(false)
const currentSubmission = ref(null)

// 1. 加载所有作业
const fetchAssignments = async () => {
  const res = await axios.get('/api/assignment/all')
  assignmentList.value = res.data
}

// 2. 选中作业
const handleSelectAssignment = (index) => {
  currentAssignmentId.value = index
  fetchSubmissions(index)
}

const fetchSubmissions = async (assignmentId) => {
  const res = await axios.get(`/api/assignment/${assignmentId}/submissions`)
  submissionList.value = res.data
  // 初始化临时分数，如果有分就显示原分，没分默认80
  submissionList.value.forEach(item => item.tempScore = item.score || 80)
}

// 👇 3. 打开批改弹窗
const viewDetail = (row) => {
  currentSubmission.value = row
  dialogVisible.value = true
}

// 4. 提交打分
const handleGrade = async () => {
  try {
    await axios.post('/api/assignment/grade', {
      id: currentSubmission.value.id,
      score: currentSubmission.value.tempScore
    })
    ElMessage.success('打分成功！')
    dialogVisible.value = false // 关弹窗
    
    // 更新列表里的显示状态
    currentSubmission.value.score = currentSubmission.value.tempScore
  } catch (error) {
    ElMessage.error('打分失败')
  }
}

onMounted(() => {
  fetchAssignments()
})
</script>

<style scoped>
.grade-container { padding: 20px; }
.assignment-menu { border-right: none; }
.content-preview {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}
.grade-section {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  justify-content: flex-end;
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}
</style>