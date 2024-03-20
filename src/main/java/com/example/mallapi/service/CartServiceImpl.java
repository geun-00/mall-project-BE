package com.example.mallapi.service;

import com.example.mallapi.domain.Cart;
import com.example.mallapi.domain.CartItem;
import com.example.mallapi.domain.Member;
import com.example.mallapi.domain.Product;
import com.example.mallapi.dto.CartItemDTO;
import com.example.mallapi.dto.CartItemListDTO;
import com.example.mallapi.repository.CartItemRepository;
import com.example.mallapi.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository; 
    
    @Override
    public List<CartItemListDTO> addOrModify(CartItemDTO cartItemDTO) {

        String email = cartItemDTO.getEmail();
        Long pno = cartItemDTO.getPno();
        int qty = cartItemDTO.getQty();
        Long cino = cartItemDTO.getCino();

        if (cino != null) { //기존에 담겨 있는 상품에 대한 처리
            Optional<CartItem> cartItemResult = cartItemRepository.findById(cino);
            CartItem cartItem = cartItemResult.orElseThrow();

            cartItem.changeQty(qty);

            return getCartItems(email);
        }

        Cart cart = getCart(email);

        CartItem cartItem;

        cartItem = cartItemRepository.getItemOfPno(email, pno);

        if (cartItem == null) {
            Product product = Product.builder().pno(pno).build();
            cartItem = CartItem.builder()
                                .product(product)
                                .cart(cart)
                                .qty(qty)
                                .build();
            cartItemRepository.save(cartItem);
        } else {
            cartItem.changeQty(qty);
        }

        return getCartItems(email);
    }

    private Cart getCart(String email) {
        //해당 email의 장바구니(Cart)가 있는지 확인, 있으면 반환
        //없으면 Cart 객체 생성하고 추가 반환

        Cart cart;

        Optional<Cart> result = cartRepository.getCartOfMember(email);
        if (result.isEmpty()) {
            log.info("Cart of the member is not exist!!");

            Member member = Member.builder().email(email).build();
            Cart tempCart = Cart.builder().owner(member).build();

            cart = cartRepository.save(tempCart);
        } else {
            cart = result.get();
        }

        return cart;
    }

    @Override
    public List<CartItemListDTO> getCartItems(String email) {
        return cartItemRepository.getItemOfCartDTOByEmail(email);
    }

    @Override
    public List<CartItemListDTO> remove(Long cino) {
        Long cno = cartItemRepository.getCartFromItem(cino);
        cartItemRepository.deleteById(cino);

        return cartItemRepository.getItemsOfCartDTOByCart(cno);
    }
}
