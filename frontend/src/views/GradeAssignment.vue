<template>
  <div class="grade-container">
    <h2>🍎 教师批改台</h2>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <h3>作业列表</h3>
          <el-menu @select="handleSelectAssignment">
            <el-menu-item v-for="item in assignmentList" :key="item.id" :index="item.id.toString()">
              <span>{{ item.title }}</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card v-if="currentAssignmentId">
          <template #header>
             <h3>提交记录 ({{ submissionList.length }} 人已交)</h3>
          </template>
          
          <el-table :data="submissionList" stripe>
            <el-table-column prop="studentId" label="学生ID" width="80" />
            <el-table-column prop="content" label="作业内容" />
            <el-table-column prop="submitTime" label="提交时间" width="180">
               <template #default="scope">
                 {{ scope.row.submitTime.replace('T', ' ') }}
               </template>
            </el-table-column>
            
            <el-table-column label="分数" width="150">
              <template #default="scope">
                <div v-if="scope.row.score !== null" style="color: green; font-weight: bold;">
                  {{ scope.row.score }} 分
                </div>
                <div v-else style="display: flex; gap: 5px;">
                  <el-input-number v-model="scope.row.tempScore" :min="0" :max="100" size="small" />
                  <el-button type="primary" size="small" @click="handleGrade(scope.row)">打分</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        
        <el-empty v-else description="请在左侧选择一个作业来批改" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const assignmentList = ref([])
const submissionList = ref([])
const currentAssignmentId = ref(null)

// 1. 加载所有作业 (为了简单，我们直接复用 course/list 接口不太行，这里我们手写一个假的作业列表或者查所有作业)
// 为了演示方便，我们暂时只查"我的作业"里的那些作业ID，或者直接查 assignment 表
// 真正的系统应该有一个 /assignment/all 接口，这里我们先用 "我的作业" 接口顶一下，假装我是老师也是学生
const fetchAssignments = async () => {
  const res = await axios.get('/api/assignment/my-list')
  assignmentList.value = res.data
}

// 2. 选中作业，查提交记录
const handleSelectAssignment = async (index) => {
  currentAssignmentId.value = index
  const res = await axios.get(`/api/assignment/${index}/submissions`)
  submissionList.value = res.data
  // 给每个数据加一个临时分数字段，用来绑定输入框
  submissionList.value.forEach(item => item.tempScore = 80)
}

// 3. 提交打分
const handleGrade = async (row) => {
  try {
    await axios.post('/api/assignment/grade', {
      id: row.id,
      score: row.tempScore
    })
    ElMessage.success('打分成功！')
    row.score = row.tempScore // 更新视图状态，把输入框变成分数文字
  } catch (error) {
    ElMessage.error('打分失败')
  }
}

onMounted(() => {
  fetchAssignments()
})
</script>

<style scoped>
.grade-container {
  padding: 20px;
}
</style>