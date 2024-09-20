package com.mysite.sbb.admin.apiUsage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class HourlyApiUsageDto {
    private LocalTime time;
    private Long count;
}
