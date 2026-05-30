<template>
  <div class="login-page">
    <!-- 登录页顶部logo -->
    <div class="login_top clearfix">
      <a @click="goToHome" class="login_logo"><img :src="require('@/assets/images/logo02.png')" alt="天天生鲜"></a>
    </div>

    <!-- 登录表单区域 -->
    <div class="login_form_bg">
      <div class="login_form_wrap clearfix">
        <div class="login_banner fl"></div>
        <div class="slogan fl">日夜兼程 · 急速送达</div>
        <div class="login_form fr">
          <div class="login_title clearfix">
            <h1>用户登录</h1>
          </div>
          <div class="form_input">
            <form @submit.prevent="handleLogin">
              <input
                  type="text"
                  v-model="loginForm.userName"
                  class="name_input"
                  placeholder="请输入用户名"
              >
              <div v-if="userErrorMsg" class="user_error">{{ userErrorMsg }}</div>
              <input
                  type="password"
                  v-model="loginForm.pwd"
                  class="pass_input"
                  placeholder="请输入密码"
              >
              <div v-if="pwdErrorMsg" class="pwd_error">{{ pwdErrorMsg }}</div>
              <div class="more_input clearfix">
                <input
                    type="checkbox"
                    v-model="loginForm.remember"
                >
                <label>记住用户名</label>
                <a @click="goToForgetPwd">忘记密码</a>
              </div>
              <input type="submit" value="登录" class="input_submit">

              <!-- 注册和管理员登录按钮 -->
              <div class="bottom-links clearfix">
                <a @click="goToRegister" class="link-btn">立即注册</a>
                <span>|</span>
                <a @click="goToAdminLogin" class="link-btn">管理员登录</a>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部组件 -->
    <div class="footer no-mp">
      <div class="foot_link">
        <a href="javascript:;">关于我们</a>
        <span>|</span>
        <a href="javascript:;">联系我们</a>
        <span>|</span>
        <a href="javascript:;">招聘人才</a>
        <span>|</span>
        <a href="javascript:;">友情链接</a>
      </div>
      <p>CopyRight © 2016 北京天天生鲜信息技术有限公司 All Rights Reserved</p>
      <p>电话：010-****888    京ICP备*******8号</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

// 响应式数据
const loginForm = ref({
  userName: '',
  pwd: '',
  remember: false
})
const userErrorMsg = ref('')
const pwdErrorMsg = ref('')

// 页面挂载时读取记住的用户名
onMounted(() => {
  const rememberName = localStorage.getItem('rememberUserName')
  if (rememberName) {
    loginForm.value.userName = rememberName
    loginForm.value.remember = true
  }
})

// 页面跳转方法
const goToHome = () => router.push('/front/index')
const goToRegister = () => router.push('/front/register')
const goToForgetPwd = () => router.push('/front/forgetPwd')
// 跳转到管理员登录页
const goToAdminLogin = () => router.push('/backend/login')

// 登录处理
const handleLogin = async () => {
  userErrorMsg.value = ''
  pwdErrorMsg.value = ''

  if (!loginForm.value.userName.trim()) {
    userErrorMsg.value = '请输入用户名'
    return
  }
  if (!loginForm.value.pwd.trim()) {
    pwdErrorMsg.value = '请输入密码'
    return
  }

  try {
    const res = await request.post('/user/doLogin', loginForm.value)
    if (res.status === 1) {
      localStorage.setItem('userInfo', JSON.stringify(res.data.user))
      localStorage.setItem('token', res.data.token) // 存储后端返回的 token
      if (loginForm.value.remember) {
        localStorage.setItem('rememberUserName', loginForm.value.userName)
      } else {
        localStorage.removeItem('rememberUserName')
      }
      ElMessage.success('登录成功')
      router.push('/front/index')
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('网络异常，登录失败')
    console.error(error)
  }
}
</script>

<style scoped>
.login-page {
  width: 1200px;
  margin: 0 auto;
}

.login_top {
  width: 1200px;
  height: 100px;
  margin: 0 auto;
}

.login_logo {
  display: block;
  margin-top: 20px;
}

.login_logo img {
  width: 170px;
  height: 60px;
}

.login_form_bg {
  background-color: #f9f9f9;
  padding: 30px 0;
}

.login_form_wrap {
  width: 1200px;
  margin: 0 auto;
  background-color: #fff;
  border: 1px solid #eee;
  padding: 20px;
}

.login_banner.fl {
  float: left;
  width: 600px;
  height: 350px;
  background: url('~@/assets/images/login_banner.png') no-repeat;
  background-size: cover;
}

.slogan.fl {
  float: left;
  width: 100px;
  height: 350px;
  line-height: 350px;
  text-align: center;
  font-size: 18px;
  color: #ff4400;
  font-weight: bold;
  margin-left: 20px;
}

.login_form.fr {
  float: right;
  width: 350px;
  height: 350px;
}

.login_title {
  height: 40px;
  line-height: 40px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.login_title h1 {
  float: left;
  font-size: 18px;
  color: #333;
  font-weight: bold;
  margin: 0;
}

.form_input {
  padding: 10px;
}

.name_input, .pass_input {
  width: 328px;
  height: 38px;
  border: 1px solid #ddd;
  padding: 0 10px;
  margin-bottom: 10px;
  outline: none;
  font-size: 14px;
}

.name_input:focus, .pass_input:focus {
  border-color: #ff4400;
}

.user_error, .pwd_error {
  color: #e4393c;
  height: 20px;
  line-height: 20px;
  margin-bottom: 10px;
}

.more_input {
  margin: 10px 0;
}

.more_input input {
  float: left;
  margin-top: 5px;
  margin-right: 5px;
}

.more_input label {
  float: left;
  color: #666;
}

.more_input a {
  float: right;
  color: #ff4400;
  text-decoration: none;
}

.more_input a:hover {
  color: #e4393c;
}

.input_submit {
  width: 330px;
  height: 40px;
  background-color: #ff4400;
  border: none;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  margin-top: 10px;
}

.input_submit:hover {
  background-color: #e4393c;
}

/* 底部链接区域样式 */
.bottom-links {
  margin-top: 150px;
  text-align: center;
}
.bottom-links .link-btn {
  color: #ff4400;
  text-decoration: none;
  margin: 0 8px;
}
.bottom-links .link-btn:hover {
  color: #e4393c;
}
.bottom-links span {
  color: #ddd;
}

.footer.no-mp {
  width: 1200px;
  margin: 20px auto 0;
  padding-top: 20px;
  border-top: 1px solid #eee;
  text-align: center;
}

.clearfix::after {
  content: "";
  display: table;
  clear: both;
}

.fl {
  float: left;
}

.fr {
  float: right;
}
</style>