package com.mysite.sbb.productlist;



import java.security.Principal;
import java.util.List;

import com.mysite.sbb.admin.logs.KeywordStatistics;
import com.mysite.sbb.admin.logs.KeywordStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryService;
import com.mysite.sbb.myhistory.HistoryService;
import com.mysite.sbb.notice.NoticeService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import com.mysite.sbb.wishlist.Wishlist;
import com.mysite.sbb.wishlist.WishlistService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/productlist")
@RequiredArgsConstructor
@Controller
public class ProductListController {

	
	private final UserService userService;
	private final WishlistService wishlistService;
	private final MyCategoryService myCategoryService;
	private final KeywordStatisticsService keywordStatisticsService;
	
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private final HistoryService historyService;
    private final NoticeService noticeService;
    private final NaverShopApi naverShopApi;

    @GetMapping()

    public String mainPage(Model model, Principal principal, @RequestParam(value = "keyword", required = false) String keyword) {
    	
    	if(principal != null) {
	    	SiteUser user =  userService.getUser(principal.getName());
//	    	Wishlist wishlist = wishlistService.getWishlistsByUser(user);
	    	Wishlist wishlist = user.getWishlist();
	    	List<MyCategory> myCategorys = myCategoryService.getMyCategorysById(wishlist.getId());
	    	model.addAttribute("username",user.getId());
	    	model.addAttribute("principal",principal);
	    	model.addAttribute("myCategorys",myCategorys);
//	    	System.out.println(myCategorys.size());

			// 검색어 로그 기록
			if (keyword != null && !keyword.isEmpty()) {
				keywordStatisticsService.logKeyword(keyword, user);
			}
		}

		// 검색어 통계 데이터를 모델에 추가
//    	System.out.println("contextPath : " + contextPath);
    	model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
		List<KeywordStatistics> keywordStatisticsList = keywordStatisticsService.getAllKeywordStatistics();
		model.addAttribute("keywordStatisticsList", keywordStatisticsList);
		return "product_list";

//=======
//    public String mainPage(Model model, Principal principal) {
//        if (principal != null) {
//            SiteUser user = userService.getUser(principal.getName());
//            model.addAttribute("username", user.getId());
//        } else {
//            model.addAttribute("username", "Guest");
//        }
//        model.addAttribute("principal", principal);
//
//>>>>>>> 00756469192fd2a7edf3a61aa463e311dde589cf
//        String keyword = naverShopApi.search("의류", 50);
//        List<OnlineShopItemDto> itemDtos = naverShopApi.fromJSONtoItems(keyword);
//        model.addAttribute("itemDtos", itemDtos);


    }

//    @GetMapping("/home/notice")
//    public String noticePage() {
//        return "notice_page";
//    }
//
//    @GetMapping("/search")
//    public String productList(Model model, @RequestParam("keyword") String keyword) {
//        String searchKeyword = naverShopApi.search(keyword, 30);
//        List<OnlineShopItemDto> itemDtos = naverShopApi.fromJSONtoItems(searchKeyword);
//        model.addAttribute("itemDtos", itemDtos);
//        return "product_list";
//    }
}
