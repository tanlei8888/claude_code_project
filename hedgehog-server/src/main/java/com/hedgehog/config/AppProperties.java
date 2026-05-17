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

    /** CORS 跨域配置 */
    private Cors cors = new Cors();

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
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

    /** CORS 子配置 */
    public static class Cors {
        /** 允许的跨域来源，逗号分隔；默认 "*" 表示允许所有 */
        private String allowedOrigins = "*";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }
}
