package com.xhm.main.model;

import lombok.Data;

@Data
public class FAddress {
    private Integer id;
    private String user_name; // 收货人姓名（对应数据库user_name）
    private String phone;     // 手机号
    private String address;   // 完整地址（省市区+详细地址）
    private Integer is_default; // 是否默认地址（0/1）
    private Integer user_id;  // 用户ID


}

