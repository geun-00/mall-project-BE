package com.example.mallapi.service;

import com.example.mallapi.dto.PageRequestDTO;
import com.example.mallapi.dto.TodoDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Log4j2
class TodoServiceImplTest {

    @Autowired TodoService todoService;
    @Test
    void getTest() {
        Long tno = 1L;
        log.info(todoService.get(tno));
    }

    @Test
    void registerTest() {
        TodoDTO todoDTO = TodoDTO.builder()
                .title("Title...")
                .content("Content...")
                .dueDate(LocalDateTime.of(2024, 2, 29, 10, 1))
                .build();

        log.info(todoService.register(todoDTO));
    }

    @Test
    void getListTest() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        log.info(todoService.getList(pageRequestDTO));
    }
}