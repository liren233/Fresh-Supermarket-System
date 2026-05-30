package com.xhm.main.dao;  // 修正为项目实际根包路径

import com.xhm.main.model.FOrder;
import com.xhm.main.model.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单DAO接口（修正包路径后）
 */
public interface FOrderDao {
    // 根据ID查询订单
    FOrder getOrderById(Integer id);

    // 添加订单
    Integer insertOrder(FOrder bGood);

    // 分页查询订单列表（后台用）
    List<FOrder> listPage(Page page);

    // 根据用户ID查询订单列表
    List<FOrder> getOrderByUserId(Integer userId);

    // 获取订单总数（分页用）
    int total(Page page);

    // 删除订单
    int deleteOrder(int id);

    // 新增这两个方法
    List<FOrder> list(@Param("userId") Integer userId, @Param("start") int start, @Param("size") int size);
    int count(@Param("userId") Integer userId);
    
    // 更新订单
    int updateOrder(FOrder order);

}