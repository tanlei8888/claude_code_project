package com.hedgehog.category;

import lombok.Data;

/**
 * 分类保存请求 DTO，创建和编辑共用。
 */
@Data
public class CategorySaveRequest {
    /** 分类 ID，编辑时必传，创建时为空 */
    private Long id;
    /** 分类名称 */
    private String name;
    /** URL 标识，为空时由名称自动生成拼音 slug */
    private String slug;
    /** 分类描述 */
    private String description;
    /** 排序值，越小越靠前 */
    private Integer sortOrder;
}
