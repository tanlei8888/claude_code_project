package com.hedgehog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.SysMedia;
import org.springframework.web.multipart.MultipartFile;

/**
 * 媒体资源服务接口。
 */
public interface SysMediaService extends IService<SysMedia> {
    /**
     * 上传文件，校验类型和大小后保存到本地 uploads/ 目录。
     *
     * @param file 上传的文件
     * @return 保存后的媒体记录
     */
    SysMedia upload(MultipartFile file);

    /**
     * 分页查询媒体列表。
     */
    IPage<SysMedia> page(int page, int size);

    /**
     * 删除媒体记录，同时删除物理文件。
     */
    void delete(Long id);
}
