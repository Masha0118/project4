package com.mysite.sbb.comparison;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComparisonProductResponse {

    private Long productId;
    private String title;
    private String image;
    private Long price;
    private String link;

    public ComparisonProductResponse(ComparisonProduct product) {
        this.productId = product.getProductId();
        this.title = product.getTitle();
        this.image = product.getImage();
        this.price = product.getPrice();
        this.link = product.getLink();
    }
}
