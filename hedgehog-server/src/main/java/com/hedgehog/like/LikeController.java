package com.hedgehog.like;

import com.hedgehog.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 点赞控制器，处理点赞/取消点赞和批量查询点赞状态。
 */
@Tag(name = "点赞管理", description = "文章点赞/取消、点赞状态查询")
@RestController
@RequestMapping("/api")
public class LikeController {

    private final BlogLikeService likeService;

    public LikeController(BlogLikeService likeService) {
        this.likeService = likeService;
    }

    /** POST /api/likes/{articleId} — 点赞/取消点赞（toggle，需登录） */
    @PostMapping("/likes/{articleId}")
    public Result<?> toggle(@PathVariable Long articleId) {
        boolean liked = likeService.toggle(articleId);
        return Result.ok(liked);
    }

    /** GET /api/likes/status — 批量查询点赞状态（需登录） */
    @GetMapping("/likes/status")
    public Result<?> status(@RequestParam String articleIds) {
        Set<Long> ids = Arrays.stream(articleIds.split(","))
                .map(Long::parseLong).collect(Collectors.toSet());
        return Result.ok(likeService.getLikeStatus(ids));
    }
}
