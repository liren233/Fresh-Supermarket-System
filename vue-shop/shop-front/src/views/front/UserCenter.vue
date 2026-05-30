<template>
  <div class="index-page">
    <Header />
    <SearchBar subPageName="用户中心" />

    <div class="main_con clearfix">
      <!-- 左侧菜单 -->
      <div class="left_menu_con clearfix">
        <h3>用户中心</h3>
        <ul>
          <li><a class="active">· 个人信息</a></li>
          <li><a @click="goToOrder">· 全部订单</a></li>
          <li><a @click="goToSite">· 收货地址</a></li>
        </ul>
      </div>

      <!-- 右侧内容 -->
      <div class="right_content clearfix">
        <div class="info_wrap">
          <h3 class="common_title2">个人信息</h3>
          <div class="info_form">
            <div class="form_item">
              <label>用户名：</label>
              <input type="text" v-model="userName" disabled />
            </div>
            <div class="form_item">
              <label>手机号：</label>
              <input type="text" v-model="phone" :disabled="!editMode" />
            </div>

            <div class="form_item" v-if="!editMode">
              <label>密码：</label>
              <input type="password" value="********" disabled />
            </div>

            <template v-else>
              <div class="form_item">
                <label>原密码：</label>
                <input type="password" v-model="oldPwd" placeholder="请输入原密码" />
              </div>
              <div class="form_item">
                <label>新密码：</label>
                <input type="password" v-model="newPwd" placeholder="请输入新密码" />
              </div>
            </template>

            <div class="form_btn">
              <button v-if="!editMode" @click="openEdit" class="edit_btn">修改信息</button>
              <template v-else>
                <button @click="saveInfo" class="save_btn">保存</button>
                <button @click="cancelEdit" class="cancel_btn">取消</button>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

import Header from './Header.vue'
import SearchBar from './SearchBar.vue'
import Footer from './Footer.vue'

const router = useRouter()

const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const editMode = ref(false)
const userName = ref('')
const phone = ref('')
const oldPwd = ref('')
const newPwd = ref('')

onMounted(() => {
  if (!userInfo.value || !userInfo.value.id) {
    ElMessage.warning('请先登录')
    router.push('/front/login')
    return
  }
  userName.value = userInfo.value.userName || ''
  phone.value = userInfo.value.phone || ''
})

const openEdit = () => { editMode.value = true }
const cancelEdit = () => { editMode.value = false; oldPwd.value = ''; newPwd.value = '' }

const saveInfo = async () => {
  if (oldPwd.value !== userInfo.value.pwd) {
    ElMessage.error('原密码错误！')
    return
  }
  if (!newPwd.value) {
    ElMessage.error('新密码不能为空！')
    return
  }
  try {
    const res = await request.post('/user/update', { phone: phone.value, pwd: newPwd.value })
    if (res.status === 1) {
      ElMessage.success('修改成功')
      editMode.value = false
      userInfo.value.phone = phone.value
      userInfo.value.pwd = newPwd.value
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      oldPwd.value = ''
      newPwd.value = ''
    }
  } catch (e) {
    ElMessage.error('修改失败')
  }
}

const goToOrder = () => router.push('/front/userCenterOrder')
const goToSite = () => router.push('/front/userCenterSite')
</script>

<style scoped>
.index-page { width: 1200px; margin: 0 auto; }
.main_con { width: 1200px; margin: 20px auto; }
.left_menu_con { float: left; width: 200px; background: #f9f9f9; border: 1px solid #eee; }
.left_menu_con h3 { height: 40px; line-height: 40px; text-align: center; margin: 0; border-bottom: 1px solid #eee; }
.left_menu_con ul { list-style: none; padding: 0; margin: 0; }
.left_menu_con li { height: 35px; line-height: 35px; padding-left: 30px; }
.left_menu_con li a { color: #666; text-decoration: none; }
.left_menu_con li a.active { color: #ff4400; }
.right_content { float: right; width: 980px; }
.common_title2 { font-size: 16px; padding-bottom: 10px; border-bottom: 2px solid #ff4400; margin-bottom: 20px; }
.info_form { width: 400px; }
.form_item { margin-bottom: 15px; display: flex; align-items: center; }
.form_item label { width: 80px; }
.form_item input { flex: 1; height: 30px; border: 1px solid #eee; padding: 0 10px; }
.form_btn { margin-top: 20px; }
.edit_btn, .save_btn, .cancel_btn { padding: 5px 15px; margin-right: 10px; border: none; border-radius: 4px; cursor: pointer; }
.edit_btn { background: #ff4400; color: #fff; }
.save_btn { background: #42b983; color: #fff; }
.cancel_btn { background: #ccc; color: #fff; }
.clearfix::after { content: ""; display: table; clear: both; }
</style>