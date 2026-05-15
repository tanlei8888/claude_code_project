package com.hedgehog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hedgehog.entity.BlogArticle;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章数据访问层。
 */
@Mapper
public interface BlogArticleMapper extends BaseMapper<BlogArticle> {
}
