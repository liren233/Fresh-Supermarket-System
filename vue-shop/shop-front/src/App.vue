<template>
  <!-- 只保留路由出口，完全由路由控制页面渲染 -->
  <router-view />
</template>

<style lang="scss">
/* 全局基础样式重置，避免默认样式影响页面布局 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: "Microsoft Yahei", Arial, Helvetica, sans-serif;
  color: #333;
  background-color: #fff;
}

a {
  text-decoration: none;
  color: #333;
}

ul, li {
  list-style: none;
}

/* 清除浮动通用类（适配首页布局） */
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
<script setup>
// 屏蔽 ResizeObserver 循环警告
const debounce = (fn, delay) => {
  let timer
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

const resizeObserverLoopErr = 'ResizeObserver loop completed with undelivered notifications.'
window.addEventListener('error', (e) => {
  if (e.message.includes(resizeObserverLoopErr)) {
    e.preventDefault()
    e.stopImmediatePropagation()
  }
})
</script>