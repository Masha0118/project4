package com.mysite.sbb.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mysite.sbb.cart.Cart;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long productId;
    private String title;
    private String link;
    private String image;
    private Long lprice;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;
}
