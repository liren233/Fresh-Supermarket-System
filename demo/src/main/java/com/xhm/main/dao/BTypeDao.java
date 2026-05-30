package com.xhm.main.dao;  // 修正为项目实际根包路径

import com.xhm.main.model.BType;  // 修正Model类的导入路径
import com.xhm.main.model.Page;   // 修正Page类的导入路径

import java.util.List;

/**
 * 商品分类DAO接口（修正包路径后）
 */
public interface BTypeDao {
    // 根据ID查询分类
    BType getTypeById(Integer id);

    // 获取所有分类列表
    List<BType> typeList();

    // 获取分类总数（分页用）
    int total(Page page);

    // 删除分类
    int deleteType(int id);

    // 修改分类
    int updateType(BType bType);

    // 添加分类
    Integer insertType(BType bType);

    // 根据父ID查询分类（层级分类用）
    List<BType> getTypeByFid(int i);

    List<BType> typeList(Page page);
}