package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.repository.DailyPickSelectionRepository;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DailyPickSchedule {
    private static final Logger logger = LoggerFactory.getLogger(DailyPickSchedule.class);

    private final DailyPickSelectionRepository repository;
    private final DailyPickSelectionService selections;

    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Taipei")
    public void refreshDailyPicks() {
        var date = DailyPickSelectionService.selectionDateAt(Instant.now());
        for (Long userId : repository.findUsersWithOlderSelections(date)) {
            try {
                // select 會鎖定使用者並檢查日期。同時開啟 App 也只會更新一次。
                selections.select(userId, false, null);
            } catch (RuntimeException error) {
                logger.warn("無法更新使用者 {} 的今日拾藏", userId, error);
            }
        }
    }
}
