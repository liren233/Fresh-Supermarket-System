// main.js / main.ts
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Element Plus 相关引入
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 创建应用实例
const app = createApp(App)

// 优化图标注册逻辑（增加注释，提升可读性）
// 批量注册 Element Plus 所有图标组件
Object.entries(ElementPlusIconsVue).forEach(([iconName, iconComponent]) => {
    app.component(iconName, iconComponent)
})

// 按规范顺序挂载插件（路由 -> UI 库）
app.use(router)
app.use(ElementPlus, {
    // 可选：全局配置 Element Plus（比如尺寸、zIndex 等）
    size: 'default', // 全局组件尺寸：large / default / small
    zIndex: 3000 // 全局弹层的 z-index 起始值
})

// 挂载到 DOM
app.mount('#app')