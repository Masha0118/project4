package com.mysite.sbb.myproduct;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.sbb.mycategory.MyCategory;
import org.springframework.stereotype.Repository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
//	List<Product> findByCategoryAndId(MyCategory category);

	Product findByCategory_idAndId(Long categoryId, Long productId);
	
	List<Product> findByCategory_id(Long categoryId);

	List<Product> findTop5ByOrderByAddTimeDesc();

	List<Product> findByCategory(MyCategory category);}
