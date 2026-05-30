<template>
  <div class="index-page">
    <Header />
    <SearchBar subPageName="用户中心" />

    <div class="main_con clearfix">
      <div class="left_menu_con clearfix">
        <h3>用户中心</h3>
        <ul>
          <li><a @click="goToInfo">· 个人信息</a></li>
          <li><a class="active">· 全部订单</a></li>
          <li><a @click="goToSite">· 收货地址</a></li>
        </ul>
      </div>

      <div class="right_content clearfix">
        <h3 class="common_title2">全部订单 (共{{ totalCount }}个)</h3>
        <div v-if="orderList.length > 0" class="order_list_wrap">
          <div v-for="(order, index) in orderList" :key="order.id" class="order_item">
            <ul class="order_list_th w978 clearfix">
              <li class="col01">{{ formatTime(order.time) }}</li>
              <li class="col02">订单号：{{ order.order_no }}</li>
              <li class="col02 stress">{{ getOrderStatusText(order.status, order.pay_status) }}</li>
              <li class="col03" v-if="order.status === 1 && order.pay_status !== 1">
                <span v-if="countdowns[order.id] > 0" class="countdown">
                  支付剩余时间：{{ countdowns[order.id] }}秒
                </span>
                <span v-else class="countdown_end">订单已超时</span>
              </li>
            </ul>
            <div class="order_address">
              <span class="address_label">收货地址：</span>
              <span class="address_info">{{ order.user_name }} {{ order.phone }} {{ order.address }}</span>
            </div>
            <table class="order_list_table w980">
              <tbody>
              <tr v-for="(detail, detailIndex) in orderDetails[order.id]" :key="detail.id" v-if="detailIndex === 0 || expandedOrders[order.id]">
                <td width="55%">
                  <div class="order_goods_list clearfix">
                    <li class="col01"><img :src="'/static/images/' + detail.img_url" alt=""></li>
                    <li class="col02">{{ detail.good_name }}<em>{{ detail.price.toFixed(2) }}元/件</em></li>
                    <li class="col03">{{ detail.num }}</li>
                    <li class="col04">{{ detail.total_price.toFixed(2) }}元</li>
                  </div>
                </td>
                <td width="15%" v-if="detailIndex === 0" :rowspan="expandedOrders[order.id] ? orderDetails[order.id].length : 1">{{ order.total_price }}元</td>
                <td width="15%" v-if="detailIndex === 0" :rowspan="expandedOrders[order.id] ? orderDetails[order.id].length : 1">{{ getPayStatusText(order.pay_status) }}</td>
                <td width="15%" v-if="detailIndex === 0" :rowspan="expandedOrders[order.id] ? orderDetails[order.id].length : 1">
                  <a v-if="order.status === 1 && countdowns[order.id] > 0" class="oper_btn" @click="goToPay(order.id)">立即支付</a>
                  <a v-else-if="order.status === 1 && countdowns[order.id] <= 0" class="oper_btn disabled">订单已超时</a>
                  <a v-else class="oper_btn">查看物流</a>
                </td>
              </tr>
              <tr v-if="orderDetails[order.id].length > 1">
                <td colspan="4">
                  <a class="expand_btn" @click="expandedOrders[order.id] = !expandedOrders[order.id]">
                    {{ expandedOrders[order.id] ? '收起' : '展开' }} (共{{ orderDetails[order.id].length }}件商品)
                  </a>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-else class="no_order">
          暂无订单记录，<a @click="goToHome">去首页逛逛</a>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

import Header from './Header.vue'
import SearchBar from './SearchBar.vue'
import Footer from './Footer.vue'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const orderList = ref([])
const orderDetails = ref({})
const countdowns = ref({})
const expandedOrders = ref({})
const totalCount = ref(0)
let countdownInterval = null

onMounted(async () => {
  if (!userInfo.value || !userInfo.value.id) {
    ElMessage.warning('请先登录')
    router.push('/front/login')
    return
  }
  await getOrderList()
  startCountdowns()
})

onUnmounted(() => {
  if (countdownInterval) {
    clearInterval(countdownInterval)
  }
})

const getOrderList = async () => {
  try {
    const res = await request.get('/order/userCenterOrder')
    if (res.status === 1 && res.data && res.data.list) {
      const orderListData = res.data.list
      orderList.value = orderListData.map(item => {
        const order = item.order
        orderDetails.value[order.id] = item.details
        return order
      })
      totalCount.value = orderList.value.length
      initCountdowns()
    } else if (res.status === 0) {
      ElMessage.warning(res.msg || '获取订单失败')
    }
  } catch (e) {
    console.error('获取订单失败:', e)
    ElMessage.error('获取订单失败')
  }
}

const initCountdowns = () => {
  orderList.value.forEach(order => {
    if (order.status === 1) {
      const orderTime = new Date(order.time).getTime()
      const currentTime = new Date().getTime()
      const elapsedTime = Math.floor((currentTime - orderTime) / 1000)
      const remainingTime = 600 - elapsedTime // 10分钟 = 600秒
      countdowns.value[order.id] = Math.max(0, remainingTime)
    }
  })
}

const startCountdowns = () => {
  countdownInterval = setInterval(() => {
    orderList.value.forEach(order => {
      if (order.status === 1 && countdowns.value[order.id] > 0) {
        countdowns.value[order.id]--
        if (countdowns.value[order.id] === 0) {
          // 订单超时，删除订单
          deleteOrder(order.id)
        }
      }
    })
  }, 1000)
}

const deleteOrder = async (orderId) => {
  try {
    const res = await request.post('/order/delete', { orderId })
    if (res.status === 1) {
      ElMessage.success('订单已超时删除')
      await getOrderList()
    } else if (res.status === 0) {
      ElMessage.warning(res.msg || '删除订单失败')
    }
  } catch (e) {
    console.error('删除订单失败:', e)
    ElMessage.error('删除订单失败')
  }
}

const formatTime = (s) => {
  if (!s) return ''
  const d = new Date(s)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,0)}-${String(d.getDate()).padStart(2,0)}`
}
const getOrderStatusText = (s, payStatus) => {
  if (payStatus === 1) return '已付款'
  return {1:'未付款',2:'已付款',3:'已发货',4:'已收货'}[s] || '未知'
}
const getPayStatusText = s => ({0:'未支付',1:'已支付'}[s] || '未知')

const goToInfo = () => router.push('/front/userCenter')
const goToSite = () => router.push('/front/userCenterSite')
const goToHome = () => router.push('/front/index')
const goToPay = (orderId) => router.push(`/front/pay?orderId=${orderId}`)
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
.order_list_th { height: 30px; line-height: 30px; background: #f9f9f9; border: 1px solid #eee; padding: 0 10px; margin: 0 0 10px 0; }
.order_list_th .col01 { float: left; width: 30%; }
.order_list_th .col02 { float: left; width: 30%; }
.order_list_th .stress { color: #ff4400; }
.order_address { padding: 10px 15px; background-color: #f9f9f9; border: 1px solid #eee; border-top: none; border-bottom: none; font-size: 14px; }
.address_label { font-weight: bold; color: #666; margin-right: 5px; }
.address_info { color: #333; }
.order_list_table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
.order_list_table td { border: 1px solid #eee; padding: 10px; vertical-align: top; }
.order_goods_list { list-style: none; padding: 0; margin: 0 0 10px 0; overflow: hidden; }
.order_goods_list .col01 { float: left; width: 80px; }
.order_goods_list .col01 img { width: 80px; height: 80px; }
.order_goods_list .col02 { float: left; width: 300px; margin-left: 10px; }
.order_goods_list .col02 em { color: #ff4400; font-style: normal; margin-left: 10px; }
.order_goods_list .col03 { float: left; width: 80px; text-align: center; }
.order_goods_list .col04 { float: left; width: 80px; text-align: center; color: #ff4400; }
.oper_btn { display: inline-block; width: 80px; height: 28px; line-height: 28px; text-align: center; background: #ff4400; color: #fff; border-radius: 4px; text-decoration: none; }
.oper_btn.disabled { background: #ccc; color: #999; cursor: not-allowed; }
.oper_btn:hover:not(.disabled) { background: #ff6633; }
.expand_btn { display: inline-block; margin: 10px 0 0 10px; color: #4a89dc; text-decoration: none; }
.expand_btn:hover { color: #ff4400; }
.no_order { padding: 50px; text-align: center; color: #666; }
.no_order a { color: #ff4400; }
.clearfix::after { content: ""; display: table; clear: both; }
</style>