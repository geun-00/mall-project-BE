package com.example.mallapi.config;

import com.example.mallapi.repository.search.ProductSearch;
import com.example.mallapi.repository.search.ProductSearchImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QueryDSLConfig {

    private final EntityManager em;

    @Bean
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public ProductSearch productSearch() {
        return new ProductSearchImpl(queryFactory());
    }
}