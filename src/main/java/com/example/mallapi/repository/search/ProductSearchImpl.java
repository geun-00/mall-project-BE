package com.example.mallapi.repository.search;

import com.example.mallapi.domain.Product;
import com.example.mallapi.dto.PageRequestDTO;
import com.example.mallapi.dto.PageResponseDTO;
import com.example.mallapi.dto.ProductDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.example.mallapi.domain.QProduct.product;
import static com.example.mallapi.domain.QProductImage.productImage;

@Slf4j
@RequiredArgsConstructor
public class ProductSearchImpl implements ProductSearch {

    private final JPAQueryFactory query;

    @Override
    public PageResponseDTO<ProductDTO> searchList(PageRequestDTO pageRequestDTO) {
        log.info("-------------searchList-------------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending());


        List<Product> productList = query.selectFrom(product)
                                         .leftJoin(product.imageList, productImage)
                                         .where(productImage.ord.eq(0))
                                         .offset(pageRequestDTO.getPage() - 1)
                                         .limit(pageRequestDTO.getSize())
                                         .fetch();


        long totalCount = query.selectFrom(product)
                               .leftJoin(product.imageList, productImage)
                               .where(productImage.ord.eq(0))
                               .fetchCount();

        return null;
    }
}