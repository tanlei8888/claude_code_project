package com.hedgehog.config;

import com.hedgehog.article.BlogArticleService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置。
 *
 * <p>启用 Spring 定时任务调度，每分钟检查一次是否有需要自动发布的文章。
 * 定时发布：文章 status=2（定时发布）且 publish_time ≤ 当前时间时，自动将状态改为 1（已发布）。
 */
@Configuration
@EnableScheduling
public class ScheduledTask {

    private final BlogArticleService articleService;

    public ScheduledTask(BlogArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 每分钟执行一次定时发布检查。
     */
    @Scheduled(fixedRate = 60000)
    public void publishScheduledArticles() {
        articleService.publishScheduled();
    }
}
