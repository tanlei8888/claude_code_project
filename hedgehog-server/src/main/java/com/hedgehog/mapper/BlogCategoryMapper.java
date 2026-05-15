package com.hedgehog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hedgehog.entity.BlogCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类数据访问层。
 */
@Mapper
public interface BlogCategoryMapper extends BaseMapper<BlogCategory> {
}
