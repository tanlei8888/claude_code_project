package com.hedgehog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hedgehog.entity.BlogTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签数据访问层。
 */
@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTag> {
}
