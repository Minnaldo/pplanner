# pplanner

📌 PPlanner - TodoList API
<div align="center">
🗂 Spring Boot 기반 RESTful TodoList 서버 프로젝트

기본기에 집중하며 설계한 백엔드 학습용 프로젝트

</div>
📖 Project Introduction

PPlanner는 Spring Boot를 활용하여 구현한
RESTful 기반의 TodoList 백엔드 API 서버입니다.

이 프로젝트는 단순 CRUD 구현을 넘어,

올바른 계층 분리

DTO 패턴 적용

예외 처리 구조 설계

RESTful API 설계 원칙 준수

를 목표로 단계적으로 개발되었습니다.

🛠 Tech Stack
Category	Tech
Language	Java 17
Framework	Spring Boot
Build Tool	Gradle
Database	H2 Database
ORM	Spring Data JPA
Test Tool	Postman
IDE	IntelliJ IDEA
🏗 Project Architecture
com.pplanner.todolist
│
├── controller        # API 요청 처리
├── service           # 비즈니스 로직 처리
├── repository        # DB 접근 계층
├── domain            # Entity 클래스
├── dto               # Request / Response 객체
└── exception         # 전역 예외 처리
📌 계층 분리 원칙

Controller → 요청/응답만 담당

Service → 비즈니스 로직 담당

Repository → DB 접근 담당

DTO → Entity 직접 노출 방지

📦 API Specification
1️⃣ Create Todo
POST /todos
Request Body
{
"title": "Spring 공부하기"
}
Response
{
"id": 1,
"title": "Spring 공부하기",
"completed": false
}
2️⃣ Get All Todos
GET /todos
Response
[
{
"id": 1,
"title": "Spring 공부하기",
"completed": false
}
]
3️⃣ Get Todo By Id
GET /todos/{id}
Example
GET /todos/1
⚠️ Exception Handling

존재하지 않는 ID 조회 시

TodoNotFoundException 발생

GlobalExceptionHandler에서 처리

Error Response Example
{
"message": "해당 Todo가 없습니다."
}
💾 Database
H2 Console 접속 정보
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:pplannerdb
Username: sa
Password:

※ 서버 재시작 시 데이터는 초기화됩니다.

🎯 Development Goals

RESTful 설계 이해

DTO 패턴 학습

계층형 아키텍처 이해

예외 처리 구조 학습

JPA 기본 동작 원리 이해

🚀 Future Improvements

✅ Todo 수정 기능 (PUT)

✅ Todo 삭제 기능 (DELETE)

⏳ 완료 여부 토글 기능

⏳ 페이지네이션 적용

⏳ Validation 적용

⏳ Swagger 적용

⏳ Docker 배포

📈 Learning Focus

이 프로젝트는 다음을 목표로 합니다:

stream 사용 이전의 기본 반복문 이해

Optional 개념 이해

예외 흐름 제어 이해

REST API 테스트 경험 축적

👨‍💻 Author

Backend Developer in Progress 🚀
꾸준히 성장하는 개발자를 목표로 학습 중입니다.

✨ 마무리

이 프로젝트는 단순 TodoList 구현이 아니라,
**“백엔드 기본기를 단단히 다지는 과정”**을 담고 있습니다.