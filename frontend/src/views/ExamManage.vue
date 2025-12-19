<template>
  <div style="padding: 20px;">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>发布新考试/测验</span>
          <el-button type="primary" @click="submitExam">立即发布</el-button>
        </div>
      </template>

      <el-form :model="form" label-width="100px">
        <el-form-item label="考试标题">
          <el-input v-model="form.title" placeholder="例如：Java基础单元测试" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="考试时长">
              <el-input-number v-model="form.duration" :min="1" /> 分钟
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="关联课程ID">
              <el-input-number v-model="form.courseId" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>题目列表</el-divider>

        <div v-for="(q, index) in form.questions" :key="index" class="q-box">
          <div style="margin-bottom: 10px; font-weight: bold;">第 {{ index + 1 }} 题</div>
          <el-form-item label="题干">
            <el-input v-model="q.content" type="textarea" />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="题目类型">
                <el-select v-model="q.type" style="width: 100%;">
                  <el-option label="单选" value="单选" />
                  <el-option label="判断" value="判断" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="分值">
                <el-input-number v-model="q.score" :min="1" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item v-if="q.type === '单选'" label="选项 (JSON)">
            <el-input v-model="q.options" placeholder='格式：{"A":"选项内容","B":"选项内容"}' />
          </el-form-item>
          <el-form-item label="正确答案">
            <el-input v-model="q.answer" placeholder="单选填字母，判断填'正确'或'错误'" />
          </el-form-item>
          <el-button type="danger" link @click="form.questions.splice(index, 1)">删除此题</el-button>
        </div>

        <el-button type="dashed" style="width: 100%; margin-top: 10px;" @click="addQuestion">
          + 添加题目
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const form = ref({
  title: '',
  duration: 60,
  courseId: 1,
  totalScore: 100,
  questions: []
})

const addQuestion = () => {
  form.value.questions.push({
    content: '', type: '单选', options: '{"A":"","B":"","C":"","D":""}', answer: '', score: 10
  })
}

const submitExam = async () => {
  if (!form.value.title) return ElMessage.warning('请填写考试标题')
  try {
    await axios.post('/api/admin/exam/publish', form.value)
    ElMessage.success('试卷发布成功！')
  } catch (e) {
    ElMessage.error('发布失败')
  }
}
</script>

<style scoped>
.q-box { border: 1px solid #eee; padding: 20px; margin-bottom: 20px; border-radius: 8px; background: #fafafa; }
</style>