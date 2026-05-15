package com.hedgehog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站点配置实体，对应 site_config 单行表。
 * id 固定为 1，通过 {@link TableId#type()} = IdType.INPUT 手动指定。
 */
@Data
@TableName("site_config")
public class SiteConfig {
    /** 主键，固定为 1 */
    @TableId(type = IdType.INPUT)
    private Long id;
    /** 博客名称 */
    private String siteName;
    /** 副标题 / Slogan */
    private String siteSubtitle;
    /** Logo URL */
    private String siteLogo;
    /** Favicon URL */
    private String siteFavicon;
    /** 关于页 Markdown 原文 */
    private String aboutContentMd;
    /** 关于页渲染后 HTML */
    private String aboutContentHtml;
    /** 作者名 */
    private String authorName;
    /** 作者头像 URL */
    private String authorAvatar;
    /** 作者简介 */
    private String authorBio;
    /** GitHub 主页 URL */
    private String socialGithub;
    /** Twitter 主页 URL */
    private String socialTwitter;
    /** 知乎主页 URL */
    private String socialZhihu;
    /** ICP 备案号 */
    private String icpNumber;
    /** 页脚文字 */
    private String footerText;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
