package com.mysite.sbb.cart;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public String showCartPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/user/login";
        }
        
        SiteUser currentUser = userService.getUser(principal.getName());
        Cart userCart = cartService.getCartByUser(currentUser);

        if (userCart == null) {
            userCart = cartService.createNewCartForUser(currentUser); // 장바구니 생성
        }

        model.addAttribute("cartId", userCart.getId()); // 모델에 cartId 추가
        model.addAttribute("items", userCart.getItems());
        model.addAttribute("username",currentUser.getId());

        return "cartPage/cart";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<CartItem> addProductToCart(@RequestBody CartItem cartItem, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SiteUser currentUser = userService.getUser(principal.getName());

        try {
            CartItem addedItem = cartService.addProductToCart(currentUser, cartItem);
            return ResponseEntity.ok(addedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/products/{itemId}")
    @ResponseBody
    public ResponseEntity<?> deleteProductFromCart(@PathVariable("itemId") Long itemId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SiteUser currentUser = userService.getUser(principal.getName());
        System.out.println("Received delete request for item ID: " + itemId + " by user: " + currentUser.getUsername());

        try {
            cartService.removeProductFromCart(currentUser, itemId);
            System.out.println("Successfully deleted cart item with ID: " + itemId);
            return ResponseEntity.ok("Product removed from cart");
        } catch (RuntimeException e) {
            System.err.println("Failed to delete cart item with ID: " + itemId + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred while removing the cart item with ID: " + itemId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing the product");
        }
    }

    // 상품 수량 업데이트
    @PutMapping("/api/items/{itemId}")
    @ResponseBody
    public ResponseEntity<?> updateProductQuantity(@PathVariable("itemId") Long itemId, @RequestBody Map<String, Integer> updateRequest, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SiteUser currentUser = userService.getUser(principal.getName());
        Integer newQuantity = updateRequest.get("quantity");

        try {
            cartService.updateProductQuantity(currentUser, itemId, newQuantity);
            return ResponseEntity.ok("Product quantity updated");
        } catch (RuntimeException e) {
            System.err.println("Failed to update quantity for cart item with ID: " + itemId + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred while updating the cart item with ID: " + itemId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the product quantity");
        }
    }

    @GetMapping("/api/items")
    @ResponseBody
    public ResponseEntity<List<CartItem>> getCartItems(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SiteUser currentUser = userService.getUser(principal.getName());
        Cart userCart = cartService.getCartByUser(currentUser);

        if (userCart == null) {
            return ResponseEntity.ok(new ArrayList<>()); // 장바구니가 없을 경우 빈 리스트 반환
        }

        return ResponseEntity.ok(userCart.getItems());
    }
}
