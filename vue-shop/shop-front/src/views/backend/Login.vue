<template>
  <div class="login-page">
    <!-- 登录表单容器 -->
    <div class="login" :class="{ 'layui-anim': true, 'layui-anim-up': true }">
      <div class="message">x-admin2.0-管理登录</div>
      <div id="darkbannerwrap"></div>

      <!-- 登录表单 -->
      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              type="text"
              size="large"
              prefix-icon="el-icon-user"
          />
        </el-form-item>

        <hr class="hr15">

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              placeholder="密码"
              type="password"
              size="large"
              prefix-icon="el-icon-lock"
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <hr class="hr15">

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              style="width: 100%;"
              @click="handleLogin"
              :loading="isLoading"
          >
            登录
          </el-button>
        </el-form-item>

        <hr class="hr20">
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request' // 引入封装的axios请求

const router = useRouter()

// 登录加载状态
const isLoading = ref(false)
// 表单引用
const loginFormRef = ref(null)

// 表单字段名和后端一致（username/password）
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单校验规则（prop同步修改）
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
})

// 处理登录逻辑
const handleLogin = async () => {
  try {
    // 表单校验
    await loginFormRef.value.validate()

    isLoading.value = true

    // 请求参数和后端字段名一致
    const res = await request.post('/admin/doAdminLogin', {
      username: loginForm.username,
      password: loginForm.password
    })

    if (res.status === 1) {
      // 存储adminInfo（和路由守卫匹配）
      localStorage.setItem('adminInfo', JSON.stringify(res.data.user))

      ElMessage.success('登录成功！')

      // 跳转到正确的后台首页路径
      router.push('/backend/index')
    } else {
      ElMessage.error(res.msg || '登录失败，请检查用户名或密码')
    }
  } catch (error) {
    console.error('登录失败：', error)
    ElMessage.error(error.message || '网络异常，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

// 页面挂载时检查是否已登录
onMounted(() => {
  // const adminInfo = localStorage.getItem('adminInfo')
  // if (adminInfo) {
  //   // 已登录直接跳转到后台首页
  //   router.push('/backend/index')
  // }
})
</script>

<style scoped>
/* 登录页面整体样式 */
.login-page {
  min-height: 100vh;
  background-color: #283443;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

/* 登录表单容器 */
.login {
  width: 100%;
  max-width: 350px;
  background: #fff;
  padding: 30px;
  border-radius: 5px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  animation: layui-anim-up 0.5s;
}

/* 登录标题 */
.message {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

/* 装饰元素 */
#darkbannerwrap {
  background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAALElEQVR4AeXNwQkAMAwE0Mn9L+3Ggtgkk35QwcnSJo9S+yGwM9DCooCbgn4YrJ4CIPUcQF7/XSBbx2TEz4sAZ2q1RAECBAiYBlCtvwN+KiYAlG7UDGj59MViT9hOwEqAhYCtAsUZvL6I6W8c2wcbd+LIWSCHSTeSAAECngN4xxIDSK9f4B9t377Wd7H5Nt7/Xz8eAgwAvesLRjYYPuUAAAAASUVORK5CYII=");
  width: 100%;
  height: 6px;
  margin-bottom: 20px;
}

/* 表单样式 */
.login-form {
  width: 100%;
}

/* 分割线样式 */
.hr15 {
  height: 15px;
  border: none;
  margin: 0;
}

.hr20 {
  height: 20px;
  border: none;
  margin: 0;
}

/* 动画效果 */
.layui-anim-up {
  animation: layui-anim-up 0.5s;
}

@keyframes layui-anim-up {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 适配Element Plus输入框样式 */
:deep(.el-input) {
  width: 100%;
}

:deep(.el-input__wrapper) {
  padding: 0 15px;
}
</style>