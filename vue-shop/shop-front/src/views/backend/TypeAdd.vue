<template>
  <div class="type-add-page x-body">
    <el-form
        ref="typeFormRef"
        :model="typeForm"
        :rules="typeRules"
        label-width="100px"
        class="layui-form"
        @submit.prevent="handleSubmit"
    >
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
          增加
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 加载状态
const isLoading = ref(false)
// 表单引用
const typeFormRef = ref(null)

// 分类表单数据
const typeForm = reactive({
  fid: '',        // 上级分类ID
  type_name: ''   // 分类名称
})

// 表单校验规则
const typeRules = reactive({
  fid: [
    { required: true, message: '请选择上级分类', trigger: 'change' }
  ],
  type_name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '分类名称长度在1~50个字符之间', trigger: 'blur' }
  ]
})

// 上级分类列表
const bTypeList = ref([])

// 格式化选项标签，自动加上层级缩进标识
const getOptionLabel = (type) => {
  let prefix = ''
  if (type.fid !== 0) {
    prefix = '├─ '
  }
  // 二级分类前面再多加一个空格，视觉上更有层次感
  if (type.fid !== 0 && type.fid !== 1 && type.fid !== 2) {
    prefix = '  ├─ '
  }
  return prefix + type.type_name
}

// 获取上级分类列表（只获取一级和二级分类）
const getTypeList = async () => {
  try {
    const res = await request.get('/type/addTypeView')
    if (res.status === 1) {
      bTypeList.value = res.data
      // 默认选中第一个分类
      if (bTypeList.value.length > 0) {
        typeForm.fid = bTypeList.value[0].id
      }
    } else {
      ElMessage.error('获取分类列表失败：' + res.msg)
    }
  } catch (error) {
    ElMessage.error('获取分类列表失败：' + error.message)
  }
}

// 提交添加分类
const handleSubmit = async () => {
  try {
    // 表单校验
    await typeFormRef.value.validate()

    isLoading.value = true

    // 调用添加分类接口
    const res = await request.post('/type/addType', {
      fid: typeForm.fid,
      type_name: typeForm.type_name
    })

    if (res.status === 1) {
      ElMessageBox.alert('添加成功', '提示', {
        type: 'success'
      }).then(() => {
        // 通知父页面刷新并关闭当前窗口
        if (window.parent) {
          window.parent.postMessage({ action: 'refresh' }, '*')
          const closeEvent = new CustomEvent('close')
          window.dispatchEvent(closeEvent)
          if (window.parent.layer) {
            const index = window.parent.layer.getFrameIndex(window.name)
            window.parent.layer.close(index)
          }
        }
      })
    } else {
      ElMessage.error(res.msg || '添加失败')
    }
  } catch (error) {
    if (error.name === 'ValidationError') {
      ElMessage.error('表单填写有误，请检查')
    } else {
      ElMessage.error('添加失败：' + (error.msg || error.message || '程序出错'))
    }
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  getTypeList()
})
</script>

<style scoped>
.type-add-page {
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