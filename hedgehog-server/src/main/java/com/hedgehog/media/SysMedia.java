package com.hedgehog.media;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 媒体资源实体，对应 sys_media 表。
 */
@Data
@TableName("sys_media")
public class SysMedia {
    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 原始文件名 */
    private String filename;
    /** 存储相对路径 */
    private String path;
    /** 访问 URL */
    private String url;
    /** 文件大小（字节） */
    private Long fileSize;
    /** MIME 类型 */
    private String mimeType;
    /** 上传者用户 ID */
    private Long uploadUserId;
    /** 媒体类型：CONTENT=文章内容 AVATAR=用户头像 PRIVATE=私密 */
    private String mediaType;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 逻辑删除：0=未删除 1=已删除 */
    @TableLogic
    private Integer deleted;
}
