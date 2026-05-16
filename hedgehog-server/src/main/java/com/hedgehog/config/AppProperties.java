package com.hedgehog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用级配置属性（强类型绑定 application.yml 中 app.* 配置）。
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /** 文件上传配置 */
    private Upload upload = new Upload();

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    /** 上传子配置 */
    public static class Upload {
        /** 本地存储根目录，默认 uploads */
        private String path = "uploads";

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
