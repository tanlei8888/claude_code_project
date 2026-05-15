package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.entity.SiteConfig;
import com.hedgehog.service.SiteConfigService;
import org.springframework.web.bind.annotation.*;

/**
 * 站点配置控制器，处理公开获取和管理端更新站点配置。
 */
@RestController
@RequestMapping("/api")
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    public SiteConfigController(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    /** GET /api/site/config — 获取站点配置（公开） */
    @GetMapping("/site/config")
    public Result<?> get() {
        return Result.ok(siteConfigService.get());
    }

    /** PUT /api/admin/site/config — 更新站点配置（管理员） */
    @AdminRequired
    @PutMapping("/admin/site/config")
    public Result<?> update(@RequestBody SiteConfig config) {
        siteConfigService.update(config);
        return Result.ok();
    }
}
