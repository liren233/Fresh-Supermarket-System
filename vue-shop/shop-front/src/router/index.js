// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // ======================================
  // 前台路由 (front)
  // ======================================
  {
    path: '/',
    redirect: '/front/index'
  },
  {
    path: '/front/index',
    name: 'Index',
    component: () => import('@/views/front/Index.vue'),
    meta: { title: '天天生鲜-首页' }
  },
  {
    path: '/front/login',
    name: 'Login',
    component: () => import('@/views/front/Login.vue'),
    meta: { title: '天天生鲜-登录' }
  },
  {
    path: '/front/register',
    name: 'Register',
    component: () => import('@/views/front/Register.vue'),
    meta: { title: '天天生鲜-注册' }
  },
  {
    path: '/front/cart',
    name: 'Cart',
    component: () => import('@/views/front/Cart.vue'),
    meta: {
      title: '天天生鲜-我的购物车',
      requiresAuth: true
    }
  },
  {
    path: '/front/goodsDetail',
    name: 'GoodsDetail',
    component: () => import('@/views/front/GoodsDetail.vue'),
    meta: { title: '天天生鲜-商品详情' }
  },
  {
    path: '/front/placeOrder',
    name: 'PlaceOrder',
    component: () => import('@/views/front/PlaceOrder.vue'),
    meta: {
      title: '天天生鲜-确认订单',
      requiresAuth: true
    }
  },
  {
    path: '/front/pay',
    name: 'Pay',
    component: () => import('@/views/front/Pay.vue'),
    meta: {
      title: '天天生鲜-支付',
      requiresAuth: true
    }
  },
  {
    path: '/front/paySuccess',
    name: 'PaySuccess',
    component: () => import('@/views/front/PaySuccess.vue'),
    meta: {
      title: '天天生鲜-支付成功',
      requiresAuth: true
    }
  },
  {
    path: '/front/userCenter',
    name: 'UserCenter',
    component: () => import('@/views/front/UserCenter.vue'),
    meta: {
      title: '天天生鲜-用户中心',
      requiresAuth: true
    }
  },
  {
    path: '/front/userCenterOrder',
    name: 'UserCenterOrder',
    component: () => import('@/views/front/UserCenterOrder.vue'),
    meta: {
      title: '天天生鲜-全部订单',
      requiresAuth: true
    }
  },
  {
    path: '/front/userCenterSite',
    name: 'UserCenterSite',
    component: () => import('@/views/front/UserCenterSite.vue'),
    meta: {
      title: '天天生鲜-收货地址',
      requiresAuth: true
    }
  },

  // ======================================
  // 后台路由 (backend)
  // ======================================
  {
    path: '/backend/login',
    name: 'BackendLogin',
    component: () => import('@/views/backend/Login.vue'),
    meta: { title: '后台登录' }
  },
  {
    path: '/backend/index',
    name: 'BackendIndex',
    component: () => import('@/views/backend/BackendIndex.vue'),
    meta: {
      title: '后台首页',
      requiresAdminAuth: true
    }
  },
  // 管理员管理（仅保留已存在的文件）
  {
    path: '/backend/admin/list',
    name: 'AdminList',
    component: () => import('@/views/backend/AdminList.vue'),
    meta: {
      title: '管理员列表',
      requiresAdminAuth: true
    }
  },
  {
    path: '/backend/admin/add',
    name: 'AdminAdd',
    component: () => import('@/views/backend/AdminAdd.vue'),
    meta: {
      title: '新增管理员',
      requiresAdminAuth: true
    }
  },
  {
    path: '/backend/admin/edit',
    name: 'AdminEdit',
    component: () => import('@/views/backend/AdminEdit.vue'),
    meta: {
      title: '编辑管理员',
      requiresAdminAuth: true
    }
  },
  // 👇 以下组件不存在，暂时注释
  // {
  //   path: '/backend/admin/role',
  //   name: 'AdminRole',
  //   component: () => import('@/views/backend/AdminRole.vue'),
  //   meta: {
  //     title: '角色管理',
  //     requiresAdminAuth: true
  //   }
  // },
  // {
  //   path: '/backend/admin/cate',
  //   name: 'AdminCate',
  //   component: () => import('@/views/backend/AdminCate.vue'),
  //   meta: {
  //     title: '权限分类',
  //     requiresAdminAuth: true
  //   }
  // },
  // {
  //   path: '/backend/admin/rule',
  //   name: 'AdminRule',
  //   component: () => import('@/views/backend/AdminRule.vue'),
  //   meta: {
  //     title: '权限管理',
  //     requiresAdminAuth: true
  //   }
  // },
  // 用户管理（仅保留已存在的文件）
  {
    path: '/backend/user/list',
    name: 'UserList',
    component: () => import('@/views/backend/UserList.vue'),
    meta: {
      title: '用户列表',
      requiresAdminAuth: true
    }
  },
  {
    path: '/backend/user/edit',
    name: 'UserEdit',
    component: () => import('@/views/backend/UserEdit.vue'),
    meta: {
      title: '编辑用户',
      requiresAdminAuth: true
    }
  },
  // 👇 组件不存在，暂时注释
  // {
  //   path: '/backend/member/del',
  //   name: 'MemberDel',
  //   component: () => import('@/views/backend/MemberDel.vue'),
  //   meta: {
  //     title: '会员删除',
  //     requiresAdminAuth: true
  //   }
  // },
  // 商品分类管理（仅保留已存在的文件）
  {
    path: '/backend/type/list',
    name: 'TypeList',
    component: () => import('@/views/backend/TypeList.vue'),
    meta: {
      title: '商品分类列表',
      requiresAdminAuth: true
    }
  },
  {
    path: '/backend/type/add',
    name: 'TypeAdd',
    component: () => import('@/views/backend/TypeAdd.vue'),
    meta: {
      title: '新增商品分类',
      requiresAdminAuth: true
    }
  },
  {
    path: '/backend/type/edit',
    name: 'TypeEdit',
    component: () => import('@/views/backend/TypeEdit.vue'),
    meta: {
      title: '编辑商品分类',
      requiresAdminAuth: true
    }
  },
  // 商品管理（仅保留已存在的文件）
  {
    path: '/backend/goods/list',
    name: 'GoodsList',
    component: () => import('@/views/backend/GoodsList.vue'),
    meta: {
      title: '商品列表',
      requiresAdminAuth: true
    }
  },
  {
    path: '/backend/goods/add',
    name: 'GoodsAdd',
    component: () => import('@/views/backend/GoodsAdd.vue'),
    meta: {
      title: '新增商品',
      requiresAdminAuth: true
    }
  },
  {
    path: '/backend/goods/edit',
    name: 'GoodsEdit',
    component: () => import('@/views/backend/GoodsEdit.vue'),
    meta: {
      title: '编辑商品',
      requiresAdminAuth: true
    }
  },
  // 订单管理
  {
    path: '/backend/order/list',
    name: 'OrderList',
    component: () => import('@/views/backend/OrderList.vue'),
    meta: {
      title: '订单列表',
      requiresAdminAuth: true
    }
  },
  // 分类管理
  // {
  //   path: '/backend/cate',
  //   name: 'Cate',
  //   component: () => import('@/views/backend/Cate.vue'),
  //   meta: {
  //     title: '多级分类',
  //     requiresAdminAuth: true
  //   }
  // },
  // 城市联动
  // {
  //   path: '/backend/city',
  //   name: 'City',
  //   component: () => import('@/views/backend/City.vue'),
  //   meta: {
  //     title: '三级地区联动',
  //     requiresAdminAuth: true
  //   }
  // },
  // 系统统计
  // {
  //   path: '/backend/echarts1',
  //   name: 'Echarts1',
  //   component: () => import('@/views/backend/Echarts1.vue'),
  //   meta: {
  //     title: '拆线图',
  //     requiresAdminAuth: true
  //   }
  // },
  // {
  //   path: '/backend/echarts2',
  //   name: 'Echarts2',
  //   component: () => import('@/views/backend/Echarts2.vue'),
  //   meta: {
  //     title: '柱状图',
  //     requiresAdminAuth: true
  //   }
  // },
  // {
  //   path: '/backend/echarts3',
  //   name: 'Echarts3',
  //   component: () => import('@/views/backend/Echarts3.vue'),
  //   meta: {
  //     title: '地图',
  //     requiresAdminAuth: true
  //   }
  // },
  // {
  //   path: '/backend/echarts4',
  //   name: 'Echarts4',
  //   component: () => import('@/views/backend/Echarts4.vue'),
  //   meta: {
  //     title: '饼图',
  //     requiresAdminAuth: true
  //   }
  // },
  // {
  //   path: '/backend/echarts5',
  //   name: 'Echarts5',
  //   component: () => import('@/views/backend/Echarts5.vue'),
  //   meta: {
  //     title: '雷达图',
  //     requiresAdminAuth: true
  //   }
  // },
  // {
  //   path: '/backend/echarts6',
  //   name: 'Echarts6',
  //   component: () => import('@/views/backend/Echarts6.vue'),
  //   meta: {
  //     title: 'k线图',
  //     requiresAdminAuth: true
  //   }
  // },
  // {
  //   path: '/backend/echarts7',
  //   name: 'Echarts7',
  //   component: () => import('@/views/backend/Echarts7.vue'),
  //   meta: {
  //     title: '热力图',
  //     requiresAdminAuth: true
  //   }
  // },
  // {
  //   path: '/backend/echarts8',
  //   name: 'Echarts8',
  //   component: () => import('@/views/backend/Echarts8.vue'),
  //   meta: {
  //     title: '仪表图',
  //     requiresAdminAuth: true
  //   }
  // },
  // 图标字体
  // {
  //   path: '/backend/unicode',
  //   name: 'Unicode',
  //   component: () => import('@/views/backend/Unicode.vue'),
  //   meta: {
  //     title: '图标对应字体',
  //     requiresAdminAuth: true
  //   }
  // },

  // ======================================
  // 404 页面
  // ======================================
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  // 前台用户登录校验
  if (to.meta.requiresAuth) {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
    if (!userInfo) {
      next({
        path: '/front/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  }
  // 后台管理员登录校验
  else if (to.meta.requiresAdminAuth) {
    const adminInfo = JSON.parse(localStorage.getItem('adminInfo') || 'null')
    if (!adminInfo) {
      next({
        path: '/backend/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  }
  // 其他页面直接放行
  else {
    next()
  }
})

export default router