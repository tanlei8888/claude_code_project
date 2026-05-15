package com.hedgehog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hedgehog.entity.BlogArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章-标签关联数据访问层。
 */
@Mapper
public interface BlogArticleTagMapper extends BaseMapper<BlogArticleTag> {
}
