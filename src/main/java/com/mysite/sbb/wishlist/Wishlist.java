package com.mysite.sbb.wishlist;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.myproduct.Product;
import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String name;

    
    @OneToOne
    @JoinColumn(name = "user_id")
    private SiteUser user;

    @JsonManagedReference
    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyCategory> mycategories;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

}
