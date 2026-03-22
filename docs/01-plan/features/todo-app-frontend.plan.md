## Executive Summary

| 항목 | 내용 |
|------|------|
| **Feature** | Todo App 프론트엔드 |
| **시작일** | 2026-03-22 |
| **예상 기간** | 1일 |

| 관점 | 설명 |
|------|------|
| **Problem** | 백엔드 CRUD API는 완성되어 있지만 사용자가 상호작용할 수 있는 UI가 없어 todo-app으로서 사용 불가 |
| **Solution** | React + Vite 기반 모던 SPA 프론트엔드를 구축하여 기존 Spring Boot API와 연결 |
| **Function UX Effect** | Todo 생성/조회/수정/삭제를 직관적인 UI에서 즉시 수행 가능, 완료 토글 및 시각적 피드백 제공 |
| **Core Value** | 기존 백엔드 코드 변경 없이 완성된 todo-app 사용자 경험 제공 |

---

# Plan: Todo App 프론트엔드

## 1. 배경 및 목적

### 1.1 현재 상태
- Spring Boot 3.5 + Java 17 + H2 DB 기반 백엔드 CRUD API 완성
- API 엔드포인트: `POST/GET/PUT/DELETE /todos`
- Entity: Todo (id, title, completed, createdAt)
- 예외 처리: GlobalExceptionHandler + TodoNotFoundException

### 1.2 문제점
- 사용자가 직접 사용할 수 있는 UI가 없음
- API 테스트는 Postman 등 도구로만 가능

### 1.3 목표
- React + Vite 기반 모던 프론트엔드 구축
- 기존 백엔드 API와 완전 연동
- 깔끔한 모던 UI + 애니메이션

## 2. 기술 스택

| 구분 | 기술 | 이유 |
|------|------|------|
| **Framework** | React 18 | 컴포넌트 기반 UI, 풍부한 생태계 |
| **Build Tool** | Vite | 빠른 HMR, 간단한 설정 |
| **HTTP Client** | Axios | Promise 기반 HTTP 통신, 인터셉터 지원 |
| **Styling** | CSS Modules | 컴포넌트 단위 스타일 격리, 별도 라이브러리 불필요 |
| **Language** | JavaScript | 프로젝트 복잡도에 적합 |

## 3. 기능 범위

### 3.1 핵심 기능 (Must Have)

| # | 기능 | 설명 | 연결 API |
|---|------|------|----------|
| F1 | Todo 생성 | 입력 필드에 제목 입력 후 추가 | `POST /todos` |
| F2 | Todo 목록 조회 | 전체 Todo 리스트 표시 | `GET /todos` |
| F3 | Todo 수정 | 제목 인라인 편집 | `PUT /todos/{id}` |
| F4 | Todo 삭제 | 삭제 버튼 클릭으로 제거 | `DELETE /todos/{id}` |
| F5 | 완료 토글 | 체크박스로 완료/미완료 전환 | `PUT /todos/{id}` |

### 3.2 UI/UX 요구사항

- 깔끔한 모던 디자인 (미니멀 스타일)
- 추가/삭제 시 부드러운 애니메이션
- 완료된 항목 시각적 구분 (취소선 + 투명도)
- 빈 목록일 때 안내 메시지
- 반응형 레이아웃 (모바일 대응)

## 4. 프로젝트 구조

프론트엔드와 백엔드를 독립된 프로젝트로 분리 관리한다.

```
Desktop/
├── pplanner/                    # 백엔드 (기존 Spring Boot 프로젝트)
│   ├── src/
│   │   └── main/java/com/pplanner/todolist/
│   │       ├── domain/Todo.java
│   │       ├── controller/TodoController.java
│   │       ├── service/TodoService.java
│   │       ├── repository/TodoRepository.java
│   │       ├── dto/
│   │       └── exception/
│   ├── build.gradle
│   └── docs/                    # PDCA 문서 (백엔드 프로젝트에서 관리)
│
└── pplanner-frontend/           # 프론트엔드 (별도 프로젝트)
    ├── index.html
    ├── package.json
    ├── vite.config.js           # Proxy 설정 (API → localhost:8080)
    └── src/
        ├── main.jsx             # 엔트리 포인트
        ├── App.jsx              # 메인 컴포넌트
        ├── App.module.css       # 앱 전체 스타일
        ├── components/
        │   ├── TodoInput.jsx        # Todo 입력 폼
        │   ├── TodoInput.module.css
        │   ├── TodoList.jsx         # Todo 목록 컨테이너
        │   ├── TodoList.module.css
        │   ├── TodoItem.jsx         # 개별 Todo 항목
        │   └── TodoItem.module.css
        └── api/
            └── todoApi.js       # Axios API 호출 모듈
```

### 4.1 분리 이유
- 백엔드/프론트엔드 독립적으로 빌드, 배포, 버전 관리 가능
- 각 프로젝트의 의존성과 설정이 섞이지 않음
- 프론트엔드: `pplanner-frontend/` (별도 Git 저장소 가능)
- 백엔드: `pplanner/` (기존 프로젝트 유지)

## 5. API 연동 명세

| 기능 | Method | Endpoint | Request Body | Response |
|------|--------|----------|-------------|----------|
| 생성 | POST | `/todos` | `{ "title": "string" }` | `{ "id": number, "title": "string" }` |
| 전체 조회 | GET | `/todos` | - | `[{ "id", "title" }]` |
| 단건 조회 | GET | `/todos/{id}` | - | `{ "id", "title" }` |
| 수정 | PUT | `/todos/{id}` | `{ "title": "string" }` | `{ "id", "title" }` |
| 삭제 | DELETE | `/todos/{id}` | - | 204 No Content |

### 5.1 백엔드 수정 필요사항

> **CORS 설정 추가 필요**: 프론트엔드(Vite dev server, port 5173)에서 백엔드(port 8080)로 요청 시 CORS 에러 방지를 위해 Vite proxy 설정 또는 Spring Boot CORS 설정 필요
>
> **완료 토글 API**: 현재 `TodoUpdateRequest`에 `title`만 있음. `completed` 필드 추가 필요

## 6. 백엔드 변경 사항

완료 토글 기능을 위해 최소한의 백엔드 수정 필요:

| 파일 | 변경 내용 |
|------|-----------|
| `TodoUpdateRequest.java` | `completed` 필드 추가 |
| `TodoService.update()` | completed 업데이트 로직 추가 |
| `TodoResponse.java` | `completed`, `createdAt` 필드 추가 |

## 7. 구현 순서

| 단계 | 작업 | 위치 | 의존성 |
|------|------|------|--------|
| 1 | `pplanner-frontend/` 프로젝트 생성 (Vite + React) | 프론트엔드 | - |
| 2 | 백엔드 수정 (completed 필드, Response 확장, CORS) | 백엔드 | - |
| 3 | API 모듈 구현 (todoApi.js) | 프론트엔드 | Step 1 |
| 4 | TodoInput 컴포넌트 | 프론트엔드 | Step 1 |
| 5 | TodoList + TodoItem 컴포넌트 | 프론트엔드 | Step 3 |
| 6 | App 통합 및 상태 관리 | 프론트엔드 | Step 4, 5 |
| 7 | 스타일링 + 애니메이션 | 프론트엔드 | Step 6 |
| 8 | Vite proxy 설정 + 연동 테스트 | 양쪽 | Step 2, 7 |

## 8. 제약 사항

- 프론트엔드 프로젝트 경로: `Desktop/pplanner-frontend/`
- 백엔드 프로젝트 경로: `Desktop/pplanner/`
- 프론트엔드 개발 서버: `localhost:5173` (Vite 기본)
- 백엔드 서버: `localhost:8080` (Spring Boot 기본)
- 개발 시 두 서버를 동시에 실행해야 함
- H2 DB는 `ddl-auto: create`이므로 서버 재시작 시 데이터 초기화
- 인증/인가 없음 (단일 사용자 가정)
