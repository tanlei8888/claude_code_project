package com.hedgehog.like;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞数据访问层。
 */
@Mapper
public interface BlogLikeMapper extends BaseMapper<BlogLike> {
}
