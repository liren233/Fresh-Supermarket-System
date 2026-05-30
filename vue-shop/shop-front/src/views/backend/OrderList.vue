<template>
  <div class="order-list-page">
    <!-- 顶部导航栏 -->
    <div class="x-nav">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><a href="/backend">首页</a></el-breadcrumb-item>
        <el-breadcrumb-item><a href="/backend">订单管理</a></el-breadcrumb-item>
        <el-breadcrumb-item>订单列表</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button
          size="small"
          style="line-height:1.6em;margin-top:3px;float:right"
          @click="getOrderList()"
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
            @submit.prevent="searchOrder"
        >
          <el-form-item>
            <el-input
                v-model="searchForm.keyword"
                placeholder="请输入订单号或收货人"
                clearable
                style="width: 250px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchOrder">
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
        </div>
        <span class="x-right" style="line-height:40px">共有数据：{{ pagination.total }} 条</span>
      </div>

      <!-- 订单列表表格 -->
      <el-table
          ref="orderTableRef"
          :data="orderList"
          border
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
      >
        <!-- 复选框列 -->
        <el-table-column type="selection" width="55" />

        <!-- ID列 -->
        <el-table-column
            label="订单ID"
            prop="id"
            width="80"
            align="center"
        />

        <!-- 订单号列 -->
        <el-table-column
            label="订单号"
            prop="order_no"
            min-width="180"
        />

        <!-- 收货人列 -->
        <el-table-column
            label="收货人"
            prop="user_name"
            min-width="100"
        />

        <!-- 联系电话列 -->
        <el-table-column
            label="联系电话"
            prop="phone"
            min-width="120"
        />

        <!-- 收货地址列 -->
        <el-table-column
            label="收货地址"
            prop="address"
            min-width="200"
        />

        <!-- 订单金额列 -->
        <el-table-column
            label="订单金额"
            min-width="100"
            align="center"
        >
          <template #default="scope">
            <span>{{ scope.row.total_price }}元</span>
          </template>
        </el-table-column>

        <!-- 订单状态列 -->
        <el-table-column
            label="订单状态"
            min-width="100"
            align="center"
            class-name="td-status"
        >
          <template #default="scope">
            <el-button
                :type="getOrderStatusType(scope.row.status)"
                style="padding: 2px 5px; font-size: 12px;"
            >
              {{ getOrderStatusText(scope.row.status) }}
            </el-button>
          </template>
        </el-table-column>

        <!-- 支付状态列 -->
        <el-table-column
            label="支付状态"
            min-width="100"
            align="center"
            class-name="td-status"
        >
          <template #default="scope">
            <el-button
                :type="scope.row.pay_status === 1 ? 'success' : 'danger'"
                style="padding: 2px 5px; font-size: 12px;"
            >
              {{ scope.row.pay_status === 1 ? '已支付' : '未支付' }}
            </el-button>
          </template>
        </el-table-column>

        <!-- 下单时间列 -->
        <el-table-column
            label="下单时间"
            min-width="180"
            align="center"
        >
          <template #default="scope">
            <span>{{ formatTime(scope.row.time) }}</span>
          </template>
        </el-table-column>

        <!-- 操作列：三个纯文字按钮 -->
        <el-table-column
            label="操作"
            min-width="180"
            align="center"
            class-name="td-manage"
        >
          <template #default="scope">
            <!-- 查看按钮 -->
            <el-button
                size="small"
                type="info"
                @click="viewOrder(scope.row.id)"
                style="margin-right: 5px"
            >
              查看
            </el-button>

            <!-- 编辑按钮 -->
            <el-button
                size="small"
                type="primary"
                @click="editOrder(scope.row.id)"
                style="margin-right: 5px"
            >
              编辑
            </el-button>

            <!-- 删除按钮 -->
            <el-button
                size="small"
                type="danger"
                @click="delOrder(scope.row.id, scope.$index)"
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
    </div>

    <!-- 查看订单详情弹窗 -->
    <el-dialog
        v-model="viewDialogVisible"
        title="订单详情"
        width="800px"
        destroy-on-close
    >
      <div v-if="viewOrderData" class="order-detail">
        <el-form :model="viewOrderData" label-width="100px">
          <el-form-item label="订单ID">
            <span>{{ viewOrderData.id }}</span>
          </el-form-item>
          <el-form-item label="订单号">
            <span>{{ viewOrderData.order_no }}</span>
          </el-form-item>
          <el-form-item label="收货人">
            <span>{{ viewOrderData.user_name }}</span>
          </el-form-item>
          <el-form-item label="联系电话">
            <span>{{ viewOrderData.phone }}</span>
          </el-form-item>
          <el-form-item label="收货地址">
            <span>{{ viewOrderData.address }}</span>
          </el-form-item>
          <el-form-item label="订单金额">
            <span>{{ viewOrderData.total_price }}元</span>
          </el-form-item>
          <el-form-item label="订单状态">
            <el-button
                :type="getOrderStatusType(viewOrderData.status)"
                style="padding: 2px 5px; font-size: 12px;"
            >
              {{ getOrderStatusText(viewOrderData.status) }}
            </el-button>
          </el-form-item>
          <el-form-item label="支付状态">
            <el-button
                :type="viewOrderData.pay_status === 1 ? 'success' : 'danger'"
                style="padding: 2px 5px; font-size: 12px;"
            >
              {{ viewOrderData.pay_status === 1 ? '已支付' : '未支付' }}
            </el-button>
          </el-form-item>
          <el-form-item label="下单时间">
            <span>{{ formatTime(viewOrderData.time) }}</span>
          </el-form-item>
        </el-form>

        <h3 style="margin-top: 20px; margin-bottom: 10px;">商品列表</h3>
        <el-table
            :data="orderDetails"
            border
            style="width: 100%"
        >
          <el-table-column
              label="商品ID"
              prop="goods_id"
              width="80"
              align="center"
          />
          <el-table-column
              label="商品名称"
              prop="good_name"
              min-width="200"
          />
          <el-table-column
              label="商品图片"
              width="100"
              align="center"
          >
            <template #default="scope">
              <el-image
                  :src="'/static/images/' + scope.row.img_url"
                  fit="cover"
                  style="width: 80px; height: 80px;"
              />
            </template>
          </el-table-column>
          <el-table-column
              label="商品数量"
              prop="count"
              width="100"
              align="center"
          />
          <el-table-column
              label="商品价格"
              width="100"
              align="center"
          >
            <template #default="scope">
              <span>{{ scope.row.price }}元</span>
            </template>
          </el-table-column>
          <el-table-column
              label="商品总价"
              width="100"
              align="center"
          >
            <template #default="scope">
              <span>{{ scope.row.count * scope.row.price }}元</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else class="loading">
        <el-icon class="is-loading"><i-ep-loading /></el-icon>
        <span>加载中...</span>
      </div>
    </el-dialog>

    <!-- 编辑订单弹窗 -->
    <el-dialog
        v-model="editDialogVisible"
        title="编辑订单"
        width="600px"
        destroy-on-close
    >
      <div v-if="editOrderData" class="order-edit">
        <el-form :model="editOrderData" label-width="100px">
          <el-form-item label="订单ID">
            <span>{{ editOrderData.id }}</span>
          </el-form-item>
          <el-form-item label="订单号">
            <span>{{ editOrderData.order_no }}</span>
          </el-form-item>
          <el-form-item label="收货人" prop="user_name">
            <el-input v-model="editOrderData.user_name" style="width: 300px" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="editOrderData.phone" style="width: 300px" />
          </el-form-item>
          <el-form-item label="收货地址" prop="address">
            <el-input v-model="editOrderData.address" type="textarea" rows="3" style="width: 300px" />
          </el-form-item>
          <el-form-item label="订单状态" prop="status">
            <el-select v-model="editOrderData.status" style="width: 300px">
              <el-option label="未付款" value="1" />
              <el-option label="已付款" value="2" />
              <el-option label="已发货" value="3" />
              <el-option label="已收货" value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付状态" prop="pay_status">
            <el-select v-model="editOrderData.pay_status" style="width: 300px">
              <el-option label="未支付" value="0" />
              <el-option label="已支付" value="1" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div v-else class="loading">
        <el-icon class="is-loading"><i-ep-loading /></el-icon>
        <span>加载中...</span>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveOrder">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 表格引用
const orderTableRef = ref(null)
// 选中的行
const selectedRows = ref([])

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

// 订单列表
const orderList = ref([])

// 查看订单弹窗
const viewDialogVisible = ref(false)
const viewOrderData = ref(null)
const orderDetails = ref([])

// 编辑订单弹窗
const editDialogVisible = ref(false)
const editOrderData = ref(null)

// 获取订单列表
const getOrderList = async () => {
  try {
    const params = {
      start: (pagination.pageNum - 1) * pagination.pageSize,
      count: pagination.pageSize
    }
    const res = await request.get('/order/orderList', { params })
    if (res.status === 1) {
      orderList.value = res.data.list || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error('获取订单列表失败：' + res.msg)
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败：' + error.message)
  }
}

// 搜索订单
const searchOrder = () => {
  pagination.pageNum = 1 // 搜索时重置页码
  getOrderList()
}

// 页码改变
const handleCurrentChange = (val) => {
  pagination.pageNum = val
  getOrderList()
}

// 每页条数改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.pageNum = 1 // 重置页码
  getOrderList()
}

// 获取订单详情
const getOrderDetail = async (id) => {
  try {
    const res = await request.get(`/order/getOrderById`, { params: { id } })
    if (res.status === 1) {
      return res.data
    } else {
      ElMessage.error('获取订单详情失败：' + res.msg)
      return null
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败：' + error.message)
    return null
  }
}

// 获取订单商品列表
const getOrderDetails = async (orderId) => {
  try {
    const res = await request.get(`/order/getOrderDetails`, { params: { orderId } })
    if (res.status === 1) {
      return res.data || []
    } else {
      ElMessage.error('获取订单商品列表失败：' + res.msg)
      return []
    }
  } catch (error) {
    ElMessage.error('获取订单商品列表失败：' + error.message)
    return []
  }
}

// 查看订单
const viewOrder = async (id) => {
  viewOrderData.value = null
  orderDetails.value = []
  viewDialogVisible.value = true
  
  const orderData = await getOrderDetail(id)
  if (orderData) {
    viewOrderData.value = orderData
    const details = await getOrderDetails(id)
    orderDetails.value = details
  }
}

// 编辑订单
const editOrder = async (id) => {
  editOrderData.value = null
  editDialogVisible.value = true
  
  const orderData = await getOrderDetail(id)
  if (orderData) {
    editOrderData.value = { ...orderData }
  }
}

// 保存订单
const saveOrder = async () => {
  try {
    const res = await request.post('/order/updateOrder', editOrderData.value)
    if (res.status === 1) {
      ElMessage.success('保存成功！')
      editDialogVisible.value = false
      getOrderList()
    } else {
      ElMessage.error('保存失败：' + res.msg)
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

// 删除订单
const delOrder = async (id, index) => {
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

    const res = await request.post('/order/delete', { orderId: id })
    if (res.status === 1) {
      // 本地删除该行
      orderList.value.splice(index, 1)
      pagination.total = orderList.value.length
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
    ElMessage.warning('请选择要删除的订单')
    return
  }

  const ids = selectedRows.value.map(item => item.id).join(',')
  try {
    await ElMessageBox.confirm(
        `确认要删除选中的${selectedRows.value.length}个订单吗？`,
        '提示',
        {
          type: 'warning'
        }
    )

    // 这里可以添加批量删除订单的逻辑
    ElMessage.success('批量删除成功！')
    getOrderList()
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

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,0)}-${String(date.getDate()).padStart(2,0)} ${String(date.getHours()).padStart(2,0)}:${String(date.getMinutes()).padStart(2,0)}:${String(date.getSeconds()).padStart(2,0)}`
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    1: '未付款',
    2: '已付款',
    3: '已发货',
    4: '已收货'
  }
  return statusMap[status] || '未知'
}

// 获取订单状态类型
const getOrderStatusType = (status) => {
  const typeMap = {
    1: 'warning',
    2: 'success',
    3: 'primary',
    4: 'info'
  }
  return typeMap[status] || 'info'
}

// 页面挂载时初始化
onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.order-list-page {
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