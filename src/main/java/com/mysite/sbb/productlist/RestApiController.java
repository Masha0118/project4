package com.mysite.sbb.productlist;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/productlists")
public class RestApiController {

    private final NaverShopApi naverShopApi;

    @GetMapping
    public List<OnlineShopItemDto> getProductList(@RequestParam("keyword") String keyword, @RequestParam("quantity") int quantity) {

        String searchKeyword = naverShopApi.search(keyword, quantity);
//        System.out.println("searchKeyword = " + searchKeyword);
        return naverShopApi.fromJSONtoItems(searchKeyword);
    }


}
