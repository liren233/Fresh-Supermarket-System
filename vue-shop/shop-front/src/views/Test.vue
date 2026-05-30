<template>
  <div class="test-page">
    <h1>{{ message }}</h1>
    <h1>你好</h1>
  </div>
</template>

<script>
export default {
  data() {
    return {
      message: '这是从Vue组件data中传递的消息'
    }
  }
  // 如果需要从后端获取，可在mounted中调用接口
  // async mounted() {
  //   const res = await request.get('/test/message');
  //   this.message = res.data;
  // }
}
</script>