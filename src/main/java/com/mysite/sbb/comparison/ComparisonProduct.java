package com.mysite.sbb.comparison;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ComparisonProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private String title;
    private String image;
    private Long price;
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comparison_history_id", nullable = false)
    private ComparisonHistory comparisonHistory;
}
