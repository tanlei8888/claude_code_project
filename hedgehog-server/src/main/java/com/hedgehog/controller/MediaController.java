package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.service.SysMediaService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 媒体管理控制器，处理文件上传、列表查询和删除。
 */
@RestController
@RequestMapping("/api/admin")
public class MediaController {

    private final SysMediaService mediaService;

    public MediaController(SysMediaService mediaService) {
        this.mediaService = mediaService;
    }

    /** POST /api/admin/upload — 上传文件 */
    @AdminRequired
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        return Result.ok(mediaService.upload(file));
    }

    /** GET /api/admin/media — 分页查询媒体列表 */
    @AdminRequired
    @GetMapping("/media")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "12") int size) {
        return Result.ok(mediaService.page(page, size));
    }

    /** DELETE /api/admin/media/{id} — 删除媒体文件 */
    @AdminRequired
    @DeleteMapping("/media/{id}")
    public Result<?> delete(@PathVariable Long id) {
        mediaService.delete(id);
        return Result.ok();
    }
}
