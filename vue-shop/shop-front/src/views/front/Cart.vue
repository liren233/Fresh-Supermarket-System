<template>
  <div class="cart-page">
    <!-- 引入头部组件 -->
    <Header />
    <SearchBar />

    <!-- 购物车内容 -->
    <div v-if="cartList.length > 0" class="cart-content">
      <!-- 商品总数 -->
      <div class="total_count">
        全部商品<em>{{ cartList.length }}</em>件
      </div>

      <!-- 表头 -->
      <ul class="cart_list_th clearfix">
        <li class="col01">选择</li>
        <li class="col02">商品图片</li>
        <li class="col03">商品名称</li>
        <li class="col04">商品单位</li>
        <li class="col05">商品价格</li>
        <li class="col06">数量</li>
        <li class="col07">小计</li>
        <li class="col08">操作</li>
      </ul>

      <!-- 购物车商品列表 -->
      <div v-for="(item, index) in cartList" :key="item.id" class="cart-item">
        <ul class="cart_list_td clearfix">
          <li class="col01">
            <el-checkbox
                v-model="item.checked"
                @change="calculateTotal"
            ></el-checkbox>
          </li>
          <li class="col02">
            <img :src="`/static/images/${item.goods.img_url}`" alt="商品图片" class="goods-img">
          </li>
          <li class="col03">
            {{ item.goods.good_name }}<br>
            <em>{{ item.goods.price }}元/{{ item.goods.unit }}</em>
          </li>
          <li class="col04">{{ item.goods.unit }}</li>
          <li class="col05">{{ item.goods.price }}元</li>
          <li class="col06">
            <div class="num_add">
              <a href="javascript:;" class="add fl" @click="changeNum(index, 1)">+</a>
              <input
                  type="text"
                  class="num_show fl"
                  v-model.number="item.num"
                  @change="calculateTotal"
              >
              <a href="javascript:;" class="minus fl" @click="changeNum(index, -1)">-</a>
            </div>
          </li>
          <li class="col07">{{ (item.goods.price * item.num).toFixed(2) }}元</li>
          <li class="col08">
            <a href="javascript:;" @click="deleteItem(index)">删除</a>
          </li>
        </ul>
      </div>

      <!-- 结算栏 -->
      <ul class="settlements">
        <li class="col01">
          <el-checkbox
              v-model="checkAll"
              @change="handleCheckAllChange"
          ></el-checkbox>
        </li>
        <li class="col02">全选</li>
        <li class="col03">
          合计(不含运费)：<span>¥</span><em>{{ totalPrice.toFixed(2) }}</em><br>
          共计<b>{{ totalNum }}</b>件商品
        </li>
        <li class="col04">
          <el-button
              type="primary"
              class="checkout-btn"
              @click="goToCheckout"
          >
            去结算
          </el-button>
        </li>
      </ul>
    </div>

    <!-- 空购物车提示 -->
    <div v-else class="empty-cart">
      <div class="empty-tips">
        <img src="/static/images/empty-cart.png" alt="空购物车">
        <p>您的购物车还是空的~</p>
        <el-button type="primary" @click="goToHome">去逛逛</el-button>
      </div>
    </div>

    <!-- 引入底部组件 -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 引入组件
import Header from './Header.vue'
import SearchBar from './SearchBar.vue'
import Footer from './Footer.vue'

const router = useRouter()

// 购物车列表数据
const cartList = ref([])

// 全选状态
const checkAll = ref(false)

// 总计价格和数量
const totalPrice = ref(0)
const totalNum = ref(0)

// 获取购物车数据
const getCartList = async () => {
  try {
    const res = await request.get('/cart/getCartList')
    if (res.status === 1) {
      cartList.value = res.data.map(item => ({
        id: item.id,
        num: item.num || 1,
        checked: true,
        goods: {
          id: item.bgood.id,
          good_name: item.bgood.good_name,
          price: item.bgood.price,
          unit: item.bgood.unit,
          img_url: item.bgood.img_url
        }
      }))
      calculateTotal()
    } else {
      ElMessage.warning('获取购物车数据失败：' + res.msg)
    }
  } catch (error) {
    ElMessage.error('获取购物车数据失败：' + error.message)
  }
}

// 计算选中商品的总价和总数
const calculateTotal = () => {
  let price = 0
  let num = 0

  cartList.value.forEach(item => {
    if (item.checked) {
      price += item.goods.price * item.num
      num += item.num
    }
  })

  totalPrice.value = price
  totalNum.value = num

  // 更新全选状态
  checkAll.value = cartList.value.length > 0 &&
      cartList.value.every(item => item.checked)
}

// 改变商品数量
const changeNum = (index, type) => {
  const item = cartList.value[index]
  const newNum = item.num + type

  if (newNum < 1) {
    ElMessage.warning('商品数量不能小于1')
    return
  }

  item.num = newNum
  updateCartItem(item.id, newNum)
  calculateTotal()
}

// 更新购物车商品数量
const updateCartItem = async (cartId, num) => {
  try {
    await request.post('/cart/updateCartNum', {
      cartId,
      num
    })
  } catch (error) {
    ElMessage.error('更新购物车数量失败：' + error.message)
  }
}

// 删除购物车商品
const deleteItem = async (index) => {
  try {
    const cartId = cartList.value[index].id

    const res = await request.post('/cart/deleteCartItem', {
      cartId
    })

    if (res.status === 1) {
      cartList.value.splice(index, 1)
      ElMessage.success('删除成功')
      calculateTotal()
    } else {
      ElMessage.error('删除失败：' + res.msg)
    }
  } catch (error) {
    ElMessage.error('删除失败：' + error.message)
  }
}

// 全选/取消全选
const handleCheckAllChange = (val) => {
  cartList.value.forEach(item => {
    item.checked = val
  })
  calculateTotal()
}

// 去结算
// Cart.vue 里的 goToCheckout 方法
const goToCheckout = () => {
  if (totalNum.value === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }

  // 把选中的商品信息完整打包，包含 img_url 和 good_name
  const selectedItems = cartList.value
      .filter(item => item.checked)
      .map(item => ({
        cartId: item.id,
        goodsId: item.goods.id,
        num: item.num,
        price: item.goods.price,
        // 关键：加上这两个字段
        good_name: item.goods.good_name,
        img_url: item.goods.img_url
      }))

  // 跳转到结算页面，并传递选中的商品信息
  router.push({
    path: '/front/placeOrder',
    query: {
      items: JSON.stringify(selectedItems)
    }
  })
}

// 去首页
const goToHome = () => {
  router.push('/front/index')
}

// 监听购物车列表变化，自动计算总价
watch(cartList, () => {
  calculateTotal()
}, { deep: true })

// 页面挂载时加载购物车数据
onMounted(() => {
  getCartList()
})
</script>

<style scoped>
.cart-page {
  width: 1200px;
  margin: 0 auto;
  padding-bottom: 50px;
}

.cart-content {
  margin-top: 20px;
}

.total_count {
  line-height: 35px;
  height: 35px;
  border-bottom: 1px solid #ccc;
  font-size: 16px;
  color: #333;
}

/* 表头和商品行统一列宽 */
.cart_list_th,
.cart_list_td {
  width: 100%;
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  align-items: center;
}

.cart_list_th {
  background-color: #f5f5f5;
  height: 35px;
  line-height: 35px;
  border: 1px solid #eee;
}

.cart_list_td {
  height: 100px;
  border: 1px solid #eee;
  border-top: none;
}

/* 每一列的宽度，表头和商品行完全对应 */
.col01 {
  width: 100px;
  text-align: center;
}

.col02 {
  width: 120px;
  text-align: center;
}

.col03 {
  width: 300px;
  padding: 0 10px;
}

.col04 {
  width: 100px;
  text-align: center;
}

.col05 {
  width: 100px;
  text-align: center;
}

.col06 {
  width: 150px;
  text-align: center;
}

.col07 {
  width: 120px;
  text-align: center;
}

.col08 {
  width: 100px;
  text-align: center;
}

/* 商品图片样式 */
.goods-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border: 1px solid #eee;
}

/* 数量加减框 */
.num_add {
  display: flex;
  align-items: center;
  justify-content: center;
}

.num_add a {
  display: inline-block;
  width: 25px;
  height: 25px;
  line-height: 25px;
  text-align: center;
  background-color: #eee;
  text-decoration: none;
  color: #333;
}

.num_show {
  width: 50px;
  height: 25px;
  line-height: 25px;
  text-align: center;
  border: 1px solid #eee;
  margin: 0 5px;
}

/* 结算栏 */
.settlements {
  display: flex;
  align-items: center;
  height: 50px;
  background-color: #ffe8c8;
  border: 1px solid #ffd8a6;
  margin-top: 10px;
  padding: 0 10px;
}

.settlements .col01 {
  width: 100px;
}

.settlements .col02 {
  width: 80px;
}

.settlements .col03 {
  flex: 1;
  text-align: right;
  font-size: 16px;
}

.settlements .col03 em {
  color: #ff4400;
  font-size: 20px;
  font-weight: bold;
}

.settlements .col04 {
  width: 120px;
  text-align: center;
}

.checkout-btn {
  width: 100px;
  height: 40px;
  line-height: 40px;
  background-color: #ff4400 !important;
  border: none !important;
  color: #fff !important;
}

/* 空购物车样式 */
.empty-cart {
  text-align: center;
  padding: 50px 0;
}

.empty-tips {
  display: inline-block;
  padding: 30px;
}

.empty-tips img {
  width: 100px;
  height: 100px;
  margin-bottom: 20px;
}

.empty-tips p {
  font-size: 16px;
  color: #999;
  margin-bottom: 20px;
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
</style>