package com.mysite.sbb.productlist;

import com.mysite.sbb.admin.apiUsage.ApiCallLog;
import com.mysite.sbb.admin.apiUsage.ApiCallLogRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverShopApi {


    private final ApiCallLogRepository apiCallLogRepository;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public String search(String keyword, int quantity) {
        String searchKeyword = keyword;

        URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com").path("/v1/search/shop")
                .queryParam("query", searchKeyword)
                .queryParam("display", quantity)
                .queryParam("start", 1)
                .queryParam("sort", "sim")
                .encode().build().toUri();

        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret).build();
        ResponseEntity<String> result = restTemplate.exchange(requestEntity, String.class);

        apiCallLogRepository.save(new ApiCallLog(LocalDateTime.now()));

        return result.getBody();
    }

//    public static void main(String[] args) {
//        NaverShopSearch naverShopSearch = new NaverShopSearch();
//        naverShopSearch.search(keyword);
//    }

    public List<OnlineShopItemDto> fromJSONtoItems(String result) {
        JSONObject rjson = new JSONObject(result);
//        System.out.println("rjson = " + rjson);

        JSONArray items = rjson.getJSONArray("items");
        List<OnlineShopItemDto> itemDtoList = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {
            JSONObject itemJson = (JSONObject) items.get(i);
            OnlineShopItemDto itemDto = new OnlineShopItemDto(itemJson);
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }
}
