package com.hedgehog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hedgehog.entity.SiteConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站点配置数据访问层。
 */
@Mapper
public interface SiteConfigMapper extends BaseMapper<SiteConfig> {
}
