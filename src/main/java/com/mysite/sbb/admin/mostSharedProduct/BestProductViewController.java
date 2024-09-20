package com.mysite.sbb.admin.mostSharedProduct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/bestproduct")
public class BestProductViewController {
	
	 @Value("${server.servlet.context-path:}")
	 private String contextPath;

    @GetMapping("/analysis")
    public String viewTopSharedProducts(Model model) {
    	
//		System.out.println("contextPath : " + contextPath);
		model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
    	
        return "adminPage/most_shared_products"; // 생성한 HTML 파일의 경로
    }
}
