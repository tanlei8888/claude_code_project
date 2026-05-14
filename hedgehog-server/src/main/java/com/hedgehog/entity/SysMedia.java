package com.hedgehog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_media")
public class SysMedia {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String filename;
    private String path;
    private String url;
    private Long fileSize;
    private String mimeType;
    private Long uploadUserId;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
