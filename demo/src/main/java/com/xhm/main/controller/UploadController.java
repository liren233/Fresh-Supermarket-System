package com.xhm.main.controller; // 修正为你的实际包路径

import com.xhm.main.model.UploadResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传控制器（适配Spring Boot + Vue前后端分离）
 * 核心改造：修正包路径、修复注入问题、优化上传路径、统一JSON返回格式
 */
@RestController
@RequestMapping("/api/upload") // 统一添加/api前缀，方便跨域和接口管理
public class UploadController {

    // ========== 移除注入，改为方法内创建对象（避免注入错误） ==========
    // 废弃：@Resource UploadResult uploadResult;
    // 废弃：@Resource UploadResult.DataBean dataBean;

    /**
     * 生成32位UUID作为文件名，避免重复
     */
    public static String getId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    /**
     * 多文件上传接口（适配富文本编辑器/多文件上传场景）
     * 前端调用路径：/api/upload/fileUpload
     */
    @PostMapping("/fileUpload")
    public UploadResult handleFormUpload(
            @RequestParam("file") List<MultipartFile> uploadfile,
            HttpServletRequest request) {

        // 方法内创建返回对象（避免注入冲突）
        UploadResult uploadResult = new UploadResult();
        UploadResult.DataBean dataBean = new UploadResult.DataBean();
        uploadResult.setData(dataBean);

        // 1. 校验文件是否为空
        if (uploadfile == null || uploadfile.isEmpty()) {
            uploadResult.setCode(1);
            uploadResult.setMsg("上传的图片不存在，请选择文件后再上传");
            return uploadResult;
        }

        // 2. 配置上传路径（适配Spring Boot项目结构）
        // 方式1：项目根目录下的upload文件夹（推荐，避免打包后路径问题）
        String basePath = System.getProperty("user.dir") + "/upload/";
        // 方式2：Tomcat运行时的静态资源目录（兼容原有逻辑）
        // String basePath = request.getServletContext().getRealPath("static/upload/");

        File filePath = new File(basePath);
        if (!filePath.exists()) {
            filePath.mkdirs(); // 创建多级目录
        }

        // 3. 循环处理上传文件
        for (MultipartFile file : uploadfile) {
            try {
                // 校验单个文件
                if (file.isEmpty()) {
                    continue; // 跳过空文件
                }

                // 获取文件原始名称和后缀
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
                    uploadResult.setCode(1);
                    uploadResult.setMsg("文件格式错误，无后缀名");
                    return uploadResult;
                }

                // 生成新文件名
                String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFileName = getId() + suffix;

                // 保存文件到指定路径
                File targetFile = new File(basePath + newFileName);
                file.transferTo(targetFile);

                // 设置返回的文件访问路径（前端可直接访问）
                // 注意：需配置Spring Boot静态资源映射，让/upload目录可访问
                String fileUrl = "/upload/" + newFileName;
                dataBean.setSrc(fileUrl);

            } catch (Exception e) {
                e.printStackTrace();
                uploadResult.setCode(1);
                uploadResult.setMsg("文件上传失败：" + e.getMessage());
                return uploadResult;
            }
        }

        // 4. 上传成功返回
        uploadResult.setCode(0);
        uploadResult.setMsg("文件上传成功");
        return uploadResult;
    }

    /**
     * 单文件上传测试接口（简化版）
     * 前端调用路径：/api/upload/uploadTest
     */
    @PostMapping("/uploadTest") // 改为Post请求，适配文件上传
    public UploadResult uploadTest(
            @RequestParam("pic") MultipartFile pic,
            HttpServletRequest request) throws IllegalStateException, IOException {

        UploadResult uploadResult = new UploadResult();
        UploadResult.DataBean dataBean = new UploadResult.DataBean();
        uploadResult.setData(dataBean);

        // 校验文件
        if (pic.isEmpty()) {
            uploadResult.setCode(1);
            uploadResult.setMsg("请选择要上传的图片");
            return uploadResult;
        }

        // 配置上传路径（优化为项目根目录）
        String basePath = System.getProperty("user.dir") + "/web/upload/";
        File filePath = new File(basePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        // 生成新文件名
        String originalFilename = pic.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = getId() + suffix;

        // 保存文件
        File newFile = new File(basePath + newFileName);
        pic.transferTo(newFile);

        // 返回上传结果
        dataBean.setSrc("/web/upload/" + newFileName);
        uploadResult.setCode(0);
        uploadResult.setMsg("测试上传成功");
        return uploadResult;
    }

    // ========== 补充：Spring Boot静态资源映射配置（需添加到启动类/配置类） ==========
    /*
    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // 映射/upload目录，让前端可访问上传的文件
            registry.addResourceHandler("/upload/**")
                    .addResourceLocations("file:" + System.getProperty("user.dir") + "/upload/");
            // 映射/web/upload目录
            registry.addResourceHandler("/web/upload/**")
                    .addResourceLocations("file:" + System.getProperty("user.dir") + "/web/upload/");
        }
    }
    */
}