<template>
  <div class="register-page">
    <div class="register_con">
      <div class="l_con fl">
        <a @click="goToHome" class="reg_logo"><img src="@/assets/images/logo02.png" alt="天天生鲜"></a>
        <div class="reg_slogan">足不出户  ·  新鲜每一天</div>
        <div class="reg_banner"></div>
      </div>

      <div class="r_con fr">
        <div class="reg_title clearfix">
          <h1>用户注册</h1>
          <a @click="goToLogin">登录</a>
        </div>
        <div class="reg_form clearfix">
          <!-- 禁用浏览器原生校验 -->
          <form @submit.prevent="handleRegister" novalidate>
            <ul>
              <li>
                <label>用户名:</label>
                <input
                    type="text"
                    v-model="registerForm.userName"
                    id="user_name"
                    @blur="checkUserName"
                    placeholder="请输入用户名"
                >
                <span v-show="userNameError" class="error-tip">{{ userNameErrorMsg }}</span>
              </li>
              <li>
                <label>密码:</label>
                <input
                    type="password"
                    v-model="registerForm.pwd"
                    id="pwd"
                    @blur="checkPwd"
                    placeholder="请输入密码"
                >
                <span v-show="pwdError" class="error-tip">{{ pwdErrorMsg }}</span>
              </li>
              <li>
                <label>确认密码:</label>
                <input
                    type="password"
                    v-model="registerForm.cpwd"
                    id="cpwd"
                    @blur="checkCPwd"
                    placeholder="请确认密码"
                >
                <span v-show="cpwdError" class="error-tip">{{ cpwdErrorMsg }}</span>
              </li>
              <li>
                <label>手机号:</label>
                <input
                    type="text"
                    v-model="registerForm.phone"
                    id="phone"
                    @blur="checkPhone"
                    placeholder="请输入手机号"
                >
                <span v-show="phoneError" class="error-tip">{{ phoneErrorMsg }}</span>
              </li>
              <li class="agreement">
                <input
                    type="checkbox"
                    v-model="registerForm.allow"
                    id="allow"
                >
                <label>同意”天天生鲜用户使用协议“</label>
                <span v-show="allowError" class="error-agree">{{ allowErrorMsg }}</span>
              </li>
              <li class="reg_sub">
                <input type="submit" value="注 册">
              </li>
            </ul>
          </form>
        </div>
      </div>
    </div>

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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

// 表单数据
const registerForm = ref({
  userName: '',
  pwd: '',
  cpwd: '',
  phone: '',
  allow: true
})

// 错误提示状态（初始全隐藏）
const userNameError = ref(false)
const userNameErrorMsg = ref('')
const pwdError = ref(false)
const pwdErrorMsg = ref('')
const cpwdError = ref(false)
const cpwdErrorMsg = ref('')
const phoneError = ref(false)
const phoneErrorMsg = ref('')
const allowError = ref(false)
const allowErrorMsg = ref('')

// 页面跳转
const goToHome = () => router.push('/front/index')
const goToLogin = () => router.push('/front/login')

// 1. 用户名校验（失焦触发，校验通过则隐藏提示）
const checkUserName = async () => {
  // 重置错误状态
  userNameError.value = false
  userNameErrorMsg.value = ''

  const val = registerForm.value.userName.trim()

  // 空值校验
  if (!val) {
    userNameError.value = true
    userNameErrorMsg.value = '请输入用户名'
    return
  }

  // 长度校验
  if (val.length < 3 || val.length > 20) {
    userNameError.value = true
    userNameErrorMsg.value = '用户名长度需在3-20位之间'
    return
  }

  // 后端唯一性校验
  try {
    const res = await request.get('/user/checkUserName', { params: { userName: val } })
    // 仅当后端返回不可用时才报错
    if (res.status === 1 && !res.data) {
      userNameError.value = true
      userNameErrorMsg.value = res.msg || '用户名已存在'
      return
    }
  } catch (e) {
    ElMessage.warning('用户名校验失败，请稍后重试')
  }
}

// 2. 密码校验（失焦触发）
const checkPwd = () => {
  // 重置错误状态
  pwdError.value = false
  pwdErrorMsg.value = ''

  const val = registerForm.value.pwd.trim()

  if (!val) {
    pwdError.value = true
    pwdErrorMsg.value = '请输入密码'
    return
  }

  if (val.length < 6 || val.length > 20) {
    pwdError.value = true
    pwdErrorMsg.value = '密码长度需在6-20位之间'
    return
  }
}

// 3. 确认密码校验（失焦触发）
const checkCPwd = () => {
  // 重置错误状态
  cpwdError.value = false
  cpwdErrorMsg.value = ''

  const pwd = registerForm.value.pwd.trim()
  const cpwd = registerForm.value.cpwd.trim()

  if (!cpwd) {
    cpwdError.value = true
    cpwdErrorMsg.value = '请确认密码'
    return
  }

  if (cpwd !== pwd) {
    cpwdError.value = true
    cpwdErrorMsg.value = '两次输入的密码不一致'
    return
  }
}

// 4. 手机号校验（失焦触发）
const checkPhone = () => {
  // 重置错误状态
  phoneError.value = false
  phoneErrorMsg.value = ''

  const val = registerForm.value.phone.trim()

  if (!val) {
    phoneError.value = true
    phoneErrorMsg.value = '请输入手机号'
    return
  }

  // 手机号格式校验（11位数字，13-19开头）
  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(val)) {
    phoneError.value = true
    phoneErrorMsg.value = '手机号格式不正确'
    return
  }
}

// 5. 注册提交（兜底校验）
const handleRegister = async () => {
  // 重置所有错误状态
  userNameError.value = false
  pwdError.value = false
  cpwdError.value = false
  phoneError.value = false
  allowError.value = false

  // 执行所有校验（兜底）
  await checkUserName()
  checkPwd()
  checkCPwd()
  checkPhone()

  // 协议勾选校验
  if (!registerForm.value.allow) {
    allowError.value = true
    allowErrorMsg.value = '请同意用户使用协议'
  }

  // 汇总错误并提示
  if (userNameError.value || pwdError.value || cpwdError.value || phoneError.value || allowError.value) {
    let errorMsg = '请修正以下错误：'
    if (userNameError.value) errorMsg += userNameErrorMsg.value + '；'
    if (pwdError.value) errorMsg += pwdErrorMsg.value + '；'
    if (cpwdError.value) errorMsg += cpwdErrorMsg.value + '；'
    if (phoneError.value) errorMsg += phoneErrorMsg.value + '；'
    if (allowError.value) errorMsg += allowErrorMsg.value

    ElMessage.error(errorMsg)
    return
  }

  // 所有校验通过，提交注册
  try {
    const res = await request.post('/user/doRegister', {
      userName: registerForm.value.userName.trim(),
      pwd: registerForm.value.pwd.trim(),
      phone: registerForm.value.phone.trim()
    })

    if (res.status === 1) {
      ElMessage.success('注册成功，请登录')
      router.push('/front/login')
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，注册失败')
    console.error('注册请求失败：', error)
  }
}
</script>

<style scoped>
/* 基础布局样式 */
.register-page {
  width: 1200px;
  margin: 0 auto;
}
.register_con {
  width: 1200px;
  margin: 20px auto;
  overflow: hidden;
}
.l_con.fl {
  float: left;
  width: 600px;
}
.reg_logo {
  display: block;
  margin-top: 50px;
  cursor: pointer;
}
.reg_logo img {
  width: 200px;
  height: 80px;
}
.reg_slogan {
  font-size: 24px;
  color: #666;
  margin: 30px 0;
  text-align: center;
}
.reg_banner {
  width: 600px;
  height: 300px;
  background: url('~@/assets/images/register_banner.png') no-repeat center;
  background-size: cover;
}
.r_con.fr {
  float: right;
  width: 400px;
  margin-top: 50px;
}
.reg_title {
  height: 40px;
  line-height: 40px;
  border-bottom: 2px solid #ff4400;
  margin-bottom: 20px;
}
.reg_title h1 {
  float: left;
  font-size: 20px;
  color: #ff4400;
  font-weight: bold;
  margin: 0;
}
.reg_title a {
  float: right;
  color: #666;
  text-decoration: none;
}
.reg_title a:hover {
  color: #ff4400;
}
.reg_form ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

/* 核心修复：防止错误提示竖排 */
.reg_form li {
  position: relative;
  height: 60px;
  line-height: 40px;
  margin-bottom: 10px;
  /* 强制水平排版，覆盖全局样式 */
  writing-mode: horizontal-tb !important;
  text-orientation: mixed !important;
}
.reg_form li label {
  display: inline-block;
  width: 80px;
  text-align: right;
  font-size: 14px;
  color: #666;
  margin-right: 10px;
}
.reg_form li input {
  width: 250px;
  height: 38px;
  border: 1px solid #ddd;
  padding: 0 10px;
  outline: none;
  font-size: 14px;
}
.reg_form li input:focus {
  border-color: #ff4400;
}

/* 错误提示样式（横向显示，仅报错时展示） */
.error-tip {
  position: absolute;
  left: 295px;
  top: 0;
  margin-left: 10px;
  color: #e4393c !important;
  font-size: 12px !important;
  height: 40px;
  line-height: 40px;
  white-space: nowrap !important;
  display: inline-block !important;
  z-index: 99;
}
.error-agree {
  position: absolute;
  left: 200px;
  top: 0;
  color: #e4393c !important;
  font-size: 12px !important;
  white-space: nowrap !important;
}

/* 按钮样式 */
.reg_sub {
  padding-left: 90px;
}
.reg_sub input {
  width: 270px;
  height: 40px;
  background-color: #ff4400;
  border: none;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
}
.reg_sub input:hover {
  background-color: #e4393c;
}

/* 协议勾选样式 */
.agreement {
  height: 40px !important;
  line-height: 40px !important;
  padding-left: 90px;
}
.agreement input {
  width: 16px !important;
  height: 16px !important;
  vertical-align: middle;
  margin-right: 5px;
}
.agreement label {
  display: inline !important;
  width: auto !important;
  text-align: left !important;
}

/* 底部样式 */
.footer.no-mp {
  width: 1200px;
  margin: 20px auto 0;
  padding-top: 20px;
  border-top: 1px solid #eee;
  text-align: center;
}
.foot_link a {
  color: #666;
  text-decoration: none;
  margin: 0 10px;
}
.foot_link a:hover {
  color: #ff4400;
}
.footer p {
  color: #666;
  line-height: 24px;
  margin: 5px 0;
}

/* 清除浮动 */
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