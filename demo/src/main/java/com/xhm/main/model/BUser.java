package com.xhm.main.model;

import lombok.Data;

/**
 * 管理员实体类
 * 优化点：使用Lombok简化代码，字段名与控制器调用保持一致
 */
@Data
public class BUser {
    private Integer id;
    private String pwd;
    private String userName;
    private Integer status;
    private String phone;
    private Integer superuser;

    // ✅ 手动加上 userName 的 getter/setter，确保 Spring 能识别
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // 其他方法不变
    public String getPassword() {
        return this.pwd;
    }

    public void setPassword(String password) {
        this.pwd = password;
    }

    public String getUsername() {
        return this.userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }
}