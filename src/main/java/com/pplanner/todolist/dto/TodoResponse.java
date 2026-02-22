package com.pplanner.todolist.dto;

import com.pplanner.todolist.domain.Todo;

public class TodoResponse {

    private Long id;
    private String title;

    public TodoResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
