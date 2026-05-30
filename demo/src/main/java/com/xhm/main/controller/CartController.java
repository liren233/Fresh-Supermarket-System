package com.xhm.main.controller;

import com.xhm.main.dao.FCartDao;
import com.xhm.main.dao.UserDao;
import com.xhm.main.model.FCart;
import com.xhm.main.model.FUser;
import com.xhm.main.model.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器（适配Spring Boot + Vue前后端分离）
 * 核心改造：适配前端请求路径、统一数据返回格式、补充前端需要的接口
 */
@RestController
@RequestMapping("/api/cart") // 移除/api前缀，和前端请求路径统一
public class CartController {

    @Resource
    private FCartDao fCartDao;
    @Resource
    private UserDao userDao;

    // ========== 统一返回结果封装类 ==========
    @Data
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

        // 成功返回（无数据）
        public static <T> Result<T> success(String msg) {
            return success(null, msg);
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

    // ========== 1. 适配前端：获取购物车列表（前端请求 /cart/getCartList） ==========
    @GetMapping("/getCartList")
    public Result<List<FCart>> getCartList(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            // 查询当前用户的购物车列表
            List<FCart> cartList = fCartDao.getCartByUserId(user.getId());

            // 给每个购物车项添加默认选中状态（前端需要）
            for (FCart cart : cartList) {
                // 可以通过反射或新增字段添加checked属性，这里简化：前端已默认设置checked=true
            }

            return Result.success(cartList, "获取购物车列表成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取购物车列表失败：" + e.getMessage());
        }
    }

    // ========== 2. 适配前端：更新购物车数量（前端请求 /cart/updateCartNum） ==========
    @PostMapping("/updateCartNum")
    public Result<Void> updateCartNum(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        try {
            Integer cartId = params.get("cartId");
            Integer num = params.get("num");

            if (cartId == null || num == null || num < 1) {
                return Result.error("参数错误：购物车ID不能为空，数量不能小于1");
            }

            // 查询购物车项
            FCart cart = fCartDao.getCartById(cartId);
            if (cart == null) {
                return Result.error("购物车商品不存在");
            }

            // 更新数量
            cart.setNum(num);
            int row = fCartDao.updateCart(cart);

            if (row > 0) {
                return Result.success("更新购物车数量成功");
            } else {
                return Result.error("更新购物车数量失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新购物车数量失败：" + e.getMessage());
        }
    }

    // ========== 3. 适配前端：删除购物车商品（前端请求 /cart/deleteCartItem） ==========
    @PostMapping("/deleteCartItem")
    public Result<Void> deleteCartItem(@RequestBody Map<String, Integer> params) {
        try {
            Integer cartId = params.get("cartId");
            if (cartId == null || cartId <= 0) {
                return Result.error("购物车ID不能为空");
            }

            // 校验购物车项是否存在
            FCart cart = fCartDao.getCartById(cartId);
            if (cart == null) {
                return Result.error("购物车商品不存在");
            }

            // 删除商品
            int row = fCartDao.deleteCart(cartId);
            if (row > 0) {
                return Result.success("删除购物车商品成功");
            } else {
                return Result.error("删除购物车商品失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除购物车商品失败：" + e.getMessage());
        }
    }

    // ========== 4. 原有接口：获取购物车数量（保留，前端Index.vue使用） ==========
    @GetMapping("/getCartCount")
    public Result<Integer> getCartCount(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.success(0, "未登录，购物车数量为0");
            }

            List<FCart> fCartList = fCartDao.getCartByUserId(user.getId());
            int totalNum = fCartList.stream().mapToInt(FCart::getNum).sum();

            return Result.success(totalNum, "获取购物车数量成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.success(0, "获取购物车数量失败，已返回默认值0：" + e.getMessage());
        }
    }

    // ========== 5. 原有接口：后台购物车列表（保留，后台管理使用） ==========
    @GetMapping("/cartList")
    public Result<Map<String, Object>> cartList(Page page) {
        try {
            Map<String, List<FCart>> map = new LinkedHashMap<>();
            List<FUser> userList = userDao.list(page);

            for (FUser fUser : userList) {
                List<FCart> cartList = fCartDao.getCartByUserId(fUser.getId());
                map.put(fUser.getUserName(), cartList);
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("userList", userList);
            data.put("list", map);

            return Result.success(data, "获取后台购物车列表成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取后台购物车列表失败：" + e.getMessage());
        }
    }

    // ========== 6. 添加商品到购物车（适配中间表） ==========
    @PostMapping("/addCart")
    public Result<Void> addCart(@RequestBody FCart fCart, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            // 1. 先查询用户的购物车中是否已有该商品
            FCart existingCart = fCartDao.getGood(fCart);
            if (existingCart != null) {
                // 已有该商品：更新数量
                existingCart.setNum(existingCart.getNum() + fCart.getNum());
                int row = fCartDao.updateCart(existingCart);
                if (row > 0) {
                    return Result.success("商品数量已更新");
                } else {
                    return Result.error("更新购物车数量失败");
                }
            } else {
                // 2. 没有该商品：先插入到 f_cart 表
                fCart.setUser_id(user.getId());
                int row = fCartDao.insertCart(fCart);
                if (row > 0) {
                    // 3. 获取刚插入的购物车ID，插入到 f_cart_goods 中间表
                    Integer cartId = fCart.getId();
                    Integer goodsId = fCart.getGoods_id();

                    // 调用 DAO 插入中间表（需要你在 FCartDao 里新增方法）
                    fCartDao.insertCartGoods(cartId, goodsId);

                    return Result.success("添加商品到购物车成功");
                } else {
                    return Result.error("添加购物车失败");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.error("添加商品到购物车失败：" + ex.getMessage());
        }
    }

    // ========== 7. 原有接口：showCart（保留，兼容旧请求） ==========
    @GetMapping("/showCart")
    public Result<Map<String, Object>> userCartList(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            List<FCart> fCartList = fCartDao.getCartByUserId(user.getId());

            double total_price = 0;
            for (FCart fc : fCartList) {
                total_price += fc.getBGood().getPrice() * fc.getNum();
            }
            int total_num = fCartList.stream().mapToInt(FCart::getNum).sum();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("fCartList", fCartList);
            data.put("total_price", String.format("%.2f", total_price));
            data.put("total_num", total_num);
            data.put("user", user);

            return Result.success(data, "获取购物车列表成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取购物车列表失败：" + e.getMessage());
        }
    }
}