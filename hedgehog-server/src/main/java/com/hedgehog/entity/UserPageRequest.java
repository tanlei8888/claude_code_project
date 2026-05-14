package com.hedgehog.entity;

import lombok.Data;

@Data
public class UserPageRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
}
