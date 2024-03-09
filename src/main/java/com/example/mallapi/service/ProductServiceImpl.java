package com.example.mallapi.service;


import com.example.mallapi.domain.Product;
import com.example.mallapi.domain.ProductImage;
import com.example.mallapi.dto.PageRequestDTO;
import com.example.mallapi.dto.PageResponseDTO;
import com.example.mallapi.dto.ProductDTO;
import com.example.mallapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        //Object[] 0번째 product, 1번째 productImage
        List<ProductDTO> dtoList = result.get().map(arr -> {
            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];

            ProductDTO productDTO = ProductDTO.builder()
                                              .pno(product.getPno())
                                              .pName(product.getPName())
                                              .price(product.getPrice())
                                              .pDesc(product.getPDesc())
                                              .build();

            String imageStr = productImage.getFileName();
            productDTO.setUploadedFileNames(List.of(imageStr));

            return productDTO;
        }).toList();

        long totalContent = result.getTotalElements();

        return PageResponseDTO.<ProductDTO>withAll()
                              .dtoList(dtoList)
                              .total(totalContent)
                              .pageRequestDTO(pageRequestDTO)
                              .build();
    }
}