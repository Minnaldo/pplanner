package com.pplanner.todolist.service;

import com.pplanner.todolist.domain.Todo;
import com.pplanner.todolist.dto.TodoCreateRequest;
import com.pplanner.todolist.dto.TodoResponse;
import com.pplanner.todolist.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoResponse create(TodoCreateRequest request) {
        Todo todo = Todo.create(request.getTitle());
        Todo saved = todoRepository.save(todo);
        return TodoResponse.from(saved);
    }

    public List<TodoResponse> findAll() {

        // 1. DB에서 모든 Todo 가져오기
        List<Todo> todos = todoRepository.findAll();

        // 2. 반환할 빈 리스트 만들기
        List<TodoResponse> todoResponses = new ArrayList<>();

        // 3. 하나씩 꺼내서 DTO로 변환
        for(Todo todo : todos) {
            TodoResponse response = TodoResponse.from(todo);
            todoResponses.add(response);
        }

        return todoResponses;
    }

    public TodoResponse findById(Long id) {

        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 Todo가 없습니다."));

        return TodoResponse.from(todo);
    }
}
