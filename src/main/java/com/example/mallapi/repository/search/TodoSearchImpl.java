package com.example.mallapi.repository.search;

import com.example.mallapi.domain.QTodo;
import com.example.mallapi.domain.Todo;
import com.example.mallapi.dto.PageRequestDTO;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2

public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<Todo> search1(PageRequestDTO pageRequestDTO) {

        log.info("search1......................");

        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = from(todo);

        int pageNum = pageRequestDTO.getPage() - 1;
        int sizeNum = pageRequestDTO.getSize();

        Pageable pageable = PageRequest.of(
                pageNum,
                sizeNum,
                Sort.by("tno").descending());

        this.getQuerydsl().applyPagination(pageable, query);

        List<Todo> list = query.fetch();//목록 데이터

        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }
}
