package com.mysite.sbb.mycategory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.sbb.myproduct.Product;
import org.springframework.data.jpa.repository.Query;

public interface MyCategoryRepository extends JpaRepository<MyCategory, Long> {

	List<MyCategory> findAllByWishlistId(Long id);

	Optional<MyCategory> findByNameAndWishlistId(String name, Long wishlistId);

	List<Product> findByIdAndWishlistId(Long id,Long wishlistId);
	
//	List<MyCategory> findByWishlist(Long wishlistId);
@Query("SELECT c FROM MyCategory c WHERE c.products IS NOT EMPTY ORDER BY c.id ASC")
Page<MyCategory> findCategoriesWithProducts(Pageable pageable);

	Optional<MyCategory> findByWishlistIdAndName(Long wishlistId, String name);
}
