package com.mysite.sbb.admin.apiUsage;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DailyApiUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Long totalCalls;

    public DailyApiUsage(LocalDate date, Long totalCalls) {
        this.date = date;
        this.totalCalls = totalCalls;
    }
}
