package com.mysite.sbb.mycategory;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.sbb.wishlist.Wishlist;
import com.mysite.sbb.wishlist.WishlistRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class MyCategoryController {

    private final MyCategoryRepository categoryRepository;
    private final WishlistRepository wishlistRepository;

	/*
	 * @PostMapping("/{wishlistId}") public MyCategory createCategory(@PathVariable
	 * Long wishlistId, @RequestBody MyCategory category) {
	 * category.setWishlistId(wishlistId); category.setState("private"); return
	 * categoryRepository.save(category); }
	 */
    @Transactional
    @PostMapping("/{wishlistId}")
    public MyCategory createCategory(@PathVariable Long wishlistId, @RequestBody MyCategory category, Principal principal) {

        // 위시리스트 객체를 데이터베이스에서 조회
        Wishlist wishlist = wishlistRepository.getById(wishlistId);

//        System.out.println("확인입니다.");
        // 카테고리의 상태를 설정
        category.setState("private");
        
        // 위시리스트와 카테고리의 양방향 관계 설정
        category.setWishlist(wishlist); // 카테고리에 위시리스트 설정
        wishlist.getMycategories().add(category); // 위시리스트에 카테고리 추가
//        System.out.println(principal.getName());
        category.setUsername(principal.getName());
        // 카테고리 저장
        MyCategory savedCategory = categoryRepository.save(category);
        
        // 위시리스트 저장 (필요한 경우)
        wishlistRepository.save(wishlist);

        return savedCategory;
    }

    @GetMapping("/{wishlistId}")
    public List<MyCategory> getCategories(@PathVariable Long wishlistId) {
        return categoryRepository.findAllByWishlistId(wishlistId);
    }
}