package com.hedgehog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hedgehog.entity.SysMedia;
import org.springframework.web.multipart.MultipartFile;

public interface SysMediaService extends IService<SysMedia> {
    SysMedia upload(MultipartFile file);
    IPage<SysMedia> page(int page, int size);
    void delete(Long id);
}
