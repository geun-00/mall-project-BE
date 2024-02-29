package com.example.mallapi.repository.search;

import com.example.mallapi.domain.QTodo;
import com.example.mallapi.domain.Todo;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);
    }


    @Override
    public Page<Todo> search1() {

        log.info("search1......................");

        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = from(todo)
                .where(todo.title.contains("1"));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("tno").descending());

        this.getQuerydsl().applyPagination(pageable, query);

        query.fetch(); //목록 데이터

        query.fetchCount();
        return null;
    }
}
