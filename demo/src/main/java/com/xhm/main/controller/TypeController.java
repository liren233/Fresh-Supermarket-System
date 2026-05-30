package com.xhm.main.controller;

import com.xhm.main.dao.BTypeDao;
import com.xhm.main.model.BType;
import com.xhm.main.model.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/type")
public class TypeController {

    @Resource
    private BTypeDao bTypeDao;

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

    @GetMapping("/typeList")
    public Result<Map<String, Object>> list(Page page) {
        try {
            // 1. 查询所有分类（无分页，一次性获取）
            List<BType> allTypes = bTypeDao.typeList(page);

            // 2. 构造树形结构
            List<BType> rootList = new ArrayList<>();
            Map<Integer, BType> typeMap = new HashMap<>();
            for (BType type : allTypes) {
                typeMap.put(type.getId(), type);
            }

            // 3. 构建父子关系
            for (BType type : allTypes) {
                if (type.getFid() == 0) {
                    rootList.add(type); // 一级分类
                } else {
                    BType parent = typeMap.get(type.getFid());
                    if (parent != null) {
                        parent.getChildren().add(type); // 添加到父节点的children列表
                    }
                }
            }

            // 4. 将树形结构“拍平”成一个有序列表，保证父节点在前，子节点紧跟其后
            List<BType> displayList = new ArrayList<>();
            for (BType root : rootList) {
                addToList(root, displayList);
            }

            int total = allTypes.size();
            page.caculateLast(total);

            Map<String, Object> data = new HashMap<>();
            data.put("typeList", displayList); // 前端用这个有序列表
            data.put("page", page);
            data.put("total", total);

            return Result.success(data, "获取商品分类列表成功");
        } catch (Exception e) {
            return Result.error("获取商品分类列表失败：" + e.getMessage());
        }
    }

    // 递归方法，将树节点及其子节点按顺序加入列表
    private void addToList(BType node, List<BType> list) {
        list.add(node);
        for (BType child : node.getChildren()) {
            addToList(child, list);
        }
    }


    @GetMapping("/addTypeView")
    public Result<List<BType>> addTypeView() {
        try {
            List<BType> bTypeList = bTypeDao.getTypeByFid(0);
            return Result.success(bTypeList, "获取一级分类列表成功");
        } catch (Exception e) {
            return Result.error("获取一级分类列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/updateTypeView")
    public Result<Map<String, Object>> updateTypeView(@RequestParam Integer id) {
        try {
            if (id == null || id <= 0) {
                return Result.error("分类ID不能为空");
            }

            List<BType> bTypeList = bTypeDao.getTypeByFid(0);
            BType bType = bTypeDao.getTypeById(id);

            if (bType == null) {
                return Result.error("商品分类不存在");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("bTypeList", bTypeList);
            data.put("bType", bType);

            return Result.success(data, "获取分类修改数据成功");
        } catch (Exception e) {
            return Result.error("获取分类修改数据失败：" + e.getMessage());
        }
    }

    @PostMapping("/updateType")
    public Result<Void> updateType(@RequestBody BType bType) {
        try {
            if (bType.getId() == null || bType.getId() <= 0) {
                return Result.error("分类ID不能为空");
            }
            if (bType.getType_name() == null || bType.getType_name().trim().isEmpty()) {
                return Result.error("分类名称不能为空");
            }

            int row = bTypeDao.updateType(bType);
            if (row > 0) {
                return Result.success("修改商品分类成功！");
            } else {
                return Result.error(0, "修改商品分类出错，请联系管理员！");
            }
        } catch (Exception ex) {
            return Result.error(0, ex.getMessage());
        }
    }

    @PostMapping("/delType")
    public Result<Void> delType(@RequestBody Map<String, Integer> params) {
        try {
            Integer id = params.get("id");
            if (id == null || id <= 0) {
                return Result.error("分类ID不能为空");
            }

            // 新增：检查是否有子分类
            List<BType> children = bTypeDao.getTypeByFid(id);
            if (!children.isEmpty()) {
                return Result.error(0, "该分类下有子分类，无法删除！");
            }

            BType bType = bTypeDao.getTypeById(id);
            if (bType == null) {
                return Result.error(0, "商品分类不存在！");
            }

            int row = bTypeDao.deleteType(id);
            if (row > 0) {
                return Result.success("删除商品分类成功！");
            } else {
                return Result.error(0, "删除商品分类出错，请联系管理员！");
            }
        } catch (Exception ex) {
            return Result.error(0, "删除商品分类失败：" + ex.getMessage());
        }
    }

    @PostMapping("/addType")
    public Result<Void> addType(@RequestBody BType bType) {
        try {
            if (bType.getType_name() == null || bType.getType_name().trim().isEmpty()) {
                return Result.error("分类名称不能为空");
            }

            int row = bTypeDao.insertType(bType);
            if (row > 0) {
                return Result.success("添加商品分类成功！");
            } else {
                return Result.error(2, "添加商品分类出错，请联系管理员！");
            }
        } catch (Exception ex) {
            return Result.error(0, ex.getMessage());
        }
    }
    @PostMapping("/delAllType")
    public Result<Void> delAllType(@RequestBody Map<String, String> params) {
        try {
            String idsStr = params.get("ids");
            if (idsStr == null || idsStr.trim().isEmpty()) {
                return Result.error("请选择要删除的分类");
            }

            // 分割ids字符串
            String[] ids = idsStr.split(",");
            for (String idStr : ids) {
                int id = Integer.parseInt(idStr.trim());
                // 先判断分类是否存在
                BType bType = bTypeDao.getTypeById(id);
                if (bType == null) {
                    return Result.error("分类ID " + id + " 不存在");
                }
                // 检查是否有子分类
                List<BType> children = bTypeDao.getTypeByFid(id);
                if (!children.isEmpty()) {
                    return Result.error("分类ID " + id + " 下有子分类，无法删除");
                }
                // 执行删除
                bTypeDao.deleteType(id);
            }
            return Result.success("批量删除成功！");
        } catch (Exception ex) {
            return Result.error(0, "批量删除失败：" + ex.getMessage());
        }
    }
}