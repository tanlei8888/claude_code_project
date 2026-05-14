package com.hedgehog.config;

import com.hedgehog.service.BlogArticleService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledTask {

    private final BlogArticleService articleService;

    public ScheduledTask(BlogArticleService articleService) {
        this.articleService = articleService;
    }

    @Scheduled(fixedRate = 60000)
    public void publishScheduledArticles() {
        articleService.publishScheduled();
    }
}
