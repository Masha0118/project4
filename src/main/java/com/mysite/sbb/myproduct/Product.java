package com.mysite.sbb.myproduct;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.wishlist.Wishlist;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long productId;
    private String title;
    private Long lprice;
    private String link;
    private String image;

    private String state;
    
    @Getter
    @Transient
    private Long categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private MyCategory category;

    @Setter
    @ManyToOne
    @JoinColumn(name = "wishlist_id")
    private Wishlist wishlist;

    private LocalDateTime addTime;

    public Product() {
        this.addTime = LocalDateTime.now();
    }
}
