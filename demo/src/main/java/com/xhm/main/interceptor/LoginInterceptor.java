package com.xhm.main.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        System.out.println("LoginInterceptor: Processing URL: " + url);
        response.setContentType("application/json;charset=UTF-8");

        // ==============================================
        // 1. 【完全放行】前端商城页面、静态资源、登录注册接口
        // ==============================================
        if (isFrontendOrPublicUrl(url)) {
            return true;
        }

        // ==============================================
        // 2. 【API接口处理】
        // ==============================================
        if (url.startsWith("/api/")) {
            // 检查是否是用户相关接口（需要用户登录）
            if (isUserApi(url)) {
                HttpSession session = request.getSession();
                Object user = session.getAttribute("USER_SESSION");
                if (user == null) {
                    // 尝试从请求头中获取token
                    String token = request.getHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        // 这里可以根据token查找会话，但是在Spring Boot中，会话是基于Cookie的，所以这里暂时跳过
                    }
                    response.getWriter().write("{\"status\":0,\"msg\":\"用户未登录，请先登录！\"}");
                    return false;
                }
                return true;
            }
            // 检查是否是管理员相关接口（需要管理员登录）
            else if (isAdminApi(url)) {
                HttpSession session = request.getSession();
                Object admin = session.getAttribute("ADMIN_SESSION");
                if (admin == null) {
                    response.getWriter().write("{\"status\":0,\"msg\":\"管理员未登录，请先登录！\"}");
                    return false;
                }
                return true;
            }
            // 其他API接口默认放行
            return true;
        }
        
        // ==============================================
        // 3. 【非API接口处理】
        // ==============================================
        // 处理 /order/addOrder 和 /order/pay 这样的请求
        if (url.startsWith("/order/")) {
            System.out.println("LoginInterceptor: Processing order-related URL: " + url);
            HttpSession session = request.getSession();
            Object user = session.getAttribute("USER_SESSION");
            if (user == null) {
                System.out.println("LoginInterceptor: User not logged in for order-related URL: " + url);
                response.getWriter().write("{\"status\":0,\"msg\":\"用户未登录，请先登录！\"}");
                return false;
            }
            System.out.println("LoginInterceptor: User logged in for order-related URL: " + url);
            return true;
        }
        
        // 处理 /addOrder 和 /pay 这样的请求
        if (url.equals("/addOrder") || url.equals("/pay") || url.equals("/api/addOrder") || url.equals("/api/pay")) {
            System.out.println("LoginInterceptor: Processing addOrder/pay URL: " + url);
            HttpSession session = request.getSession();
            Object user = session.getAttribute("USER_SESSION");
            if (user == null) {
                System.out.println("LoginInterceptor: User not logged in for addOrder/pay URL: " + url);
                response.getWriter().write("{\"status\":0,\"msg\":\"用户未登录，请先登录！\"}");
                return false;
            }
            System.out.println("LoginInterceptor: User logged in for addOrder/pay URL: " + url);
            return true;
        }
        
        // 处理其他可能的订单相关接口
        if (url.contains("addOrder") || url.contains("pay")) {
            System.out.println("LoginInterceptor: Processing addOrder/pay-containing URL: " + url);
            HttpSession session = request.getSession();
            Object user = session.getAttribute("USER_SESSION");
            if (user == null) {
                System.out.println("LoginInterceptor: User not logged in for addOrder/pay-containing URL: " + url);
                response.getWriter().write("{\"status\":0,\"msg\":\"用户未登录，请先登录！\"}");
                return false;
            }
            System.out.println("LoginInterceptor: User logged in for addOrder/pay-containing URL: " + url);
            return true;
        }

        // ==============================================
        // 3. 【必须登录】后台管理页面 + 所有管理接口
        // ==============================================
        HttpSession session = request.getSession();
        Object admin = session.getAttribute("ADMIN_SESSION");

        // 未登录 → 返回错误提示
        if (admin == null) {
            response.getWriter().write("{\"status\":0,\"msg\":\"管理员未登录，请先登录！\"}");
            return false;
        }

        // 已登录 → 放行
        return true;
    }

    /**
     * 判断：前端页面 / 公共资源 / 登录接口 → 全部放行
     */
    private boolean isFrontendOrPublicUrl(String url) {
        return
                // 登录、退出接口
                url.contains("/api/user/doLogin")
                        || url.contains("/api/user/doRegister")
                        || url.contains("/api/user/logout")
                        || url.contains("/doAdminLogin")

                        // 前端商城页面（完全不拦截）
                        || url.startsWith("/index")
                        || url.startsWith("/goods")
                        || url.startsWith("/cart")
                        || url.startsWith("/user/center")
                        || url.startsWith("/user/login")
                        || url.startsWith("/user/register")

                        // 静态资源
                        || url.startsWith("/static/")
                        || url.endsWith(".html")
                        || url.endsWith(".js")
                        || url.endsWith(".css")
                        || url.endsWith(".woff")
                        || url.endsWith(".ttf")
                        || url.endsWith(".jpg")
                        || url.endsWith(".png");
    }

    /**
     * 判断：用户相关API接口
     */
    private boolean isUserApi(String url) {
        // 移除查询参数
        String cleanUrl = url.split("\\?")[0];
        return (cleanUrl.startsWith("/api/order/") && !cleanUrl.startsWith("/api/order/orderList"))
                || cleanUrl.startsWith("/api/address/")
                || cleanUrl.startsWith("/api/cart/")
                || cleanUrl.startsWith("/api/user/user/info");
    }

    /**
     * 判断：管理员相关API接口
     */
    private boolean isAdminApi(String url) {
        // 移除查询参数
        String cleanUrl = url.split("\\?")[0];
        return cleanUrl.startsWith("/api/admin/")
                || cleanUrl.startsWith("/api/user/userList")
                || cleanUrl.startsWith("/api/user/addUser")
                || cleanUrl.startsWith("/api/user/updateForAdmin")
                || cleanUrl.startsWith("/api/user/delUser")
                || cleanUrl.startsWith("/api/user/delAllUser")
                || cleanUrl.startsWith("/api/user/updateUserStatus")
                || cleanUrl.startsWith("/api/order/orderList");
    }
}