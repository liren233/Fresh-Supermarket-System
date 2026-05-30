package com.xhm.main.model;  // 修正为项目实际根包路径

import lombok.Data;

/**
 * 订单详情实体类（修正包路径+优化字段类型）
 */
@Data  // 可选：添加lombok注解，自动生成getter/setter，简化代码
public class FOrderDetail {
    // 核心优化：基本类型int改为包装类型Integer，支持null判断（适配前后端交互）
    private Integer id;
    private String good_name;
    private Double price;
    private Integer num;
    private String img_url;
    // private String unit; // 暂时注释掉，数据库表中没有这个字段
    // private String intro; // 暂时注释掉，数据库表中没有这个字段
    private Double total_price;
    private Integer order_id;

    // 如果你不想使用Lombok，保留手动getter/setter（仅修改类型，兼容原有代码）：
    /*
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGood_name() {
        return good_name;
    }

    public void setGood_name(String good_name) {
        this.good_name = good_name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Double total_price) {
        this.total_price = total_price;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }
    */
}