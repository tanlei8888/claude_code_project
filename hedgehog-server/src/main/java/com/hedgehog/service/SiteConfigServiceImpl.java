package com.hedgehog.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.entity.SiteConfig;
import com.hedgehog.mapper.SiteConfigMapper;
import com.hedgehog.util.MarkdownUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 站点配置服务实现（单行表模式）。
 */
@Service
public class SiteConfigServiceImpl extends ServiceImpl<SiteConfigMapper, SiteConfig> implements SiteConfigService {

    @Override
    public SiteConfig get() {
        SiteConfig config = super.getById(1L);
        // 数据库无记录时返回默认配置
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
        // 确保始终操作 id=1 的记录
        config.setId(1L);
        // 关于页 Markdown → HTML
        if (StringUtils.hasText(config.getAboutContentMd())) {
            config.setAboutContentHtml(MarkdownUtil.render(config.getAboutContentMd()));
        }
        saveOrUpdate(config);
    }
}
