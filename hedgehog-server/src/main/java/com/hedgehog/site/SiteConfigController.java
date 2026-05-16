package com.hedgehog.site;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 站点配置控制器，处理公开获取和管理端更新站点配置。
 */
@Tag(name = "站点配置", description = "站点名称/关于页/社交链接配置")
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
