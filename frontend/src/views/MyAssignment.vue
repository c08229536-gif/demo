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

    <el-dialog v-model="dialogVisible" title="提交作业" width="500px">
      <el-form label-position="top">
        <el-form-item label="作业文本内容">
          <el-input
            v-model="submitForm.content"
            :rows="4"
            type="textarea"
            placeholder="在此输入答案..."
          />
        </el-form-item>
        
        <el-form-item label="图片附件 (可选)">
          <el-input v-model="submitForm.imageUrl" placeholder="粘贴图片URL (如图床链接)" />
          <div style="margin-top: 10px;" v-if="submitForm.imageUrl">
             <span style="font-size: 12px; color: #999;">预览：</span>
             <img :src="submitForm.imageUrl" style="height: 100px; display: block; border-radius: 4px;" />
          </div>
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
import { ref, onMounted, reactive } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const assignments = ref([])
const dialogVisible = ref(false)
// 👇 改用对象来管理表单数据
const submitForm = reactive({
  assignmentId: null,
  content: '',
  imageUrl: ''
})

const fetchAssignments = async () => {
  try {
    const res = await axios.get('/api/assignment/my-list')
    assignments.value = res.data
  } catch (error) {
    console.error('获取作业失败', error)
  }
}

const openSubmitDialog = (id) => {
  submitForm.assignmentId = id
  submitForm.content = ''
  submitForm.imageUrl = ''
  dialogVisible.value = true
}

const confirmSubmit = async () => {
  if (!submitForm.content && !submitForm.imageUrl) {
    ElMessage.warning('内容和图片不能同时为空')
    return
  }
  try {
    await axios.post('/api/assignment/submit', submitForm)
    ElMessage.success('提交成功！')
    dialogVisible.value = false
    fetchAssignments() 
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

onMounted(() => {
  fetchAssignments()
})
</script>

<style scoped>
.assignment-container { padding: 20px; }
</style>