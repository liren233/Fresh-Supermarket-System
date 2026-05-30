package com.xhm.main.dao;

import com.xhm.main.model.FUser;
import com.xhm.main.model.Page;

import java.util.List;

/**
 * 用户DAO接口（修正包路径后）
 */
public interface UserDao {
    // 根据ID查询用户
    FUser getUserById(Integer id);

    // 用户登录验证
    FUser userLogin(FUser fUser);

    // 新增用户（注册/添加管理员）
    Integer insertUser(FUser fUser);

    // 获取所有用户列表（旧方法，兼容）
    List<FUser> userList();

    // 分页查询用户列表（后台用）
    List<FUser> list(Page page);

    // 获取用户总数（分页用）
    int total(Page page);

    // 修改用户信息
    int updateUser(FUser fUser);

    // 删除用户
    int deleteUser(int id);

    // 启用/禁用用户
    int updateUserStatus(FUser fUser);

    // ========== 新增：根据用户名查询用户（核心） ==========
    FUser findUserByUserName(String userName);

}