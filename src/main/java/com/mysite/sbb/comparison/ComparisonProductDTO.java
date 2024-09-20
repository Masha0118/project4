package com.mysite.sbb.comparison;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComparisonProductDTO {

    private Long productId;
    private String title;
    private String image;
    private Long price;
    private String link;
}
