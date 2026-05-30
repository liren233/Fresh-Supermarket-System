package com.xhm.main.dao;  // 修正为项目实际根包路径

import com.xhm.main.model.FOrderDetail;
import com.xhm.main.model.Page;

import java.util.List;

/**
 * 订单详情DAO接口（修正包路径后）
 */
public interface FOderDetailDao {
    // 根据ID查询订单详情
    FOrderDetail getById(Integer id);

    // 添加订单详情
    Integer insert(FOrderDetail bGood);

    // 分页查询订单详情列表（后台用）
    List<FOrderDetail> list(Page page);

    // 根据订单ID查询详情列表
    List<FOrderDetail> getByOrderId(Integer order_id);

    // 获取订单详情总数（分页用）
    int total(Page page);

    // 删除订单详情
    int delete(int id);

    // 根据订单ID删除订单详情
    int deleteByOrderId(Integer order_id);

}