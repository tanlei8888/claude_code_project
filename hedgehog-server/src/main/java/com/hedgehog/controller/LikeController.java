package com.hedgehog.controller;

import com.hedgehog.common.Result;
import com.hedgehog.service.BlogLikeService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LikeController {

    private final BlogLikeService likeService;

    public LikeController(BlogLikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/likes/{articleId}")
    public Result<?> toggle(@PathVariable Long articleId) {
        boolean liked = likeService.toggle(articleId);
        return Result.ok(liked);
    }

    @GetMapping("/likes/status")
    public Result<?> status(@RequestParam String articleIds) {
        Set<Long> ids = Arrays.stream(articleIds.split(","))
                .map(Long::parseLong).collect(Collectors.toSet());
        return Result.ok(likeService.getLikeStatus(ids));
    }
}
