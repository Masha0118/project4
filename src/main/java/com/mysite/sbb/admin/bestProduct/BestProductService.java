package com.mysite.sbb.admin.bestProduct;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BestProductService {

    private final BestProductRepository bestProductRepository;

    @Transactional
    public BestProduct addOrUpdateProduct(String productId, String title, String image, String link) {
        return bestProductRepository.findByProductId(productId)
                .map(product -> {
                    product.setCount(product.getCount() + 1); // 중복된 제품이 발견되면 카운트를 증가시킴
                    return bestProductRepository.save(product);
                })
                .orElseGet(() -> {
                    BestProduct newProduct = new BestProduct();
                    newProduct.setProductId(productId);
                    newProduct.setTitle(title);
                    newProduct.setImage(image);
                    newProduct.setLink(link);
                    return bestProductRepository.save(newProduct);
                });
    }

    public List<BestProduct> getTopSharedProducts(int limit) {
        return bestProductRepository.findTopSharedProducts(PageRequest.of(0, limit));
    }
    public List<BestProduct> getTopSharedProducts() {
        return getTopSharedProducts(100);  // 기본값 5개로 설정
    }
}