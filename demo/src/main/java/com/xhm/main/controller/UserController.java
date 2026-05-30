package com.xhm.main.controller;

import com.xhm.main.dao.UserDao;
import com.xhm.main.model.FUser;
import com.xhm.main.model.Page; // 新增导入
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserDao userDao;

    // ========== 统一返回结果封装类 ==========
    @lombok.Data
    public static class Result<T> {
        private int status;
        private String msg;
        private T data;

        public static <T> Result<T> success(T data, String msg) {
            Result<T> result = new Result<>();
            result.setStatus(1);
            result.setMsg(msg);
            result.setData(data);
            return result;
        }

        public static <T> Result<T> success(String msg) {
            return success(null, msg);
        }

        public static <T> Result<T> error(int status, String msg) {
            Result<T> result = new Result<>();
            result.setStatus(status);
            result.setMsg(msg);
            result.setData(null);
            return result;
        }

        public static <T> Result<T> error(String msg) {
            return error(0, msg);
        }
    }

    // ========== 新增：用户名唯一性校验接口（适配你的 UserDao） ==========
    @GetMapping("/checkUserName")
    public Result<Boolean> checkUserName(@RequestParam String userName) {
        try {
            // 调用新增的 UserDao 方法查询用户名
            FUser existUser = userDao.findUserByUserName(userName);
            if (existUser != null) {
                // 用户名已存在
                return Result.success(false, "用户名已被占用");
            } else {
                // 用户名可用
                return Result.success(true, "用户名可用");
            }
        } catch (Exception e) {
            return Result.error("校验用户名失败：" + e.getMessage());
        }
    }

    // ========== 登录接口（原有代码完全保留） ==========
    @PostMapping("/doLogin")
    public Result<Map<String, Object>> doLogin(
            @RequestBody FUser fUser,
            HttpServletRequest request) {
        try {
            // 1. 校验入参
            if (fUser.getUserName() == null || fUser.getPwd() == null) {
                return Result.error("用户名和密码不能为空");
            }

            // 2. 执行登录查询
            FUser fUser1 = userDao.userLogin(fUser);
            if (fUser1 == null) {
                return Result.error("账号或密码错误，请重试！");
            }

            // 3. 校验用户状态（status=0 禁用，status=1 正常）
            if (fUser1.getStatus() == 0) {
                return Result.error("你的账号已被禁用，请联系管理员");
            }

            // 4. 登录成功，保存Session
            HttpSession session = request.getSession();
            session.setAttribute("USER_SESSION", fUser1);
            session.setMaxInactiveInterval(3600);

            // 5. 封装返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("user", fUser1);
            data.put("token", session.getId());

            return Result.success(data, "登录成功");
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    // ========== 退出登录（原有代码完全保留） ==========
    @GetMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            session.removeAttribute("USER_SESSION");
            session.invalidate();
            return Result.success("退出登录成功");
        } catch (Exception e) {
            return Result.error("退出登录失败：" + e.getMessage());
        }
    }

    // ========== 注册接口（已适配手机号 · 最终版） ==========
    @PostMapping("/doRegister")
    public Result<Void> doRegister(@RequestBody FUser fUser) {
        try {
            if (fUser.getUserName() == null || fUser.getPwd() == null) {
                return Result.error("用户名和密码不能为空");
            }

            // 前端传过来的 phone 会自动映射到 fUser.phone
            // 给新用户默认启用 status=1
            fUser.setStatus(1);

            int row = userDao.insertUser(fUser);
            if (row > 0) {
                return Result.success("注册成功，请登录！");
            } else {
                return Result.error(2, "注册失败，用户名已存在");
            }
        } catch (Exception ex) {
            return Result.error(0, "注册失败：" + ex.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<FUser> update(@RequestBody FUser user, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser loginUser = (FUser) session.getAttribute("USER_SESSION");
            if (loginUser == null) {
                return Result.error("未登录");
            }

            user.setId(loginUser.getId());
            userDao.updateUser(user); // ✅ 这里改成 updateUser

            FUser updatedUser = userDao.getUserById(loginUser.getId());
            session.setAttribute("USER_SESSION", updatedUser);

            return Result.success(updatedUser, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改失败");
        }
    }

    @GetMapping("/user/info")
    public Result<FUser> getUserInfo(HttpServletRequest request) {
        // 关键：先拿到 session 对象
        HttpSession session = request.getSession();
        FUser loginUser = (FUser) session.getAttribute("USER_SESSION");

        if (loginUser == null) {
            return Result.error("未登录");
        }

        return Result.success(loginUser, "查询成功");
    }

    @GetMapping("/userList")
    public Result<Map<String, Object>> userList(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        try {
            // 1. 计算 start，用 int 类型接收
            int start = (pageNum - 1) * pageSize;

            // 2. 创建 Page 对象，完全匹配你的类
            Page page = new Page();
            page.setStart(start);
            // 拆箱为 int，彻底消除类型不匹配警告
            page.setCount(pageSize.intValue());
            page.setSearch(search);

            // 3. 调用你 DAO 里的方法
            List<FUser> list = userDao.list(page);
            int total = userDao.total(page);

            // 4. 封装返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);

            // ✅ 关键修正：Result.success 的参数顺序
            return Result.success(data, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    // ================== 【新增】启用/禁用用户接口 ==================
    @PostMapping("/updateUserStatus")
    public Result<FUser> updateUserStatus(@RequestBody FUser user) {
        try {
            // 直接调用你的Mapper修改状态
            userDao.updateUserStatus(user);
            return Result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    // ================== 【新增】删除用户接口 ==================
    @PostMapping("/delUser")
    public Result<FUser> delUser(@RequestBody FUser user) {
        try {
            userDao.deleteUser(user.getId());
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败");
        }
    }

    // ================== 【新增】批量删除用户接口 ==================
    @PostMapping("/delAllUser")
    public Result<FUser> delAllUser(@RequestBody Map<String, String> params) {
        try {
            String ids = params.get("ids");
            String[] idArr = ids.split(",");
            for (String id : idArr) {
                userDao.deleteUser(Integer.parseInt(id));
            }
            return Result.success("批量删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量删除失败");
        }
    }
    // ================== 【新增】后台添加用户接口 ==================
    @PostMapping("/addUser")
    public Result<Void> addUser(@RequestBody FUser fUser) {
        try {
            // 1. 校验必填字段
            if (fUser.getUserName() == null || fUser.getPwd() == null) {
                return Result.error("用户名和密码不能为空");
            }

            // 2. 校验用户名是否已存在
            FUser existUser = userDao.findUserByUserName(fUser.getUserName());
            if (existUser != null) {
                return Result.error("用户名已被占用");
            }

            // 3. 给新用户默认启用状态
            fUser.setStatus(1);

            // 4. 调用Mapper插入用户
            int row = userDao.insertUser(fUser);
            if (row > 0) {
                return Result.success("添加用户成功");
            } else {
                return Result.error("添加用户失败，请重试");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.error("添加用户失败：" + ex.getMessage());
        }
    }
    @GetMapping("/getUserById/{id}")
    public Result<FUser> getUserById(@PathVariable Integer id) {
        FUser user = userDao.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user, "成功");
    }
    @PostMapping("/updateForAdmin")
    public Result<FUser> updateForAdmin(@RequestBody FUser user) {
        userDao.updateUser(user);
        return Result.success("修改成功");
    }
}