package com.mysite.sbb.admin.apiUsage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ApiUsageDto {
    private LocalDate date;
    private Long count;
}
