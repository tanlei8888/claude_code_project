package com.hedgehog.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.entity.SiteConfig;
import com.hedgehog.mapper.SiteConfigMapper;
import com.hedgehog.util.MarkdownUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SiteConfigServiceImpl extends ServiceImpl<SiteConfigMapper, SiteConfig> implements SiteConfigService {

    @Override
    public SiteConfig get() {
        SiteConfig config = super.getById(1L);
        if (config == null) {
            config = new SiteConfig();
            config.setId(1L);
            config.setSiteName("Hedgehog Blog");
            config.setSiteSubtitle("A personal tech blog");
        }
        return config;
    }

    @Override
    public void update(SiteConfig config) {
        config.setId(1L);
        if (StringUtils.hasText(config.getAboutContentMd())) {
            config.setAboutContentHtml(MarkdownUtil.render(config.getAboutContentMd()));
        }
        saveOrUpdate(config);
    }
}
