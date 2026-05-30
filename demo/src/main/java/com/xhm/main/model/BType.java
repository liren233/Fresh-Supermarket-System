package com.xhm.main.model;  // 修正为项目实际根包路径

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类实体类（修正包路径+优化字段类型）
 */
@Data  // 可选：添加lombok注解，自动生成getter/setter，简化代码
public class BType {
    // 核心优化：基本类型int改为包装类型Integer，支持null判断（适配前后端交互）
    private Integer id;
    private String type_name;
    private Integer fid;

    // 如果你不想使用Lombok，保留手动getter/setter（兼容原有代码）：
    /*
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }
    */
    private List<BType> children = new ArrayList<>();

    public List<BType> getChildren() { return children; }
    public void setChildren(List<BType> children) { this.children = children; }
}