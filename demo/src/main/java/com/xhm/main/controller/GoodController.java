package com.xhm.main.controller;

import com.xhm.main.dao.BGoodDao;
import com.xhm.main.dao.BTypeDao;
import com.xhm.main.model.BGood;
import com.xhm.main.model.BType;
import com.xhm.main.model.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.xhm.main.controller.GoodController.Result.error;
import static com.xhm.main.controller.GoodController.Result.success;

/**
 * 商品控制器（适配数据库真实数据 + 前端接口）
 */
@RestController
@RequestMapping("/api/goods") // 匹配前端 /api/goods 前缀
public class GoodController {

    @Resource
    private BGoodDao bGoodDao;
    @Resource
    private BTypeDao bTypeDao;

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

        // 成功返回（默认提示）
        public static <T> Result<T> success(T data) {
            return success(data, "操作成功");
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

        // 默认失败返回
        public static <T> Result<T> error(String msg) {
            return error(0, msg);
        }
    }

    // ========== 首页分类列表接口（前端 /goods/getTypeList） ==========
    @GetMapping("/getTypeList")
    public Result<List<BType>> getTypeList() {
        try {
            // 查询数据库b_type表的一级分类（fid=0）
            List<BType> allType = bTypeDao.typeList();
            List<BType> firstLevelType = allType.stream()
                    .filter(type -> type.getFid() == 0) // 只取一级分类（水果、蔬菜）
                    .collect(Collectors.toList());

            if (firstLevelType.isEmpty()) {
                return error("暂无商品分类数据");
            }
            return success(firstLevelType, "获取分类列表成功");
        } catch (Exception e) {
            return error("获取分类列表失败：" + e.getMessage());
        }
    }

    // ========== 首页商品列表接口（前端 /goods/getIndexGoodsList） ==========
    @GetMapping("/getIndexGoodsList")
    public Result<List<Map<String, Object>>> getIndexGoodsList() {
        try {
            // 1. 查询所有商品（不分页，拿到数据库全部商品）
            List<BGood> allGoods = bGoodDao.list(new Page());

            // 2. 只保留前4个商品（满足显示4个的需求）
            List<BGood> fourGoods = allGoods.stream()
                    .limit(4) // 精准截取前4个商品
                    .collect(Collectors.toList());

            // 3. 封装成前端需要的格式（只显示水果专区，放4个商品）
            List<Map<String, Object>> goodsList = new ArrayList<>();
            Map<String, Object> fruitGroup = new HashMap<>();
            fruitGroup.put("key", "水果专区");
            fruitGroup.put("value", fourGoods); // 放入4个商品
            goodsList.add(fruitGroup);

            // 4. 补充蔬菜专区（保持页面结构，无商品）
            Map<String, Object> vegGroup = new HashMap<>();
            vegGroup.put("key", "蔬菜专区");
            vegGroup.put("value", new ArrayList<>());
            goodsList.add(vegGroup);

            return success(goodsList, "获取首页商品列表成功（4个商品）");
        } catch (Exception e) {
            return error("获取商品列表失败：" + e.getMessage());
        }
    }

    // ========== 原有接口（保留并适配） ==========
    @GetMapping("/goodList")
    public Result<Map<String, Object>> goodList(Page page) {
        try {
            List<BGood> goodList = bGoodDao.list(page);
            int total = bGoodDao.total(page);
            page.caculateLast(total);

            Map<String, Object> data = new HashMap<>();
            data.put("goodList", goodList);
            data.put("page", page);
            data.put("total", total);

            return success(data, "获取商品列表成功");
        } catch (Exception e) {
            return error("获取商品列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/addGoodView")
    public Result<List<BType>> addGoodView() {
        try {
            List<BType> typeList = bTypeDao.typeList();
            return success(typeList, "获取商品分类列表成功");
        } catch (Exception e) {
            return error("获取商品分类列表失败：" + e.getMessage());
        }
    }

    @PostMapping("/addGood")
    public Result<Void> addGood(@RequestBody BGood bGood) {
        try {
            if (bGood.getGood_name() == null || bGood.getPrice() <= 0) {
                return error(2, "商品名称和价格不能为空且价格需大于0");
            }

            int row = bGoodDao.insertGood(bGood);
            if (row > 0) {
                return success("添加商品成功！");
            } else {
                return error(2, "添加商品出错，请联系管理员！");
            }
        } catch (Exception ex) {
            return error(0, ex.getMessage());
        }
    }

    @PostMapping("/delGood")
    public Result<Void> delGood(@RequestParam Integer id) {
        try {
            if (id == null || id <= 0) {
                return error("商品ID不能为空");
            }

            BGood bGood = bGoodDao.getGoodById(id);
            if (bGood == null) {
                return error(0, "商品不存在！");
            }

            int row = bGoodDao.deleteGood(id);
            if (row > 0) {
                return success("删除商品成功！");
            } else {
                return error(0, "删除商品出错，请联系管理员！");
            }
        } catch (Exception ex) {
            return error(0, "删除商品失败：" + ex.getMessage());
        }
    }

    @GetMapping("/updateGoodView")
    public Result<Map<String, Object>> updateGoodView(@RequestParam Integer id) {
        try {
            if (id == null || id <= 0) {
                return error("商品ID不能为空");
            }

            BGood bGood1 = bGoodDao.getGoodById(id);
            if (bGood1 == null) {
                return error("商品不存在");
            }

            List<BType> typeList = bTypeDao.typeList();

            Map<String, Object> data = new HashMap<>();
            data.put("typeList", typeList);
            data.put("BGood", bGood1);

            return success(data, "获取商品信息成功");
        } catch (Exception e) {
            return error("获取商品信息失败：" + e.getMessage());
        }
    }

    @PostMapping("/updateGood")
    public Result<Void> updateGood(@RequestBody BGood bGood) {
        try {
            if (bGood.getId() == null || bGood.getId() <= 0) {
                return error("商品ID不能为空");
            }
            if (bGood.getGood_name() == null || bGood.getPrice() <= 0) {
                return error("商品名称和价格不能为空且价格需大于0");
            }

            int row = bGoodDao.updateGood(bGood);
            if (row > 0) {
                return success("修改商品成功！");
            } else {
                return error(0, "修改商品出错，请联系管理员！");
            }
        } catch (Exception ex) {
            return error(0, ex.getMessage());
        }
    }

    @GetMapping("/detailView")
    public Result<Map<String, Object>> detailView(@RequestParam Integer id) {
        try {
            if (id == null || id <= 0) {
                return error("商品ID不能为空");
            }

            BGood bGood = bGoodDao.getGoodById(id);
            if (bGood == null) {
                return error("商品不存在");
            }

            List<BType> typeList = bTypeDao.typeList();

            Map<String, Object> data = new HashMap<>();
            data.put("bGood", bGood);
            data.put("typeList", typeList);

            return success(data, "获取商品详情成功");
        } catch (Exception e) {
            return error("获取商品详情失败：" + e.getMessage());
        }
    }
    // ========== 获取单个商品详情接口（给编辑页用） ==========
    @GetMapping("/getGoodById/{id}")
    public Result<BGood> getGoodById(@PathVariable("id") Integer id) {
        try {
            if (id == null || id <= 0) {
                return error("商品ID不能为空");
            }
            BGood bGood = bGoodDao.getGoodById(id);
            if (bGood == null) {
                return error("商品不存在");
            }
            return success(bGood, "获取商品详情成功");
        } catch (Exception e) {
            return error("获取商品详情失败：" + e.getMessage());
        }
    }
    // 批量删除商品
    @PostMapping("/delAllGood")
    public Result<Void> delAllGood(@RequestParam String ids) {
        try {
            if (ids == null || ids.trim().isEmpty()) {
                return error("请选择要删除的商品");
            }
            String[] idArr = ids.split(",");
            for (String idStr : idArr) {
                Integer id = Integer.parseInt(idStr.trim());
                bGoodDao.deleteGood(id);
            }
            return success("批量删除成功");
        } catch (Exception e) {
            return error("批量删除失败：" + e.getMessage());
        }
    }
}