<template>
  <div class="admin-feedback">
    <h2>🛠️ 反馈处理中心</h2>
    <el-card>
      <el-table :data="list" stripe style="width: 100%">
        <el-table-column label="类型" width="100">
          <template #default="{row}">
            <el-tag :type="row.type==='BUG'?'danger':'primary'">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="提交人" width="120" />
        <el-table-column prop="content" label="反馈内容" show-overflow-tooltip />
        <el-table-column prop="contact" label="联系方式" width="150" />
        <el-table-column prop="createTime" label="时间" width="160">
           <template #default="{row}">{{ row.createTime?.substring(0,16).replace('T',' ') }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{row}">
            <el-tag v-if="row.status==='已回复'" type="success">已回复</el-tag>
            <el-tag v-else type="danger">待处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{row}">
            <el-button size="small" type="primary" @click="openReply(row)" :disabled="row.status==='已回复'">
              {{ row.status==='已回复'?'已办':'回复' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="回复用户反馈" width="500px">
      <div style="margin-bottom: 15px; padding: 10px; background: #f5f7fa; border-radius: 4px;">
        <span style="font-weight: bold;">用户说：</span> {{ currentItem.content }}
      </div>
      <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="请输入处理结果，将展示给用户..." />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReply">发送回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const list = ref([])
const dialogVisible = ref(false)
const currentItem = ref({})
const replyContent = ref('')

const fetchAll = async () => {
  const res = await axios.get('/api/sys-feedback/all')
  list.value = res.data
}

const openReply = (row) => {
  currentItem.value = row
  replyContent.value = ''
  dialogVisible.value = true
}

const confirmReply = async () => {
  if(!replyContent.value) return ElMessage.warning('请输入回复内容')
  try {
    await axios.post('/api/sys-feedback/reply', { id: currentItem.value.id, reply: replyContent.value })
    ElMessage.success('回复成功')
    dialogVisible.value = false
    fetchAll()
  } catch(e) { ElMessage.error('操作失败') }
}

onMounted(() => fetchAll())
</script>

<style scoped> .admin-feedback { padding: 20px; } </style>