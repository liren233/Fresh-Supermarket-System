<template>
  <div class="admin-edit-page">
    <el-card shadow="hover" class="form-card">
      <el-form
          ref="adminFormRef"
          :model="adminForm"
          :rules="adminRules"
          label-width="100px"
      >
        <el-input v-model="adminForm.id" type="hidden" />

        <el-form-item label="登录名" prop="userName">
          <el-input
              v-model="adminForm.username"
              placeholder="请输入登录名"
              autocomplete="off"
          />
          <div style="margin-top: 5px; color: #999; font-size: 12px;">
            <span class="x-red">*</span>将会成为您唯一的登入名
          </div>
        </el-form-item>

        <el-form-item label="手机" prop="phone">
          <el-input
              v-model="adminForm.phone"
              placeholder="请输入手机号"
              autocomplete="off"
          />
          <div style="margin-top: 5px; color: #999; font-size: 12px;">
            <span class="x-red">*</span>请输入正确的手机号
          </div>
        </el-form-item>

        <el-form-item label="密码" prop="pwd">
          <el-input
              v-model="adminForm.pwd"
              type="password"
              placeholder="请输入密码"
              autocomplete="off"
          />
          <div style="margin-top: 5px; color: #999; font-size: 12px;">
            6到16个字符
          </div>
        </el-form-item>

        <el-form-item label="确认密码" prop="repass">
          <el-input
              v-model="adminForm.repass"
              type="password"
              placeholder="请再次输入密码"
              autocomplete="off"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 🔥 关键：通过 props 接收父组件传来的管理员ID
const props = defineProps({
  adminId: {
    type: [String, Number],
    required: true
  }
})

const adminFormRef = ref(null)

const adminForm = reactive({
  id: '',
  username: '',
  phone: '',
  pwd: '',
  repass: ''
})

const adminRules = reactive({
  username: [
    { required: true, message: '请输入登录名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  pwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须在6到16个字符之间', trigger: 'blur' }
  ],
  repass: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== adminForm.pwd) {
          callback(new Error('两次密码输入不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 🔥 获取管理员详情（从 props 拿ID）
const getAdminDetail = async () => {
  try {
    const adminId = props.adminId
    if (!adminId) {
      ElMessage.error('未获取到管理员ID')
      return
    }

    const res = await request.get(`/admin/getAdminById/${adminId}`)
    if (res.status === 1) {
      const data = res.data
      adminForm.id = data.id
      adminForm.username = data.username // 适配后端返回的字段名
      adminForm.phone = data.phone
      adminForm.pwd = ''
      adminForm.repass = ''
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('获取管理员信息失败：' + error.message)
  }
}

// 提交修改
// 提交修改
const submitForm = async () => {
  try {
    await adminFormRef.value.validate()

    const res = await request.post('/admin/updateAdmin', {
      id: adminForm.id,
      userName: adminForm.username, // 改成和后端BUser字段名一致
      phone: adminForm.phone,
      pwd: adminForm.pwd
    })

    if (res.status === 1) {
      ElMessage.success('修改成功')
      // 通知父页面刷新并关闭弹窗
      window.parent.postMessage({ action: 'refresh' }, '*')
      window.dispatchEvent(new CustomEvent('close'))
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('修改失败：' + (error.msg || '程序出错'))
  }
}

// 🔥 监听 props.adminId 变化，弹窗打开时自动加载数据
watch(() => props.adminId, () => {
  getAdminDetail()
}, { immediate: true })
</script>

<style scoped>
.admin-edit-page {
  padding: 20px;
}
.form-card {
  max-width: 800px;
  margin: 0 auto;
}
.x-red {
  color: #f56c6c;
}
</style>