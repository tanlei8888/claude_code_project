package com.hedgehog.entity;

import lombok.Data;

@Data
public class TagSaveRequest {
    private Long id;
    private String name;
    private String slug;
}
