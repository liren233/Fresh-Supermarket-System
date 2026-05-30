<template>
  <div class="index-page">
    <!-- 头部组件（复用已改造的Header.vue） -->
    <Header />

    <!-- 搜索栏（保留样式，移除搜索功能） -->
    <div class="search_bar clearfix">
      <a @click="goToHome" class="logo fl"><img src="@/assets/images/logo.png" alt="天天生鲜"></a>
      <div class="search_con fl">
        <input
            type="text"
            class="input_text fl"
            v-model="searchKeyword"
            placeholder="搜索商品"
            disabled  <!-- 禁用输入 -->
        >
        <input
            type="button"
            class="input_btn fr"
            value="搜索"
            disabled  <!-- 禁用按钮 -->
        >
      </div>
      <div class="guest_cart fr">
        <a @click="goToCart" class="cart_name fl">我的购物车</a>
        <div class="goods_count fl" id="show_count">{{ cartCount }}</div>
      </div>
    </div>

    <!-- 导航栏 -->
    <div class="navbar_con">
      <div class="navbar">
        <h1 class="fl">全部商品分类</h1>
        <ul class="navlist fl">
          <li><a @click="goToHome">首页</a></li>
          <li class="interval">|</li>
          <li><a href="javascript:;">手机生鲜</a></li>
          <li class="interval">|</li>
          <li><a href="javascript:;">抽奖</a></li>
        </ul>
      </div>
    </div>

    <!-- 轮播+分类区域 -->
    <div class="center_con clearfix">
      <ul class="subnav fl">
        <!-- 仅渲染接口返回的分类 -->
        <li v-for="(item, index) in typeList" :key="item.id">
          <a :href="`#model0${index+1}`" class="fruit">{{ item.type_name }}</a>
        </li>
      </ul>
      <div class="slide fl">
        <ul class="slide_pics">
          <li><img src="@/assets/images/slide.jpg" alt="幻灯片"></li>
          <li><img src="@/assets/images/slide02.jpg" alt="幻灯片"></li>
          <li><img src="@/assets/images/slide03.jpg" alt="幻灯片"></li>
          <li><img src="@/assets/images/slide04.jpg" alt="幻灯片"></li>
        </ul>
        <div class="prev"></div>
        <div class="next"></div>
        <ul class="points"></ul>
      </div>
      <div class="adv fl">
        <a href="javascript:;"><img src="@/assets/images/adv01.jpg"></a>
        <a href="javascript:;"><img src="@/assets/images/adv02.jpg"></a>
      </div>
    </div>

    <!-- 商品列表区域 -->
    <div v-for="(item, index) in goodsList" :key="index" class="list_model">
      <div class="list_title clearfix">
        <h3 class="fl" :id="`model0${index+1}`">{{ item.key }}</h3>
        <div class="subtitle fl">
          <span>|</span>
          <a href="javascript:;">鲜芒</a>
          <a href="javascript:;">加州提子</a>
          <a href="javascript:;">亚马逊牛油果</a>
        </div>
        <a href="javascript:;" class="goods_more fr" :id="`fruit_more`" @click="goToGoodsList">查看更多 ></a>
      </div>

      <div class="goods_con clearfix">
        <div class="goods_banner fl"><img :src="`/static/images/banner0${index+1}.jpg`"></div>
        <ul class="goods_list fl">
          <li v-for="(good, gIndex) in item.value" :key="good.id">
            <h4><a @click="goToDetail(good.id)">{{ good.good_name }}</a></h4>
            <a @click="goToDetail(good.id)"><img :src="`/static/images/${good.img_url}`"></a>
            <div class="prize">¥ {{ good.price }}</div>
          </li>
        </ul>
      </div>
    </div>

    <!-- 底部组件（复用已改造的Footer.vue） -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, onUpdated } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 引入头部/底部组件
import Header from './Header.vue'
import Footer from './Footer.vue'

const router = useRouter()

// 响应式数据（无兜底）
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
const typeList = ref([])
const goodsList = ref([])
const searchKeyword = ref('')
const cartCount = ref(0)

// 页面跳转方法
const goToHome = () => router.push('/front/index')
// 修复：跳转路径改为 /front/cart（和路由一致）
const goToCart = () => router.push('/front/cart')
const goToGoodsList = () => router.push('/front/list')
const goToDetail = (goodsId) => {
  router.push({ path: '/front/goodsDetail', query: { id: goodsId } })
}

// 移除搜索功能（因为不需要搜索结果页）
const handleSearch = () => {
  ElMessage.info('暂未开放搜索功能')
}

// 获取商品分类列表
const getTypeList = async () => {
  try {
    const res = await request.get('/goods/getTypeList')
    if (res.status === 1 && res.data && res.data.length > 0) {
      typeList.value = res.data
    } else {
      ElMessage.warning('暂无商品分类数据')
    }
  } catch (error) {
    console.error('获取分类列表失败：', error)
    ElMessage.error('获取商品分类失败，请稍后重试')
  }
}

// 获取首页商品列表
const getGoodsList = async () => {
  try {
    const res = await request.get('/goods/getIndexGoodsList')
    if (res.status === 1 && res.data && res.data.length > 0) {
      goodsList.value = res.data
    } else {
      ElMessage.warning('暂无商品数据')
    }
  } catch (error) {
    console.error('获取商品列表失败：', error)
    ElMessage.error('获取商品列表失败，请稍后重试')
  }
}

// 获取购物车数量
const getCartCount = async () => {
  if (!userInfo.value) {
    cartCount.value = 0
    return
  }
  try {
    const res = await request.get('/cart/getCartCount')
    if (res.status === 1) {
      cartCount.value = res.data
    } else {
      cartCount.value = 0
    }
  } catch (error) {
    console.error('获取购物车数量失败：', error)
    cartCount.value = 0
  }
}

// 初始化轮播
const initSlideshow = () => {
  if (window.BCSlideshow) {
    window.BCSlideshow('focuspic')
  }
}

// 页面挂载时初始化数据
onMounted(async () => {
  await getTypeList()
  await getGoodsList()
  await getCartCount()
  initSlideshow()
})

// 页面更新后重新初始化轮播
onUpdated(() => {
  initSlideshow()
})
</script>

<style scoped>
/* 样式和之前一致，无需修改 */
.index-page {
  width: 1200px;
  margin: 0 auto;
}

.search_bar {
  width: 1200px;
  height: 100px;
  margin: 0 auto;
}

.logo.fl {
  float: left;
  margin-top: 10px;
}

.logo img {
  width: 170px;
  height: 80px;
}

.search_con.fl {
  float: left;
  width: 450px;
  height: 35px;
  margin-top: 25px;
  margin-left: 100px;
  border: 1px solid #ff4400;
}

.input_text.fl {
  float: left;
  width: 350px;
  height: 33px;
  border: none;
  padding: 0 10px;
  outline: none;
}

.input_btn.fr {
  float: right;
  width: 88px;
  height: 35px;
  background-color: #ff4400;
  border: none;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
}

.guest_cart.fr {
  float: right;
  width: 150px;
  height: 35px;
  margin-top: 25px;
  margin-right: 20px;
  text-align: center;
  line-height: 35px;
  border: 1px solid #eee;
}

.cart_name.fl {
  float: left;
  width: 100px;
  color: #ff4400;
  text-decoration: none;
}

.goods_count.fl {
  float: left;
  width: 40px;
  height: 35px;
  background-color: #ff4400;
  color: #fff;
  font-weight: bold;
}

.navbar_con {
  height: 40px;
  background-color: #ff4400;
}

.navbar {
  width: 1200px;
  margin: 0 auto;
  height: 40px;
}

.navbar h1.fl {
  float: left;
  width: 200px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: normal;
  background-color: #e4393c;
  margin: 0;
}

.navlist.fl {
  float: left;
  margin-left: 30px;
}

.navlist li {
  float: left;
  height: 40px;
  line-height: 40px;
  margin-left: 30px;
}

.navlist li a {
  font-size: 16px;
  color: #fff;
  text-decoration: none;
}

.navlist li a:hover {
  color: #ffe400;
}

.interval {
  color: #fff;
}

.center_con {
  width: 1200px;
  margin: 0 auto;
  margin-top: 10px;
  overflow: hidden;
}

.subnav.fl {
  float: left;
  width: 200px;
  height: 460px;
  background-color: #fff;
  border: 1px solid #e4393c;
}

.subnav li {
  height: 30px;
  line-height: 30px;
  padding-left: 20px;
}

.subnav li a {
  font-size: 14px;
  color: #666;
  text-decoration: none;
}

.subnav li a:hover {
  color: #ff4400;
}

.slide.fl {
  float: left;
  width: 730px;
  height: 458px;
  margin-left: 10px;
  border: 1px solid #eee;
  position: relative;
  overflow: hidden;
}

.slide_pics {
  width: 730px;
  height: 458px;
}

.slide_pics li {
  width: 730px;
  height: 458px;
}

.slide_pics img {
  width: 100%;
  height: 100%;
}

.prev, .next {
  position: absolute;
  top: 50%;
  width: 30px;
  height: 60px;
  margin-top: -30px;
  background-color: rgba(0,0,0,0.3);
  color: #fff;
  text-align: center;
  line-height: 60px;
  cursor: pointer;
  display: none;
}

.slide:hover .prev, .slide:hover .next {
  display: block;
}

.prev {
  left: 0;
}

.next {
  right: 0;
}

.points {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
}

.points li {
  float: left;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #fff;
  margin: 0 5px;
  cursor: pointer;
}

.points li.active {
  background-color: #ff4400;
}

.adv.fl {
  float: left;
  width: 250px;
  height: 458px;
  margin-left: 10px;
}

.adv a {
  display: block;
  margin-bottom: 10px;
}

.adv img {
  width: 100%;
  height: auto;
}

.list_model {
  width: 1200px;
  margin: 0 auto;
  margin-top: 20px;
}

.list_title {
  height: 30px;
  line-height: 30px;
  border-bottom: 2px solid #e4393c;
}

.list_title h3.fl {
  float: left;
  font-size: 16px;
  color: #e4393c;
  font-weight: bold;
}

.subtitle.fl {
  float: left;
  margin-left: 10px;
}

.subtitle span {
  color: #999;
  margin: 0 5px;
}

.subtitle a {
  color: #666;
  text-decoration: none;
  margin: 0 5px;
}

.subtitle a:hover {
  color: #ff4400;
}

.goods_more.fr {
  float: right;
  color: #666;
  text-decoration: none;
}

.goods_more.fr:hover {
  color: #ff4400;
}

.goods_con {
  margin-top: 10px;
  overflow: hidden;
}

.goods_banner.fl {
  float: left;
  width: 200px;
}

.goods_banner img {
  width: 100%;
  height: auto;
}

.goods_list.fl {
  float: left;
  width: 1000px;
  overflow: hidden;
}

.goods_list li {
  float: left;
  width: 248px;
  height: 280px;
  border: 1px solid #eee;
  margin-left: 1px;
  margin-bottom: 1px;
  text-align: center;
}

.goods_list li h4 {
  height: 30px;
  line-height: 30px;
  overflow: hidden;
  padding: 0 10px;
}

.goods_list li h4 a {
  color: #666;
  text-decoration: none;
  cursor: pointer;
}

.goods_list li h4 a:hover {
  color: #ff4400;
}

.goods_list li img {
  width: 200px;
  height: 200px;
  margin: 10px 0;
  cursor: pointer;
}

.goods_list li .prize {
  color: #e4393c;
  font-size: 16px;
  font-weight: bold;
}
</style>