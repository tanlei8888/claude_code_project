package com.hedgehog.entity;

import lombok.Data;

@Data
public class CategorySaveRequest {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Integer sortOrder;
}
