package com.mysite.sbb.myproduct;

import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;
    private final MyCategoryRepository myCategoryRepository;

    @GetMapping("/api/category/{categoryId}/products")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        MyCategory category = myCategoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        System.out.println("Category: " + category);
        return productService.getProductsByCategory(category);
    }
}
