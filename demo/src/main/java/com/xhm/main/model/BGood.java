package com.xhm.main.model;

import lombok.Data;

@Data
public class BGood {
    private Integer id;
    private String good_name;
    private String img_url;
    private double price;
    private String unit;
    private Integer type_id;
    private String intro;

    // 关联分类（保持和你原有代码一致）
    private BType bType;

    // 以下是兼容 MyBatis 映射的规范 getter（必须保留）
    public BType getBType() {
        return bType;
    }

    public void setBType(BType bType) {
        this.bType = bType;
    }
}