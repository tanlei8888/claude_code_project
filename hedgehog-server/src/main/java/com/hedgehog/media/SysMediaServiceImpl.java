package com.hedgehog.media;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedgehog.common.ResultCode;
import com.hedgehog.config.AppProperties;
import com.hedgehog.config.UserContext;
import com.hedgehog.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 媒体资源服务实现。
 */
@Slf4j
@Service
public class SysMediaServiceImpl extends ServiceImpl<SysMediaMapper, SysMedia> implements SysMediaService {

    /** 允许上传的文件类型 */
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml");
    /** 单文件最大大小：10MB */
    private static final long MAX_SIZE = 10 * 1024 * 1024;

    private final AppProperties appProperties;

    public SysMediaServiceImpl(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public SysMedia upload(MultipartFile file, String mediaType) {
        String uploadPath = appProperties.getUpload().getPath();
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
        // 校验文件类型
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ResultCode.UNSUPPORTED_FILE_TYPE);
        }
        // 校验文件大小
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED);
        }
        // 按日期分子目录：yyyy/MM
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        File dir = new File(uploadPath, dateDir);
        // 确保上传目录存在，若创建失败则抛出异常
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("无法创建上传目录: {}", dir.getAbsolutePath());
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
        // 生成唯一文件名
        String ext = getExtension(file.getOriginalFilename());
        String newFilename = UUID.randomUUID().toString() + ext;
        File dest = new File(dir, newFilename);
        try {
            // transferTo 内部使用 getPath() 取路径，相对路径会被 Tomcat 拼到临时工作目录，必须传绝对路径
            file.transferTo(dest.getAbsoluteFile());
        } catch (IOException e) {
            log.error("文件写入失败: dest={}, size={}", dest.getAbsolutePath(), file.getSize(), e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
        String relativePath = dateDir + "/" + newFilename;
        SysMedia media = new SysMedia();
        media.setFilename(file.getOriginalFilename());
        media.setPath(relativePath);
        // URL 格式与 WebConfig 资源映射对应
        media.setUrl("/api/admin/upload/" + relativePath);
        media.setFileSize(file.getSize());
        media.setMimeType(file.getContentType());
        media.setUploadUserId(UserContext.getUserId());
        // 使用上传时指定的媒体类型，未指定则默认为 CONTENT
        media.setMediaType(mediaType != null ? mediaType : "CONTENT");
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
        File file = new File(appProperties.getUpload().getPath(), media.getPath());
        if (file.exists()) file.delete();
        super.removeById(id);
    }

    /**
     * 获取文件扩展名（含点号）。
     */
    private String getExtension(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return i >= 0 ? filename.substring(i) : "";
    }

    @Override
    public List<SysMedia> getAvatars() {
        return list(new LambdaQueryWrapper<SysMedia>()
                .eq(SysMedia::getMediaType, "AVATAR")
                .orderByDesc(SysMedia::getCreateTime));
    }
}
