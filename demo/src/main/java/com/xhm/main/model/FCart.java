package com.xhm.main.model;

import lombok.Data;

@Data
public class FCart {
    private Integer id;
    private Integer user_id;
    // 临时加回 goods_id，兼容 addCart 接口
    private Integer goods_id;
    private Integer num;

    private FUser fUser;
    private BGood bGood;
    private BType bType;

    // ========== 规范 getter/setter ==========
    public FUser getFUser() {
        return fUser;
    }

    public void setFUser(FUser fUser) {
        this.fUser = fUser;
    }

    public BGood getBGood() {
        return bGood;
    }

    public void setBGood(BGood bGood) {
        this.bGood = bGood;
    }

    public BType getBType() {
        return bType;
    }

    public void setBType(BType bType) {
        this.bType = bType;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    // 给 goods_id 加 getter/setter
    public Integer getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
    }
}