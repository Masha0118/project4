package com.mysite.sbb.admin.bestProduct;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BestProductRequest {
    private String productId;
    private String title;
    private String image;
    private String link;

}
