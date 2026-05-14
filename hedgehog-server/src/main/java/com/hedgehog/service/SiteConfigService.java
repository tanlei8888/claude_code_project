package com.hedgehog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.SiteConfig;

public interface SiteConfigService extends IService<SiteConfig> {
    SiteConfig get();
    void update(SiteConfig config);
}
