package com.xhm.main.dao;  // 修正为项目实际根包路径

import com.xhm.main.model.FCart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车DAO接口（修正包路径后）
 */
public interface FCartDao {
    // 根据ID查询购物车商品
    FCart getCartById(int id);

    // 根据用户ID查询购物车列表
    List<FCart> getCartByUserId(int id);

    // 获取所有购物车数据（后台用）
    List<FCart> list();

    // 添加商品到购物车
    int insertCart(FCart fCart);

    // 更新购物车商品数量
    int updateCart(FCart fCart);

    // 删除购物车商品
    int deleteCart(int id);

    // 清空购物车
    void cleanCart();

    // 检查商品是否已在购物车（用于数量累加）
    FCart getGood(FCart fCart);

    void insertCartGoods(@Param("cartId") Integer cartId, @Param("goodsId") Integer goodsId);


}