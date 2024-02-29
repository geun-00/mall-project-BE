package com.example.mallapi.repository;

import com.example.mallapi.domain.Todo;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Log4j2
class TodoRepositoryTest {

    @Autowired TodoRepository todoRepository;

    @Test
    void test1() {
        Assertions.assertNotNull(todoRepository);
        log.info(todoRepository.getClass().getName());
    }

    @Test
    void testInsert() {
        Todo todo = Todo.builder()
                        .title("Title")
                        .content("Content...")
                        .dueDate(LocalDate.of(2024, 2, 29))
                        .build();

        Todo result = todoRepository.save(todo);

        assertThat(todo).isEqualTo(result);
        log.info("result={}", result);
    }

    @Test
    void testRead() {
        Long tno = 1L;
        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        log.info("todo={}", todo);
        assertThat(todo).isNotNull();
    }

    @Test
    void testUpdate() {
        Long tno = 1L;
        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        todo.changeTitle("Update Title");
        todo.changeContent("Update Content");
        todo.changeComplete(true);

        log.info("result={}", result);
        assertThat(todo.getTitle()).isEqualTo("Update Title");
        assertThat(todo.getContent()).isEqualTo("Update Content");
        assertThat(todo.isComplete()).isTrue();
    }

    @Test
    void testPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("tno").descending());
        Page<Todo> result = todoRepository.findAll(pageable);

        log.info(result.getTotalElements());

        log.info(result.getContent());
    }

    @Test
    void testSearch1() {
        todoRepository.search1();
    }
}