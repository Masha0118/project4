package com.mysite.sbb.admin.adminwishlist;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysite.sbb.myproduct.Product;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import com.mysite.sbb.wishlist.WishlistService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/adminwishlist")
@RequiredArgsConstructor
public class AdminWishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    
    // 위시리스트 통계 페이지
    @GetMapping("/statistics-page")
    public String statisticsPage(Model model) {
    	
    	List<SiteUser> users=userService.getAll();
    	model.addAttribute("users",users);
//    	System.out.println("contextPath : " + contextPath);
    	model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
        return "adminPage/statistics_ad";
    }

    // 특정 사용자의 위시리스트에 있는 상품의 카테고리별 통계를 반환
    @GetMapping("/statistics/{userId}")
    @ResponseBody
    public Map<String, Long> getWishlistStatisticsByUser(@PathVariable("userId") Long userId) {
        // 사용자별 위시리스트 카테고리 통계 정보 (주어진 사용자 ID에 대한 위시리스트의 카테고리별 항목 수를 반환)
        return wishlistService.getCategoryWishlistStatisticsByUser(userId);
    }

    // 전체 사용자의 위시리스트 상세 목록을 카테고리별로 반환
    @GetMapping("/details")
    @ResponseBody
    public Map<String, List<Product>> getWishlistDetails() {
        return wishlistService.getCategoryWishlistDetails();
    }

    // 특정 사용자의 위시리스트 상세 목록을 카테고리별로 반환
    @GetMapping("/details/{userId}")
    @ResponseBody
    public Map<String, List<Product>> getWishlistDetailsByUser(@PathVariable("userId") Long userId) {
        return wishlistService.getCategoryWishlistDetailsByUser(userId);
    }

    @DeleteMapping("/{userId}/categories/{categoryId}")
    public ResponseEntity<?> deleteUserCategory(@PathVariable("userId") Long userId, @PathVariable("categoryId") String categoryId) {
        try {
            wishlistService.removeCategoryForUser(userId, categoryId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    // 카테고리와 관련된 모든 상품 삭제 API
//    @DeleteMapping("/category/delete/{userId}/{encodedCategoryName}")
//    @ResponseBody
//    public ResponseEntity<String> deleteCategoryAndProducts(@PathVariable("userId") Long userId, @PathVariable("encodedCategoryName") String encodedCategoryName) {
//        // URL 디코딩하여 원래 카테고리 이름을 가져옵니다.
//        String categoryName = URLDecoder.decode(encodedCategoryName, StandardCharsets.UTF_8);
//
//        // userId를 통해 wishlistId 가져오기
//        Long wishlistId = adminWishlistService.getWishlistIdByUserId(userId);
//
//        // 카테고리 이름으로 해당 카테고리 삭제
//        adminWishlistService.deleteCategoryByName(wishlistId, categoryName);
//
//        return ResponseEntity.ok("Category and related products deleted successfully");
//    }

}
