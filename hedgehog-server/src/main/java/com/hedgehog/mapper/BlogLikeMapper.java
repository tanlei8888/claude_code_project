package com.hedgehog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hedgehog.entity.BlogLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞数据访问层。
 */
@Mapper
public interface BlogLikeMapper extends BaseMapper<BlogLike> {
}
