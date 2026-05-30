<template>
  <div class="user-list-page">
    <!-- 顶部导航栏 -->
    <div class="x-nav">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><a href="/backend">首页</a></el-breadcrumb-item>
        <el-breadcrumb-item><a href="/backend">演示</a></el-breadcrumb-item>
        <el-breadcrumb-item>导航元素</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button
          size="small"
          style="line-height:1.6em;margin-top:3px;float:right"
          @click="getUserList()"
          title="刷新"
      >
        刷新
      </el-button>
    </div>

    <!-- 主内容区 -->
    <div class="x-body">
      <!-- 搜索栏：搜索按钮在输入框右侧 -->
      <div class="layui-row">
        <el-form
            :inline="true"
            :model="searchForm"
            class="layui-form layui-col-md12 x-so"
            @submit.prevent="searchUser"
        >
          <el-form-item>
            <el-input
                v-model="searchForm.keyword"
                placeholder="请输入用户名或手机号"
                clearable
                style="width: 250px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchUser">
              搜索
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作栏：批量删除和添加按钮都在左侧紧挨 -->
      <div class="xblock">
        <div class="left-buttons">
          <el-button
              type="danger"
              @click="delAll"
              class="layui-btn layui-btn-danger"
          >
            批量删除
          </el-button>
          <el-button
              type="primary"
              @click="openAddDialog"
              class="layui-btn"
              style="margin-left: 10px"
          >
            添加
          </el-button>
        </div>
        <span class="x-right" style="line-height:40px">共有数据：{{ pagination.total }} 条</span>
      </div>

      <!-- 用户列表表格 -->
      <el-table
          ref="userTableRef"
          :data="userList"
          border
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
      >
        <!-- 复选框列 -->
        <el-table-column type="selection" width="55" />

        <!-- ID列 -->
        <el-table-column
            label="ID"
            prop="id"
            width="70"
            align="center"
        />

        <!-- 登录名列 -->
        <el-table-column
            label="登录名"
            prop="userName"
            min-width="100"
        />

        <!-- 手机列 -->
        <el-table-column
            label="手机"
            prop="phone"
            min-width="120"
        />

        <!-- 角色列 -->
        <el-table-column
            label="角色"
            min-width="80"
        >
          <template #default="scope">
            <span>{{ scope.row.isSuperuser === 1 ? '超级管理员' : '普通用户' }}</span>
          </template>
        </el-table-column>

        <!-- 状态列 -->
        <el-table-column
            label="状态"
            min-width="100"
            align="center"
            class-name="td-status"
        >
          <template #default="scope">
            <el-button
                :type="scope.row.status === 1 ? 'success' : 'danger'"
                :disabled="scope.row.status === 1"
                style="padding: 2px 5px; font-size: 12px;"
            >
              {{ scope.row.status === 1 ? '已启用' : '已禁用' }}
            </el-button>
          </template>
        </el-table-column>

        <!-- 操作列：三个纯文字按钮 -->
        <el-table-column
            label="操作"
            min-width="220"
            align="center"
            class-name="td-manage"
        >
          <template #default="scope">
            <!-- 启用/禁用按钮，文字根据状态变化 -->
            <el-button
                size="small"
                :type="scope.row.status === 1 ? 'danger' : 'success'"
                @click="memberStop(scope.row.id, scope.row.status === 1 ? 0 : 1)"
                style="margin-right: 5px"
                v-if="scope.row.userName !== 'admin'"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>

            <!-- 编辑按钮 -->
            <el-button
                size="small"
                type="primary"
                @click="openEditDialog(scope.row.id)"
                style="margin-right: 5px"
            >
              编辑
            </el-button>

            <!-- 删除按钮 -->
            <el-button
                size="small"
                type="danger"
                @click="memberDel(scope.row.id, scope.$index)"
                v-if="scope.row.userName !== 'admin'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页控件 -->
      <div class="page">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pagination.pageNum"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            style="margin-top: 20px; text-align: right"
        >
        </el-pagination>
      </div>

      <!-- 添加用户弹窗 -->
      <el-dialog
          title="添加用户"
          v-model="addDialogVisible"
          width="500px"
          @close="handleDialogClose"
      >
        <UserAdd />
      </el-dialog>

      <!-- 编辑用户弹窗 -->
      <el-dialog
          title="编辑用户"
          v-model="editDialogVisible"
          width="500px"
          @close="handleDialogClose"
      >
        <UserEdit :id="editUserId" :key="editUserId" />
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
// 引入添加/编辑组件
import UserAdd from './UserAdd.vue'
import UserEdit from './UserEdit.vue'

const router = useRouter()
const route = useRoute()

// 表格引用
const userTableRef = ref(null)
// 选中的行
const selectedRows = ref([])
// 弹窗显隐控制
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
// 编辑的用户ID
const editUserId = ref('')

// 搜索表单
const searchForm = reactive({
  keyword: '' // 搜索关键词
})

// 分页参数
const pagination = reactive({
  pageNum: 1,    // 当前页码
  pageSize: 10,  // 每页条数
  total: 0       // 总条数
})

// 用户列表
const userList = ref([])

// 获取用户列表
const getUserList = async () => {
  try {
    const params = {
      search: searchForm.keyword || '',
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await request.get('/user/userList', { params })
    if (res.status === 1) {
      userList.value = res.data.list || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error('获取用户列表失败：' + res.msg)
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败：' + error.message)
  }
}

// 搜索用户
const searchUser = () => {
  pagination.pageNum = 1 // 搜索时重置页码
  getUserList()
}

// 页码改变
const handleCurrentChange = (val) => {
  pagination.pageNum = val
  getUserList()
}

// 每页条数改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.pageNum = 1 // 重置页码
  getUserList()
}

// 打开添加用户弹窗
const openAddDialog = () => {
  addDialogVisible.value = true
}

// 打开编辑用户弹窗
const openEditDialog = (id) => {
  editUserId.value = id
  editDialogVisible.value = true
}

// 启用/禁用用户
const memberStop = async (id, status) => {
  try {
    const confirmText = status === 1 ? '启用' : '停用'
    await ElMessageBox.confirm(
        `确认要${confirmText}吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await request.post('/user/updateUserStatus', {
      id,
      status
    })

    if (res.status === 1) {
      ElMessage.success(`${confirmText}成功！`)
      getUserList() // 重新加载列表
    } else {
      ElMessage.error(res.msg || `${confirmText}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败：' + (error.msg || error.message || '程序出错'))
    }
  }
}

// 删除用户
const memberDel = async (id, index) => {
  try {
    await ElMessageBox.confirm(
        '确认要删除吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await request.post('/user/delUser', { id })
    if (res.status === 1) {
      // 本地删除该行
      userList.value.splice(index, 1)
      pagination.total = userList.value.length
      ElMessage.success('已删除！')
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.msg || error.message || '程序出错'))
    }
  }
}

// 批量删除
const delAll = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }

  const ids = selectedRows.value.map(item => item.id).join(',')
  try {
    await ElMessageBox.confirm(
        `确认要删除选中的${selectedRows.value.length}个用户吗？`,
        '提示',
        {
          type: 'warning'
        }
    )

    const res = await request.post('/user/delAllUser', { ids })
    if (res.status === 1) {
      // 重新加载列表
      getUserList()
      ElMessage.success('批量删除成功！')
    } else {
      ElMessage.error(res.msg || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败：' + error.message)
    }
  }
}

// 监听表格选中行变化
const handleSelectionChange = (val) => {
  selectedRows.value = val
}

// 关闭弹窗后刷新列表
const handleDialogClose = () => {
  getUserList()
}

// 监听页面消息（接收子组件的刷新通知）
const handleMessage = (event) => {
  if (event.data && event.data.action === 'refresh') {
    getUserList()
  }
}

// 页面挂载时初始化
onMounted(() => {
  getUserList()
  window.addEventListener('message', handleMessage)
})

// 页面卸载时移除监听
onMounted(() => {
  return () => {
    window.removeEventListener('message', handleMessage)
  }
})

// 监听路由参数变化（编辑弹窗）
watch(
    () => route.params.id,
    (newVal) => {
      if (newVal) {
        editUserId.value = newVal
      }
    }
)
</script>

<style scoped>
.user-list-page {
  padding: 0 10px;
}

/* 顶部导航 */
.x-nav {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}

/* 搜索栏 */
.layui-row {
  margin-bottom: 20px;
}

.x-so {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 操作栏：左侧按钮布局 */
.xblock {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.left-buttons {
  display: flex;
  align-items: center;
}

.x-right {
  color: #666;
  font-size: 12px;
}

/* 按钮样式适配 */
.layui-btn {
  background-color: #1E9FFF;
  color: #fff;
  border: none;
  padding: 9px 15px;
  border-radius: 2px;
}

.layui-btn-danger {
  background-color: #FF5722;
}

/* 状态列样式 */
.td-status {
  text-align: center;
}

/* 操作列样式 */
.td-manage {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

/* 分页样式 */
.page {
  margin-top: 20px;
  text-align: right;
}

/* 适配Element Plus表格 */
:deep(.el-table) {
  --el-table-row-hover-bg-color: #f5f7fa;
}

:deep(.el-table th) {
  background-color: #f8f8f8;
}

/* 适配Element Plus分页 */
:deep(.el-pagination) {
  padding: 10px 0;
}
</style>