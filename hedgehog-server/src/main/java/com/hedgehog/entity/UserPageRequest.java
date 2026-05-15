package com.hedgehog.entity;

import lombok.Data;

/**
 * 用户分页查询请求 DTO。
 */
@Data
public class UserPageRequest {
    /** 页码，默认第 1 页 */
    private Integer page = 1;
    /** 每页条数，默认 10 */
    private Integer size = 10;
    /** 按用户名或昵称模糊搜索 */
    private String keyword;
}
