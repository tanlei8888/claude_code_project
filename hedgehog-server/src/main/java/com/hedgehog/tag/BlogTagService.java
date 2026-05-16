package com.hedgehog.tag;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 标签服务接口。
 */
public interface BlogTagService extends IService<BlogTag> {
    /**
     * 获取全部标签列表。
     */
    List<BlogTag> listAll();

    /**
     * 创建标签，slug 为空时由名称自动生成拼音。
     */
    void save(TagSaveRequest request);

    /**
     * 更新标签。
     */
    void update(TagSaveRequest request);

    /**
     * 删除标签。
     */
    void delete(Long id);
}
