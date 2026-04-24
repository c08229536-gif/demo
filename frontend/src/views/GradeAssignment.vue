<template>
  <div class="grade-container">
    <h2>🍎 教师批改台</h2>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>作业任务列表</span>
            </div>
          </template>
          <el-menu @select="handleSelectAssignment" class="assignment-menu">
            <el-empty v-if="assignmentList.length === 0" description="暂无发布作业" image-size="60" />
            <el-menu-item v-for="item in assignmentList" :key="item.id" :index="item.id.toString()">
              <el-icon :style="{ color: item.pendingCount > 0 ? '#E6A23C' : '#67C23A' }">
                <Document />
              </el-icon>
              <span class="menu-title" :title="item.title">{{ item.title }}</span>
              <el-badge v-if="item.pendingCount > 0" :value="item.pendingCount" class="pending-badge" type="danger" />
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card v-if="currentAssignmentId" shadow="hover">
          <template #header>
             <div style="display: flex; justify-content: space-between; align-items: center;">
               <h3>提交记录 ({{ submissionList.length }} 人已交)</h3>
               <el-button type="primary" link @click="fetchSubmissions(currentAssignmentId)">
                 <el-icon style="margin-right: 4px"><Refresh /></el-icon> 刷新
               </el-button>
             </div>
          </template>
          
          <el-table :data="submissionList" stripe style="width: 100%">
            <el-table-column prop="studentId" label="学生ID" width="80" />
            
            <el-table-column label="作业概览" show-overflow-tooltip>
              <template #default="{row}">
                <div class="content-preview">
                   {{ row.content ? row.content : '[无文本]' }}
                </div>
                <el-tag v-if="row.fileUrl" size="small" type="warning" effect="plain">
                  <el-icon><Picture /></el-icon> 含图片
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="submitTime" label="提交时间" width="160">
               <template #default="{row}">
                 {{ row.submitTime ? row.submitTime.replace('T', ' ').substring(0, 16) : '' }}
               </template>
            </el-table-column>
            
            <el-table-column label="状态" width="100">
              <template #default="{row}">
                <el-tag v-if="row.status === '已批改'" type="success">已批改</el-tag>
                <el-tag v-else type="danger">待批改</el-tag>
              </template>
            </el-table-column>

            <el-table-column label="操作/成绩" width="180">
              <template #default="{row}">
                <div style="display: flex; align-items: center; gap: 10px;">
                  <el-button size="small" type="primary" plain @click="viewDetail(row)">
                    {{ row.status === '已批改' ? '修改' : '批阅' }}
                  </el-button>

                  <div v-if="row.score !== null" style="color: #67C23A; font-weight: bold;">
                    {{ row.score }} 分
                  </div>
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
          <el-descriptions-item label="提交时间">{{ currentSubmission.submitTime?.replace('T', ' ') }}</el-descriptions-item>
          
          <el-descriptions-item label="文本内容">
            <div style="white-space: pre-wrap; line-height: 1.5;">{{ currentSubmission.content || '无文本内容' }}</div>
          </el-descriptions-item>
          
          <el-descriptions-item label="图片附件">
            <div v-if="currentSubmission.fileUrl">
              <el-image 
                style="width: 100%; max-height: 300px; border-radius: 4px;" 
                :src="currentSubmission.fileUrl" 
                :preview-src-list="[currentSubmission.fileUrl]"
                fit="contain" 
              />
            </div>
            <div v-else style="color: #999">无图片</div>
          </el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 20px;">
           <div style="margin-bottom: 8px; font-weight: bold;">老师评语：</div>
           <el-input 
              v-model="currentSubmission.tempFeedback" 
              type="textarea" 
              :rows="3" 
              placeholder="写点评语，鼓励一下学生吧..." 
           />
        </div>

        <div class="grade-section">
          <span>给分：</span>
          <el-input-number v-model="currentSubmission.tempScore" :min="0" :max="100" />
          <el-button type="primary" @click="handleGrade">确认提交</el-button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Document, Refresh, Picture } from '@element-plus/icons-vue'

const assignmentList = ref([])
const submissionList = ref([])
const currentAssignmentId = ref(null)

const dialogVisible = ref(false)
const currentSubmission = ref(null)

// 1. 加载所有作业
const fetchAssignments = async () => {
  try {
    const res = await axios.get('/api/assignment/all')
    assignmentList.value = res.data
  } catch(e) {}
}

// 2. 选中作业
const handleSelectAssignment = (index) => {
  currentAssignmentId.value = index
  fetchSubmissions(index)
}

const fetchSubmissions = async (assignmentId) => {
  try {
    const res = await axios.get(`/api/assignment/${assignmentId}/submissions`)
    submissionList.value = res.data
    // 初始化临时数据 (分数和评语)
    submissionList.value.forEach(item => {
      item.tempScore = item.score || 80
      item.tempFeedback = item.feedback || '' // 👇 初始化评语
    })
  } catch(e) { ElMessage.error('加载提交记录失败') }
}

// 3. 打开批改弹窗
const viewDetail = (row) => {
  currentSubmission.value = row
  dialogVisible.value = true
}

// 4. 提交打分 (包含评语)
const handleGrade = async () => {
  try {
    await axios.post('/api/assignment/grade', {
      id: currentSubmission.value.id,
      score: currentSubmission.value.tempScore,
      feedback: currentSubmission.value.tempFeedback // 👇 发送评语
    })
    
    ElMessage.success('打分成功，已通知学生！')
    dialogVisible.value = false 
    
    // 更新列表显示的最新状态
    currentSubmission.value.score = currentSubmission.value.tempScore
    currentSubmission.value.feedback = currentSubmission.value.tempFeedback
    currentSubmission.value.status = '已批改' // 👇 更新状态标签

    // 👇 实时更新左侧列表的待批改数量
    const assignment = assignmentList.value.find(a => a.id.toString() === currentAssignmentId.value)
    if (assignment && assignment.pendingCount > 0) {
      assignment.pendingCount--
    }
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
.assignment-menu { border-right: none; max-height: 500px; overflow-y: auto; }
.menu-title { 
  white-space: nowrap; 
  overflow: hidden; 
  text-overflow: ellipsis; 
  display: inline-block; 
  width: 140px;
  color: #333;
  font-weight: 500;
}
.pending-badge {
  margin-left: 5px;
}
.el-menu-item.is-active .menu-title {
  color: var(--el-color-primary);
  font-weight: bold;
}
.el-menu-item:hover {
  background-color: #f0f7ff !important;
}
.content-preview {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.grade-section {
  margin-top: 15px;
  display: flex;
  align-items: center;
  gap: 15px;
  justify-content: flex-end;
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}
</style>