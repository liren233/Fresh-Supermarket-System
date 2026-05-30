// vue.config.js
const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        port: 8081,
        host: 'localhost',
        open: true,
        hot: true,
        proxy: {
            '/api': {
                target: 'http://localhost:8080', // 后端地址
                changeOrigin: true,
                pathRewrite: { '^/api': '/api' } // 保留 /api 前缀（后端接口有 /api）
            }
        }
    },
    productionSourceMap: false,
    outputDir: 'dist',
    assetsDir: 'static',



})