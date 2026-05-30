<template>
  <div class="user-edit-page">
    <el-card shadow="hover" class="form-card">
      <el-form
          ref="userFormRef"
          :model="userForm"
          :rules="userRules"
          label-width="100px"
          @submit.prevent="handleSubmit"
      >
        <el-input v-model="userForm.id" type="hidden" />

        <!-- 登录名 -->
        <el-form-item label="登录名" prop="userName">
          <el-input
              v-model="userForm.userName"
              placeholder="请输入登录名"
              autocomplete="off"
          />
          <el-form-item__error style="margin-top: 5px;">
            <span class="x-red">*</span>将会成为您唯一的登入名
          </el-form-item__error>
        </el-form-item>

        <!-- 手机 -->
        <el-form-item label="手机" prop="phone">
          <el-input
              v-model="userForm.phone"
              placeholder="请输入手机号"
              autocomplete="off"
          />
          <el-form-item__error style="margin-top: 5px;">
            <span class="x-red">*</span>请输入正确的手机号
          </el-form-item__error>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item label="密码" prop="pwd">
          <el-input
              v-model="userForm.pwd"
              type="password"
              placeholder="不修改请留空"
              autocomplete="off"
          />
          <el-form-item__error style="margin-top: 5px;">
            6到16个字符
          </el-form-item__error>
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item label="确认密码" prop="repass">
          <el-input
              v-model="userForm.repass"
              type="password"
              placeholder="请再次输入密码"
              autocomplete="off"
          />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="isLoading">
            修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 从父组件弹窗接收 ID
const props = defineProps({
  id: {
    type: [String, Number],
    required: true
  }
})

const isLoading = ref(false)
const userFormRef = ref(null)

const userForm = reactive({
  id: '',
  userName: '',
  phone: '',
  pwd: '',
  repass: ''
})

// 验证规则
const validateRepass = (rule, value, callback) => {
  if (userForm.pwd && value !== userForm.pwd) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}
const validatePhone = (rule, value, callback) => {
  const phoneReg = /^1[3-9]\d{9}$/
  if (!value || !phoneReg.test(value)) {
    callback(new Error('请输入正确手机号'))
  } else {
    callback()
  }
}
const validatePwd = (rule, value, callback) => {
  if (value && (value.length < 6 || value.length > 16)) {
    callback(new Error('密码6-16位'))
  } else {
    callback()
  }
}

const userRules = reactive({
  userName: [{ required: true, message: '请输入登录名', trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  pwd: [{ validator: validatePwd, trigger: 'blur' }],
  repass: [{ validator: validateRepass, trigger: 'blur' }]
})

// 获取用户详情
const getUserDetail = async () => {
  try {
    const userId = props.id
    if (!userId) return

    const res = await request.get(`/user/getUserById/${userId}`)
    if (res.status === 1) {
      const u = res.data
      userForm.id = u.id
      userForm.userName = u.userName || ''
      userForm.phone = u.phone || ''
      userForm.pwd = ''
      userForm.repass = ''
    }
  } catch (e) {
    ElMessage.error('加载用户失败')
  }
}

// 提交修改
const handleSubmit = async () => {
  try {
    await userFormRef.value.validate()
    isLoading.value = true

    const res = await request.post('/user/updateForAdmin', {
      id: userForm.id,
      userName: userForm.userName,
      phone: userForm.phone,
      pwd: userForm.pwd
    })

    if (res.status === 1) {
      ElMessage.success('修改成功')
      setTimeout(() => {
        window.parent.postMessage({ action: 'refresh' }, '*')
        window.dispatchEvent(new CustomEvent('close'))
      }, 300)
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } catch (e) {
    ElMessage.error('提交失败')
  } finally {
    isLoading.value = false
  }
}

// 监听 ID 变化自动加载
watch(() => props.id, () => {
  getUserDetail()
}, { immediate: true })
</script>

<style scoped>
.user-edit-page {
  padding: 20px;
}
.form-card {
  max-width: 800px;
  margin: 0 auto;
}
.x-red {
  color: #f56c6c;
}
.el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
}
</style>