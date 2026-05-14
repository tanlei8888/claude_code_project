package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.service.SysMediaService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class MediaController {

    private final SysMediaService mediaService;

    public MediaController(SysMediaService mediaService) {
        this.mediaService = mediaService;
    }

    @AdminRequired
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        return Result.ok(mediaService.upload(file));
    }

    @AdminRequired
    @GetMapping("/media")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "12") int size) {
        return Result.ok(mediaService.page(page, size));
    }

    @AdminRequired
    @DeleteMapping("/media/{id}")
    public Result<?> delete(@PathVariable Long id) {
        mediaService.delete(id);
        return Result.ok();
    }
}
