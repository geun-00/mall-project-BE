package com.example.mallapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageList")
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    private String pName;
    private int price;
    private String pDesc;

    private boolean delFlag;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    public void changeName(String pName) {
        this.pName = pName;
    }

    public void changePrice(int price) {
        this.price = price;
    }

    public void changeDesc(String pDesc) {
        this.pDesc = pDesc;
    }

    public void changeDel(boolean delFlag) {
        this.delFlag = delFlag;
    }

    public void addImage(ProductImage image) {
        image.setOrd(imageList.size());
        imageList.add(image);
    }

    public void addImageString(String fileName) {
        ProductImage productImage = ProductImage.builder()
                                                .fileName(fileName)
                                                .build();
        addImage(productImage);
    }

    public void clearImage() {
        imageList.clear();
    }
}