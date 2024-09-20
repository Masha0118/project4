package com.mysite.sbb.myproduct;

import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MyCategoryRepository categoryRepository;

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    public Product changefindByCategory_idAndId(Long categoryId, Long productId) {
        Product product = productRepository.findByCategory_idAndId(categoryId, productId);
//        System.out.println(product.getState());
        if (product.getState().equals("private")) {
            product.setState("public");
        } else {
            product.setState("private");
        }
        productRepository.save(product);
//        System.out.println("이후" + product.getState());
        return productRepository.save(product);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategory_id(categoryId);
    }

    @Transactional
    public void moveProductToCategory(Long productId, Long droppedCategoryId) {
        if (droppedCategoryId == null) {
            throw new IllegalArgumentException("Dropped Category ID must not be null");
        }

        MyCategory newCategory = categoryRepository.findById(droppedCategoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setCategory(newCategory);
        productRepository.save(product);
    }

    public List<Product> getProductsByCategory(MyCategory category) {
        return productRepository.findByCategory(category);
    }
}
