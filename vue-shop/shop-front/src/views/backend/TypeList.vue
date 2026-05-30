<template>
  <div class="type-list-page">
    <!-- 顶部导航栏 -->
    <div class="x-nav">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><a href="/backend">首页</a></el-breadcrumb-item>
        <el-breadcrumb-item>商品管理</el-breadcrumb-item>
        <el-breadcrumb-item>商品分类</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button
          size="small"
          style="line-height:1.6em;margin-top:3px;float:right"
          @click="getTypeList()"
          title="刷新"
      >
        刷新
      </el-button>
    </div>

    <!-- 主内容区 -->
    <div class="x-body">
      <!-- 搜索栏 -->
      <div class="layui-row">
        <el-form
            :inline="true"
            :model="searchForm"
            class="layui-form layui-col-md12 x-so"
            @submit.prevent="searchType"
        >
          <el-form-item>
            <el-input
                v-model="searchForm.keyword"
                placeholder="请输入类型名称"
                clearable
                style="width: 250px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchType">
              搜索
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作栏 -->
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
        <span class="x-right" style="line-height:40px">共有数据：{{total}} 条</span>
      </div>

      <!-- 分类列表表格 -->
      <el-table
          ref="typeTableRef"
          :data="showList"
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

        <!-- 商品类型列：自动层级缩进 -->
        <el-table-column label="商品类型" min-width="200">
          <template #default="scope">
            <span class="type-name" :style="{ paddingLeft: getRealIndent(scope.row) + 'px' }">
              <span v-if="getRealIndent(scope.row) > 0">├─ </span>
              {{ scope.row.type_name }}
            </span>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="160" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openEditDialog(scope.row.id)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="delType(scope.row.id, scope.$index)" style="margin-left:5px">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加/编辑弹窗 -->
      <el-dialog title="添加分类" v-model="addDialogVisible" width="500px" @close="handleDialogClose">
        <TypeAdd />
      </el-dialog>
      <el-dialog title="编辑分类" v-model="editDialogVisible" width="500px" @close="handleDialogClose">
        <TypeEdit :id="editTypeId" />
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import TypeAdd from './TypeAdd.vue'
import TypeEdit from './TypeEdit.vue'

const searchForm = reactive({ keyword: '' })
const typeList = ref([])
const showList = ref([]) // 自动排序后的正确列表
const total = ref(0)
const selectedRows = ref([])
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editTypeId = ref('')

// ===================== 【自动构建树形顺序】 =====================
const buildTreeList = (list) => {
  let result = []
  // 一级分类
  let roots = list.filter(i => i.fid === 0)
  roots.forEach(root => {
    result.push(root)
    // 二级分类
    let children = list.filter(i => i.fid === root.id)
    children.forEach(c => {
      result.push(c)
      // 三级分类
      let gc = list.filter(i => i.fid === c.id)
      result.push(...gc)
    })
  })
  return result
}

// ===================== 【自动计算缩进】 =====================
const getRealIndent = (row) => {
  // 一级
  if (row.fid === 0) return 0
  // 二级
  let p = typeList.value.find(i => i.id === row.fid)
  if (p && p.fid === 0) return 24
  // 三级
  return 48
}

// ===================== 获取分类列表 =====================
const getTypeList = async () => {
  try {
    const res = await request.get('/type/typeList', {
      params: {
        search: searchForm.keyword,
        start: 0,
        count: 1000
      }
    })
    if (res.status === 1) {
      typeList.value = res.data.typeList
      showList.value = buildTreeList(res.data.typeList)
      total.value = res.data.total
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

// 搜索
const searchType = () => {
  getTypeList()
}

// 打开添加
const openAddDialog = () => {
  addDialogVisible.value = true
}

// 打开编辑
const openEditDialog = (id) => {
  editTypeId.value = id
  editDialogVisible.value = true
}

// 删除
const delType = async (id, index) => {
  await ElMessageBox.confirm('确定删除？')
  const res = await request.post('/type/delType', { id })
  if (res.status === 1) {
    showList.value.splice(index, 1)
    total.value = showList.value.length
    ElMessage.success('删除成功')
  }
}

// 批量删除
const delAll = async () => {
  if (selectedRows.value.length === 0) return ElMessage.warning('请选择')
  const ids = selectedRows.value.map(i => i.id).join(',')
  await ElMessageBox.confirm('确定批量删除？')
  const res = await request.post('/type/delAllType', { ids })
  if (res.status === 1) {
    getTypeList()
    ElMessage.success('批量删除成功')
  }
}

const handleSelectionChange = (val) => {
  selectedRows.value = val
}

const handleDialogClose = () => {
  getTypeList()
}

onMounted(() => {
  getTypeList()
})
</script>

<style scoped>
.type-list-page { padding: 0 10px; }
.x-nav { padding: 10px 0; border-bottom: 1px solid #eee; margin-bottom: 10px; }
.layui-row { margin-bottom: 20px; }
.x-so { display: flex; align-items: center; gap: 10px; }
.xblock { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.left-buttons { display: flex; align-items: center; }
.x-right { color: #666; font-size: 12px; }

.layui-btn { background: #1E9FFF; color: #fff; border: none; padding: 7px 14px; }
.layui-btn-danger { background: #FF5722; }

.type-name {
  display: inline-block;
  width: 100%;
  line-height: 40px;
}

:deep(.el-table) { --el-table-row-hover-bg-color: #f5f7fa; }
:deep(.el-table th) { background: #f8f8f8; }
</style>