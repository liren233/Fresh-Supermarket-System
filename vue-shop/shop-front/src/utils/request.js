// src/utils/request.js - 直接覆盖原文件
import axios from 'axios'

const request = axios.create({
    baseURL: '/api',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    },
    withCredentials: true
})

// 请求拦截器：自动携带 token
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// 响应拦截器：统一处理错误
request.interceptors.response.use(
    res => res.data,
    err => {
        console.error('请求失败：', err)
        return Promise.reject(err)
    }
)

export default request