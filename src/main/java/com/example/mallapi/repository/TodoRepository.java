package com.example.mallapi.repository;

import com.example.mallapi.domain.Todo;
import com.example.mallapi.repository.search.TodoSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {
}
