package com.mysite.sbb.comparison;

import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class ComparisonHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser user;

    private LocalDateTime comparisonDate;

    @OneToMany(mappedBy = "comparisonHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComparisonProduct> products = new ArrayList<>();;
}
