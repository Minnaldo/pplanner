package com.pplanner.todolist.dto;

import com.pplanner.todolist.domain.Todo;

import java.time.LocalDateTime;

public class TodoResponse {

    private Long id;
    private String title;
    private boolean completed;
    private LocalDateTime createdAt;

    public TodoResponse(Long id, String title, boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted(), todo.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
