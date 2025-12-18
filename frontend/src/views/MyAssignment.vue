<template>
  <div class="my-assignment">
    <h2>📝 我的作业</h2>

    <el-card>
      <el-table :data="assignments" stripe style="width: 100%">
        <el-table-column prop="title" label="作业标题" width="200">
          <template #default="{row}">
            <span style="font-weight: bold">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="作业要求" show-overflow-tooltip />
        <el-table-column prop="deadline" label="截止时间" width="180">
           <template #default="{row}">{{ row.deadline ? row.deadline.replace('T', ' ') : '无' }}</template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{row}">
            <el-tag v-if="row.status === '已批改'" type="success">已批改</el-tag>
            <el-tag v-else-if="row.status === '已提交'" type="primary">已提交</el-tag>
            <el-tag v-else type="info">待提交</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="成绩/评语">
          <template #default="{row}">
             <div v-if="row.status === '已批改'">
               <span style="font-weight: bold; color: #67C23A; font-size: 16px;">{{ row.score }}分</span>
               <div v-if="row.feedback" style="font-size: 12px; color: #666; margin-top: 4px;">评语: {{ row.feedback }}</div>
             </div>
             <div v-else>-</div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120">
          <template #default="{row}">
            <el-button 
              v-if="row.status === '待提交'" 
              type="primary" size="small" 
              @click="openSubmitDialog(row.id)">
              去提交
            </el-button>
            <el-button v-else type="info" plain size="small" disabled>
              {{ row.status === '已批改' ? '已完成' : '已提交' }}
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
    // 👇 关键修改：将 imageUrl 映射为后端需要的 fileUrl 字段
    await axios.post('/api/assignment/submit', {
      assignmentId: submitForm.assignmentId,
      content: submitForm.content,
      fileUrl: submitForm.imageUrl // 映射字段
    })
    ElMessage.success('提交成功！')
    dialogVisible.value = false
    fetchAssignments() 
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '提交失败')
  }
}

onMounted(() => {
  fetchAssignments()
})
</script>

<style scoped>
.my-assignment { padding: 20px; }
</style>