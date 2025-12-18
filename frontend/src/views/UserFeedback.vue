<template>
  <div class="feedback-page">
    <h2>📝 意见反馈</h2>
    <el-row :gutter="40">
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header><h3>我要反馈</h3></template>
          <el-form label-position="top">
            <el-form-item label="反馈类型">
              <el-radio-group v-model="form.type">
                <el-radio label="建议">功能建议</el-radio>
                <el-radio label="BUG">系统BUG</el-radio>
                <el-radio label="投诉">违规投诉</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="详细内容">
              <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请详细描述您遇到的问题或建议..." />
            </el-form-item>
            <el-form-item label="联系方式 (选填)">
              <el-input v-model="form.contact" placeholder="手机号/邮箱，方便我们联系您" />
            </el-form-item>
            <el-button type="primary" style="width: 100%" @click="submitFeedback">提交反馈</el-button>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card shadow="hover">
          <template #header><h3>我的反馈记录</h3></template>
          <el-timeline>
            <el-timeline-item v-for="item in list" :key="item.id" :timestamp="item.createTime?.substring(0,10)" placement="top">
              <el-card :body-style="{ padding: '15px' }">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <el-tag :type="item.type==='BUG'?'danger':'primary'" size="small">{{ item.type }}</el-tag>
                  <span style="font-size: 12px; color: #999" v-if="item.status==='待处理'">等待管理员处理...</span>
                  <el-tag v-else type="success" size="small">已回复</el-tag>
                </div>
                <p style="margin: 10px 0;">{{ item.content }}</p>
                
                <div v-if="item.reply" style="background: #f0f9eb; padding: 10px; border-radius: 4px; border-left: 3px solid #67C23A;">
                  <span style="color: #67C23A; font-weight: bold; font-size: 13px;">管理员回复：</span>
                  <div style="font-size: 13px; color: #333; margin-top: 5px;">{{ item.reply }}</div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const list = ref([])
const form = reactive({ type: '建议', content: '', contact: '' })

const fetchMyFeedback = async () => {
  const res = await axios.get('/api/sys-feedback/my')
  list.value = res.data
}

const submitFeedback = async () => {
  if(!form.content) return ElMessage.warning('请填写内容')
  try {
    await axios.post('/api/sys-feedback/add', form)
    ElMessage.success('提交成功')
    form.content = ''
    fetchMyFeedback()
  } catch(e) { ElMessage.error('提交失败') }
}

onMounted(() => fetchMyFeedback())
</script>

<style scoped> .feedback-page { padding: 20px; } </style>