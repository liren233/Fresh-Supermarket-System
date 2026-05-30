<template>
  <div class="admin-add-page">
    <el-card shadow="hover" class="form-card">
      <el-form
          ref="adminFormRef"
          :model="adminForm"
          :rules="adminRules"
          label-width="100px"
          @submit.prevent="submitForm"
      >
        <!-- 登录名 -->
        <el-form-item label="登录名" prop="userName">
          <el-input
              v-model="adminForm.userName"
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
              v-model="adminForm.phone"
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
              v-model="adminForm.pwd"
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
              v-model="adminForm.repass"
              type="password"
              placeholder="请再次输入密码"
              autocomplete="off"
          />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm">增加</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const adminFormRef = ref(null)

const adminForm = reactive({
  userName: '',
  phone: '',
  pwd: '',
  repass: ''
})

const adminRules = reactive({
  userName: [
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

// 提交表单
const submitForm = async () => {
  try {
    await adminFormRef.value.validate()

    const res = await request.post('/admin/addAdmin', {
      userName: adminForm.userName,
      pwd: adminForm.pwd,
      phone: adminForm.phone
    })

    if (res.status === 1) {
      ElMessage.success('添加成功')
      // 通知父页面刷新并关闭弹窗
      window.parent.postMessage({ action: 'refresh' }, '*')
      window.dispatchEvent(new CustomEvent('close'))

      // ✅ 关键：重置表单数据，清空所有输入框
      adminFormRef.value.resetFields()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('表单填写有误，请检查')
  }
}
</script>

<style scoped>
.admin-add-page {
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