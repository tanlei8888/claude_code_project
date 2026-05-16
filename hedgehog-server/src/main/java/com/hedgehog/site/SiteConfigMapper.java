package com.hedgehog.site;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站点配置数据访问层。
 */
@Mapper
public interface SiteConfigMapper extends BaseMapper<SiteConfig> {
}
