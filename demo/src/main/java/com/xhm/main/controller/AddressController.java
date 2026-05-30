package com.xhm.main.controller;

import com.xhm.main.dao.FAddressDao;
import com.xhm.main.model.FAddress;
import com.xhm.main.model.FUser;
import com.xhm.main.model.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Resource
    private FAddressDao fAddressDao;

    @Data
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

        public static <T> Result<T> error(String msg) {
            Result<T> result = new Result<>();
            result.setStatus(0);
            result.setMsg(msg);
            result.setData(null);
            return result;
        }
    }

    // 获取地址列表
    @GetMapping("/list")
    public Result<List<FAddress>> list(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录");
            }

            List<FAddress> list = fAddressDao.getAddressListByUserId(user.getId());
            return Result.success(list, "获取地址列表成功");
        } catch (Exception e) {
            return Result.error("获取地址失败：" + e.getMessage());
        }
    }

    @GetMapping("/myAddress")
    public Result<Map<String, Object>> getMyAddress(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            FAddress fAddress = fAddressDao.getAddressByUserId(user.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("fAddress", fAddress);
            data.put("user", user);

            return Result.success(data, "获取收货地址成功");
        } catch (Exception e) {
            return Result.error("获取收货地址失败：" + e.getMessage());
        }
    }

    @GetMapping("/addressList")
    public Result<Map<String, Object>> list(Page page) {
        try {
            List<FAddress> list = fAddressDao.list(page);
            int total = fAddressDao.total(page);
            page.caculateLast(total);

            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("page", page);
            data.put("total", total);

            return Result.success(data, "获取地址列表成功");
        } catch (Exception e) {
            return Result.error("获取地址列表失败：" + e.getMessage());
        }
    }

    // 新增地址
    @PostMapping("/addAddress")
    public Result<Void> addAddress(@RequestBody FAddress fAddress, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            fAddress.setUser_id(user.getId());

            // 自动给默认值，防止数据库报错
            if (fAddress.getIs_default() == null) {
                fAddress.setIs_default(0);
            }

            fAddressDao.insertAddress(fAddress);
            return Result.success("添加收货地址成功");

        } catch (Exception e) {
            return Result.error("操作收货地址失败: " + e.getMessage());
        }
    }

    // 删除地址
    @PostMapping("/delAddress")
    public Result<Void> delAddress(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        try {
            Integer id = params.get("id");
            if (id == null) {
                return Result.error("地址ID不能为空");
            }

            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");
            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            FAddress fAddress = fAddressDao.getAddressById(id);
            if (fAddress == null) {
                return Result.error("地址不存在！");
            }

            // 验证地址是否属于当前用户
            if (!fAddress.getUser_id().equals(user.getId())) {
                return Result.error("无权删除此地址");
            }

            int row = fAddressDao.deleteAddress(id);
            if (row > 0) {
                return Result.success("删除地址成功！");
            } else {
                return Result.error("删除地址失败，请联系管理员！");
            }
        } catch (Exception e) {
            return Result.error("删除地址出错：" + e.getMessage());
        }
    }

    // 修改地址
    @PostMapping("/updateAddress")
    public Result<Void> updateAddress(@RequestBody FAddress fAddress, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");
            if (user == null) return Result.error("用户未登录");

            if (fAddress == null || fAddress.getId() <= 0) {
                return Result.error("地址信息或ID不能为空");
            }

            int row = fAddressDao.updateAddress(fAddress);
            if (row > 0) {
                return Result.success("修改收货地址成功！");
            } else {
                return Result.error("修改收货地址失败，请联系管理员！");
            }
        } catch (Exception e) {
            return Result.error("修改地址出错：" + e.getMessage());
        }
    }

    // ✅ 设置默认地址（有且只有一个）
    @PostMapping("/setDefault")
    public Result<Void> setDefault(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        try {
            Integer id = params.get("id");

            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");
            if (user == null) return Result.error("用户未登录");

            // 1. 全部取消默认
            fAddressDao.setAllNotDefault(user.getId());

            // 2. 设置当前为默认
            fAddressDao.setOneDefault(id);

            return Result.success("设置默认地址成功");
        } catch (Exception e) {
            return Result.error("设置失败");
        }
    }
}