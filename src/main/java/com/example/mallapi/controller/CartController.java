package com.example.mallapi.controller;

import com.example.mallapi.dto.CartItemDTO;
import com.example.mallapi.dto.CartItemListDTO;
import com.example.mallapi.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

//    @PreAuthorize("#itemDTO.email == authentication.name")
    @PostMapping("/change")
    public List<CartItemListDTO> changeCart(@RequestBody CartItemDTO itemDTO, Authentication authentication) {
        log.info("itemDTO = {}", itemDTO);
        if (itemDTO.getEmail().equals(authentication.getName())) {
            if (itemDTO.getQty() <= 0) {
                return cartService.remove(itemDTO.getCino());
            } else {
                return cartService.addOrModify(itemDTO);
            }
        }
        log.error("이메일이 맞지 않음");
        return List.of();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/items")
    public List<CartItemListDTO> getCartItems(Principal principal) {
        String email = principal.getName();
        log.info("email = {}", email);

        return cartService.getCartItems(email);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @DeleteMapping("/{cino}")
    public List<CartItemListDTO> removeFromCart(@PathVariable("cino") Long cino) {
        log.info("cino = {}", cino);
        return cartService.remove(cino);
    }
}