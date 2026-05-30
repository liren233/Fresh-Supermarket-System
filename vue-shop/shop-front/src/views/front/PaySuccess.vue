<template>
  <div class="pay-success-page">
    <Header />

    <div class="success-content">
      <div class="success-icon">✅</div>
      <h2>支付成功</h2>
      <p class="success-message">您的订单已支付成功，我们将尽快为您发货</p>
      
      <div class="order-info">
        <div class="info-item">
          <span>订单编号：</span>
          <span class="info-value">{{ orderInfo.orderNo }}</span>
        </div>
        <div class="info-item">
          <span>支付金额：</span>
          <span class="info-value amount">¥{{ orderInfo.amount }}</span>
        </div>
        <div class="info-item">
          <span>支付时间：</span>
          <span class="info-value">{{ currentTime }}</span>
        </div>
      </div>

      <div class="action-buttons">
        <button class="btn-primary" @click="goToOrderList">查看订单</button>
        <button class="btn-secondary" @click="goToHome">返回首页</button>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

import Header from './Header.vue'
import Footer from './Footer.vue'

const router = useRouter()
const route = useRoute()

const orderInfo = reactive({
  orderId: '',
  orderNo: '',
  amount: ''
})

const currentTime = computed(() => {
  const date = new Date()
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
})

onMounted(() => {
  const orderId = route.query.orderId
  const orderNo = route.query.orderNo
  const amount = route.query.amount
  
  if (!orderId || !orderNo) {
    ElMessage.error('缺少订单信息')
    router.push('/front/userCenter')
    return
  }
  
  orderInfo.orderId = orderId
  orderInfo.orderNo = orderNo
  orderInfo.amount = amount || '0.00'
})

const goToOrderList = () => {
  router.push('/front/userCenter')
}

const goToHome = () => {
  router.push('/front/index')
}
</script>

<style scoped>
.pay-success-page {
  width: 1200px;
  margin: 0 auto;
  padding: 40px 0;
}

.success-content {
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 60px;
  text-align: center;
  margin: 40px 0;
}

.success-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.success-content h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.success-message {
  color: #666;
  margin-bottom: 40px;
  font-size: 16px;
}

.order-info {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 30px;
  margin: 0 auto 40px;
  max-width: 500px;
  text-align: left;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  line-height: 1.5;
}

.info-item span:first-child {
  color: #999;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.amount {
  color: #ff4400;
  font-weight: bold;
  font-size: 18px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.btn-primary {
  padding: 12px 40px;
  background-color: #ff4400;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.btn-primary:hover {
  background-color: #e4393c;
}

.btn-secondary {
  padding: 12px 40px;
  border: 1px solid #ddd;
  background-color: #fff;
  color: #666;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.btn-secondary:hover {
  border-color: #999;
  color: #333;
}
</style>