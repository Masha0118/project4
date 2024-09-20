package com.mysite.sbb.admin.publicsettings;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryService;
import com.mysite.sbb.wishlist.Wishlist;
import com.mysite.sbb.wishlist.WishlistService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/admin/publicSettings")
@Controller
public class PublicSettingsController {
	private final MyCategoryService mycategoryService;
	private final WishlistService wishlistService;
	
    @Value("${server.servlet.context-path:}")
    private String contextPath;
	
	@GetMapping()
	public String wishlistManagementPage(@RequestParam(name = "page", defaultValue = "0") int page, 
		    @RequestParam(name = "size", defaultValue = "3") int size,  Model model) {
		
			List<MyCategory> mycategorys = mycategoryService.getAll();
		    List<Wishlist> wishlists =  wishlistService.getAll();
		    
		    int publicCount = 0;
		    int privateCount = 0;
		    
		    for (MyCategory mycategory : mycategorys) {
		        if ("public".equals(mycategory.getState())) {
		            publicCount++;
		        } else if ("private".equals(mycategory.getState())) {
		            privateCount++;
		        }
		    }
		    
		    int total = publicCount + privateCount;
		    
//		    System.out.println("contextPath : " + contextPath);
			model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
		    model.addAttribute("publicCount",publicCount);
		    model.addAttribute("privateCount",privateCount);
		    model.addAttribute("total",total);

	        Pageable pageable = PageRequest.of(page, size);
	        Page<Wishlist> wishlistsPage = wishlistService.getAll(pageable);
	        model.addAttribute("wishlistsPage", wishlistsPage);
		    model.addAttribute("wishlists",wishlists);
		    model.addAttribute("mycategorys",mycategorys);
		    return "adminPage/wishlist_management";
	}
}
