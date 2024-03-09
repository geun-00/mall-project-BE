package com.example.mallapi.repository;

import com.example.mallapi.domain.Product;
import com.example.mallapi.dto.PageRequestDTO;
import com.example.mallapi.repository.search.ProductSearch;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    EntityManager em;

    @Autowired
    ProductSearch productSearch;

    @BeforeEach
    void test() {
        for (int i = 1; i <= 10; i++) {
            Product product = Product.builder()
                    .pName("Test name " + i)
                    .pDesc("Test Desc " + i)
                    .price(1000)
                    .build();
            product.addImageString(UUID.randomUUID().toString().substring(0, 8) + "_IMAGE1.jpg");
            product.addImageString(UUID.randomUUID().toString().substring(0, 8) + "_IMAGE2.jpg");

            productRepository.save(product);
        }
    }

    /**
     * @EntityGraph 미적용
     * 지연 로딩 발생
     */
    @Transactional
    @Test
    void testRead1() {
        Product result = productRepository.findById(1L).orElseThrow();

        log.info("result = {}", result);
        log.info("result = {}", result.getImageList());
    }

    /**
     * @EntityGraph 적용
     */
    @Test
    void testRead2() {
        Optional<Product> product = productRepository.selectOne(1L);
        if (product.isPresent()) {
            Product result = product.get();
            log.info("result = {}", result);
            log.info("result = {}", result.getImageList());
        }
    }

    @Test
    void testDelete() {
        productRepository.updateToDelete(true, 1L);
    }

    @Test
//    @Commit //쿼리 확인
    void testUpdate() {
        Optional<Product> result = productRepository.selectOne(1L);
        if (result.isPresent()) {
            Product product = result.get();

            assertThat(product.getImageList().size()).isEqualTo(2);

            product.changePrice(3000);
            product.clearImage();
            assertThat(product.getImageList().size()).isEqualTo(0);

            product.addImageString(UUID.randomUUID().toString().substring(0, 8) + "_IMAGE1.jpg");
            product.addImageString(UUID.randomUUID().toString().substring(0, 8) + "_IMAGE2.jpg");
            product.addImageString(UUID.randomUUID().toString().substring(0, 8) + "_IMAGE3.jpg");
            assertThat(product.getImageList().size()).isEqualTo(3);

            em.flush();
        }
    }

    @Test
    void testList() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("pno").descending());
        // when
        Page<Object[]> result = productRepository.selectList(pageable);
        // then
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
    }

    @Test
    void testSearch() {
        // given
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
        // when
        productSearch.searchList(pageRequestDTO);
        // then
    }
}