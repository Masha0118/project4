package com.mysite.sbb.wishlist;

import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryRepository;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WishlistConfig {

    private final WishlistRepository wishlistRepository;
    private final MyCategoryRepository myCategoryRepository;
    private final UserService userService;

    @Bean
    public ApplicationRunner initializeBasicCategories() {
        return args -> {
            for (SiteUser user : userService.getAll()) {
                Wishlist wishlist = wishlistRepository.findByUser(user);
                if (wishlist != null && !basicCategoryExists(wishlist.getId())) {
                    createBasicCategory(wishlist.getId());
                }
            }
        };
    }

    private boolean basicCategoryExists(Long wishlistId) {
        return myCategoryRepository.findByNameAndWishlistId("기본 카테고리", wishlistId).isPresent();
    }

    private void createBasicCategory(Long wishlistId) {
        MyCategory basicCategory = new MyCategory();
        basicCategory.setName("기본 카테고리");
        basicCategory.setWishlistId(wishlistId);
        basicCategory.setState("private");
        myCategoryRepository.save(basicCategory);
    }
}
