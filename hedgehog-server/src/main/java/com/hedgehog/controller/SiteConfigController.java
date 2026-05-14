package com.hedgehog.controller;

import com.hedgehog.annotation.AdminRequired;
import com.hedgehog.common.Result;
import com.hedgehog.entity.SiteConfig;
import com.hedgehog.service.SiteConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    public SiteConfigController(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    @GetMapping("/site/config")
    public Result<?> get() {
        return Result.ok(siteConfigService.get());
    }

    @AdminRequired
    @PutMapping("/admin/site/config")
    public Result<?> update(@RequestBody SiteConfig config) {
        siteConfigService.update(config);
        return Result.ok();
    }
}
