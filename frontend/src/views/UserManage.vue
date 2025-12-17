<template>
  <div class="user-manage">
    <h2>🛡️ 用户权限管理</h2>

    <div style="margin-bottom: 20px;">
      <el-button type="primary" @click="openAddDialog">
        <el-icon style="margin-right: 5px"><Plus /></el-icon>
        新增用户 (分配账号)
      </el-button>
    </div>

    <el-card>
      <el-table :data="users" stripe style="width: 100%">
        <el-table-column prop="username" label="学号/工号/账号" width="180" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 'admin'" type="danger">管理员</el-tag>
            <el-tag v-else-if="scope.row.role === 'teacher'" type="warning">教师</el-tag>
            <el-tag v-else>学生</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180">
           <template #default="scope">
             {{ scope.row.createTime ? scope.row.createTime.substring(0, 10) : '' }}
           </template>
        </el-table-column>

        <el-table-column label="操作" width="250">
          <template #default="scope">
            <div v-if="scope.row.username !== currentUsername">
              <el-button size="small" type="warning" @click="openRoleDialog(scope.row)">
                变角色
              </el-button>
              <el-button size="small" type="danger" plain @click="handleResetPwd(scope.row)">
                重置密码
              </el-button>
            </div>
            <span v-else style="color: #999; font-size: 12px;">(当前用户)</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="addDialogVisible" title="新增用户 (分配账号)" width="400px">
      <el-form :model="addUserForm" label-width="80px">
        <el-form-item label="角色">
          <el-radio-group v-model="addUserForm.role">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">教师</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item :label="getAccountLabel">
          <el-input v-model="addUserForm.username" :placeholder="'请输入' + getAccountLabel" />
        </el-form-item>
        
        <el-form-item label="真实姓名">
          <el-input v-model="addUserForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        
        <div style="color: #999; font-size: 12px; margin-left: 80px;">
          * 默认初始密码为 123456
        </div>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddUser">确认添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="变更用户角色" width="400px">
      <p>正在修改用户：<strong>{{ editingUser.realName }}</strong></p>
      <div style="margin-top: 20px;">
        <el-radio-group v-model="selectedRole">
          <el-radio label="student" border>学生</el-radio>
          <el-radio label="teacher" border>教师</el-radio>
          <el-radio label="admin" border>管理员</el-radio>
        </el-radio-group>
      </div>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRoleChange">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue' // 记得引入 Plus

const users = ref([])
const currentUsername = ref('')
const roleDialogVisible = ref(false)
const addDialogVisible = ref(false)
const editingUser = ref({})
const selectedRole = ref('student')

// 新增用户表单
const addUserForm = reactive({
  username: '',
  realName: '',
  role: 'student'
})

// 计算属性：动态显示“学号”或“工号”
const getAccountLabel = computed(() => {
  if (addUserForm.role === 'student') return '学号'
  if (addUserForm.role === 'teacher') return '工号'
  return '账号'
})

// 1. 加载列表
const fetchUsers = async () => {
  try {
    const res = await axios.get('/api/admin/users')
    users.value = res.data
  } catch (error) { ElMessage.error('加载失败') }
}

const fetchMe = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    currentUsername.value = res.data.username
  } catch(e) {}
}

// 2. 打开新增弹窗
const openAddDialog = () => {
  addUserForm.username = ''
  addUserForm.realName = ''
  addUserForm.role = 'student'
  addDialogVisible.value = true
}

// 3. 确认添加
const confirmAddUser = async () => {
  if(!addUserForm.username || !addUserForm.realName) return ElMessage.warning('请填写完整')
  try {
    await axios.post('/api/admin/user/add', addUserForm)
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    fetchUsers()
  } catch (error) {
    // 显示后端返回的具体错误信息 (如"学号已存在")
    ElMessage.error(error.response?.data?.message || '添加失败')
  }
}

// ... 下面的 openRoleDialog, confirmRoleChange, handleResetPwd 保持不变 ...
const openRoleDialog = (user) => {
  editingUser.value = user
  selectedRole.value = user.role ? user.role.toLowerCase() : 'student'
  roleDialogVisible.value = true
}

const confirmRoleChange = async () => {
  try {
    await axios.post('/api/admin/user/role', {
      userId: editingUser.value.userId,
      role: selectedRole.value
    })
    ElMessage.success('修改成功')
    roleDialogVisible.value = false
    fetchUsers()
  } catch (error) { ElMessage.error('修改失败') }
}

const handleResetPwd = (user) => {
  ElMessageBox.confirm(`重置 [${user.realName}] 密码为 123456？`, '提示', { type: 'warning' })
    .then(async () => {
      await axios.post('/api/admin/user/reset-password', { userId: user.userId })
      ElMessage.success('重置成功')
    })
}

onMounted(() => {
  fetchUsers()
  fetchMe()
})
</script>

<style scoped>
.user-manage { padding: 20px; }
</style>