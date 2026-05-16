package com.hedgehog.site;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 站点配置服务接口（单行表模式）。
 */
public interface SiteConfigService extends IService<SiteConfig> {
    /**
     * 获取站点配置，数据库无记录时返回默认值。
     */
    SiteConfig get();

    /**
     * 更新站点配置，同时将关于页 Markdown 转换为 HTML。
     */
    void update(SiteConfig config);
}
