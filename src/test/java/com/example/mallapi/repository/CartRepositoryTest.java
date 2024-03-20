package com.example.mallapi.repository;

import com.example.mallapi.domain.Cart;
import com.example.mallapi.domain.CartItem;
import com.example.mallapi.domain.Member;
import com.example.mallapi.domain.Product;
import com.example.mallapi.dto.CartItemListDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class CartRepositoryTest {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;

    @Test
    void testListOfMember() {
        // given
        String email = "user1@aaa.com";
        // when
        List<CartItemListDTO> result = cartItemRepository.getItemOfCartDTOByEmail(email);
        // then

        for (CartItemListDTO cartItemListDTO : result) {
            log.info("cartItemListDTO = {}", cartItemListDTO);
        }
    }

    @Commit
    @Test
    void testInsertByProduct() {
        // given
        String email = "user1@aaa.com";
        Long pno = 2L;
        int qty = 5;

        // when
        //이메일, 상품번호로 장바구니 아이템 확인 후 없으면 추가, 있으면 수량 변경해서 저장
        CartItem cartItem = cartItemRepository.getItemOfPno(email, pno);
        Cart cart;
        if (cartItem != null) {
            cartItem.changeQty(qty);
            cartItemRepository.save(cartItem);
            return;
        }

        //장바구니 자체가 없을 수도 잇음
        //사용자의 장바구니에 장바구니 아이템 만들어서 저장
        Optional<Cart> result = cartRepository.getCartOfMember(email);
        if (result.isEmpty()) {
            Member member = Member.builder().email(email).build();
            Cart tempCart = Cart.builder().owner(member).build();
            cart = cartRepository.save(tempCart);
        } else { //장바구니는 있으나 해당 상품의 장바구니 아이템이 없는 경우
            cart = result.get();
        }

//        if (cartItem == null) {
        Product product = Product.builder().pno(pno).build();
        cartItem = CartItem.builder().cart(cart).product(product).qty(qty).build();
//        }

        cartItemRepository.save(cartItem);


        // then
    }

    @Commit
    @Test
    void testUpdateByCino() {
        Long cino = 1L;
        int qty = 3;

        Optional<CartItem> result = cartItemRepository.findById(cino);
        CartItem cartItem = result.orElseThrow();

        cartItem.changeQty(qty);
    }

    @Commit
    @Test
    void testDeleteThenList() {
        Long cino = 1L;
        Long cno = cartItemRepository.getCartFromItem(cino);

        cartItemRepository.deleteById(cino);

        List<CartItemListDTO> result = cartItemRepository.getItemsOfCartDTOByCart(cno);
        for (CartItemListDTO cartItemListDTO : result) {
            log.info("cartItemListDTO = {}", cartItemListDTO);
        }
    }
}