package com.example.mallapi.repository.search;

import com.example.mallapi.domain.Todo;
import com.example.mallapi.dto.PageRequestDTO;
import org.springframework.data.domain.Page;

public interface TodoSearch {

    Page<Todo> search1(PageRequestDTO pageRequestDTO);
}
