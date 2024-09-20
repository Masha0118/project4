package com.mysite.sbb.wishlist;

import com.mysite.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("SELECT p.productId, MAX(p.title), MAX(p.lprice), MAX(p.link), MAX(p.image), COUNT(p) FROM Product p GROUP BY p.productId")
    List<Object[]> findProductCounts();

    //    List<Wishlist> findByUser(SiteUser user);
    Wishlist findByUser(SiteUser user);

    Optional<Wishlist> findByUserId(Long userId);
}
