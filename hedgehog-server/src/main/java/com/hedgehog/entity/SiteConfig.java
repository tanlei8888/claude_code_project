package com.hedgehog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("site_config")
public class SiteConfig {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String siteName;
    private String siteSubtitle;
    private String siteLogo;
    private String siteFavicon;
    private String aboutContentMd;
    private String aboutContentHtml;
    private String authorName;
    private String authorAvatar;
    private String authorBio;
    private String socialGithub;
    private String socialTwitter;
    private String socialZhihu;
    private String icpNumber;
    private String footerText;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
