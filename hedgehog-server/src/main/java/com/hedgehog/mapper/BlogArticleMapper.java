package com.hedgehog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hedgehog.entity.BlogArticle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogArticleMapper extends BaseMapper<BlogArticle> {
}
