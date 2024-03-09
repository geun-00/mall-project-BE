package com.example.mallapi.service;

import com.example.mallapi.dto.PageRequestDTO;
import com.example.mallapi.dto.PageResponseDTO;
import com.example.mallapi.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}