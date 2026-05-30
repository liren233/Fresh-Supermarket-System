package com.xhm.main.controller;

import com.xhm.main.dao.FAddressDao;
import com.xhm.main.dao.FCartDao;
import com.xhm.main.dao.FOderDetailDao;
import com.xhm.main.dao.FOrderDao;
import com.xhm.main.model.FAddress;
import com.xhm.main.model.FCart;
import com.xhm.main.model.FOrder;
import com.xhm.main.model.FOrderDetail;
import com.xhm.main.model.FUser;
import com.xhm.main.model.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private FOrderDao orderDao;
    @Resource
    private FOderDetailDao fOderDetailDao;
    @Resource
    private FAddressDao fAddressDao;
    @Resource
    private FCartDao fCartDao;

    // ========== 统一返回结果封装类 ==========
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

    // ===================== ✅ 新增：订单列表接口（解决404问题） =====================
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            int start = (page - 1) * size;
            List<FOrder> list = orderDao.list(userId, start, size);
            int total = orderDao.count(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);
            data.put("page", page);
            data.put("size", size);

            return Result.success(data, "获取订单列表成功");
        } catch (Exception e) {
            return Result.error("获取订单列表失败：" + e.getMessage());
        }
    }

    // ========== 1. 前台用户订单列表（按用户分组带详情） ==========
    @GetMapping("/userCenterOrder")
    public Result<Map<String, Object>> userOrderList(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            List<Map<String, Object>> orderList = new ArrayList<>();
            List<FOrder> orders = orderDao.getOrderByUserId(user.getId());

            for (FOrder fOrder : orders) {
                List<FOrderDetail> orderDetailList = fOderDetailDao.getByOrderId(fOrder.getId());
                Map<String, Object> orderMap = new LinkedHashMap<>();
                orderMap.put("order", fOrder);
                orderMap.put("details", orderDetailList);
                orderList.add(orderMap);
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("list", orderList);
            data.put("user", user);

            return Result.success(data, "获取用户订单列表成功");
        } catch (Exception e) {
            return Result.error("获取用户订单列表失败：" + e.getMessage());
        }
    }

    // ========== 2. 后台订单列表（带分页） ==========
    @GetMapping("/orderList")
    public Result<Map<String, Object>> list(Page page) {
        try {
            List<FOrder> list = orderDao.listPage(page);
            int total = orderDao.total(page);
            page.caculateLast(total);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("list", list);
            data.put("page", page);
            data.put("total", total);

            return Result.success(data, "获取后台订单列表成功");
        } catch (Exception e) {
            return Result.error("获取后台订单列表失败：" + e.getMessage());
        }
    }

    // ========== 3. 结算页面数据（购物车→结算） ==========
    @GetMapping("/placeOrder")
    public Result<Map<String, Object>> addOrderView(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            FAddress fAddress = fAddressDao.getAddressByUserId(user.getId());
            if (fAddress == null) {
                return Result.error("未设置收货地址，请先添加收货地址");
            }

            List<FCart> fCartList = fCartDao.getCartByUserId(user.getId());
            double total_price = 0;
            for (FCart fc : fCartList) {
                total_price += fc.getBGood().getPrice() * fc.getNum();
            }
            int total_num = fCartList.stream().mapToInt(FCart::getNum).sum();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("fAddress", fAddress);
            data.put("fCartList", fCartList);
            data.put("total_price", String.format("%.2f", total_price));
            data.put("total_num", total_num);

            return Result.success(data, "获取结算页面数据成功");
        } catch (Exception e) {
            return Result.error("获取结算页面数据失败：" + e.getMessage());
        }
    }

    // ========== 4. 提交订单（核心业务） ==========
    @PostMapping("/addOrder")
    public Result<Map<String, Object>> addOrder(@RequestBody Map<String, Object> orderData, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            Integer addressId = (Integer) orderData.get("addressId");
            if (addressId == null) {
                return Result.error("请选择收货地址");
            }

            FAddress fAddress = fAddressDao.getAddressById(addressId);
            if (fAddress == null) {
                return Result.error("收货地址不存在");
            }

            // 验证地址所属用户
            if (!fAddress.getUser_id().equals(user.getId())) {
                return Result.error("无权使用此地址");
            }

            List<Map<String, Object>> goodsList = (List<Map<String, Object>>) orderData.get("goodsList");
            if (goodsList == null || goodsList.isEmpty()) {
                return Result.error("商品信息为空，无法提交订单");
            }

            String order_no = UUID.randomUUID().toString().replace("-", "").substring(12);
            FOrder fOrder = new FOrder();
            fOrder.setOrder_no(order_no);
            fOrder.setUser_name(fAddress.getUser_name());
            fOrder.setAddress(fAddress.getAddress());

            fOrder.setTime(new Date());
            fOrder.setStatus(1);
            fOrder.setPhone(fAddress.getPhone());
            fOrder.setUser_id(user.getId());
            fOrder.setPay_status(0); // 0表示未支付

            int total_price = 0;
            for (Map<String, Object> goods : goodsList) {
                int price = Integer.parseInt(goods.get("price").toString());
                int count = Integer.parseInt(goods.get("count").toString());
                total_price += price * count;
            }
            fOrder.setTotal_price(total_price);

            int row = orderDao.insertOrder(fOrder);
            if (row < 1) {
                return Result.error("提交订单失败，请重试");
            }

            for (Map<String, Object> goods : goodsList) {
                System.out.println("商品信息：" + goods);
                FOrderDetail fOrderDetail = new FOrderDetail();
                fOrderDetail.setOrder_id(fOrder.getId());
                fOrderDetail.setImg_url(goods.get("img_url").toString()); // 从商品列表中获取图片路径
                fOrderDetail.setGood_name(goods.get("good_name").toString());
                fOrderDetail.setNum(Integer.parseInt(goods.get("count").toString()));
                // fOrderDetail.setUnit("件"); // 暂时注释掉，数据库表中没有这个字段
                // fOrderDetail.setIntro(""); // 暂时注释掉，数据库表中没有这个字段
                fOrderDetail.setPrice(Double.parseDouble(goods.get("price").toString()));
                fOrderDetail.setTotal_price(Double.parseDouble(goods.get("price").toString()) * Integer.parseInt(goods.get("count").toString()));
                fOderDetailDao.insert(fOrderDetail);
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("fAddress", fAddress);
            data.put("user", user);
            data.put("orderNo", order_no);
            data.put("orderId", fOrder.getId());

            return Result.success(data, "提交订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交订单失败：" + e.getMessage());
        }
    }

    // ========== 5. 支付订单（简易版本） ==========
    @PostMapping("/pay")
    public Result<Map<String, Object>> pay(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            // 处理orderId，可能是字符串类型
            Integer orderId = null;
            Object orderIdObj = params.get("orderId");
            if (orderIdObj != null) {
                if (orderIdObj instanceof String) {
                    orderId = Integer.parseInt((String) orderIdObj);
                } else if (orderIdObj instanceof Integer) {
                    orderId = (Integer) orderIdObj;
                }
            }
            
            // 处理payType，可能是字符串类型
            Integer payType = null;
            Object payTypeObj = params.get("payType");
            if (payTypeObj != null) {
                if (payTypeObj instanceof String) {
                    payType = Integer.parseInt((String) payTypeObj);
                } else if (payTypeObj instanceof Integer) {
                    payType = (Integer) payTypeObj;
                }
            }

            if (orderId == null) {
                return Result.error("订单ID不能为空");
            }

            // 查询订单
            FOrder order = orderDao.getOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 验证订单所属用户
            if (!order.getUser_id().equals(user.getId())) {
                return Result.error("无权操作此订单");
            }

            // 简易支付逻辑：直接更新订单状态为已支付，然后修改为等待发货
            order.setStatus(3); // 3表示已发货
            order.setPay_status(1); // 1表示支付成功

            // 更新订单状态到数据库
            orderDao.updateOrder(order);

            // 这里可以添加支付记录等逻辑

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("orderId", orderId);
            data.put("orderNo", order.getOrder_no());
            data.put("status", "success");

            return Result.success(data, "支付成功");
        } catch (NumberFormatException e) {
            return Result.error("订单ID格式错误");
        } catch (Exception e) {
            return Result.error("支付失败：" + e.getMessage());
        }
    }

    // ========== 6. 删除订单（用于超时订单） ==========
    @PostMapping("/delete")
    public Result<Map<String, Object>> deleteOrder(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            FUser user = (FUser) session.getAttribute("USER_SESSION");

            if (user == null) {
                return Result.error("用户未登录，请先登录");
            }

            // 处理orderId，可能是字符串类型
            Integer orderId = null;
            Object orderIdObj = params.get("orderId");
            if (orderIdObj != null) {
                if (orderIdObj instanceof String) {
                    orderId = Integer.parseInt((String) orderIdObj);
                } else if (orderIdObj instanceof Integer) {
                    orderId = (Integer) orderIdObj;
                }
            }

            if (orderId == null) {
                return Result.error("订单ID不能为空");
            }

            // 查询订单
            FOrder order = orderDao.getOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 验证订单所属用户
            if (!order.getUser_id().equals(user.getId())) {
                return Result.error("无权操作此订单");
            }

            // 删除订单详情
            fOderDetailDao.deleteByOrderId(orderId);
            // 删除订单
            orderDao.deleteOrder(orderId);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("orderId", orderId);

            return Result.success(data, "删除订单成功");
        } catch (NumberFormatException e) {
            return Result.error("订单ID格式错误");
        } catch (Exception e) {
            return Result.error("删除订单失败：" + e.getMessage());
        }
    }

    // ========== 7. 获取订单详情（用于后台查看） ==========
    @GetMapping("/getOrderById")
    public Result<FOrder> getOrderById(@RequestParam Integer id) {
        try {
            FOrder order = orderDao.getOrderById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order, "获取订单详情成功");
        } catch (Exception e) {
            return Result.error("获取订单详情失败：" + e.getMessage());
        }
    }

    // ========== 8. 获取订单商品列表（用于后台查看） ==========
    @GetMapping("/getOrderDetails")
    public Result<List<FOrderDetail>> getOrderDetails(@RequestParam Integer orderId) {
        try {
            List<FOrderDetail> orderDetails = fOderDetailDao.getByOrderId(orderId);
            return Result.success(orderDetails, "获取订单商品列表成功");
        } catch (Exception e) {
            return Result.error("获取订单商品列表失败：" + e.getMessage());
        }
    }

    // ========== 9. 更新订单（用于后台编辑） ==========
    @PostMapping("/updateOrder")
    public Result<Map<String, Object>> updateOrder(@RequestBody FOrder order) {
        try {
            if (order.getId() == null) {
                return Result.error("订单ID不能为空");
            }

            // 查询订单是否存在
            FOrder existingOrder = orderDao.getOrderById(order.getId());
            if (existingOrder == null) {
                return Result.error("订单不存在");
            }

            // 更新订单信息
            orderDao.updateOrder(order);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("orderId", order.getId());

            return Result.success(data, "更新订单成功");
        } catch (Exception e) {
            return Result.error("更新订单失败：" + e.getMessage());
        }
    }
}