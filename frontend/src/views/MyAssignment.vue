<template>
  <div class="assignment-container">
    <h2>📝 我的作业列表</h2>

    <el-card>
      <el-table :data="assignments" stripe style="width: 100%">
        
        <el-table-column prop="title" label="作业标题" width="200">
          <template #default="scope">
            <span style="font-weight: bold">{{ scope.row.title }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="作业要求" />

        <el-table-column prop="deadline" label="截止时间" width="180">
           <template #default="scope">
             {{ scope.row.deadline ? scope.row.deadline.replace('T', ' ') : '无' }}
           </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已提交' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="成绩" width="100">
          <template #default="scope">
            <span v-if="scope.row.status === '已提交' && scope.row.score != null" 
                  style="color: #67C23A; font-weight: bold; font-size: 16px;">
              {{ scope.row.score }} 分
            </span>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button 
              size="small" 
              type="primary" 
              :disabled="scope.row.status === '已提交'"
              @click="openSubmitDialog(scope.row.id)">
              {{ scope.row.status === '已提交' ? '已完成' : '提交' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="提交作业"
      width="500px"
    >
      <el-form>
        <el-form-item label="作业内容">
          <el-input
            v-model="submitContent"
            :rows="5"
            type="textarea"
            placeholder="请输入你的答案，或者粘贴网盘链接..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSubmit">确认提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const assignments = ref([])
const dialogVisible = ref(false)
const submitContent = ref('')
const currentAssignmentId = ref(null)

// 获取我的作业
const fetchAssignments = async () => {
  try {
    const res = await axios.get('/api/assignment/my-list')
    assignments.value = res.data
  } catch (error) {
    console.error('获取作业失败', error)
  }
}

// 打开弹窗
const openSubmitDialog = (id) => {
  currentAssignmentId.value = id
  submitContent.value = ''
  dialogVisible.value = true
}

// 确认提交
const confirmSubmit = async () => {
  if (!submitContent.value) {
    ElMessage.warning('请填写作业内容')
    return
  }
  try {
    await axios.post('/api/assignment/submit', {
      assignmentId: currentAssignmentId.value,
      content: submitContent.value
    })
    ElMessage.success('提交成功！')
    dialogVisible.value = false
    fetchAssignments() // 刷新列表，状态变更为已提交
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

onMounted(() => {
  fetchAssignments()
})
</script>

<style scoped>
.assignment-container {
  padding: 20px;
}
h2 {
  margin-bottom: 20px;
  color: #333;
}
</style>