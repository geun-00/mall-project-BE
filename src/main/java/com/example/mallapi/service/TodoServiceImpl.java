package com.example.mallapi.service;

import com.example.mallapi.domain.Todo;
import com.example.mallapi.dto.PageRequestDTO;
import com.example.mallapi.dto.PageResponseDTO;
import com.example.mallapi.dto.TodoDTO;
import com.example.mallapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;

    @Override
    public TodoDTO get(Long tno) {
        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        return entityToDTO(todo);
    }

    @Override
    public Long register(TodoDTO dto) {
        return todoRepository.save(dtoToEntity(dto)).getTno();
    }

    @Override
    public void modify(TodoDTO dto) {
        Optional<Todo> result = todoRepository.findById(dto.getTno());
        Todo todo = result.orElseThrow();

        todo.changeTitle(dto.getTitle());
        todo.changeContent(dto.getContent());
        todo.changeComplete(dto.isComplete());
        todo.changeDueDate(dto.getDueDate());
    }

    @Override
    public void remove(Long tno) {
        todoRepository.deleteById(tno);
    }

    @Override
    public PageResponseDTO<TodoDTO> getList(PageRequestDTO pageRequestDTO) {
        Page<Todo> result = todoRepository.search1(pageRequestDTO);

        List<TodoDTO> dtoList = result.get().map(this::entityToDTO).toList();

        return PageResponseDTO.<TodoDTO>withAll()
                              .dtoList(dtoList)
                              .pageRequestDTO(pageRequestDTO)
                              .total(result.getTotalElements())
                              .build();
    }
}