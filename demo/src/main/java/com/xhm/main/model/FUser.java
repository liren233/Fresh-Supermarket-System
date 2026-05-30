package com.xhm.main.model;  // 修正为项目实际根包路径

import lombok.Data;

/**
 * 用户实体类（修正包路径，字段类型已最优）
 */
@Data  // 可选：添加lombok注解，自动生成getter/setter，简化代码
public class FUser {
    // 该类字段已使用包装类型，无需额外优化
    private Integer id;
    private Integer status;
    private String userName;
    private String pwd;
    private String phone;

    // 如果你不想使用Lombok，保留原有手动getter/setter（无需修改）：
    /*
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    */
}