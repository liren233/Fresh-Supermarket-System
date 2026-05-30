<template>
  <div class="index-page">
    <Header />
    <SearchBar subPageName="用户中心" />

    <div class="main_con clearfix">
      <div class="left_menu_con clearfix">
        <h3>用户中心</h3>
        <ul>
          <li><a @click="goToInfo">· 个人信息</a></li>
          <li><a @click="goToOrder">· 全部订单</a></li>
          <li><a class="active">· 收货地址</a></li>
        </ul>
      </div>

      <div class="right_content clearfix">
        <h3 class="common_title2">收货地址管理</h3>

        <div class="address_list">
          <div v-for="addr in addressList" :key="addr.id" class="address_item">
            <div class="address_info">
              <p class="recipient">{{ addr.user_name }} <span class="phone">{{ addr.phone }}</span></p>
              <p class="address">{{ addr.address }}</p>
              <span v-if="addr.is_default === 1" class="default_tag">默认地址</span>
            </div>
            <div class="address_actions">
              <button v-if="addr.is_default !== 1" @click="setDefault(addr.id)" class="btn default_btn">设为默认</button>
              <button @click="openEdit(addr)" class="btn edit_btn">编辑</button>
              <button @click="deleteAddress(addr.id)" class="btn delete_btn">删除</button>
            </div>
          </div>
        </div>

        <button @click="openAdd" class="btn add_btn">新增地址</button>

        <div class="modal_mask" v-if="showModal" @click.self="closeModal">
          <div class="modal_content">
            <h3>{{ isEdit ? '编辑地址' : '新增地址' }}</h3>
            <form @submit.prevent="handleSaveAddress">
              <div class="form_group">
                <label>收件人：</label>
                <input type="text" v-model="addressForm.user_name" placeholder="请输入收件人姓名">
              </div>
              <div class="form_group">
                <label>手机号：</label>
                <input type="text" v-model="addressForm.phone" placeholder="请输入手机号">
              </div>
              <div class="form_group">
                <label>所在地区：</label>
                <div class="region_select">
                  <select v-model="regionForm.province" @change="handleProvinceChange">
                    <option value="">请选择省份</option>
                    <option v-for="province in provinces" :key="province" :value="province">{{ province }}</option>
                  </select>
                  <select v-model="regionForm.city" @change="handleCityChange">
                    <option value="">请选择城市</option>
                    <option v-for="city in cities" :key="city" :value="city">{{ city }}</option>
                  </select>
                  <select v-model="regionForm.district">
                    <option value="">请选择区域</option>
                    <option v-for="district in districts" :key="district" :value="district">{{ district }}</option>
                  </select>
                </div>
              </div>
              <div class="form_group">
                <label>详细地址：</label>
                <textarea v-model="addressForm.address" placeholder="请输入详细地址"></textarea>
              </div>
              <div class="modal_btns">
                <button type="submit" class="btn save_btn">保存</button>
                <button type="button" @click="closeModal" class="btn cancel_btn">取消</button>
              </div>
            </form>
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
import { provinces, cityData, districtData } from '@/utils/regionData'

import Header from './Header.vue'
import SearchBar from './SearchBar.vue'
import Footer from './Footer.vue'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const addressList = ref([])
const showModal = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const addressForm = ref({
  user_name: '',
  phone: '',
  address: '',
  is_default: 0
})

// 地区选择表单
const regionForm = ref({
  province: '',
  city: '',
  district: ''
})

// 城市列表
const cities = ref([])
// 区域列表
const districts = ref([])

// 处理省份选择变化
const handleProvinceChange = () => {
  const province = regionForm.value.province
  cities.value = cityData[province] || []
  regionForm.value.city = ''
  regionForm.value.district = ''
  districts.value = []
}

// 处理城市选择变化
const handleCityChange = () => {
  const city = regionForm.value.city
  districts.value = districtData[city] || []
  regionForm.value.district = ''
}

const getAddressList = async () => {
  try {
    const res = await request.get('/address/list')
    if (res.status === 1) addressList.value = res.data
  } catch (e) {
    ElMessage.error('获取地址失败')
  }
}

const openAdd = () => {
  isEdit.value = false
  addressForm.value = {
    user_name: '',
    phone: '',
    address: '',
    is_default: 0
  }
  regionForm.value = {
    province: '',
    city: '',
    district: ''
  }
  cities.value = []
  districts.value = []
  showModal.value = true
}

const openEdit = (addr) => {
  isEdit.value = true
  editingId.value = addr.id
  addressForm.value = {
    user_name: addr.user_name,
    phone: addr.phone,
    address: addr.address,
    is_default: addr.is_default
  }
  // 尝试解析地址，提取地区信息
  parseAddress(addr.address)
  showModal.value = true
}

// 解析地址，提取地区信息
const parseAddress = (fullAddress) => {
  // 简单的地址解析逻辑，实际项目中可能需要更复杂的解析
  regionForm.value = {
    province: '',
    city: '',
    district: ''
  }
  
  // 重置城市和区域列表
  cities.value = []
  districts.value = []
}

const closeModal = () => {
  showModal.value = false
}

const handleSaveAddress = async () => {
  if (!addressForm.value.user_name || !addressForm.value.phone || !addressForm.value.address) {
    ElMessage.error('请填写完整信息')
    return
  }

  if (!regionForm.value.province || !regionForm.value.city || !regionForm.value.district) {
    ElMessage.error('请选择完整的地区信息')
    return
  }

  try {
    // 合并地区信息和详细地址
    const fullAddress = `${regionForm.value.province}${regionForm.value.city}${regionForm.value.district}${addressForm.value.address.trim()}`
    
    const data = {
      user_name: addressForm.value.user_name.trim(),
      phone: addressForm.value.phone.trim(),
      address: fullAddress,
      is_default: 0
    }

    if (isEdit.value) {
      data.id = editingId.value
      await request.post('/address/updateAddress', data)
      ElMessage.success('修改成功')
    } else {
      await request.post('/address/addAddress', data)
      ElMessage.success('新增成功')
    }

    closeModal()
    getAddressList()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const setDefault = async (id) => {
  try {
    await request.post('/address/setDefault', { id })
    ElMessage.success('已设为默认地址')
    getAddressList()
  } catch (e) {
    ElMessage.error('设置失败')
  }
}

const deleteAddress = async (id) => {
  if (!confirm('确定删除？')) return
  try {
    await request.post('/address/delAddress', { id })
    ElMessage.success('删除成功')
    getAddressList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  if (!userInfo.value.id) {
    ElMessage.warning('请先登录')
    router.push('/front/login')
    return
  }
  getAddressList()
})

const goToInfo = () => router.push('/front/userCenter')
const goToOrder = () => router.push('/front/userCenterOrder')
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

.address_list { margin-bottom: 20px; }
.address_item {
  border: 1px solid #eee;
  padding: 15px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.address_info .recipient { margin: 0 0 5px; font-weight: bold; }
.address_info .phone { margin-left: 10px; color: #666; font-weight: normal; }
.address_info .address { margin: 0 0 5px; color: #333; }
.default_tag { background: #ff4400; color: #fff; padding: 2px 8px; font-size: 12px; border-radius: 2px; }
.address_actions .btn { margin-left: 10px; padding: 5px 10px; border: none; border-radius: 4px; cursor: pointer; }
.default_btn { background: #ff9f00; color: #fff; }
.edit_btn { background: #42b983; color: #fff; }
.delete_btn { background: #e4393c; color: #fff; }

.add_btn { background: #ff4400; color: #fff; padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; }

.modal_mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 999; }
.modal_content { background: #fff; padding: 20px; border-radius: 4px; width: 450px; }
.form_group { margin-bottom: 15px; }
.form_group label { display: block; margin-bottom: 5px; }
.form_group input, .form_group textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
.form_group textarea { height: 80px; resize: none; }
.region_select {
  display: flex;
  gap: 10px;
}
.region_select select {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}
.modal_btns { text-align: right; margin-top: 20px; }
.modal_btns .btn { margin-left: 10px; padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; }
.save_btn { background: #42b983; color: #fff; }
.cancel_btn { background: #ccc; color: #333; }

.clearfix::after { content: ""; display: table; clear: both; }
</style>