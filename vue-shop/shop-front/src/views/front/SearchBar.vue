<template>
  <div class="search_bar clearfix">
    <!-- Logo：跳转首页 -->
    <a @click="goToHome" class="logo fl">
      <img src="/static/images/logo.png" alt="天天生鲜">
    </a>

    <!-- 子页面名称：支持动态传入 -->
    <div class="sub_page_name fl" v-if="subPageName">|{{ ' '.repeat(3) }}{{ subPageName }}</div>

    <!-- 搜索框区域 -->
    <div class="search_con fr">
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
  </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 定义组件属性：支持外部传入子页面名称（如“购物车”）
const props = defineProps({
  subPageName: {
    type: String,
    default: '' // 默认不显示子页面名称
  }
})

// 响应式数据：搜索关键词
const searchKeyword = ref('')

// 跳转首页
const goToHome = () => {
  router.push('/front/index')
}

// 搜索处理逻辑
const handleSearch = () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    // 无关键词时提示
    alert('请输入要搜索的商品名称') // 或使用 ElMessage：import { ElMessage } from 'element-plus'
    return
  }

  // 跳转到搜索结果页，携带关键词参数
  router.push({
    path: '/front/search',
    query: { keyword }
  })

  // 清空搜索框
  searchKeyword.value = ''
}
</script>

<style scoped>
/* 核心样式：1:1还原原 search_bar.jsp 样式 */
.search_bar {
  width: 1200px;
  height: 80px;
  margin: 0 auto;
  line-height: 80px;
  border-bottom: 1px solid #eee;
}

/* Logo 样式 */
.logo.fl {
  float: left;
  margin-right: 20px;
  cursor: pointer;
}

.logo img {
  width: 150px;
  height: 60px;
  margin-top: 10px;
}

/* 子页面名称样式 */
.sub_page_name.fl {
  float: left;
  font-size: 14px;
  color: #666;
}

/* 搜索框容器 */
.search_con.fr {
  float: right;
  width: 400px;
  height: 38px;
  margin-top: 21px;
  border: 1px solid #ff4400;
}

/* 搜索输入框 */
.input_text.fl {
  float: left;
  width: 320px;
  height: 36px;
  border: none;
  outline: none;
  padding: 0 10px;
  font-size: 14px;
}

/* 搜索按钮 */
.input_btn.fr {
  float: right;
  width: 60px;
  height: 38px;
  background-color: #ff4400;
  border: none;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
}

.input_btn:hover {
  background-color: #e4393c;
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