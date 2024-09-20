package com.mysite.sbb.admin.apiUsage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailyApiUsageRepository extends JpaRepository<DailyApiUsage, Long> {
    DailyApiUsage findByDate(LocalDate date);
}
