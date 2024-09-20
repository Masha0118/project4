package com.mysite.sbb.admin.bestProduct;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BestProductRepository extends JpaRepository<BestProduct, Long> {
    Optional<BestProduct> findByProductId(String productId);
    Optional<BestProduct> findByTitle(String title);

    @Query("SELECT p FROM BestProduct p ORDER BY p.count DESC")
    List<BestProduct> findTopSharedProducts(Pageable pageable);
}
