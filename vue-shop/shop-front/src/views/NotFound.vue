<template>
  <div style="text-align: center; padding: 50px;">
    <h1>404 - 页面不存在</h1>
    <p>您访问的页面走丢了...</p>
    <button @click="$router.push('/front/index')" style="margin-top: 20px; padding: 10px 20px; background: #ff4400; color: #fff; border: none; cursor: pointer;">
      返回首页
    </button>
  </div>
</template>