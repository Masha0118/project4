package com.mysite.sbb.mycategory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mysite.sbb.myproduct.Product;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MyCategoryService {

	private final MyCategoryRepository myCategoryRepository;
	private final UserRepository userRepository;

	public List<MyCategory> getMyCategorysById(long id) {
		return myCategoryRepository.findAllByWishlistId(id);
	}

	public List<Product> getProductsByCategoryIdAndWishlistId(Long wishlistId, Long categoryId) {
		return myCategoryRepository.findByIdAndWishlistId(categoryId, wishlistId);
	}

	public MyCategory saveCategory(MyCategory category) {
		return myCategoryRepository.save(category);
	}

	public Page<MyCategory> getCategoriesWithProducts(Pageable pageable) {
		// 모든 카테고리 가져오기
		List<MyCategory> allCategories = myCategoryRepository.findAll();

		// 카테고리 리스트를 순회하며 정렬 및 필터링 수행
		List<MyCategory> sortedCategories = allCategories.stream()
				.map(category -> {
					// 사용자 정보 설정
					Long userId = category.getUser() != null ? category.getUser().getId() : null;
					String userName = category.getUsername();
					if (userName != null) {
						SiteUser user = userRepository.findByUsername(userName)
								.orElse(new SiteUser()); // 사용자 정보가 없을 경우 기본 사용자 설정
						category.setUsername(user.getUsername());
					} else {
						category.setUsername("Unknown User");
					}

					// 'public' 상태의 제품만 필터링 후 정렬하여 상위 4개 가져오기
					List<Product> top4PublicProducts = category.getProducts() == null ?
							Collections.emptyList() : category.getProducts().stream()
							.filter(product -> "public".equals(product.getState())) // 'public' 상태 필터링
							.sorted((p1, p2) -> {
								LocalDateTime addTime1 = p1.getAddTime() != null ? p1.getAddTime() : LocalDateTime.MIN;
								LocalDateTime addTime2 = p2.getAddTime() != null ? p2.getAddTime() : LocalDateTime.MIN;
								return addTime2.compareTo(addTime1);
							})
							.limit(4) // 상위 4개 제품만 가져오기
							.collect(Collectors.toList());
					category.setProducts(top4PublicProducts);
					return category;
				})
				// 카테고리를 각 카테고리의 최신 제품 추가 시간 기준으로 정렬
				.sorted((c1, c2) -> {
					LocalDateTime latestTimeC1 = c1.getProducts().stream()
							.map(Product::getAddTime)
							.filter(addTime -> addTime != null)
							.max(LocalDateTime::compareTo)
							.orElse(LocalDateTime.MIN);

					LocalDateTime latestTimeC2 = c2.getProducts().stream()
							.map(Product::getAddTime)
							.filter(addTime -> addTime != null)
							.max(LocalDateTime::compareTo)
							.orElse(LocalDateTime.MIN);

					return latestTimeC2.compareTo(latestTimeC1); // 최신순으로 정렬
				})
				.collect(Collectors.toList());

		// 페이징 처리
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), sortedCategories.size());
		List<MyCategory> pagedCategories = sortedCategories.subList(start, end);

		// 페이지 객체 반환
		return new PageImpl<>(pagedCategories, pageable, sortedCategories.size());
	}


	public List<MyCategory> getAll() {
		return myCategoryRepository.findAll();
	}
}
