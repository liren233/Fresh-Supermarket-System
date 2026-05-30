package com.xhm.main.dao;

import com.xhm.main.model.Page;
import com.xhm.main.model.BUser;

import java.util.List;

public interface adminDao {
    BUser adminLogin(BUser bUser);
    BUser getUserById(Integer id);
    List<BUser> userList();

    List<BUser> list(Page page);//管理员列表（实现分页、搜索）
    int total(Page page);

    int insertUser(BUser bUser);//添加管理员列表
    int updateUser(BUser bUser);//修改管理员
    int deleteUser(int id);//删除管理

    int updateStatus(BUser bUser);//启用禁用管理员
}
