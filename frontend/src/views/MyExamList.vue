<template>
  <div class="exam-list-container" style="padding: 20px;">
    <h2>📑 我的考试</h2>
    
    <el-card>
      <el-table :data="examList" stripe style="width: 100%">
        <el-table-column prop="title" label="考试标题" min-width="200" />
        <el-table-column prop="courseName" label="所属课程" width="180">
           <template #default="scope">
             <el-tag>{{ scope.row.courseName || '未知课程' }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="120" />
        <el-table-column prop="totalScore" label="总分" width="100" />
        
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '已提交'" type="success">已完成</el-tag>
            <el-tag v-else-if="scope.row.status === '进行中'" type="warning">进行中</el-tag>
            <el-tag v-else type="info">未开始</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="得分" width="100">
           <template #default="scope">
             <span v-if="scope.row.status === '已提交'" style="font-weight: bold; color: #67C23A;">
               {{ scope.row.score }} 分
             </span>
             <span v-else>-</span>
           </template>
        </el-table-column>

        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status !== '已提交'" 
              type="primary" 
              size="small" 
              @click="goToExam(scope.row.id)"
            >
              {{ scope.row.status === '进行中' ? '继续考试' : '进入考试' }}
            </el-button>
            <el-button v-else type="info" size="small" disabled>已结束</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="examList.length === 0" description="暂无考试安排" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const examList = ref([])

const fetchExams = async () => {
  try {
    const res = await axios.get('/api/exam/my-list')
    examList.value = res.data
  } catch (error) {
    ElMessage.error('加载考试列表失败')
  }
}

const goToExam = (id) => {
  router.push(`/home/exam/${id}`)
}

onMounted(() => {
  fetchExams()
})
</script>
