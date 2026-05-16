CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL            COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL            COMMENT '密码（BCrypt 加密）',
    `nickname`    VARCHAR(50)  DEFAULT NULL        COMMENT '昵称',
    `email`       VARCHAR(100) DEFAULT NULL        COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL        COMMENT '手机号',
    `status`      TINYINT      NOT NULL DEFAULT 1  COMMENT '状态 1-启用 0-禁用',
    `role`        VARCHAR(10)  NOT NULL DEFAULT 'USER' COMMENT 'ADMIN|USER',
    `avatar`      VARCHAR(500) DEFAULT NULL        COMMENT '头像URL',
    `bio`         VARCHAR(500) DEFAULT NULL        COMMENT '个人简介',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0  COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS `blog_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(50)  NOT NULL            COMMENT '分类名称',
    `slug`        VARCHAR(50)  NOT NULL            COMMENT 'URL 标识',
    `description` VARCHAR(200) DEFAULT NULL        COMMENT '分类描述',
    `sort_order`  INT          DEFAULT 0           COMMENT '排序',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0           COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类';

CREATE TABLE IF NOT EXISTS `blog_tag` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(30) NOT NULL             COMMENT '标签名',
    `slug`        VARCHAR(30) NOT NULL             COMMENT 'URL 标识',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT     DEFAULT 0            COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签';

CREATE TABLE IF NOT EXISTS `blog_article` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `title`         VARCHAR(200) NOT NULL            COMMENT '标题',
    `slug`          VARCHAR(200) NOT NULL            COMMENT 'URL 标识',
    `summary`       VARCHAR(500) DEFAULT NULL        COMMENT '摘要',
    `content_md`    LONGTEXT     NOT NULL            COMMENT 'Markdown 原文',
    `content_html`  LONGTEXT     NOT NULL            COMMENT '渲染后 HTML',
    `cover_image`   VARCHAR(500) DEFAULT NULL        COMMENT '封面图 URL',
    `category_id`   BIGINT       DEFAULT NULL        COMMENT '外键 blog_category.id',
    `status`        INT          DEFAULT 0           COMMENT '0=草稿 1=已发布 2=定时发布 3=私密',
    `is_top`        TINYINT      DEFAULT 0           COMMENT '是否置顶',
    `view_count`    BIGINT       DEFAULT 0           COMMENT '阅读次数',
    `like_count`    BIGINT       DEFAULT 0           COMMENT '点赞数',
    `comment_count` BIGINT       DEFAULT 0           COMMENT '评论数',
    `publish_time`  DATETIME     DEFAULT NULL        COMMENT '定时发布时间',
    `author_id`     BIGINT       NOT NULL            COMMENT '外键 sys_user.id',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT      DEFAULT 0           COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章';

CREATE TABLE IF NOT EXISTS `blog_article_tag` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT,
    `article_id` BIGINT NOT NULL COMMENT '外键 blog_article.id',
    `tag_id`     BIGINT NOT NULL COMMENT '外键 blog_tag.id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章-标签关联';

CREATE TABLE IF NOT EXISTS `blog_comment` (
    `id`               BIGINT    NOT NULL AUTO_INCREMENT,
    `article_id`       BIGINT    NOT NULL            COMMENT '外键 blog_article.id',
    `user_id`          BIGINT    NOT NULL            COMMENT '外键 sys_user.id',
    `parent_id`        BIGINT    DEFAULT NULL        COMMENT '父评论ID',
    `reply_to_user_id` BIGINT    DEFAULT NULL        COMMENT '回复目标用户ID',
    `content`          TEXT      NOT NULL            COMMENT '评论内容',
    `status`           INT       DEFAULT 0           COMMENT '0=待审核 1=已通过 2=已拒绝',
    `ip_address`       VARCHAR(50) DEFAULT NULL      COMMENT '评论者IP',
    `create_time`      DATETIME  DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`          TINYINT   DEFAULT 0           COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论';

CREATE TABLE IF NOT EXISTS `blog_like` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `article_id`  BIGINT   NOT NULL COMMENT '外键 blog_article.id',
    `user_id`     BIGINT   NOT NULL COMMENT '外键 sys_user.id',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_user` (`article_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞';

CREATE TABLE IF NOT EXISTS `sys_media` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `filename`       VARCHAR(200) NOT NULL            COMMENT '原始文件名',
    `path`           VARCHAR(500) NOT NULL            COMMENT '存储相对路径',
    `url`            VARCHAR(500) NOT NULL            COMMENT '访问 URL',
    `file_size`      BIGINT       DEFAULT NULL        COMMENT '字节数',
    `mime_type`      VARCHAR(100) DEFAULT NULL        COMMENT 'MIME 类型',
    `upload_user_id` BIGINT       DEFAULT NULL        COMMENT '上传者',
    `media_type`     VARCHAR(20)  NOT NULL DEFAULT 'CONTENT' COMMENT 'CONTENT|AVATAR|PRIVATE',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `deleted`        TINYINT      DEFAULT 0           COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体资源';

CREATE TABLE IF NOT EXISTS `site_config` (
    `id`                BIGINT       NOT NULL,
    `site_name`         VARCHAR(100) DEFAULT NULL COMMENT '博客名称',
    `site_subtitle`     VARCHAR(200) DEFAULT NULL COMMENT '副标题',
    `site_logo`         VARCHAR(500) DEFAULT NULL COMMENT 'Logo URL',
    `site_favicon`      VARCHAR(500) DEFAULT NULL COMMENT 'Favicon URL',
    `about_content_md`  LONGTEXT     DEFAULT NULL COMMENT '关于页 Markdown',
    `about_content_html` LONGTEXT    DEFAULT NULL COMMENT '关于页渲染 HTML',
    `author_name`       VARCHAR(50)  DEFAULT NULL COMMENT '作者名',
    `author_avatar`     VARCHAR(500) DEFAULT NULL COMMENT '作者头像',
    `author_bio`        VARCHAR(500) DEFAULT NULL COMMENT '作者简介',
    `social_github`     VARCHAR(200) DEFAULT NULL,
    `social_twitter`    VARCHAR(200) DEFAULT NULL,
    `social_zhihu`      VARCHAR(200) DEFAULT NULL,
    `icp_number`        VARCHAR(50)  DEFAULT NULL COMMENT '备案号',
    `footer_text`       VARCHAR(200) DEFAULT NULL COMMENT '页脚文字',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点配置';

INSERT IGNORE INTO `site_config` (`id`, `site_name`, `site_subtitle`, `author_name`)
VALUES (1, 'Hedgehog Blog', 'A personal tech blog', 'Hedgehog');
