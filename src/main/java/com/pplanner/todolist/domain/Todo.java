package com.pplanner.todolist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "todos")
public class Todo {

    @Id     // Entity의 기본 키(PK) 
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // PK 값을 DB가 자동으로 숫자를 증가
    private Long id;

    @Column(nullable = false)       // DB 컬럼에 NOT NULL 제약 조건을 추가한다는 뜻.
    private String title;

    private boolean completed;

    private LocalDateTime createdAt;

    public static Todo create(String title) {
        Todo todo = new Todo();
        todo.title = title;
        todo.completed = false;
        todo.createdAt = LocalDateTime.now();
        return todo;
    }

    public void complete() {
        this.completed = true;
    }
}
