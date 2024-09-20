package com.mysite.sbb.wishlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryRepository;
import com.mysite.sbb.myproduct.Product;
import com.mysite.sbb.myproduct.ProductRepository;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final MyCategoryRepository myCategoryRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    // 특정 사용자의 위시리스트를 조회하는 메서드
    public Wishlist getWishlistsByUser(SiteUser user) {
        // 주어진 사용자의 위시리스트를 데이터베이스에서 조회
        return wishlistRepository.findByUser(user);
    }

    // 위시리스트에서 새로운 카테고리를 추가하는 메서드
    public MyCategory addCategoryToWishlist(Long wishlistId, MyCategory category) {
        category.setWishlistId(wishlistId); // 카테고리 객체에 위시리스트 ID를 설정
        category.setState("private");   // 카테고리 상태를 "private"으로 설정
        return myCategoryRepository.save(category); // 새로운 카테고리를 데이터베이스에 저장
    }

    // 위시리스트에서 카테고리를 삭제하고, 해당 카테고리의 상품을 기본 카테고리로 이동시키는 메서드 (미완성)
    @Transactional
    public List<Product> removeCategoryFromWishlist(Long wishlistId, Long categoryId) {
        // 삭제할 카테고리를 조회
        MyCategory category = myCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 기본 카테고리는 삭제할 수 없도록 예외 처리
        if ("기본 카테고리".equals(category.getName())) {
            throw new RuntimeException("기본 카테고리는 삭제할 수 없습니다.");
        }

        // 기본 카테고리를 조회
        MyCategory basicCategory = myCategoryRepository.findByNameAndWishlistId("기본 카테고리", wishlistId)
                .orElseThrow(() -> new RuntimeException("기본 카테고리를 찾을 수 없습니다."));

        // 삭제할 카테고리의 상품들을 기본 카테고리로 이동
        List<Product> products = category.getProducts();
        for (Product product : products) {
            product.setCategory(basicCategory);
            productRepository.save(product);    // 상품을 기본 카테고리로 업데이트
        }

        // 삭제할 카테고리를 데이터베이스에서 제거
        myCategoryRepository.delete(category);
        return products;
    }

    // 위시리스트에서 특정 상품을 삭제하는 메서드
    public void deleteProduct(Long productId) {
        // 삭제할 상품을 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);  // 상품을 데이터베이스에서 삭제
    }

    // 특정 카테고리에 새로운 상품을 추가하는 메서드
    public Product addProductToCategory(Long categoryId, Product product) {
        // 상품을 추가할 카테고리를 조회
        MyCategory category = myCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 해당 카테고리에 동일한 상품이 이미 존재하는지 확인
        boolean exists = category.getProducts().stream()
                .anyMatch(p -> p.getProductId().equals(product.getProductId()));

        // 상품이 이미 존재하면 예외를 던짐
        if (exists) {
            throw new RuntimeException("Product already exists in this category");
        }

        // 상품을 카테고리에 추가하고 저장
        product.setCategory(category);
        return productRepository.save(product);
    }

    // 특정 카테고리에 특정 상품이 존재하는지 확인하는 메서드
    public boolean isProductInCategory(Long categoryId, Long productId) {
        // 확인할 카테고리를 조회
        MyCategory category = myCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        // 해당 카테고리에 상품이 존재하는지 여부를 확인
        return category.getProducts().stream().anyMatch(product -> product.getProductId().equals(productId));
    }

    // 모든 사용자의 위시리스트에 있는 카테고리별 상품 수 통계를 계산하는 메서드 (추후 사용 예정)
    public Map<String, Long> getCategoryWishlistStatistics() {
        List<MyCategory> categories = myCategoryRepository.findAll();   // 모든 카테고리를 조회
        // 카테고리별로 상품 수를 계산하여 Map으로 반환
        return categories.stream()
                .collect(Collectors.groupingBy(MyCategory::getName, Collectors.summingLong(category -> category.getProducts().size())));
    }

    // 특정 사용자의 위시리스트에 있는 모든 카테고리를 조회하는 메서드
    public List<MyCategory> getCategoriesByUserId(Long userId) {
        SiteUser user = userService.getUserById(userId);    // 주어진 ID로 사용자를 조회
        Wishlist wishlist = wishlistRepository.findByUser(user);    // 사용자의 위시리스트를 조회
        if (wishlist == null) {
            throw new RuntimeException("Wishlist not found");
        }
        // 위시리스트에 속한 모든 카테고리를 조회하여 반환
        return myCategoryRepository.findAllByWishlistId(wishlist.getId());
    }

    // 특정 사용자의 위시리스트에 있는 카테고리별 상품 수 통계를 계산하는 메서드
    public Map<String, Long> getCategoryWishlistStatisticsByUser(Long userId) {
        List<MyCategory> categories = getCategoriesByUserId(userId);    // 특정 사용자의 카테고리 목록을 조회
        // 카테고리별로 상품 수를 계산하여 Map으로 반환
        return categories.stream()
                .collect(Collectors.groupingBy(MyCategory::getName, Collectors.summingLong(category -> category.getProducts().size())));
    }

    // 특정 카테고리를 ID로 조회하는 메서드
    public MyCategory getCategoryById(Long id) {
        return myCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // 모든 위시리스트를 조회하는 메서드
    public List<Wishlist> getAll(){
/*    	List<Wishlist> wishlists =  wishlistRepository.findAll();
    	return wishlists;*/
        return wishlistRepository.findAll();
    }

    // 모든 사용자의 위시리스트에 있는 카테고리별 상품 목록을 조회하는 메서드
    public Map<String, List<Product>> getCategoryWishlistDetails() {
        List<MyCategory> categories = myCategoryRepository.findAll();   // 모든 카테고리를 조회
        // 카테고리별로 상품 목록을 그룹화하여 Map으로 반환
        return categories.stream()
                .collect(Collectors.groupingBy(MyCategory::getName,
                        Collectors.flatMapping(category -> category.getProducts().stream(), Collectors.toList())));
    }

    // 특정 사용자의 위시리스트에 있는 카테고리별 상품 목록을 조회하는 메서드
    public Map<String, List<Product>> getCategoryWishlistDetailsByUser(Long userId) {
        List<MyCategory> categories = getCategoriesByUserId(userId);    // 특정 사용자의 카테고리 목록을 조회
        // 카테고리별로 상품 목록을 그룹화하여 Map으로 반환
        return categories.stream()
                .collect(Collectors.groupingBy(MyCategory::getName,
                        Collectors.flatMapping(category -> category.getProducts().stream(), Collectors.toList())));
    }

    // 사용자 ID로 카테고리 목록을 조회하는 메서드
    public List<MyCategory> getCategories(Long userId) {
        return getCategoriesByUserId(userId);
    }
    
    // ID로 위시리스트를 조회하는 메서드
    public Wishlist getWishlistById(Long wishlistId){
    	return wishlistRepository.getById(wishlistId);
    	
    }

    // 위시리스트를 저장하는 메서드
    public Wishlist saveWishlist(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    // 상품별 누적 찜 수 통계를 조회하는 메서드
    public List<Map<String, Object>> getProductWishlistStatistics() {
        // 상품별로 누적된 찜 수를 조회하는 쿼리를 실행
        List<Object[]> results = wishlistRepository.findProductCounts();
        List<Map<String, Object>> productCounts = new ArrayList<>();

        // 쿼리 결과를 바탕으로 각 상품의 정보와 누적 찜 횟수를 Map으로 저장
        for (Object[] result : results) {
            Long productId = (Long) result[0];
            String title = (String) result[1];
            Long lprice = (Long) result[2];
            String link = (String) result[3];
            String image = (String) result[4];
            Long count = (Long) result[5];

            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("productId", productId);
            productInfo.put("title", title);
            productInfo.put("lprice", lprice);
            productInfo.put("link", link);
            productInfo.put("image", image);
            productInfo.put("count", count);

            productCounts.add(productInfo);
        }

        // 수량(count)이 많은 순으로 정렬
        productCounts.sort((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")));

        // 순위 부여
        int rank = 1;
        for (Map<String, Object> product : productCounts) {
            product.put("rank", rank++);
        }

//        System.out.println("Product Wishlist Statistics: " + productCounts);
        return productCounts;
    }

    // 페이징 처리된 모든 위시리스트를 조회하는 메서드
    public Page<Wishlist> getAll(Pageable pageable) {
        return wishlistRepository.findAll(pageable);
    }

    @Transactional
    public void removeCategoryForUser(Long userId, String categoryName) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자의 위시리스트를 찾을 수 없습니다."));

        MyCategory category = myCategoryRepository.findByNameAndWishlistId(categoryName, wishlist.getId())
                .orElseThrow(() -> new RuntimeException("해당 카테고리를 찾을 수 없습니다."));

        if ("기본 카테고리".equals(category.getName())) {
            throw new RuntimeException("기본 카테고리는 삭제할 수 없습니다.");
        }

        // 기본 카테고리 가져오기
        MyCategory basicCategory = myCategoryRepository.findByNameAndWishlistId("기본 카테고리", wishlist.getId())
                .orElseThrow(() -> new RuntimeException("기본 카테고리를 찾을 수 없습니다."));

        // 삭제할 카테고리의 상품들을 기본 카테고리로 이동
        List<Product> products = category.getProducts();
        for (Product product : products) {
            product.setCategory(basicCategory);
            productRepository.save(product);  // 상품을 기본 카테고리로 업데이트
        }

        // 카테고리를 데이터베이스에서 제거
        myCategoryRepository.delete(category);
    }

}
