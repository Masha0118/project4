package com.mysite.sbb.myproduct;

import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/wishlist/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final MyCategoryRepository categoryRepository;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        MyCategory category = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
//        System.out.println("Product created: " + product); // 추가된 로그
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping("/{categoryId}")
    @ResponseBody
    public List<Product> getProducts(@PathVariable Long categoryId) {
        MyCategory category = categoryRepository.findById(categoryId).orElseThrow();
        return category.getProducts();
    }

    @GetMapping("/detail")
    public String getProductDetail(@RequestParam("productId") Long productId, Model model) {
        // 상품 상세 정보를 가져오는 로직 (예: 서비스 호출)
        Product product = productService.getProductById(productId);

        // 모델에 상품 정보를 추가
        model.addAttribute("image", product.getImage());
        model.addAttribute("title", product.getTitle());
        model.addAttribute("lprice", product.getLprice());
        model.addAttribute("link", product.getLink());

        return "product_detail"; // product_detail.html 템플릿으로 이동
    }


    @PostMapping("/move")
    @ResponseBody
    public ResponseEntity<?> moveProduct(@RequestBody MoveProductRequest request) {
        if (request.getDroppedCategoryId() == null || request.getProductId() == null) {
            return ResponseEntity.badRequest().body("{\"error\": \"droppedCategoryId and productId are required\"}");
        }

        try {
//            System.out.println("Moving product ID: " + request.getProductId() + " to category ID: " + request.getDroppedCategoryId());
            productService.moveProductToCategory(request.getProductId(), request.getDroppedCategoryId());
            return ResponseEntity.ok("{\"message\": \"Product moved successfully\"}");
        } catch (Exception e) {
            e.printStackTrace(); // 에러 스택 트레이스를 로그에 출력
            return ResponseEntity.status(500).body("{\"error\": \"Error moving product: " + e.getMessage() + "\"}");
        }
    }

    @Getter
    public static class MoveProductRequest {
        private Long droppedCategoryId;
        private Long productId;

        public void setDroppedCategoryId(Long droppedCategoryId) {
            this.droppedCategoryId = droppedCategoryId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }
    }

}