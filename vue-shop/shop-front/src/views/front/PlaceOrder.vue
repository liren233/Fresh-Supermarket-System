<template>
  <div class="place-order-page">
    <Header />

    <div class="order_nav">
      <div class="order_nav_wrap clearfix">
        <div class="nav_item active">
          <span class="step">1</span>
          <p>确认订单信息</p>
        </div>
        <div class="nav_item">
          <span class="step">2</span>
          <p>提交订单</p>
        </div>
        <div class="nav_item">
          <span class="step">3</span>
          <p>支付完成</p>
        </div>
      </div>
    </div>

    <div class="order_main clearfix">
      <div class="order_left fl">
        <div class="address_wrap">
          <div class="address_title clearfix">
            <h3 class="fl">收货地址</h3>
            <a @click="goToAddAddress" class="fr">新增地址</a>
          </div>
          <div class="address_list">
            <div
                class="address_item"
                v-for="(addr, index) in addressList"
                :key="addr.id"
                :class="{ active: addr.id === selectedAddrId }"
                @click="selectAddress(addr.id)"
            >
              <!-- 把标签移到这里，用绝对定位放在右上角 -->
              <span v-if="addr.is_default === 1" class="default-tag">默认地址</span>
              <p class="addr_name">
                {{ addr.user_name }} {{ addr.phone }}
              </p>
              <p class="addr_detail">{{ addr.address }}</p>
              <div class="addr_op">
                <a @click.stop="editAddress(addr.id)">编辑</a>
                <span>|</span>
                <a @click.stop="deleteAddress(addr.id)">删除</a>
              </div>
            </div>
            <div v-if="addressList.length === 0" class="no_address">
              暂无收货地址，请<a @click="goToAddAddress">新增</a>
            </div>
          </div>
        </div>

        <div class="goods_list_wrap">
          <div class="goods_list_title clearfix">
            <h3 class="fl">商品清单</h3>
          </div>
          <table class="goods_table">
            <thead>
            <tr>
              <th class="col_img">商品图片</th>
              <th class="col_name">商品名称</th>
              <th class="col_price">单价</th>
              <th class="col_count">数量</th>
              <th class="col_total">小计</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(goods, index) in orderGoodsList" :key="goods.cartId">
              <td class="col_img"><img :src="'/static/images/'+goods.img_url" alt=""></td>
              <td class="col_name">{{ goods.good_name }}</td>
              <td class="col_price">¥{{ goods.price }}</td>
              <td class="col_count">{{ goods.num }}</td>
              <td class="col_total">¥{{ (goods.price * goods.num).toFixed(2) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="order_right fr">
        <div class="order_total_wrap">
          <div class="total_title">订单结算</div>
          <div class="total_list">
            <div class="total_item clearfix">
              <span class="fl">商品总价：</span>
              <span class="fr">¥{{ goodsTotalPrice.toFixed(2) }}</span>
            </div>
            <div class="total_item clearfix">
              <span class="fl">配送费：</span>
              <span class="fr">¥{{ deliveryFee.toFixed(2) }}</span>
            </div>
            <div class="total_item clearfix">
              <span class="fl">优惠：</span>
              <span class="fr discount">-¥{{ discount.toFixed(2) }}</span>
            </div>
            <div class="total_amount clearfix">
              <span class="fl">实付款：</span>
              <span class="fr amount">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
          </div>
          <button
              class="submit_order_btn"
              @click="submitOrder"
              :disabled="!selectedAddrId || orderGoodsList.length === 0"
          >
            提交订单
          </button>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

import Header from './Header.vue'
import Footer from './Footer.vue'

const router = useRouter()
const route = useRoute()

const addressList = ref([])
const selectedAddrId = ref('')
const orderGoodsList = ref([])
const deliveryFee = ref(10.00)
const discount = ref(0.00)

const goodsTotalPrice = computed(() => {
  return orderGoodsList.value.reduce((sum, item) => {
    return sum + (item.price * item.num)
  }, 0)
})

const totalAmount = computed(() => {
  return goodsTotalPrice.value + deliveryFee.value - discount.value
})

onMounted(async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  if (!userInfo) {
    ElMessage.warning('请先登录')
    router.push('/front/login')
    return
  }

  await getAddressList()
  await getOrderGoodsList()
})

// ✅ 获取地址列表（接口已修复）
const getAddressList = async () => {
  try {
    const res = await request.get('/address/list')
    if (res.status === 1) {
      addressList.value = res.data
      if (addressList.value.length > 0) {
        // 优先找 is_default === 1 的地址
        const defaultAddr = addressList.value.find(addr => addr.is_default === 1)
        if (defaultAddr) {
          selectedAddrId.value = defaultAddr.id
        } else {
          selectedAddrId.value = addressList.value[0].id
        }
      }
    }
  } catch (error) {
    ElMessage.error('获取收货地址失败')
    console.error(error)
  }
}

// ✅ 获取购物车商品（从路由参数）
const getOrderGoodsList = async () => {
  try {
    if (route.query.items) {
      const items = JSON.parse(route.query.items)
      orderGoodsList.value = items
    }
  } catch (error) {
    ElMessage.error('获取订单商品失败')
    console.error(error)
  }
}

const selectAddress = (addrId) => {
  selectedAddrId.value = addrId
}

const goToAddAddress = () => {
  router.push('/front/addAddress')
}

const editAddress = (addrId) => {
  router.push({ path: '/front/editAddress', query: { id: addrId } })
}

// ✅ 删除地址（接口已修复）
const deleteAddress = async (addrId) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？','提示',{
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await request.delete(`/api/address/delete/${addrId}`)
    if (res.status === 1) {
      ElMessage.success('删除成功')
      await getAddressList()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 提交订单
const submitOrder = async () => {
  if (!selectedAddrId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (orderGoodsList.value.length === 0) {
    ElMessage.warning('暂无订单商品')
    return
  }

  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  const orderData = {
    userId: userInfo.id,
    addressId: selectedAddrId.value,
    goodsList: orderGoodsList.value.map(item => ({
      goodsId: item.goodsId,
      good_name: item.good_name,
      count: item.num,
      price: item.price,
      img_url: item.img_url
    })),
    goodsTotal: goodsTotalPrice.value,
    deliveryFee: deliveryFee.value,
    discount: discount.value,
    totalAmount: totalAmount.value
  }

  try {
      const res = await request.post('/order/addOrder', orderData)
      if (res.status === 1) {
        ElMessage.success('订单提交成功')
        router.push({ path: '/front/pay', query: { orderId: res.data.orderId, orderNo: res.data.orderNo, totalAmount: totalAmount.value.toFixed(2) } })
      } else {
        ElMessage.error('订单提交失败')
      }
    } catch (error) {
      ElMessage.error('网络异常')
    }
}
</script>

<style scoped>
.place-order-page {
  width: 1200px;
  margin: 0 auto;
}

.order_nav {
  height: 80px;
  line-height: 80px;
  background-color: #f9f9f9;
  margin-top: 10px;
}

.order_nav_wrap {
  width: 1200px;
  margin: 0 auto;
}

.nav_item {
  float: left;
  width: 33.33%;
  text-align: center;
  position: relative;
}

.nav_item .step {
  display: inline-block;
  width: 30px;
  height: 30px;
  line-height: 30px;
  border-radius: 50%;
  background-color: #ccc;
  color: #fff;
  margin-right: 10px;
}

.nav_item.active .step {
  background-color: #ff4400;
}

.nav_item p {
  display: inline-block;
  font-size: 16px;
  color: #666;
  margin: 0;
}

.nav_item.active p {
  color: #ff4400;
  font-weight: bold;
}

.order_main {
  width: 1200px;
  margin: 20px auto;
}

.order_left.fl {
  float: left;
  width: 850px;
}

.order_right.fr {
  float: right;
  width: 330px;
}

.address_wrap {
  border: 1px solid #eee;
  padding: 20px;
  margin-bottom: 20px;
}

.address_title {
  height: 30px;
  line-height: 30px;
  margin-bottom: 15px;
}

.address_title h3 {
  float: left;
  font-size: 16px;
  color: #333;
  margin: 0;
}

.address_title a {
  float: right;
  color: #ff4400;
  text-decoration: none;
}

.address_list {
  overflow: hidden;
}

.address_item {
  float: left;
  width: 250px;
  padding: 15px;
  border: 1px solid #eee;
  margin-right: 15px;
  margin-bottom: 15px;
  cursor: pointer;
  position: relative; /* 关键：让子元素绝对定位 */
}

.address_item.active {
  border-color: #ff4400;
  background-color: #fff8f5;
}

/* 👇 把标签移到右上角 */
.default-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: #ff4400;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 3px;
}

.addr_name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.addr_detail {
  font-size: 12px;
  color: #666;
  line-height: 1.5;
}

.addr_op {
  margin-top: 10px;
  font-size: 12px;
}

.addr_op a {
  color: #ff4400;
  text-decoration: none;
  margin-right: 5px;
}

.addr_op span {
  color: #999;
  margin: 0 5px;
}

.no_address {
  padding: 20px;
  text-align: center;
  color: #666;
}

.no_address a {
  color: #ff4400;
  text-decoration: none;
}

.goods_list_wrap {
  border: 1px solid #eee;
  padding: 20px;
}

.goods_list_title h3 {
  font-size: 16px;
  color: #333;
  margin: 0 0 15px 0;
}

.goods_table {
  width: 100%;
  border-collapse: collapse;
}

.goods_table thead {
  background-color: #f9f9f9;
}

.goods_table th, .goods_table td {
  border: 1px solid #eee;
  padding: 10px;
  text-align: center;
}

.col_img img {
  width: 80px;
  height: 80px;
}

.col_name {
  text-align: left;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.col_price, .col_total {
  color: #e4393c;
}

.order_total_wrap {
  border: 1px solid #eee;
  padding: 20px;
  background-color: #f9f9f9;
}

.total_title {
  font-size: 16px;
  color: #333;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.total_item {
  height: 30px;
  line-height: 30px;
  font-size: 14px;
}

.total_item .fr {
  color: #333;
}

.discount {
  color: #e4393c !important;
}

.total_amount {
  height: 40px;
  line-height: 40px;
  font-size: 16px;
  font-weight: bold;
  border-top: 1px solid #eee;
  margin-top: 10px;
  padding-top: 10px;
}

.amount {
  color: #e4393c;
  font-size: 18px;
}

.submit_order_btn {
  width: 100%;
  height: 40px;
  line-height: 40px;
  background-color: #ff4400;
  border: none;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  margin-top: 20px;
}

.submit_order_btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.submit_order_btn:hover:not(:disabled) {
  background-color: #e4393c;
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