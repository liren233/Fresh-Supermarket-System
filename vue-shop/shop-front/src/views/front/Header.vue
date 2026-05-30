<template>
  <div class="header_con">
    <div class="header">
      <div class="welcome fl">欢迎来到天天生鲜!</div>
      <div class="fr">
        <!-- 登录/未登录状态切换 -->
        <div v-if="userInfo" class="login_btn fl">
          欢迎您：<em style="color: red">{{ userInfo.userName }}</em>
          <span>|</span>
          <a href="javascript:;" @click="logout">退出登录</a>
        </div>
        <div v-else class="login_btn fl">
          <a href="javascript:;" @click="goToLogin">登录</a>
          <span>|</span>
          <a href="javascript:;" @click="goToRegister">注册</a>
        </div>

        <!-- 用户功能链接 -->
        <div class="user_link fl">
          <span>|</span>
          <a href="javascript:;" @click="goToUserCenter">用户中心</a>
          <span>|</span>
          <a href="javascript:;" @click="goToCart">我的购物车</a>
          <span>|</span>
          <a href="javascript:;" @click="goToOrder">我的订单</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

// 响应式数据：用户信息（从本地存储获取，对应原JSP的${user}）
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

// 页面跳转方法（核心修复：购物车路径改为 /front/cart）
const goToLogin = () => router.push('/front/login')
const goToRegister = () => router.push('/front/register')
const goToUserCenter = () => router.push('/front/userCenter') // 修正：原路径 /front/myAddress 改为路由中注册的 /front/userCenter
const goToCart = () => router.push('/front/cart') // 修复：从 /front/showCart 改为 /front/cart
const goToOrder = () => router.push('/front/userCenterOrder')

// 退出登录（修改版：优先清理前端，再调用后端）
const logout = async () => {
  try {
    // 1. 先清理前端本地存储（不管后端是否成功，都要让前端变成未登录状态）
    localStorage.removeItem('userInfo')
    userInfo.value = null

    // 2. 再调用后端退出接口（即使后端返回未登录，前端也已经退出了）
    try {
      const res = await request.get('/user/logout')
      if (res.status === 1) {
        ElMessage.success('退出登录成功')
      } else {
        ElMessage.info('已清理本地登录状态')
      }
    } catch (err) {
      // 后端报错也没关系，前端已经退出了
      ElMessage.info('已清理本地登录状态')
    }

    // 3. 强制跳转到首页
    router.push('/front/index')
  } catch (error) {
    ElMessage.error('退出登录失败：' + error.message)
  }
}

// 页面挂载时，校验用户登录状态（可选：刷新用户信息）
onMounted(() => {
  if (userInfo.value) {
    // 可选：调用接口刷新用户信息
    // refreshUserInfo()
  }
})
</script>

<style scoped>
/* 仅保留组件专属布局样式，公共样式依赖全局引入的 reset.css/main.css */
.header_con {
  height: 30px;
  background-color: #f7f7f7;
  color: #666;
}

.header {
  width: 1200px;
  margin: 0 auto;
  height: 30px;
  line-height: 30px;
}

.welcome.fl {
  float: left;
}

.fr {
  float: right;
}

.login_btn.fl {
  float: left;
  margin-right: 10px;
}

.login_btn a {
  color: #666;
  text-decoration: none;
  margin: 0 5px;
}

.login_btn a:hover {
  color: #ff4400;
}

.login_btn span {
  color: #999;
  margin: 0 5px;
}

.user_link.fl {
  float: left;
}

.user_link a {
  color: #666;
  text-decoration: none;
  margin: 0 5px;
}

.user_link a:hover {
  color: #ff4400;
}

.user_link span {
  color: #999;
  margin: 0 5px;
}

/* 清除浮动 */
.fl {
  float: left;
}

.fr {
  float: right;
}
</style>