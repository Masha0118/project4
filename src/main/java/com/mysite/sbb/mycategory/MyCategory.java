package com.mysite.sbb.mycategory;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysite.sbb.myproduct.Product;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.wishlist.Wishlist;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty("name")
    private String name;

    private Long wishlistId;

    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<Product> products;

    private String state;

    @ManyToOne
    @JoinColumn(name = "wishlist")
    @JsonBackReference
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private SiteUser user;

//    @Transient // Ensure this field is not persisted in the database
    private String username;
}

