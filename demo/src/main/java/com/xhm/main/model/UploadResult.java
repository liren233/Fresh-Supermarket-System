package com.xhm.main.model;  // 修正为项目统一包路径

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 文件上传返回结果类（Lombok优化 + 包路径修正）
 * 适配富文本编辑器/文件上传组件的返回格式要求
 */
@Component
@Data  // Lombok注解：自动生成所有字段的getter/setter、toString等方法
public class UploadResult {
    /**
     * code : 0 成功 | 1 失败
     * msg : 提示信息
     * data : {"src":"文件访问路径"}
     */
    private int code;
    private String msg;
    private DataBean data;

    /**
     * 内部数据类（文件路径）
     */
    @Component
    @Data  // 内部类同样使用Lombok简化
    public static class DataBean {
        /**
         * src : 文件访问路径（如：/upload/xxx.jpg）
         */
        private String src;
    }
}