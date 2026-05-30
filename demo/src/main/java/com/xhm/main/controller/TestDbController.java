package com.xhm.main.controller;

import com.xhm.main.model.FUser;
import com.xhm.main.dao.UserDao;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestDbController {

    @Resource
    private UserDao userDao;

    // 测试查询 f_user 表
    @GetMapping("/test/db")
    public String testDbConnection() {
        try {
            // 关键：用你 UserDao 里实际存在的方法！
            List<FUser> userList = userDao.userList();
            return "数据库连接成功！查询到用户数量：" + userList.size();
        } catch (Exception e) {
            return "数据库连接失败：" + e.getMessage();
        }
    }
}