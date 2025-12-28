<template>
  <div style="padding: 20px;">
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: bold;">首页运营管理</span>
          <el-button type="primary" @click="handleAdd">新增轮播图</el-button>
        </div>
      </template>

      <el-table :data="banners" border>
        <el-table-column prop="title" label="标题" />
        <el-table-column label="图片预览" width="180">
          <template #default="scope">
            <el-image :src="scope.row.imageUrl" style="width: 120px; height: 60px;" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序权重" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.isActive" :active-value="1" :inactive-value="0" @change="toggleStatus(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播' : '新增轮播'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="可直接输入链接，或点击右侧上传">
            <template #append>
              <el-upload
                action="/api/upload"
                :show-file-list="false"
                :on-success="handleImageUploadSuccess"
                :before-upload="beforeImageUpload"
              >
                <el-button><el-icon><Upload /></el-icon></el-button>
              </el-upload>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'

const banners = ref([])
const dialogVisible = ref(false)
const form = ref({ title: '', imageUrl: '', linkUrl: '', sortOrder: 0, isActive: 1 })

// 👈 修复点：添加 /admin 前缀
const fetchBanners = async () => {
  try {
    const res = await axios.get('/api/admin/banners')
    banners.value = res.data
  } catch(e) { ElMessage.error('获取轮播失败') }
}

const handleAdd = () => {
  form.value = { title: '', imageUrl: '', linkUrl: '', sortOrder: 0, isActive: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    // 👈 修复点：添加 /admin 前缀
    await axios.post('/api/admin/banners/save', form.value)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    fetchBanners()
  } catch(e) { ElMessage.error('保存失败') }
}

const toggleStatus = async (row) => {
  await axios.post('/api/admin/banners/save', row)
  ElMessage.success('状态更新成功')
}

const handleDelete = async (id) => {
  try {
    // 👈 修复点：添加 /admin 前缀
    await axios.delete(`/api/admin/banners/${id}`)
    ElMessage.success('删除成功')
    fetchBanners()
  } catch(e) { ElMessage.error('删除失败') }
}

const handleImageUploadSuccess = (response) => {
  form.value.imageUrl = response
  ElMessage.success('上传成功')
}

const beforeImageUpload = (rawFile) => {
  const isImg = ['image/jpeg', 'image/png', 'image/gif'].includes(rawFile.type)
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isImg) {
    ElMessage.error('只能上传 JPG/PNG/GIF 格式的图片!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }
  return isImg && isLt2M
}

onMounted(() => fetchBanners())
</script>