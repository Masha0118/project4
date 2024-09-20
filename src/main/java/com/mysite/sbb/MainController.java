package com.mysite.sbb;

import java.security.Principal;
import java.util.List;

import com.mysite.sbb.admin.bestProduct.BestProduct;
import com.mysite.sbb.admin.bestProduct.BestProductService;
import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryService;
import com.mysite.sbb.myhistory.History;
import com.mysite.sbb.myhistory.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mysite.sbb.myproduct.Product;
import com.mysite.sbb.myproduct.ProductRepository;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    UserService userService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MyCategoryService myCategoryService;
    @Autowired
    private BestProductService bestProductService;
    @Autowired
    private HistoryService historyService;

    @Value("${server.servlet.context-path:}") private String contextPath;

    @GetMapping("/")
    public String root(Model model, Principal principal) {
        if (principal != null) {
            SiteUser user = userService.getUser(principal.getName());
            model.addAttribute("principal", principal);
            model.addAttribute("username", user.getId());
        }
        return "redirect:home";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        if (principal != null) {
            SiteUser user = userService.getUser(principal.getName());
            model.addAttribute("principal", principal);
            model.addAttribute("username", user.getId());
        }

        List<Product> products = productRepository.findTop5ByOrderByAddTimeDesc();
        model.addAttribute("products", products);

        // Log each product's link value
        for (Product product : products) {
//            System.out.println("Product ID: " + product.getId() + ", Link: " + product.getLink());
        }

        Page<MyCategory> categories = myCategoryService.getCategoriesWithProducts(PageRequest.of(0, 10));//페이지 사이즈로 위시리스트 개수 제한
        model.addAttribute("categories", categories.getContent());

        List<BestProduct> topSharedProducts = bestProductService.getTopSharedProducts(5);
        model.addAttribute("topSharedProducts", topSharedProducts);
//		System.out.println("contextPath : " + contextPath);
		model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);

        return "homePage/home";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myhistory")
    public String myHistory(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "kw", defaultValue = "") String kw, Principal principal) {

        if (principal != null) {
            SiteUser user = userService.getUser(principal.getName());
            Page<History> paging = this.historyService.getList(page, kw);
            model.addAttribute("paging", paging);
            model.addAttribute("kw", kw);
            model.addAttribute("principal", principal);
            model.addAttribute("username", user.getId());
        }
        return "cscenterPage/";
    }

    @GetMapping("/product")
    public String product(Model model, Principal principal) {
        if (principal != null) {
            SiteUser user = userService.getUser(principal.getName());
            model.addAttribute("principal", principal);
            model.addAttribute("username", user.getId());
        }
        return "product";
    }

    @GetMapping("/myshopping")
    public String myShopping(Model model, Principal principal) {
        SiteUser user = userService.getUser(principal.getName());
        model.addAttribute("principal", principal);
        model.addAttribute("username", user.getId());
        return "myshopping";
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model, Principal principal) {
        if (principal != null) {
            SiteUser user = userService.getUser(principal.getName());
            model.addAttribute("principal", principal);
            model.addAttribute("username", user.getId());
        }
        return "portfolio";
    }


//	@GetMapping("/cscenter")
//	public String cscenter() {
//		return "cscenter";
//	}


}
