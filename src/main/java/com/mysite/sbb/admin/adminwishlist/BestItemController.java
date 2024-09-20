package com.mysite.sbb.admin.adminwishlist;

import com.mysite.sbb.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class BestItemController {

    private final WishlistService wishlistService;

    // 베스트 아이템 통계 페이지 (미완성)
    @GetMapping("/best-item-page")
    public String bestItemPage(Model model) {
        List<Map<String, Object>> statistics = wishlistService.getProductWishlistStatistics();
        model.addAttribute("statistics", statistics);
//        System.out.println("Best Item Page Data: " + statistics);  // 로그 추가
        return "adminPage/best_item_ad";
    }
}
