<template>
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
</template>

<script setup>
    // 底部组件无业务逻辑，仅展示静态内容
</script>

<style scoped>
    /* 仅保留组件专属布局样式，公共样式依赖全局引入的 reset.css/main.css */
    .footer {
        width: 1200px;
        margin: 0 auto;
        margin-top: 50px;
        padding-top: 20px;
        border-top: 1px solid #eee;
        text-align: center;
    }

    .foot_link {
        margin-bottom: 10px;
    }

    .foot_link a {
        color: #666;
        text-decoration: none;
        margin: 0 10px;
    }

    .foot_link a:hover {
        color: #ff4400;
    }

    .foot_link span {
        color: #999;
    }

    .footer p {
        color: #666;
        line-height: 24px;
        margin: 5px 0;
    }
</style>