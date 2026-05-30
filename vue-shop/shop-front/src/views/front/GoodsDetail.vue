<template>
  <div class="goods-detail-page">
    <!-- 复用 Header 组件（和首页一致，包含用户名/退出登录） -->
    <Header />

    <!-- 搜索栏 -->
    <div class="search_bar clearfix">
      <a @click="goToHome" class="logo fl"><img :src="require('@/assets/images/logo.png')" alt="天天生鲜"></a>
      <div class="search_con fl">
        <input
            type="text"
            class="input_text fl"
            v-model="searchKeyword"
            placeholder="搜索商品"
            @keyup.enter="handleSearch"
        >
        <input
            type="button"
            class="input_btn fr"
            value="搜索"
            @click="handleSearch"
        >
      </div>
      <div class="guest_cart fr">
        <a @click="goToCart" class="cart_name fl">我的购物车</a>
        <div class="goods_count fl" id="show_count">{{ cartCount }}</div>
      </div>
    </div>

    <!-- 导航栏 -->
    <div class="navbar_con">
      <div class="navbar clearfix">
        <div class="subnav_con fl">
          <h1>全部商品分类</h1>
          <span></span>
          <ul class="subnav">
            <li v-for="(type, index) in typeList" :key="type.id">
              <a :href="`#midel0${index+1}`" class="fruit">{{ type.type_name }}</a>
            </li>
          </ul>
        </div>
        <ul class="navlist fl">
          <li><a @click="goToHome">首页</a></li>
          <li class="interval">|</li>
          <li><a href="javascript:;">手机生鲜</a></li>
          <li class="interval">|</li>
          <li><a href="javascript:;">抽奖</a></li>
        </ul>
      </div>
    </div>

    <!-- 面包屑 -->
    <div class="breadcrumb">
      <a href="javascript:;">全部分类</a>
      <span>></span>
      <a href="javascript:;">新鲜水果</a>
      <span>></span>
      <a href="javascript:;">商品详情</a>
    </div>

    <!-- 商品详情表单 -->
    <form @submit.prevent>
      <div class="goods_detail_con clearfix">
        <input type="hidden" name="goods_id" :value="goodsInfo.id">

        <!-- 商品图片 -->
        <div class="goods_detail_pic fl">
          <img :src="`/static/images/${goodsInfo.img_url}`" alt="商品图片">
        </div>

        <!-- 商品信息 -->
        <div class="goods_detail_list fr">
          <h3>{{ goodsInfo.good_name }}</h3>
          <p>{{ goodsInfo.intro }}</p>

          <!-- 价格区域 -->
          <div class="prize_bar">
            <span class="show_pirze">¥<em>{{ goodsInfo.price }}</em></span>
            <span class="show_unit">单  位：{{ goodsInfo.unit }}</span>
          </div>

          <!-- 数量选择 -->
          <div class="goods_num clearfix">
            <div class="num_name fl">数 量：</div>
            <div class="num_add fl">
              <input
                  type="text"
                  name="num"
                  class="num_show fl"
                  v-model.number="goodsNum"
                  @change="calculateTotalPrice"
              >
              <a href="javascript:;" class="add fr" @click="changeNum(1)">+</a>
              <a href="javascript:;" class="minus fr" @click="changeNum(-1)">-</a>
            </div>
          </div>

          <!-- 总价 -->
          <div class="total">总价：<em>{{ totalPrice.toFixed(2) }}元</em></div>

          <!-- 操作按钮 -->
          <div class="operate_btn">
            <input
                type="button"
                class="add_cart"
                id="add_cart"
                value="加入购物车"
                @click="addToCart"
            >
          </div>
        </div>
      </div>
    </form>

    <!-- 商品详情主体 -->
    <div class="main_wrap clearfix">
      <!-- 新品推荐 -->
      <div class="l_wrap fl clearfix">
        <div class="new_goods">
          <h3>新品推荐</h3>
          <ul>
            <li>
              <a href="javascript:;"><img :src="require('@/assets/images/goods/goods001.jpg')" alt="进口柠檬"></a>
              <h4><a href="javascript:;">进口柠檬</a></h4>
              <div class="prize">￥3.90</div>
            </li>
            <li>
              <a href="javascript:;"><img :src="require('@/assets/images/goods/goods002.jpg')" alt="玫瑰香葡萄"></a>
              <h4><a href="javascript:;">玫瑰香葡萄</a></h4>
              <div class="prize">￥16.80</div>
            </li>
          </ul>
        </div>
      </div>

      <!-- 商品详情/评论 -->
      <div class="r_wrap fr clearfix">
        <ul class="detail_tab clearfix">
          <li class="active">商品介绍</li>
          <li>评论</li>
        </ul>

        <div class="tab_content">
          <dl>
            <dt>商品详情：</dt>
            <dd>{{ goodsInfo.intro }}</dd>
          </dl>
        </div>
      </div>
    </div>

    <!-- 底部 -->
    <div class="footer">
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

    <!-- 加入购物车动画元素（保留原功能） -->
    <div class="add_jump"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 引入 Header 组件（和首页共用）
import Header from './Header.vue'

const router = useRouter()
const route = useRoute()

// 响应式数据定义
const typeList = ref([])         // 商品分类列表
const goodsInfo = ref({})        // 商品详情信息
const goodsNum = ref(1)          // 商品数量
const totalPrice = ref(0)        // 总价
const cartCount = ref(0)         // 购物车数量
const searchKeyword = ref('')    // 搜索关键词

// 计算总价
const calculateTotalPrice = () => {
  // 数量最小值限制
  if (goodsNum.value < 1) {
    goodsNum.value = 1
    ElMessage.warning('商品数量不能小于1')
  }
  // 计算总价
  totalPrice.value = goodsInfo.value.price * goodsNum.value
}

// 改变商品数量
const changeNum = (type) => {
  goodsNum.value += type
  calculateTotalPrice()
}

// 获取商品详情（适配后端 detailView 接口）
const getGoodsDetail = async () => {
  try {
    // 从路由参数获取商品ID
    const goodsId = route.query.id || route.params.id
    if (!goodsId) {
      ElMessage.error('未获取到商品ID')
      router.push('/front/index')
      return
    }

    // 调用后端已有的 detailView 接口（查询参数形式）
    const res = await request.get('/goods/detailView', {
      params: { id: goodsId }
    })

    if (res.status === 1) {
      // 后端返回的是 {bGood: {...}, typeList: [...]}，提取 bGood 赋值
      goodsInfo.value = res.data.bGood
      // 提取 typeList 赋值
      typeList.value = res.data.typeList
      // 初始化总价
      calculateTotalPrice()
    } else {
      ElMessage.error('获取商品详情失败：' + res.msg)
      // 无兜底，直接返回首页
      router.push('/front/index')
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败：' + error.message)
    // 无兜底，直接返回首页
    router.push('/front/index')
  }
}

// 获取商品分类列表
const getTypeList = async () => {
  try {
    const res = await request.get('/goods/getTypeList')
    if (res.status === 1) {
      typeList.value = res.data
    }
  } catch (error) {
    console.error('获取分类列表失败：', error)
  }
}

// 获取购物车数量
const getCartCount = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  if (!userInfo) return

  try {
    const res = await request.get('/cart/getCartCount')
    if (res.status === 1) {
      cartCount.value = res.data
    }
  } catch (error) {
    console.error('获取购物车数量失败：', error)
  }
}

// 加入购物车
const addToCart = async () => {
  // 检查用户是否登录
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  if (!userInfo) {
    ElMessageBox.confirm(
        '请先登录后再加入购物车',
        '提示',
        {
          confirmButtonText: '去登录',
          cancelButtonText: '取消',
          type: 'warning'
        }
    ).then(() => {
      router.push('/front/login')
    })
    return
  }

  try {
    // 调用加入购物车接口
    const res = await request.post('/cart/addCart', {
      goods_id: goodsInfo.value.id,
      num: goodsNum.value
    })

    if (res.status === 1) {
      ElMessage.success('商品已加入购物车！')
      // 更新购物车数量
      getCartCount()
    } else {
      ElMessage.error(res.msg || '加入购物车失败')
    }
  } catch (error) {
    ElMessage.error('加入购物车失败：' + error.message)
  }
}

// 页面跳转方法
const goToHome = () => router.push('/front/index')
const goToLogin = () => router.push('/front/login')
const goToRegister = () => router.push('/front/register')
const goToUserCenter = () => router.push('/front/userCenter')
const goToCart = () => router.push('/front/cart')
const goToOrder = () => router.push('/front/order')

// 搜索功能
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  router.push({
    path: '/front/search',
    query: { keyword: searchKeyword.value }
  })
}

// 页面挂载时初始化数据
onMounted(() => {
  getGoodsDetail()
  getTypeList()
  getCartCount()
})
</script>

<style scoped>
/* 仅保留组件专属布局样式，公共样式已在index.html全局引入 */
.goods-detail-page {
  width: 1200px;
  margin: 0 auto;
}

/* 加入购物车动画元素 */
.add_jump {
  display: none;
  position: absolute;
  width: 20px;
  height: 20px;
  background: url('~@/assets/images/shop_cart.png') no-repeat;
  background-size: 100% 100%;
  z-index: 999;
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