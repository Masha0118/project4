package com.mysite.sbb.comparison;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ComparisonHistoryResponse {

    private Long id;
    private LocalDateTime comparisonDate;
    private List<ComparisonProductResponse> products;

    public ComparisonHistoryResponse(ComparisonHistory history) {
        this.id = history.getId();
        this.comparisonDate = history.getComparisonDate();
        this.products = history.getProducts() != null ?
                history.getProducts().stream()
                        .map(ComparisonProductResponse::new)
                        .collect(Collectors.toList())
                : new ArrayList<>();
    }
}
