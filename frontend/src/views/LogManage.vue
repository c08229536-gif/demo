<template>
  <div style="padding: 20px;">
    <el-card shadow="never">
      <template #header>
        <div style="font-weight: bold;">系统操作日志</div>
      </template>

      <el-table :data="logList" border stripe style="width: 100%;">
        <el-table-column prop="createTime" label="操作时间" width="180">
          <template #default="scope">
             {{ scope.row.createTime ? scope.row.createTime.replace('T', ' ') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="operation" label="行为描述" width="150" />
        <el-table-column prop="method" label="请求方法" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="params" label="请求参数" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const logList = ref([])

const fetchLogs = async () => {
  try {
    const res = await axios.get('/api/admin/logs')
    // 👈 修正：后端 AdminController 的 getLogs 返回的是 List，直接赋值即可
    logList.value = res.data 
  } catch (error) {
    ElMessage.error('获取日志失败')
  }
}

onMounted(() => fetchLogs())
</script>