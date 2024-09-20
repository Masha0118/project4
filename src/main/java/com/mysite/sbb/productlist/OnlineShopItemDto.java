package com.mysite.sbb.productlist;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Getter
@Setter
public class OnlineShopItemDto {
    private String title;
    private String link;
    private String image;
    private int lprice;
    private Long productId;
    private String category3;

    public OnlineShopItemDto(JSONObject itemJson) {
        this.title = itemJson.getString("title");
        this.link = itemJson.getString("link");
        this.image = itemJson.getString("image");
        this.lprice = itemJson.getInt("lprice");
        this.productId = itemJson.getLong("productId");
    }
}
