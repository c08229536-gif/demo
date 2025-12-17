<template>
  <div class="audit-container">
    <h2>⚖️ 课程审核台</h2>

    <el-card>
      <el-table :data="pendingCourses" stripe style="width: 100%">
        <el-table-column label="封面" width="120">
          <template #default="scope">
            <img :src="scope.row.cover" style="width: 100px; height: 60px; object-fit: cover; border-radius: 4px;" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="课程标题" width="200" />
        <el-table-column prop="teacher" label="申请教师" width="120" />
        <el-table-column prop="description" label="简介" />
        
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="success" size="small" @click="handleAudit(scope.row, true)">通过</el-button>
            <el-button type="danger" size="small" @click="handleAudit(scope.row, false)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="pendingCourses.length === 0" description="暂无待审核课程，太棒了！" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const pendingCourses = ref([])

// 加载待审核列表
const fetchPending = async () => {
  try {
    const res = await axios.get('/api/admin/courses/pending')
    pendingCourses.value = res.data
  } catch (error) {
    console.error(error)
  }
}

// 审核操作
const handleAudit = (course, pass) => {
  const actionText = pass ? '通过' : '驳回'
  const confirmType = pass ? 'success' : 'warning'

  ElMessageBox.confirm(
    `确定要 [${actionText}] 课程《${course.title}》吗？`,
    '审核确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: confirmType }
  ).then(async () => {
    try {
      await axios.post('/api/admin/course/audit', {
        courseId: course.courseId,
        pass: pass
      })
      ElMessage.success(`课程已${actionText}`)
      fetchPending() // 刷新列表
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

onMounted(() => {
  fetchPending()
})
</script>

<style scoped>
.audit-container { padding: 20px; }
</style>