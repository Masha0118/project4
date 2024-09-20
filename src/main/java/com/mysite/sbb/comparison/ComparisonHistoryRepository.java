package com.mysite.sbb.comparison;

import com.mysite.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComparisonHistoryRepository extends JpaRepository<ComparisonHistory, Long> {
    List<ComparisonHistory> findByUser(SiteUser user);
}