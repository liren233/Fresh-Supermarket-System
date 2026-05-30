<template>
  <div class="type-edit-page x-body">
    <el-form
        ref="typeFormRef"
        :model="typeForm"
        :rules="typeRules"
        label-width="100px"
        class="layui-form"
        @submit.prevent="handleSubmit"
    >
      <!-- 隐藏的分类ID -->
      <el-input v-model="typeForm.id" type="hidden" />

      <!-- 上级分类选择 -->
      <el-form-item label="上级分类" prop="fid">
        <template #label>
          <span>
            <span class="x-red">*</span>上级分类
          </span>
        </template>
        <el-select
            v-model="typeForm.fid"
            placeholder="请选择上级分类"
            style="width: 200px"
        >
          <el-option
              v-for="type in bTypeList"
              :key="type.id"
              :label="getOptionLabel(type)"
              :value="type.id"
          />
        </el-select>
      </el-form-item>

      <!-- 分类名称 -->
      <el-form-item label="分类名称" prop="type_name">
        <template #label>
          <span>
            <span class="x-red">*</span>分类名称
          </span>
        </template>
        <el-input
            v-model="typeForm.type_name"
            placeholder="请输入分类名称"
            autocomplete="off"
            style="width: 200px"
        />
        <div class="layui-form-mid layui-word-aux">
          <span class="x-red">*</span>
        </div>
      </el-form-item>

      <!-- 提交按钮 -->
      <el-form-item>
        <el-button
            type="primary"
            @click="handleSubmit"
            :loading="isLoading"
            class="layui-btn"
        >
          修改
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, defineProps } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 接收弹窗传来的 ID ✅ 关键修复
const props = defineProps({
  id: [Number, String]
})

// 加载状态
const isLoading = ref(false)
// 表单引用
const typeFormRef = ref(null)

// 分类表单数据
const typeForm = reactive({
  id: '',
  fid: '',
  type_name: ''
})

// 表单校验规则
const typeRules = reactive({
  fid: [
    { required: true, message: '请选择上级分类', trigger: 'change' }
  ],
  type_name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在1~50个字符', trigger: 'blur' }
  ]
})

// 上级分类列表
const bTypeList = ref([])

// 下拉框显示层级（美化用）
const getOptionLabel = (type) => {
  if (type.fid === 0) return type.type_name
  if (type.fid !== 0) return '├─ ' + type.type_name
  return type.type_name
}

// 获取 一级+二级 分类（用于下拉选择）
const getTypeList = async () => {
  try {
    const res = await request.get('/type/typeList', {
      params: { start: 0, count: 1000 }
    })
    if (res.status === 1) {
      bTypeList.value = res.data.typeList
    }
  } catch (error) {}
}

// 获取分类详情（自动回显）✅ 关键修复
const getTypeDetail = async () => {
  try {
    const typeId = props.id
    if (!typeId) {
      ElMessage.error('未获取到分类ID')
      return
    }

    // 调用你后端正确接口
    const res = await request.get('/type/updateTypeView', {
      params: { id: typeId }
    })

    if (res.status === 1) {
      const data = res.data
      const currentType = data.bType

      typeForm.id = currentType.id
      typeForm.fid = currentType.fid
      typeForm.type_name = currentType.type_name
    }
  } catch (error) {
    ElMessage.error('加载分类信息失败')
  }
}

// 提交修改
const handleSubmit = async () => {
  try {
    await typeFormRef.value.validate()
    isLoading.value = true

    const res = await request.post('/type/updateType', {
      id: typeForm.id,
      fid: typeForm.fid,
      type_name: typeForm.type_name
    })

    if (res.status === 1) {
      ElMessageBox.alert('修改成功', '提示').then(() => {
        window.parent.postMessage({ action: 'refresh' }, '*')
        window.dispatchEvent(new CustomEvent('close'))
      })
    } else {
      ElMessage.error('修改失败')
    }
  } catch (error) {
    ElMessage.error('修改失败')
  } finally {
    isLoading.value = false
  }
}

// 挂载加载
onMounted(() => {
  getTypeList().then(() => {
    getTypeDetail()
  })
})
</script>

<style scoped>
.type-edit-page {
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 40px);
}

.layui-form {
  max-width: 600px;
}

.x-red {
  color: #ff5722;
}

.layui-form-mid {
  margin-left: 10px;
  display: inline-block;
}

.layui-word-aux {
  color: #999;
  font-size: 12px;
}

.layui-btn {
  background-color: #1E9FFF;
  color: #fff;
  border: none;
  padding: 9px 15px;
  border-radius: 2px;
  cursor: pointer;
}

.layui-btn:hover {
  background-color: #0088ff;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-select), :deep(.el-input) {
  width: 200px;
}
</style>