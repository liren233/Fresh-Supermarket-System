package com.xhm.main.model;  // 修正为项目统一包路径

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 通用返回结果类（Lombok优化 + 包路径修正 + 功能增强）
 */
@Component
@Data  // Lombok注解：自动生成getter/setter、toString、equals、hashCode等方法
public class Result {
    // 保留原有核心字段
    private int status;  // 改为private，符合封装规范（Lombok自动生成get/set）
    private String msg;

    // 新增data字段：支持返回任意业务数据（适配前后端分离的JSON返回需求）
    private Object data;
}