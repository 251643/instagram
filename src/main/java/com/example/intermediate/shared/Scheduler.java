package com.example.intermediate.shared;


import com.example.intermediate.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final ArticleService articleService;

    @Scheduled(cron = "0 0 1 * * *")
    public void removeImage() {
        articleService.removeS3Image();
    }
}