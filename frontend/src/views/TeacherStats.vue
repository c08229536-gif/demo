<template>
  <div class="stats-container">
    <div class="page-header">
      <h2>📊 班级学情统计</h2>
      <el-button type="primary" :icon="Refresh" @click="fetchData">刷新数据</el-button>
    </div>

    <!-- 顶部数据概览 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="8">
        <el-card shadow="hover" class="data-card">
          <div class="card-content">
            <div class="label">负责课程总数</div>
            <div class="value">{{ statsData.totalCourses || 0 }}</div>
          </div>
          <el-icon class="card-icon"><Reading /></el-icon>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="data-card blue">
          <div class="card-content">
            <div class="label">覆盖学生总数</div>
            <div class="value">{{ statsData.totalStudents || 0 }}</div>
          </div>
          <el-icon class="card-icon"><UserFilled /></el-icon>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="data-card green">
          <div class="card-content">
            <div class="label">平均作业得分</div>
            <div class="value">{{ totalAvgScore }}</div>
          </div>
          <el-icon class="card-icon"><Edit /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover" header="课程人数分布">
          <div ref="pieChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" header="课程平均成绩对比">
          <div ref="barChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细列表 -->
    <el-card shadow="hover" style="margin-top: 20px;" header="课程学情明细">
      <el-table :data="statsData.courseDetails" stripe style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="studentCount" label="选课人数" align="center" sortable />
        <el-table-column prop="avgScore" label="平均成绩" align="center" sortable>
          <template #default="{row}">
            <span :style="{ color: row.avgScore >= 60 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
              {{ row.avgScore }} 分
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150">
          <template #default="{row}">
            <el-button type="primary" link :icon="UserFilled" @click="viewStudents(row)">
              查看名单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生名单对话框 -->
    <el-dialog
      v-model="studentDialogVisible"
      :title="`选课名单 - ${currentCourseName}`"
      width="800px"
      destroy-on-close
    >
      <el-table :data="studentList" v-loading="loadingStudents" stripe>
        <el-table-column label="头像" width="70">
          <template #default="{row}">
            <el-avatar :size="32" :src="row.avatar || ''">{{ row.realName?.charAt(0) || row.username?.charAt(0) }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="studentNo" label="学号" width="150" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
        <el-table-column prop="enrollTime" label="选课时间" width="160">
          <template #default="{row}">
            {{ row.enrollTime ? new Date(row.enrollTime).toLocaleString() : '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import { Refresh, Reading, UserFilled, Edit } from '@element-plus/icons-vue'

const statsData = ref({
  totalCourses: 0,
  totalStudents: 0,
  courseDetails: []
})

const pieChartRef = ref(null)
const barChartRef = ref(null)

// 学生名单相关
const studentDialogVisible = ref(false)
const studentList = ref([])
const loadingStudents = ref(false)
const currentCourseName = ref('')

const totalAvgScore = computed(() => {
  if (!statsData.value.courseDetails || statsData.value.courseDetails.length === 0) return 0
  const sum = statsData.value.courseDetails.reduce((acc, curr) => acc + curr.avgScore, 0)
  return (sum / statsData.value.courseDetails.length).toFixed(1)
})

const fetchData = async () => {
  try {
    const res = await axios.get('/api/statistics/teacher/overview')
    statsData.value = res.data
    await nextTick()
    initCharts()
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

const initCharts = () => {
  // 1. 饼图：人数分布
  const pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%', left: 'center' },
    series: [{
      name: '选课人数',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
      data: statsData.value.courseDetails.map(item => ({
        name: item.courseName,
        value: item.studentCount
      }))
    }]
  })

  // 2. 柱状图：平均分对比
  const barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', boundaryGap: [0, 0.01] },
    yAxis: { type: 'category', data: statsData.value.courseDetails.map(item => item.courseName) },
    series: [{
      name: '平均分',
      type: 'bar',
      data: statsData.value.courseDetails.map(item => item.avgScore),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  })

  window.addEventListener('resize', () => {
    pieChart.resize()
    barChart.resize()
  })
}

const viewStudents = async (row) => {
  currentCourseName.value = row.courseName
  studentDialogVisible.value = true
  loadingStudents.value = true
  try {
    const res = await axios.get(`/api/statistics/teacher/course/${row.courseId}/students`)
    studentList.value = res.data
  } catch (error) {
    console.error('获取学生名单失败', error)
  } finally {
    loadingStudents.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.stats-container { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px); }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; }
.overview-cards .data-card { position: relative; border: none; border-radius: 12px; overflow: hidden; height: 120px; display: flex; align-items: center; }
.data-card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.data-card.blue { background: linear-gradient(135deg, #2af598 0%, #009efd 100%); }
.data-card.green { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }

.card-content { flex: 1; padding-left: 20px; z-index: 1; }
.label { font-size: 14px; opacity: 0.8; margin-bottom: 8px; }
.value { font-size: 32px; font-weight: bold; }
.card-icon { position: absolute; right: -10px; bottom: -10px; font-size: 80px; opacity: 0.2; transform: rotate(-15deg); }

:deep(.el-card__header) { font-weight: bold; border-bottom: 1px solid #f0f0f0; }
</style>
