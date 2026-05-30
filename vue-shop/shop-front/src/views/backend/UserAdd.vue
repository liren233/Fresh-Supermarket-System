<template>
  <div class="user-add-page">
    <el-card shadow="hover" class="form-card">
      <el-form
          ref="userFormRef"
          :model="userForm"
          :rules="userRules"
          label-width="100px"
          @submit.prevent="handleSubmit"
      >
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

        <!-- 手机号 -->
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
              placeholder="请输入密码"
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
          <el-button type="primary" @click="handleSubmit" :loading="isLoading">增加</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const isLoading = ref(false)
const userFormRef = ref(null)

const userForm = reactive({
  userName: '',
  phone: '',
  pwd: '',
  repass: ''
})

const userRules = reactive({
  userName: [
    { required: true, message: '请输入登录名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  pwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须在6到16个字符之间', trigger: 'blur' }
  ],
  repass: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== userForm.pwd) {
          callback(new Error('两次密码输入不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 提交表单
const handleSubmit = async () => {
  try {
    await userFormRef.value.validate()
    isLoading.value = true

    const res = await request.post('/user/addUser', {
      userName: userForm.userName,
      phone: userForm.phone,
      pwd: userForm.pwd
    })

    if (res.status === 1) {
      ElMessage.success('添加用户成功！')
      userFormRef.value.resetFields()
      window.parent.postMessage({ action: 'refresh' }, '*')
      window.dispatchEvent(new CustomEvent('close'))
    } else {
      ElMessage.error(res.msg || '添加失败')
    }
  } catch (error) {
    ElMessage.error('表单填写有误，请检查')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.user-add-page {
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