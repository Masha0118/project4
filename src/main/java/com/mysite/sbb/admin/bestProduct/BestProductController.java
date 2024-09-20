package com.mysite.sbb.admin.bestProduct;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/bestproduct")
@RequiredArgsConstructor
public class BestProductController {

    private final BestProductService bestProductService;

    @PostMapping("/add")
    public ResponseEntity<BestProduct> addProduct(@RequestBody BestProductRequest productRequest) {
        BestProduct product = bestProductService.addOrUpdateProduct(productRequest.getProductId(), productRequest.getTitle(), productRequest.getImage(), productRequest.getLink());
        return ResponseEntity.ok(product); // JSON 응답 반환
    }

    @GetMapping("/top_shared")
    public List<BestProduct> getTopSharedProducts() {
        return bestProductService.getTopSharedProducts();
    }
}