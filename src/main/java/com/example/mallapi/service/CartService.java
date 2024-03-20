package com.example.mallapi.service;

import com.example.mallapi.dto.CartItemDTO;
import com.example.mallapi.dto.CartItemListDTO;

import java.util.List;

public interface CartService {

    List<CartItemListDTO> addOrModify(CartItemDTO cartItemDTO);
    List<CartItemListDTO> getCartItems(String email);
    List<CartItemListDTO> remove(Long cino);
}
