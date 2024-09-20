package com.mysite.sbb.cart;

import java.util.Optional;

import com.mysite.sbb.user.SiteUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Cart getCartByUser(SiteUser user) {
        if (user == null) {
            throw new IllegalStateException("로그인이 필요합니다."); // 로그인되지 않은 경우 예외 처리
        }
        return cartRepository.findByUser(user)
                .orElseGet(() -> createNewCartForUser(user)); // 장바구니 없을 경우 새로 생성
    }

    public Cart createNewCartForUser(SiteUser user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }

    public CartItem addProductToCart(SiteUser user, CartItem cartItem) {
        Cart cart = getCartByUser(user);

        // 동일한 상품이 이미 장바구니에 있는지 확인
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(cartItem.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + cartItem.getQuantity());
            // 로그 추가
            System.out.println("Updated quantity of existing cart item: " + item.getProductId());
            return cartItemRepository.save(item);
        } else {
            cartItem.setCart(cart);
            // 로그 추가
            System.out.println("Added new cart item: " + cartItem.getProductId());
            return cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeProductFromCart(SiteUser user, Long cartItemId) {
        Cart cart = getCartByUser(user);

        // 장바구니에서 해당 아이템을 찾기
        Optional<CartItem> cartItemOptional = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();

        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cart.getItems().remove(cartItem); // 장바구니에서 아이템을 제거
            cartItemRepository.delete(cartItem); // 데이터베이스에서 아이템을 삭제
            System.out.println("Deleted CartItem: " + cartItem.getId());
        } else {
            throw new RuntimeException("CartItem not found");
        }
    }

    // 새로운 수량 업데이트
    @Transactional
    public void updateProductQuantity(SiteUser user, Long cartItemId, int newQuantity) {
        Cart cart = getCartByUser(user);

        // 장바구니에서 해당 아이템을 찾기
        Optional<CartItem> cartItemOptional = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();

        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem); // 변경된 수량을 저장
            System.out.println("Updated CartItem quantity: " + cartItem.getId());
        } else {
            throw new RuntimeException("CartItem not found");
        }
    }

}
