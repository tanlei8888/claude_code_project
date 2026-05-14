package com.hedgehog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.common.ResultCode;
import com.hedgehog.config.UserContext;
import com.hedgehog.entity.SysMedia;
import com.hedgehog.exception.BusinessException;
import com.hedgehog.mapper.SysMediaMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class SysMediaServiceImpl extends ServiceImpl<SysMediaMapper, SysMedia> implements SysMediaService {

    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml");
    private static final long MAX_SIZE = 10 * 1024 * 1024;

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    @Override
    public SysMedia upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ResultCode.UNSUPPORTED_FILE_TYPE);
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED);
        }
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        File dir = new File(uploadPath, dateDir);
        if (!dir.exists()) dir.mkdirs();
        String ext = getExtension(file.getOriginalFilename());
        String newFilename = UUID.randomUUID().toString() + ext;
        File dest = new File(dir, newFilename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
        String relativePath = dateDir + "/" + newFilename;
        SysMedia media = new SysMedia();
        media.setFilename(file.getOriginalFilename());
        media.setPath(relativePath);
        media.setUrl("/api/admin/upload/" + relativePath);
        media.setFileSize(file.getSize());
        media.setMimeType(file.getContentType());
        media.setUploadUserId(UserContext.getUserId());
        baseMapper.insert(media);
        return media;
    }

    @Override
    public IPage<SysMedia> page(int page, int size) {
        return baseMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<SysMedia>().orderByDesc(SysMedia::getCreateTime));
    }

    @Override
    public void delete(Long id) {
        SysMedia media = super.getById(id);
        if (media == null) {
            throw new BusinessException(ResultCode.MEDIA_NOT_FOUND);
        }
        // 删除物理文件
        File file = new File(uploadPath, media.getPath());
        if (file.exists()) file.delete();
        super.removeById(id);
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return i >= 0 ? filename.substring(i) : "";
    }
}
