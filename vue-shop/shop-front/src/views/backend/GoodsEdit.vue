<template>
  <div class="goods-edit-page">
    <el-card shadow="hover" class="form-card">
      <el-form
          ref="goodsFormRef"
          :model="goodsForm"
          :rules="goodsRules"
          label-width="100px"
          @submit.prevent="submitForm"
      >
        <!-- 商品ID（隐藏） -->
        <el-input v-model="goodsForm.id" type="hidden" />

        <!-- 商品名称 -->
        <el-form-item label="商品名称" prop="good_name">
          <el-input
              v-model="goodsForm.good_name"
              placeholder="请输入商品名称"
              autocomplete="off"
          >
            <template #prefix>
              <span class="x-red">*</span>
            </template>
          </el-input>
        </el-form-item>

        <!-- 商品图片上传 -->
        <el-form-item label="商品图片" prop="img_url">
          <el-row :gutter="10">
            <el-col :span="8">
              <el-input
                  v-model="goodsForm.img_url"
                  placeholder="图片地址"
                  readonly
              />
            </el-col>
            <el-col :span="6">
              <el-image
                  :src="'/static/images/' + goodsForm.img_url"
                  style="width: 150px; height: 150px"
                  fit="cover"
              />
            </el-col>
            <el-col :span="6">
              <el-upload
                  class="avatar-uploader"
                  action="/api/fileUpload"
                  :show-file-list="false"
                  :on-success="handleUploadSuccess"
                  :before-upload="beforeUpload"
                  :limit="1"
              >
                <el-button type="primary" plain>修改图片</el-button>
              </el-upload>
            </el-col>
            <el-col :span="24" style="margin-top: 5px">
              <span class="layui-word-aux">图片大小在500kb以内</span>
            </el-col>
          </el-row>
        </el-form-item>

        <!-- 商品价格 -->
        <el-form-item label="商品价格" prop="price">
          <el-input
              v-model.number="goodsForm.price"
              placeholder="请输入商品价格"
              autocomplete="off"
          >
            <template #prefix>
              <span class="x-red">*</span>
            </template>
            <template #suffix>元</template>
          </el-input>
        </el-form-item>

        <!-- 商品单位 -->
        <el-form-item label="商品单位" prop="unit">
          <el-input
              v-model="goodsForm.unit"
              placeholder="请输入商品单位（如个/斤/件）"
              autocomplete="off"
          >
            <template #prefix>
              <span class="x-red">*</span>
            </template>
          </el-input>
        </el-form-item>

        <!-- 商品分类 -->
        <el-form-item label="商品分类" prop="type_id">
          <template #label>
            <span>
              <span class="x-red">*</span>商品分类
            </span>
          </template>
          <el-select
              v-model="goodsForm.type_id"
              placeholder="请选择商品分类"
              style="width: 200px"
          >
            <el-option
                v-for="type in typeList"
                :key="type.id"
                :label="type.type_name"
                :value="type.id"
            />
          </el-select>
        </el-form-item>

        <!-- 商品描述 -->
        <el-form-item label="描述" prop="intro">
          <el-input
              v-model="goodsForm.intro"
              type="textarea"
              placeholder="请输入商品描述"
              :rows="5"
          />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button
              type="primary"
              @click="submitForm"
              :loading="isLoading"
              class="layui-btn"
          >
            修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// ✅ 1. 定义 props 接收列表页传来的 ID
const props = defineProps({
  id: {
    type: [String, Number],
    default: ''
  }
})

const isLoading = ref(false)
const goodsFormRef = ref(null)

// 表单数据
const goodsForm = reactive({
  id: '',
  good_name: '',
  img_url: '',
  price: 0.01,
  unit: '',
  type_id: '',
  intro: ''
})

// 表单校验
const goodsRules = reactive({
  good_name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  img_url: [{ required: true, message: '请上传商品图片', trigger: 'change' }],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', message: '价格必须为数字', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value < 0.01) callback(new Error('价格不能小于0.01元'))
        else callback()
      }, trigger: 'blur' }
  ],
  unit: [{ required: true, message: '请输入商品单位', trigger: 'blur' }],
  type_id: [{ required: true, message: '请选择商品分类', trigger: 'change' }]
})

const typeList = ref([])

// 获取分类列表
const getTypeList = async () => {
  try {
    const res = await request.get('/goods/getTypeList')
    if (res.status === 1) typeList.value = res.data
  } catch (error) { ElMessage.error('获取分类失败') }
}

// ✅ 2. 加载商品详情（使用你后端的 updateGoodView 接口）
const loadGoodsInfo = async (id) => {
  if (!id) return
  try {
    const res = await request.get(`/goods/updateGoodView?id=${id}`)
    if (res.status === 1) {
      const goods = res.data.BGood
      goodsForm.id = goods.id
      goodsForm.good_name = goods.good_name
      goodsForm.img_url = goods.img_url || ''
      goodsForm.price = goods.price
      goodsForm.unit = goods.unit
      goodsForm.type_id = goods.type_id
      goodsForm.intro = goods.intro || ''
    } else {
      ElMessage.error('加载商品信息失败：' + res.msg)
    }
  } catch (error) {
    ElMessage.error('加载商品信息失败')
  }
}

// ✅ 3. 监听 props.id 变化，弹窗打开时自动加载数据
watch(() => props.id, (newId) => {
  if (newId) {
    loadGoodsInfo(newId)
  }
})

// 图片上传校验
const beforeUpload = (file) => {
  const isLt500K = file.size / 1024 < 500
  if (!isLt500K) {
    ElMessage.error('上传图片大小不能超过500KB！')
    return false
  }
  const isImage = /\.(jpg|jpeg|png|gif)$/.test(file.name)
  if (!isImage) {
    ElMessage.error('只能上传图片格式！')
    return false
  }
  return true
}

// 上传成功回调
const handleUploadSuccess = (response) => {
  if (response.code === 0) {
    goodsForm.img_url = response.src
    ElMessage.success('图片上传成功！')
  } else {
    ElMessage.error('上传失败：' + response.msg)
  }
}

// 提交修改
const submitForm = async () => {
  try {
    await goodsFormRef.value.validate()
    isLoading.value = true
    const res = await request.post('/goods/updateGood', goodsForm)
    if (res.status === 1) {
      ElMessageBox.alert('修改成功').then(() => {
        window.parent.postMessage({ action: 'refresh' }, '*')
        window.dispatchEvent(new CustomEvent('close'))
      })
    } else {
      ElMessage.error('修改失败：' + res.msg)
    }
  } catch (error) {
    ElMessage.error('表单校验失败，请检查')
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  getTypeList()
  // 初始加载
  if (props.id) {
    loadGoodsInfo(props.id)
  }
})
</script>

<style scoped>
.goods-edit-page { padding: 20px; }
.form-card { max-width: 900px; margin: 0 auto; }
.x-red { color: #ff5722; }
.layui-word-aux { color: #999; font-size: 12px; }
.layui-btn { background: #1E9FFF; color: #fff; padding: 9px 15px; border: none; }
</style>