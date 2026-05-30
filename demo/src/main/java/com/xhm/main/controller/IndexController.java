package com.xhm.main.controller; // 修正为你的实际包路径

import com.xhm.main.dao.BGoodDao;
import com.xhm.main.dao.BTypeDao;
import com.xhm.main.model.BGood;
import com.xhm.main.model.BType;
import com.xhm.main.model.FUser;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制器（适配Spring Boot + Vue前后端分离）
 * 核心改造：移除视图返回、统一JSON格式、修正包路径、保留首页核心数据逻辑
 */
@RestController
@RequestMapping("/api/index") // 统一添加/api前缀，方便跨域和接口管理
public class IndexController {

    @Resource
    private BTypeDao bTypeDao;
    @Resource
    private BGoodDao bGoodDao;

    // ========== 统一返回结果封装类 ==========
    @lombok.Data
    public static class Result<T> {
        private int status;  // 1成功/0失败
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

        // 失败返回
        public static <T> Result<T> error(String msg) {
            Result<T> result = new Result<>();
            result.setStatus(0);
            result.setMsg(msg);
            result.setData(null);
            return result;
        }
    }

    // ========== 前台首页数据接口（替代原首页视图） ==========
    @GetMapping("/") // 保留根路径，也可访问 /api/index/
    public Result<Map<String, Object>> index(HttpServletRequest request) {
        try {
            // 1. 核心逻辑：按商品分类封装商品列表（保留原有业务逻辑）
            Map<String, List<BGood>> map = new LinkedHashMap<>();
            List<BType> typeList = bTypeDao.typeList();

            for (BType bType : typeList) {
                List<BGood> goodList = bGoodDao.getGoodByTypeId(bType.getId());
                map.put(bType.getType_name(), goodList);
            }

            // 2. 获取当前登录用户信息
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            // 3. 封装所有返回数据（替代ModelMap）
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("user", user);          // 当前登录用户
            data.put("typeList", typeList);  // 商品分类列表
            data.put("list", map);           // 分类-商品映射字典

            return Result.success(data, "获取首页数据成功");
        } catch (Exception e) {
            return Result.error("获取首页数据失败：" + e.getMessage());
        }
    }

    // ========== 备用：单独的首页数据接口（推荐前端调用这个） ==========
    @GetMapping("/homeData")
    public Result<Map<String, Object>> getHomeData(HttpServletRequest request) {
        return this.index(request); // 复用首页核心逻辑
    }
}