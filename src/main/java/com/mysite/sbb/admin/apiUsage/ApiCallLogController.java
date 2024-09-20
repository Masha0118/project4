package com.mysite.sbb.admin.apiUsage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class ApiCallLogController {
    private final ApiCallLogRepository apiCallLogRepository;

    @GetMapping("/apiusage")
    public String getApiUsage(Model model) {
        List<ApiUsageDto> dailyApiUsage  = apiCallLogRepository.findAll().stream()
                .collect(Collectors.groupingBy(log -> log.getCallTime().toLocalDate(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new ApiUsageDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // 시간대별 그룹화
        Map<LocalTime, Long> hourlyApiUsage = apiCallLogRepository.findAll().stream()
                .collect(Collectors.groupingBy(log -> log.getCallTime().toLocalTime().withMinute(0).withSecond(0).withNano(0), Collectors.counting()));
        List<HourlyApiUsageDto> hourlyApiUsageDtoList = hourlyApiUsage.entrySet().stream()
                .map(entry -> new HourlyApiUsageDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        model.addAttribute("dailyApiUsage", dailyApiUsage);
        model.addAttribute("hourlyApiUsage", hourlyApiUsageDtoList);
        return "adminPage/api_usage";
    }
}
