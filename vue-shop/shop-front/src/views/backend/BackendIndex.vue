<template>
  <div class="backend-index">
    <!-- 顶部导航 -->
    <div class="container">
      <div class="logo"><a href="/backend/index">X-admin v2.0</a></div>
      <div class="left_open" @click="toggleSideNav">
        <i title="展开左侧栏" class="iconfont">&#xe699;</i>
      </div>

      <!-- 左侧：+新增 下拉菜单 -->
      <el-dropdown class="layui-nav left fast-add">
        <span class="el-dropdown-link">
          +新增<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="xAdminShow('资讯','http://www.baidu.com')">
            <i class="iconfont">&#xe6a2;</i>资讯
          </el-dropdown-item>
          <el-dropdown-item @click.native="xAdminShow('图片','http://www.baidu.com')">
            <i class="iconfont">&#xe6a8;</i>图片
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!-- 右侧：admin + 三个按钮（直接显示） -->
      <div class="right-menu">
        <span class="admin-name">{{ adminName }}</span>
        <a href="javascript:;" class="top-btn" @click="handlePersonalInfo">个人信息</a>
        <a href="javascript:;" class="top-btn" @click="handleSwitchAccount">切换账号</a>
        <a href="javascript:;" class="top-btn exit-btn" @click="handleLogout">退出</a>
        <a href="/" target="_blank" class="top-btn">前台首页</a>
      </div>
    </div>

    <!-- 中部布局 -->
    <div class="main-layout">
      <div class="left-nav" :class="{ collapsed: isSideNavCollapsed }">
        <div id="side-nav">
          <el-menu
              default-active="1"
              class="el-menu-vertical-demo"
              :collapse="isSideNavCollapsed"
              background-color="#2e3b4e"
              text-color="#fff"
              active-text-color="#ffd04b"
          >
            <!-- 会员管理 -->
            <el-sub-menu index="1">
              <template #title>
                <i class="iconfont">&#xe6b8;</i>
                <span>会员管理</span>
                <i class="iconfont nav_right">&#xe697;</i>
              </template>
              <el-menu-item index="1-1" @click="loadTab('/backend/user/list', '会员列表')">
                <i class="iconfont">&#xe6a7;</i>
                <span>会员列表</span>
              </el-menu-item>
              <el-menu-item index="1-2" @click="loadTab('member-del.html', '会员删除')">
                <i class="iconfont">&#xe6a7;</i>
                <span>会员删除</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 商品管理 -->
            <el-sub-menu index="2">
              <template #title>
                <i class="iconfont">&#xe723;</i>
                <span>商品管理</span>
                <i class="iconfont nav_right">&#xe697;</i>
              </template>
              <el-menu-item index="2-1" @click="loadTab('/backend/goods/list', '商品列表')">
                <i class="iconfont">&#xe6a7;</i>
                <span>商品列表</span>
              </el-menu-item>
              <el-menu-item index="2-2" @click="loadTab('/backend/type/list', '商品分类')">
                <i class="iconfont">&#xe6a7;</i>
                <span>商品分类</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 订单管理 -->
            <el-sub-menu index="3">
              <template #title>
                <i class="iconfont">&#xe723;</i>
                <span>订单管理</span>
                <i class="iconfont nav_right">&#xe697;</i>
              </template>
              <el-menu-item index="3-1" @click="loadTab('/backend/order/list', '订单列表')">
                <i class="iconfont">&#xe6a7;</i>
                <span>订单列表</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 分类管理 -->
            <el-sub-menu index="4">
              <template #title>
                <i class="iconfont">&#xe723;</i>
                <span>分类管理</span>
                <i class="iconfont nav_right">&#xe697;</i>
              </template>
              <el-menu-item index="4-1" @click="loadTab('cate.html', '多级分类')">
                <i class="iconfont">&#xe6a7;</i>
                <span>多级分类</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 城市联动 -->
            <el-sub-menu index="5">
              <template #title>
                <i class="iconfont">&#xe723;</i>
                <span>城市联动</span>
                <i class="iconfont nav_right">&#xe697;</i>
              </template>
              <el-menu-item index="5-1" @click="loadTab('city.html', '三级地区联动')">
                <i class="iconfont">&#xe6a7;</i>
                <span>三级地区联动</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 管理员管理 -->
            <el-sub-menu index="6">
              <template #title>
                <i class="iconfont">&#xe726;</i>
                <span>管理员管理</span>
                <i class="iconfont nav_right">&#xe697;</i>
              </template>
              <el-menu-item index="6-1" @click="loadTab('/backend/admin/list', '管理员列表')">
                <i class="iconfont">&#xe6a7;</i>
                <span>管理员列表</span>
              </el-menu-item>
              <el-menu-item index="6-2" @click="loadTab('admin-role.html', '角色管理')">
                <i class="iconfont">&#xe6a7;</i>
                <span>角色管理</span>
              </el-menu-item>
              <el-menu-item index="6-3" @click="loadTab('admin-cate.html', '权限分类')">
                <i class="iconfont">&#xe6a7;</i>
                <span>权限分类</span>
              </el-menu-item>
              <el-menu-item index="6-4" @click="loadTab('admin-rule.html', '权限管理')">
                <i class="iconfont">&#xe6a7;</i>
                <span>权限管理</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 系统统计 -->
            <el-sub-menu index="7">
              <template #title>
                <i class="iconfont">&#xe6ce;</i>
                <span>系统统计</span>
                <i class="iconfont nav_right">&#xe697;</i>
              </template>
              <el-menu-item index="7-1" @click="loadTab('echarts1.html', '拆线图')">
                <i class="iconfont">&#xe6a7;</i>
                <span>拆线图</span>
              </el-menu-item>
              <el-menu-item index="7-2" @click="loadTab('echarts2.html', '柱状图')">
                <i class="iconfont">&#xe6a7;</i>
                <span>柱状图</span>
              </el-menu-item>
              <el-menu-item index="7-3" @click="loadTab('echarts3.html', '地图')">
                <i class="iconfont">&#xe6a7;</i>
                <span>地图</span>
              </el-menu-item>
              <el-menu-item index="7-4" @click="loadTab('echarts4.html', '饼图')">
                <i class="iconfont">&#xe6a7;</i>
                <span>饼图</span>
              </el-menu-item>
              <el-menu-item index="7-5" @click="loadTab('echarts5.html', '雷达图')">
                <i class="iconfont">&#xe6a7;</i>
                <span>雷达图</span>
              </el-menu-item>
              <el-menu-item index="7-6" @click="loadTab('echarts6.html', 'k线图')">
                <i class="iconfont">&#xe6a7;</i>
                <span>k线图</span>
              </el-menu-item>
              <el-menu-item index="7-7" @click="loadTab('echarts7.html', '热力图')">
                <i class="iconfont">&#xe6a7;</i>
                <span>热力图</span>
              </el-menu-item>
              <el-menu-item index="7-8" @click="loadTab('echarts8.html', '仪表图')">
                <i class="iconfont">&#xe6a7;</i>
                <span>仪表图</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 图标字体 -->
            <el-sub-menu index="8">
              <template #title>
                <i class="iconfont">&#xe6b4;</i>
                <span>图标字体</span>
                <i class="iconfont nav_right">&#xe697;</i>
              </template>
              <el-menu-item index="8-1" @click="loadTab('unicode.html', '图标对应字体')">
                <i class="iconfont">&#xe6a7;</i>
                <span>图标对应字体</span>
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="page-content">
        <el-tabs v-model="activeTab" type="card" closable @tab-remove="handleTabRemove">
          <el-tab-pane label="我的桌面" name="home">
            <div class="welcome-page">
              <h1>欢迎来到天天生鲜后台管理系统</h1>
              <p>请从左侧菜单选择功能进行操作</p>
            </div>
          </el-tab-pane>
          <el-tab-pane v-for="tab in tabs" :key="tab.name" :label="tab.label" :name="tab.name">
            <!-- 🔥 修复 iframe Session 共享问题 -->
            <iframe :src="tab.path" frameborder="0" class="x-iframe"></iframe>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 底部 -->
    <div class="footer">
      <div class="copyright">Copyright ©2017 x-admin v2.3 All Rights Reserved</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const isSideNavCollapsed = ref(false)
const activeTab = ref('home')
const tabs = ref([])
const adminName = ref('admin')

// 切换左侧菜单折叠
const toggleSideNav = () => {
  isSideNavCollapsed.value = !isSideNavCollapsed.value
}

// 加载标签页
const loadTab = (path, label) => {
  const tabName = path.replace(/\//g, '-').substring(1)
  const existTab = tabs.value.find(tab => tab.name === tabName)
  if (existTab) {
    activeTab.value = tabName
    return
  }
  tabs.value.push({ name: tabName, label, path })
  activeTab.value = tabName
}

// 关闭标签页
const handleTabRemove = (targetName) => {
  if (targetName === 'home') {
    ElMessage.warning('首页标签不能关闭！')
    return
  }
  const idx = tabs.value.findIndex(t => t.name === targetName)
  if (activeTab.value === targetName) {
    activeTab.value = idx > 0 ? tabs.value[idx - 1].name : 'home'
  }
  tabs.value.splice(idx, 1)
}

// 退出登录（唯一实现功能）
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('adminInfo')
    router.push('/backend/login')
    ElMessage.success('退出成功')
  })
}

// 个人信息（预留）
const handlePersonalInfo = () => {
  ElMessage.info('功能开发中...')
}

// 切换账号（预留）
const handleSwitchAccount = () => {
  ElMessage.info('功能开发中...')
}

// 弹窗
const xAdminShow = (title, url) => {
  window.open(url, title, 'width=800,height=700')
}

onMounted(() => {
  const adminInfo = JSON.parse(localStorage.getItem('adminInfo') || '{}')
  if (adminInfo.username) {
    adminName.value = adminInfo.username
  }
})
</script>

<style scoped>
/* 全局布局 */
.backend-index {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

/* 顶部导航 */
.container {
  height: 60px;
  background-color: #2e3b4e;
  color: #fff;
  display: flex;
  align-items: center;
  padding: 0 20px;
  position: relative;
  z-index: 100;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  margin-right: 20px;
}

.left_open {
  cursor: pointer;
  font-size: 18px;
  margin-right: 20px;
}

/* 右侧按钮组 */
.right-menu {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 管理员名称 */
.admin-name {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}

/* 顶部按钮样式 */
.top-btn {
  color: #fff;
  text-decoration: none;
  font-size: 14px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 3px;
}

.top-btn:hover {
  background: rgba(255,255,255,0.1);
}

/* 退出按钮高亮 */
.exit-btn {
  color: #ff6b6b;
  font-weight: 500;
}

/* 中部布局 */
.main-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 左侧菜单 */
.left-nav {
  width: 200px;
  background-color: #2e3b4e;
  color: #fff;
  transition: width 0.3s;
}

.left-nav.collapsed {
  width: 64px;
}

.el-menu-vertical-demo {
  border-right: none;
  height: 100%;
}

/* 修复菜单图标乱码 */
:deep(.el-sub-menu__icon-arrow) {
  display: none !important;
}

.nav_right {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

/* 右侧内容区 */
.page-content {
  flex: 1;
  padding: 10px;
  background-color: #f5f3f4;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 标签页高度铺满 */
:deep(.el-tabs) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.el-tabs__content) {
  flex: 1;
  overflow: hidden;
}

:deep(.el-tab-pane) {
  height: 100%;
  overflow: auto;
}

/* 我的桌面居中 */
.welcome-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  text-align: center;
}

.welcome-page h1 {
  font-size: 24px;
  color: #333;
}

.welcome-page p {
  font-size: 14px;
  color: #666;
}

/* iframe */
.x-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* 底部 */
.footer {
  height: 40px;
  line-height: 40px;
  text-align: center;
  background: #f5f3f4;
  color: #666;
  font-size: 12px;
}
</style>

<style>
/* 全局引入iconfont，避免scoped影响 */
.iconfont {
  font-family: "iconfont" !important;
  font-size: 16px !important;
  font-style: normal !important;
  -webkit-font-smoothing: antialiased !important;
  -moz-osx-font-smoothing: grayscale !important;
}
</style>