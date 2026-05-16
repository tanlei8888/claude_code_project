package com.hedgehog.comment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论数据访问层。
 */
@Mapper
public interface BlogCommentMapper extends BaseMapper<BlogComment> {
}
