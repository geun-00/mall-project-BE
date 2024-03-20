package com.example.mallapi.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"product", "cart"})
@Table(name = "tbl_cart_item", indexes = {
        @Index(columnList = "cart_cno", name = "idx_cartitem_cart"),
        @Index(columnList = "product_pno, cart_cno", name = "idx_cartitem_pno_cart")
})
public class CartItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_pno")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_cno")
    private Cart cart;

    private int qty;

    public void changeQty(int qty) {
        this.qty = qty;
    }
}
