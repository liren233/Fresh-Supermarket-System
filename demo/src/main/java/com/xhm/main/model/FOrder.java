package com.xhm.main.model;

import lombok.Data;
import java.util.Date;

@Data
public class FOrder {
    private Integer id;
    private String order_no;
    private Integer status;
    private Date time;
    private String user_name;
    private String phone;
    private String address;
    private Integer total_price;
    // private Integer pay_style; // 暂时注释掉，数据库表中没有这个字段
    private Integer user_id;
    private Integer pay_status; // 取消注释，数据库表中有这个字段

    // 暂时注释掉 fUser 字段，避免映射问题
    // private FUser fUser;
}