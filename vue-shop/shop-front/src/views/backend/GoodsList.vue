<template>
  <div class="goods-list-page">
    <!-- 顶部导航栏 -->
    <div class="x-nav">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><a href="/backend">首页</a></el-breadcrumb-item>
        <el-breadcrumb-item><a href="/goods/list">演示</a></el-breadcrumb-item>
        <el-breadcrumb-item>商品列表</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button
          size="small"
          style="line-height:1.6em;margin-top:3px;float:right"
          @click="getGoodsList()"
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
            @submit.prevent="searchGoods"
        >
          <el-form-item>
            <el-input
                v-model="searchForm.keyword"
                placeholder="请输入商品名称"
                clearable
                style="width: 250px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchGoods">
              搜索
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作栏：批量删除 + 添加按钮 -->
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
        <span class="x-right" style="line-height:40px">共有数据：{{ total }} 条</span>
      </div>

      <!-- 商品列表表格 -->
      <el-table
          ref="goodsTableRef"
          :data="goodsList"
          border
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
      >
        <!-- 复选框列 -->
        <el-table-column type="selection" width="55" />
        <!-- ID列 -->
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <!-- 商品图片列 -->
        <el-table-column label="商品图片" width="100" align="center">
          <template #default="scope">
            <el-image
                :src="scope.row.img_url ? '/static/images/' + scope.row.img_url : '/static/images/404.jpg'"
                style="width: 80px; height: 80px"
                fit="cover"
            />
          </template>
        </el-table-column>
        <!-- 商品名称列 -->
        <el-table-column prop="good_name" label="商品名称" min-width="120" />
        <!-- 商品分类列 -->
        <el-table-column label="商品分类" min-width="100" align="center">
          <template #default="scope">
            {{ scope.row.btype?.type_name || "未分类" }}
          </template>
        </el-table-column>
        <!-- 商品价格列 -->
        <el-table-column prop="price" label="商品价格" width="100" align="center">
          <template #default="scope">
            {{ scope.row.price }} 元
          </template>
        </el-table-column>
        <!-- 商品单位列 -->
        <el-table-column prop="unit" label="商品单位" width="100" align="center" />
        <!-- 商品描述列 -->
        <el-table-column prop="intro" label="商品描述" min-width="150">
          <template #default="scope">
            <el-tooltip :content="scope.row.intro || '无'" placement="top">
              <span>{{ (scope.row.intro || '').length > 20 ? (scope.row.intro.slice(0,20) + '...') : scope.row.intro || '无' }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <!-- 操作列 -->
        <el-table-column label="操作" width="140" align="center" class-name="td-manage">
          <template #default="scope">
            <el-button
                size="small"
                type="primary"
                @click="openEditDialog(scope.row.id)"
                style="margin-right: 5px"
            >
              编辑
            </el-button>
            <el-button
                size="small"
                type="danger"
                @click="delGood(scope.row.id, scope.$index)"
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
            :current-page="pageInfo.pageNum"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            style="text-align: right"
        >
        </el-pagination>
      </div>
    </div>

    <!-- 添加商品弹窗 -->
    <el-dialog
        title="添加商品"
        v-model="addDialogVisible"
        width="800px"
        @close="handleDialogClose"
    >
      <GoodsAdd />
    </el-dialog>

    <!-- 编辑商品弹窗 -->
    <el-dialog
        title="编辑商品"
        v-model="editDialogVisible"
        width="800px"
        @close="handleDialogClose"
    >
      <GoodsEdit :id="editGoodsId" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import GoodsAdd from './GoodsAdd.vue'
import GoodsEdit from './GoodsEdit.vue'

const goodsTableRef = ref(null)
const selectedRows = ref([])
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editGoodsId = ref('')

const searchForm = reactive({
  keyword: ''
})

const pageInfo = reactive({
  pageNum: 1,
  pageSize: 10,
  start: 0
})

const goodsList = ref([])
const total = ref(0)

// 获取商品列表
const getGoodsList = async () => {
  try {
    const res = await request.get('/goods/goodList', {
      params: {
        search: searchForm.keyword,
        start: (pageInfo.pageNum - 1) * pageInfo.pageSize,
        count: pageInfo.pageSize
      }
    })
    if (res.status === 1) {
      goodsList.value = res.data.goodList
      total.value = res.data.total
    } else {
      ElMessage.error('获取商品列表失败：' + res.msg)
    }
  } catch (error) {
    ElMessage.error('获取商品列表失败：' + error.message)
  }
}

// 搜索
const searchGoods = () => {
  pageInfo.pageNum = 1
  getGoodsList()
}

// 打开添加
const openAddDialog = () => {
  addDialogVisible.value = true
}

// 打开编辑
const openEditDialog = (id) => {
  editGoodsId.value = id
  editDialogVisible.value = true
}

// 关闭弹窗刷新
const handleDialogClose = () => {
  getGoodsList()
}

// 删除单个
const delGood = async (id, index) => {
  try {
    await ElMessageBox.confirm('确认要删除吗？', '提示', {
      type: 'error'
    })
    // 👇 关键修改：用 params 传参，和后端 @RequestParam 匹配
    const res = await request.post('/goods/delGood', null, {
      params: { id }
    })
    if (res.status === 1) {
      ElMessage.success('已删除！')
      goodsList.value.splice(index, 1)
      total.value -= 1
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const delAll = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  const ids = selectedRows.value.map(item => item.id).join(',')
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 个商品？`, '提示', {
      type: 'error'
    })
    // 👇 和单条删除一样，用 params 传参
    const res = await request.post('/goods/delAllGood', null, {
      params: { ids }
    })
    if (res.status === 1) {
      ElMessage.success('批量删除成功')
      getGoodsList()
      goodsTableRef.value.clearSelection()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 分页
const handleSizeChange = (val) => {
  pageInfo.pageSize = val
  getGoodsList()
}
const handleCurrentChange = (val) => {
  pageInfo.pageNum = val
  getGoodsList()
}

// 选中变化
const handleSelectionChange = (val) => {
  selectedRows.value = val
}

onMounted(() => {
  getGoodsList()

  window.addEventListener('close', () => {
    addDialogVisible.value = false
    editDialogVisible.value = false
  })
  window.addEventListener('message', (e) => {
    if (e.data.action === 'refresh') {
      getGoodsList()
    }
  })
})
</script>

<style scoped>
.goods-list-page {
  padding: 0 10px;
}

.x-nav {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}

.layui-row {
  margin-bottom: 20px;
}

.x-so {
  display: flex;
  align-items: center;
  gap: 10px;
}

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

.td-manage {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.page {
  margin-top: 20px;
  text-align: right;
}

:deep(.el-table) {
  --el-table-row-hover-bg-color: #f5f7fa;
}

:deep(.el-table th) {
  background-color: #f8f8f8;
}

:deep(.el-pagination) {
  padding: 10px 0;
}
</style>