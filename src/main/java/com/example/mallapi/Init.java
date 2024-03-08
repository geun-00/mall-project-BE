package com.example.mallapi;

import com.example.mallapi.domain.Todo;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

@Profile("local")
@Component
@RequiredArgsConstructor
public class Init {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.init();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        private final Random random = new Random();
        public void init() {

            for (int i = 1; i <= 500; i++) {
                Todo todo = Todo.builder()
                                .title("TITLE - " + i)
                                .content("CONTENT - " + i)
                                .complete(i % 2 == 0)
                                .dueDate(generateRandomDate())
                                .build();
                em.persist(todo);
            }
        }
        private LocalDate generateRandomDate() {
            // 최대 30일까지 랜덤으로 더함
            return LocalDate.now().plusDays(random.nextInt(30));
        }
    }
}
