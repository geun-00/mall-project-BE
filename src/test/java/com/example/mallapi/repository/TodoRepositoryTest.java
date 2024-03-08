package com.example.mallapi.repository;

import com.example.mallapi.domain.Todo;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Log4j2
@Transactional
class TodoRepositoryTest {

    @Autowired TodoRepository todoRepository;

    @BeforeEach
    void insert() {
        for (int i = 1; i <= 100; i++) {
            Todo todo = Todo.builder()
                    .title("Title " + i)
                    .content("Content " + i)
                    .dueDate(LocalDate.now())
                    .build();

            todoRepository.save(todo);
        }
    }
    @Test
    void test() {
        Assertions.assertNotNull(todoRepository);
        log.info(todoRepository.getClass().getName());
    }

    @Test
    void testRead() {
        Todo result = todoRepository.findById(1L).get();

        assertThat(result.getTitle()).isEqualTo("Title 1");
        assertThat(result.getContent()).isEqualTo("Content 1");
        assertThat(result.isComplete()).isFalse();
        assertThat(result.getDueDate()).isToday();
    }

    @Test
//    @Transactional
    void testUpdate() {
        Optional<Todo> result = todoRepository.findById(1L);

        if (result.isPresent()) {
            Todo todo = result.get();
            todo.changeTitle("Update Title");
            todo.changeContent("Update Content");
            todo.changeComplete(true);

            assertThat(todo.getTitle()).isEqualTo("Update Title");
            assertThat(todo.getContent()).isEqualTo("Update Content");
            assertThat(todo.isComplete()).isTrue();
        }
    }

    @Test
    void testPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("tno").descending());
        Page<Todo> result = todoRepository.findAll(pageable);

        assertThat(result.getTotalElements()).isEqualTo(100L);
        assertThat(result.getTotalPages()).isEqualTo(10);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();

        List<Todo> content = result.getContent();
        assertThat(content.size()).isEqualTo(10);
    }

/*
    @Test
    void testSearch1() {
        todoRepository.search1();
    }
*/
}