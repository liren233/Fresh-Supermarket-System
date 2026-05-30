<template>
  <div class="pay-page">
    <Header />

    <div class="pay-main">
      <div class="pay-steps">
        <div class="step-item">
          <span class="step-number active">1</span>
          <p>确认订单信息</p>
        </div>
        <div class="step-item">
          <span class="step-number active">2</span>
          <p>提交订单</p>
        </div>
        <div class="step-item">
          <span class="step-number active">3</span>
          <p>支付完成</p>
        </div>
      </div>

      <div class="pay-content">
        <div class="order-info">
          <h3>订单信息</h3>
          <div class="order-detail">
            <p><span>订单编号：</span>{{ orderInfo.orderNo }}</p>
            <p><span>订单金额：</span>¥{{ orderInfo.totalAmount }}</p>
            <p><span>订单状态：</span>{{ orderInfo.statusText }}</p>
          </div>
        </div>

        <div class="payment-method">
          <h3>选择支付方式</h3>
          <div class="payment-options">
            <div 
              class="payment-item" 
              v-for="method in paymentMethods" 
              :key="method.id"
              :class="{ active: selectedPayment === method.id }"
              @click="selectedPayment = method.id"
            >
              <div class="payment-icon">{{ method.icon }}</div>
              <div class="payment-name">{{ method.name }}</div>
              <div class="payment-desc">{{ method.desc }}</div>
            </div>
          </div>
        </div>

        <div class="pay-actions">
          <button class="cancel-btn" @click="goToOrderList">取消支付</button>
          <button class="pay-btn" @click="handlePay" :disabled="isPaying">
            {{ isPaying ? '支付中...' : `立即支付 ¥${orderInfo.totalAmount}` }}
          </button>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

import Header from './Header.vue'
import Footer from './Footer.vue'

const router = useRouter()
const route = useRoute()

const orderInfo = reactive({
  orderId: '',
  orderNo: '',
  totalAmount: '',
  statusText: '待支付'
})

const selectedPayment = ref(1) // 默认选择微信支付
const isPaying = ref(false)

const paymentMethods = [
  {
    id: 1,
    name: '微信支付',
    icon: '💳',
    desc: '推荐使用微信支付'
  },
  {
    id: 2,
    name: '支付宝',
    icon: '💰',
    desc: '支付宝快捷支付'
  },
  {
    id: 3,
    name: '银行卡',
    icon: '💵',
    desc: '储蓄卡/信用卡支付'
  }
]

onMounted(() => {
  const orderId = route.query.orderId
  const orderNo = route.query.orderNo
  if (!orderId || !orderNo) {
    ElMessage.error('缺少订单信息')
    router.push('/front/userCenter')
    return
  }
  
  orderInfo.orderId = orderId
  orderInfo.orderNo = orderNo
  // 这里可以从后端获取订单详情，暂时使用前端传递的数据
  orderInfo.totalAmount = route.query.totalAmount || '0.00'
})

const handlePay = async () => {
  isPaying.value = true
  
  try {
    // 直接调用支付接口，不需要模拟支付过程
    const res = await request.post('/order/pay', {
      orderId: orderInfo.orderId,
      payType: selectedPayment.value
    })
    
    if (res.status === 1) {
      ElMessage.success('支付成功')
      // 跳转到支付成功页面
      router.push({ 
        path: '/front/paySuccess', 
        query: { 
          orderId: orderInfo.orderId,
          orderNo: orderInfo.orderNo,
          amount: orderInfo.totalAmount
        }
      })
    } else {
      ElMessage.error('支付失败，请重试')
    }
  } catch (error) {
    ElMessage.error('支付过程中出现错误')
  } finally {
    isPaying.value = false
  }
}

const goToOrderList = () => {
  router.push('/front/userCenter')
}
</script>

<style scoped>
.pay-page {
  width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
}

.pay-steps {
  display: flex;
  justify-content: center;
  margin: 40px 0;
}

.step-item {
  display: flex;
  align-items: center;
  margin: 0 40px;
  position: relative;
}

.step-item:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 15px;
  right: -40px;
  width: 80px;
  height: 2px;
  background-color: #ddd;
}

.step-number {
  display: inline-block;
  width: 30px;
  height: 30px;
  line-height: 30px;
  border-radius: 50%;
  background-color: #ccc;
  color: #fff;
  text-align: center;
  margin-right: 10px;
}

.step-number.active {
  background-color: #ff4400;
}

.step-item p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.step-item.active p {
  color: #ff4400;
  font-weight: bold;
}

.pay-content {
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 40px;
}

.order-info {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.order-info h3,
.payment-method h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 20px;
  font-weight: bold;
}

.order-detail p {
  line-height: 2;
  color: #666;
}

.order-detail span {
  color: #999;
  margin-right: 10px;
}

.payment-method {
  margin-bottom: 30px;
}

.payment-options {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.payment-item {
  width: 200px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.payment-item:hover {
  border-color: #ff4400;
}

.payment-item.active {
  border-color: #ff4400;
  background-color: #fff8f5;
}

.payment-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.payment-name {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.payment-desc {
  font-size: 12px;
  color: #999;
}

.pay-actions {
  display: flex;
  justify-content: flex-end;
  gap: 20px;
  margin-top: 30px;
}

.cancel-btn {
  padding: 10px 30px;
  border: 1px solid #ddd;
  background-color: #fff;
  color: #666;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.cancel-btn:hover {
  border-color: #999;
}

.pay-btn {
  padding: 12px 40px;
  background-color: #ff4400;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.pay-btn:hover:not(:disabled) {
  background-color: #e4393c;
}

.pay-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>