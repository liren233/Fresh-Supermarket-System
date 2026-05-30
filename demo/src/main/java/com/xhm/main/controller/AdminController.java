package com.xhm.main.controller;

import com.xhm.main.dao.adminDao;
import com.xhm.main.model.BUser;
import com.xhm.main.model.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器（适配Spring Boot + Vue前后端分离）
 * 所有接口统一返回JSON，移除视图跳转，添加/api/admin前缀
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private adminDao adminDao;

    // ========== 统一返回结果封装类 ==========
    @lombok.Data
    public static class Result<T> {
        private int status;  // 1成功/0失败/2业务异常
        private String msg;  // 提示信息
        private T data;      // 业务数据

        // 成功返回（带数据）
        public static <T> Result<T> success(T data, String msg) {
            Result<T> result = new Result<>();
            result.setStatus(1);
            result.setMsg(msg);
            result.setData(data);
            return result;
        }

        // 成功返回（无数据）
        public static <T> Result<T> success(String msg) {
            return success(null, msg);
        }

        // 失败返回
        public static <T> Result<T> error(int status, String msg) {
            Result<T> result = new Result<>();
            result.setStatus(status);
            result.setMsg(msg);
            result.setData(null);
            return result;
        }

        // 默认失败返回（status=0）
        public static <T> Result<T> error(String msg) {
            return error(0, msg);
        }
    }

    // ========== 1. 管理员登录接口 ==========
    @PostMapping("/doAdminLogin")
    public Result<Map<String, Object>> adminLogin(@RequestBody BUser bUser, HttpSession session) {
        try {
            // 校验入参
            if (bUser.getUsername() == null || bUser.getPassword() == null) {
                return Result.error("账号或密码不能为空");
            }

            BUser bUser1 = adminDao.adminLogin(bUser);
            if (bUser1 != null) {
                // ✅ 修正：status=0 是禁用，status=1 是启用
                if (bUser1.getStatus() == 0) {
                    return Result.error(2, "你的账号已被禁用");
                } else {
                    // 登录成功，存入管理员 Session
                    session.setAttribute("ADMIN_SESSION", bUser1);

                    Map<String, Object> data = new HashMap<>();
                    data.put("user", bUser1);
                    data.put("token", "admin_" + bUser1.getId());
                    return Result.success(data, "登录成功");
                }
            } else {
                return Result.error(2, "账号或密码错误，请重试输入！");
            }
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    // ========== 新增：管理员登出接口 ==========
    @GetMapping("/adminLogout")
    public Result<Void> adminLogout(HttpSession session) {
        // 清除管理员 Session
        session.removeAttribute("ADMIN_SESSION");
        return Result.success("管理员已登出！");
    }

    // ========== 2. 获取管理员列表（带分页） ==========
    @GetMapping("/adminList")
    public Result<Map<String, Object>> adminList(Page page) {
        try {
            List<BUser> userList = adminDao.list(page);
            int total = adminDao.total(page);
            page.caculateLast(total);

            // 封装分页数据
            Map<String, Object> data = new HashMap<>();
            data.put("adminList", userList);
            data.put("page", page);
            data.put("total", total);

            return Result.success(data, "获取管理员列表成功");
        } catch (Exception e) {
            return Result.error("获取管理员列表失败：" + e.getMessage());
        }
    }

    // ========== 3. 添加管理员 ==========
    @PostMapping("/addAdmin")
    public Result<Void> addAdmin(@RequestBody BUser bUser) {
        try {
            // ✅ 用 getUserName() 校验
            if (bUser.getUserName() == null || bUser.getPwd() == null) {
                return Result.error("管理员账号和密码不能为空");
            }
            bUser.setStatus(0);
            bUser.setSuperuser(0);
            adminDao.insertUser(bUser);
            return Result.success("添加成功！");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    // ========== 4. 获取单个管理员信息（供修改页面） ==========
    @GetMapping("/updateAdminView")
    public Result<BUser> updateAdminView(@RequestParam Integer id) {
        try {
            // 校验ID
            if (id == null || id <= 0) {
                return Result.error("管理员ID不能为空");
            }

            BUser bUser1 = adminDao.getUserById(id);
            if (bUser1 == null) {
                return Result.error("管理员不存在");
            }
            return Result.success(bUser1, "获取管理员信息成功");
        } catch (Exception e) {
            return Result.error("获取管理员信息失败：" + e.getMessage());
        }
    }

    // ========== 5. 修改管理员信息 ==========


    // ========== 6. 删除管理员 ==========
    @PostMapping("/delAdmin")
    public Result<Void> delAdmin(@RequestBody Map<String, Object> params) {
        try {
            Integer id = (Integer) params.get("id");

            if (id == null || id <= 0) {
                return Result.error("管理员ID不能为空");
            }

            BUser bUser = adminDao.getUserById(id);
            if (bUser == null) {
                return Result.error("管理员不存在！");
            }

            // 超级管理员不能删除
            if (bUser.getSuperuser() == 1) {
                return Result.error("超级管理员不能删除！");
            }

            int row = adminDao.deleteUser(id);
            if (row > 0) {
                return Result.success("删除管理员成功！");
            } else {
                return Result.error("删除管理员出错，请联系管理员！");
            }
        } catch (Exception ex) {
            return Result.error("删除管理员出错，请联系管理员！");
        }
    }

    // ========== 7. 启用/禁用管理员 ==========
    @PostMapping("/updateStatus")
    public Result<Void> updateStatus(@RequestBody BUser bUser) {
        try {
            // 校验入参
            if (bUser.getId() == null || bUser.getId() <= 0) {
                return Result.error("管理员ID不能为空");
            }

            BUser bUser1 = adminDao.getUserById(bUser.getId());
            if (bUser1 == null) {
                return Result.error(0, "管理员不存在！");
            } else {
                // 超级管理员不能禁用
                if (bUser1.getSuperuser() == 1) {
                    return Result.error(0, "超级管理员不能禁用！");
                } else {
                    int row = adminDao.updateStatus(bUser);
                    if (row > 0) {
                        return Result.success("设置管理员状态成功！");
                    } else {
                        return Result.error(0, "设置管理员状态出错");
                    }
                }
            }
        } catch (Exception ex) {
            return Result.error(0, ex.getMessage());
        }
    }
    // ========== 根据ID查询管理员（给编辑弹窗用） ==========
    @GetMapping("/getAdminById/{id}")
    public Result<BUser> getAdminById(@PathVariable Integer id) {
        try {
            // 直接用你已有的 DAO 方法
            BUser admin = adminDao.getUserById(id);

            if (admin == null) {
                return Result.error("管理员不存在");
            }

            return Result.success(admin, "查询成功");
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    // ========== 5. 修改管理员信息 ==========
    @PostMapping("/updateAdmin")
    public Result<Void> updateAdmin(@RequestBody BUser bUser) {
        try {
            // 校验入参
            if (bUser.getId() == null || bUser.getId() <= 0) {
                return Result.error("管理员ID不能为空");
            }
            // 必须传id才能修改
            int row = adminDao.updateUser(bUser);
            if (row > 0) {
                return Result.success("修改管理员成功！");
            } else {
                return Result.error(0, "修改管理员出错，请联系管理员！");
            }
        } catch (Exception ex) {
            return Result.error(0, ex.getMessage());
        }
    }
    // ========== 批量删除管理员 ==========
    @PostMapping("/delAllAdmin")
    public Result<Void> delAllAdmin(@RequestBody Map<String, String> params) {
        try {
            String idsStr = params.get("ids");
            if (idsStr == null || idsStr.trim().isEmpty()) {
                return Result.error("请选择要删除的管理员");
            }

            String[] idArr = idsStr.split(",");

            for (String idTemp : idArr) {
                int id = Integer.parseInt(idTemp);

                BUser user = adminDao.getUserById(id);
                if (user == null) continue;

                // 超级管理员不让删
                if (user.getSuperuser() == 1) {
                    continue;
                }

                adminDao.deleteUser(id);
            }

            return Result.success("批量删除成功！");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }
}