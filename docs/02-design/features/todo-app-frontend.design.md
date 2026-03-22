# Design: Todo App 프론트엔드

> Architecture: **Clean Architecture** (Option B)
> Plan 문서: `docs/01-plan/features/todo-app-frontend.plan.md`

---

## 1. 아키텍처 개요

```
┌─────────────────────────────────────────────────────┐
│  App.jsx (ErrorBoundary 래핑)                        │
│  ┌───────────────────────────────────────────────┐  │
│  │  TodoContext.Provider                          │  │
│  │  ┌─────────────┐  ┌────────────────────────┐  │  │
│  │  │ TodoInput   │  │ TodoList               │  │  │
│  │  │             │  │  ┌──────────────────┐  │  │  │
│  │  │ [입력] [추가]│  │  │ TodoItem         │  │  │  │
│  │  │             │  │  │ □ 제목  [✎] [🗑] │  │  │  │
│  │  │             │  │  │ □ 제목  [✎] [🗑] │  │  │  │
│  │  └─────────────┘  │  └──────────────────┘  │  │  │
│  │                    │  Loading / Empty State  │  │  │
│  │                    └────────────────────────┘  │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
         │                        ▲
         ▼                        │
    useTodos (Hook)          TodoContext
         │
         ▼
    todoApi.js (Axios)
         │
         ▼
    Spring Boot API (localhost:8080)
```

### 1.1 레이어 구조

| 레이어 | 역할 | 파일 |
|--------|------|------|
| **View** | UI 렌더링, 사용자 이벤트 | `App.jsx`, `TodoInput`, `TodoList`, `TodoItem` |
| **State** | 전역 상태 관리 | `TodoContext.jsx` |
| **Logic** | 비즈니스 로직, API 호출 조합 | `useTodos.js` |
| **API** | HTTP 통신 | `todoApi.js` |
| **Utility** | 공통 헬퍼 | `formatDate.js` |
| **Error** | 에러 처리 | `ErrorBoundary.jsx`, `Loading.jsx` |

---

## 2. 프로젝트 구조 (상세)

```
pplanner-frontend/
├── index.html
├── package.json
├── vite.config.js
└── src/
    ├── main.jsx                    # ReactDOM.createRoot 엔트리
    ├── App.jsx                     # ErrorBoundary + Provider + 레이아웃
    ├── App.module.css              # 전체 레이아웃 스타일
    ├── index.css                   # CSS reset + 전역 변수
    ├── context/
    │   └── TodoContext.jsx         # createContext + Provider + useTodoContext
    ├── hooks/
    │   └── useTodos.js             # CRUD 로직 + 상태 관리
    ├── components/
    │   ├── TodoInput.jsx           # 입력 폼
    │   ├── TodoInput.module.css
    │   ├── TodoItem.jsx            # 개별 항목 (토글, 수정, 삭제)
    │   ├── TodoItem.module.css
    │   ├── TodoList.jsx            # 목록 컨테이너 + Empty State
    │   ├── TodoList.module.css
    │   ├── Loading.jsx             # 로딩 스피너
    │   ├── Loading.module.css
    │   ├── ErrorBoundary.jsx       # React Error Boundary
    │   └── ErrorBoundary.module.css
    ├── api/
    │   └── todoApi.js              # Axios 인스턴스 + API 함수
    └── utils/
        └── formatDate.js           # 날짜 포맷 유틸
```

---

## 3. 컴포넌트 상세 설계

### 3.1 App.jsx

```
역할: 최상위 레이아웃 + ErrorBoundary + TodoContext.Provider 래핑
```

```jsx
// 구조 (의사 코드)
<ErrorBoundary>
  <TodoProvider>
    <div className={styles.container}>
      <h1>Todo App</h1>
      <TodoInput />
      <TodoList />
    </div>
  </TodoProvider>
</ErrorBoundary>
```

### 3.2 TodoContext.jsx

```
역할: 전역 상태(todos, loading, error) + CRUD 액션을 Context로 제공
```

| 상태 | 타입 | 설명 |
|------|------|------|
| `todos` | `Todo[]` | Todo 목록 |
| `loading` | `boolean` | 로딩 상태 |
| `error` | `string \| null` | 에러 메시지 |

| 액션 | 파라미터 | 설명 |
|------|----------|------|
| `addTodo(title)` | `string` | 새 Todo 추가 |
| `toggleTodo(id)` | `number` | 완료 토글 |
| `updateTodo(id, title)` | `number, string` | 제목 수정 |
| `deleteTodo(id)` | `number` | Todo 삭제 |

```jsx
// Provider 구조
const TodoContext = createContext();

function TodoProvider({ children }) {
  const todoActions = useTodos(); // hook에서 상태+액션 가져옴
  return (
    <TodoContext.Provider value={todoActions}>
      {children}
    </TodoContext.Provider>
  );
}

// 커스텀 훅으로 Context 접근
function useTodoContext() {
  const context = useContext(TodoContext);
  if (!context) throw new Error('TodoProvider 내에서 사용해야 합니다');
  return context;
}
```

### 3.3 useTodos.js (Custom Hook)

```
역할: API 호출 + 상태 관리 + 에러 핸들링 캡슐화
```

```jsx
function useTodos() {
  const [todos, setTodos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 초기 로드
  useEffect(() => { fetchTodos(); }, []);

  async function fetchTodos() { /* GET /todos */ }
  async function addTodo(title) { /* POST /todos */ }
  async function toggleTodo(id) { /* PUT /todos/{id} */ }
  async function updateTodo(id, title) { /* PUT /todos/{id} */ }
  async function deleteTodo(id) { /* DELETE /todos/{id} */ }

  return { todos, loading, error, addTodo, toggleTodo, updateTodo, deleteTodo };
}
```

### 3.4 TodoInput.jsx

```
역할: 새 Todo 제목 입력 + 추가 버튼
상태: title (로컬 state)
이벤트: Enter 키 또는 버튼 클릭 → addTodo(title)
유효성: 빈 문자열 방지
```

### 3.5 TodoList.jsx

```
역할: todos 배열을 순회하며 TodoItem 렌더링
조건부 렌더링:
  - loading → <Loading />
  - todos.length === 0 → Empty State 메시지
  - else → TodoItem 목록
```

### 3.6 TodoItem.jsx

```
역할: 개별 Todo 표시 + 완료토글/수정/삭제
Props: todo 객체
상태: isEditing, editTitle (로컬 state)
기능:
  - 체크박스 클릭 → toggleTodo(id)
  - 제목 더블클릭 → 인라인 편집 모드
  - 편집 확인 (Enter/blur) → updateTodo(id, newTitle)
  - 삭제 버튼 → deleteTodo(id)
스타일:
  - completed → 취소선 + 투명도 0.5
  - 삭제 시 fadeOut 애니메이션
```

### 3.7 ErrorBoundary.jsx

```
역할: React 렌더링 에러 캐치 + 폴백 UI
구현: class 컴포넌트 (getDerivedStateFromError + componentDidCatch)
폴백: "문제가 발생했습니다. 페이지를 새로고침 해주세요." + 새로고침 버튼
```

### 3.8 Loading.jsx

```
역할: 로딩 스피너 컴포넌트
구현: CSS 애니메이션 기반 스피너
```

---

## 4. API 레이어 설계

### 4.1 todoApi.js

```jsx
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',   // Vite proxy를 통해 localhost:8080으로 전달
  timeout: 5000,
  headers: { 'Content-Type': 'application/json' }
});

export const todoApi = {
  getAll:    ()              => api.get('/todos'),
  getById:   (id)            => api.get(`/todos/${id}`),
  create:    (title)         => api.post('/todos', { title }),
  update:    (id, data)      => api.put(`/todos/${id}`, data),
  delete:    (id)            => api.delete(`/todos/${id}`)
};
```

### 4.2 Vite Proxy 설정

```js
// vite.config.js
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
});
```

---

## 5. 백엔드 수정 설계

### 5.1 TodoUpdateRequest.java — `completed` 필드 추가

```java
// 변경 전
private String title;

// 변경 후
private String title;
private Boolean completed;  // null 허용 (부분 업데이트)
```

### 5.2 TodoResponse.java — `completed`, `createdAt` 필드 추가

```java
// 변경 전
private Long id;
private String title;

// 변경 후
private Long id;
private String title;
private boolean completed;
private LocalDateTime createdAt;
```

### 5.3 TodoService.update() — completed 업데이트 로직

```java
// 변경 후
@Transactional
public TodoResponse update(Long id, TodoUpdateRequest request) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new TodoNotFoundException("해당 Todo가 없습니다."));

    if (request.getTitle() != null) {
        todo.updateTitle(request.getTitle());
    }
    if (request.getCompleted() != null) {
        if (request.getCompleted()) {
            todo.complete();
        } else {
            todo.uncomplete();  // 새로 추가 필요
        }
    }

    return TodoResponse.from(todo);
}
```

### 5.4 Todo.java — `uncomplete()` 메서드 추가

```java
public void uncomplete() {
    this.completed = false;
}
```

---

## 6. 데이터 흐름

### 6.1 Todo 생성 흐름

```
[TodoInput] → addTodo(title)
    → [useTodos] → todoApi.create(title)
        → POST /api/todos → Spring Boot
    ← response (id, title, completed, createdAt)
    → setTodos([...todos, newTodo])
    → [TodoList] 리렌더링
```

### 6.2 완료 토글 흐름

```
[TodoItem] 체크박스 클릭 → toggleTodo(id)
    → [useTodos] → todoApi.update(id, { completed: !current })
        → PUT /api/todos/{id} → Spring Boot
    ← response (updated todo)
    → setTodos(todos.map(t => t.id === id ? updated : t))
    → [TodoItem] 취소선 + 투명도 변경
```

### 6.3 삭제 흐름

```
[TodoItem] 삭제 버튼 → deleteTodo(id)
    → [useTodos] → todoApi.delete(id)
        → DELETE /api/todos/{id} → Spring Boot
    ← 204 No Content
    → setTodos(todos.filter(t => t.id !== id))
    → [TodoList] 리렌더링 (fadeOut 애니메이션)
```

---

## 7. 스타일링 설계

### 7.1 디자인 토큰 (CSS 변수)

```css
/* index.css */
:root {
  --color-primary: #6C63FF;
  --color-bg: #F5F5F5;
  --color-card: #FFFFFF;
  --color-text: #333333;
  --color-text-muted: #999999;
  --color-danger: #FF6B6B;
  --color-success: #51CF66;
  --border-radius: 12px;
  --shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  --transition: 0.2s ease;
}
```

### 7.2 애니메이션

| 요소 | 애니메이션 | CSS |
|------|-----------|-----|
| Todo 추가 | slideIn (위에서 아래) | `@keyframes slideIn` |
| Todo 삭제 | fadeOut (투명도 + 높이) | `@keyframes fadeOut` |
| 완료 토글 | 취소선 + 투명도 전환 | `transition: opacity 0.2s` |
| 체크박스 | scale 효과 | `transform: scale(1.2)` |
| 버튼 hover | 배경색 전환 | `transition: background 0.2s` |

### 7.3 반응형 브레이크포인트

| 화면 | 너비 | 카드 최대 너비 |
|------|------|--------------|
| Mobile | < 480px | 100% (padding 16px) |
| Tablet | 480-768px | 480px |
| Desktop | > 768px | 560px |

---

## 8. 구현 순서

| 단계 | 작업 | 파일 | 위치 |
|------|------|------|------|
| 1 | Vite + React 프로젝트 초기화 | `package.json`, `vite.config.js` | FE |
| 2 | 글로벌 CSS + 디자인 토큰 | `index.css` | FE |
| 3 | 백엔드 수정 (completed, Response 확장) | 4개 Java 파일 | BE |
| 4 | API 모듈 | `todoApi.js` | FE |
| 5 | useTodos 커스텀 훅 | `useTodos.js` | FE |
| 6 | TodoContext | `TodoContext.jsx` | FE |
| 7 | Loading + ErrorBoundary | 2개 컴포넌트 | FE |
| 8 | TodoInput 컴포넌트 | `TodoInput.jsx` + CSS | FE |
| 9 | TodoItem 컴포넌트 | `TodoItem.jsx` + CSS | FE |
| 10 | TodoList 컴포넌트 | `TodoList.jsx` + CSS | FE |
| 11 | App 통합 | `App.jsx` + CSS | FE |
| 12 | 애니메이션 적용 + 반응형 | 각 CSS Module | FE |
| 13 | Vite proxy + 연동 테스트 | `vite.config.js` | 양쪽 |

---

## 9. 의존성 (package.json)

```json
{
  "dependencies": {
    "react": "^18.3.0",
    "react-dom": "^18.3.0",
    "axios": "^1.7.0"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.3.0",
    "vite": "^6.0.0"
  }
}
```
