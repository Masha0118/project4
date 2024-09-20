package com.mysite.sbb.admin.apiUsage;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class ApiUsageResetScheduler {
    private final ApiCallLogRepository apiCallLogRepository;
    private final DailyApiUsageRepository dailyApiUsageRepository;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void resetApiUsage() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDate today = now.toLocalDate();
//        System.out.println("Resetting API usage logs at: " + now);

        long totalCallsToday = apiCallLogRepository.count();

        DailyApiUsage dailyApiUsage = new DailyApiUsage(today, totalCallsToday);
        dailyApiUsageRepository.save(dailyApiUsage);

        apiCallLogRepository.deleteAll();
    }
}
