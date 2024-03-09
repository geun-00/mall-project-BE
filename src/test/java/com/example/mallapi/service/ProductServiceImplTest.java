package com.example.mallapi.service;

import com.example.mallapi.dto.PageRequestDTO;
import com.example.mallapi.dto.PageResponseDTO;
import com.example.mallapi.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ProductServiceImplTest {

    @Autowired ProductService productService;

    @Test
    void testList() {
        // given
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build(); //기본 page=1, 기본 size=10
        // when
        PageResponseDTO<ProductDTO> responseDTO = productService.getList(pageRequestDTO);

        // then
        log.info("responseDTO = {}", responseDTO.getDtoList());
    }

    @Test
    void testRegister() {
        // given
        ProductDTO productDTO = ProductDTO.builder()
                                          .pName("새로운 상품")
                                          .pDesc("신규 추가 상품입니다.")
                                          .price(1000)
                                          .build();

        productDTO.setUploadedFileNames(
                List.of(
                        UUID.randomUUID().toString().substring(0, 8) + "_Test1.jpg",
                        UUID.randomUUID().toString().substring(0, 8) + "_Test2.jpg"
                ));
        // when
        productService.register(productDTO);
        // then
    }

}