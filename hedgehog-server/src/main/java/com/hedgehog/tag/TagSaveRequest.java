package com.hedgehog.tag;

import lombok.Data;

/**
 * 标签保存请求 DTO，创建和编辑共用。
 */
@Data
public class TagSaveRequest {
    /** 标签 ID，编辑时必传，创建时为空 */
    private Long id;
    /** 标签名称 */
    private String name;
    /** URL 标识，为空时由名称自动生成拼音 slug */
    private String slug;
}
