## Java 단계별 도전과제

### STEP 0. 환경 (완료)

* MySQL 실행
* JDBC 드라이버 추가
* Spring ❌ / Tomcat ❌

<br>
<br>

### STEP 1. JDBC (완료)

* 1-1. DriverManager로 MySQL 연결
* 1-2. Connection 하나 잡기
* 1-3. Statement / PreparedStatement 사용
* 1-4. CRUD 쿼리 직접 실행
* 1-5. autoCommit on/off 실험
* 1-6. commit / rollback 직접 호출

🎯 목표

1. “트랜잭션 = Connection 단위” 체감
2. JDBC 반복 작업 경험

<br>
<br>

### STEP 2. JdbcTemplate 만들기

노가다 제거 단계

* 2-1. 반복되는 JDBC 코드 정리
* 2-2. Connection 열고 닫는 책임 한 곳으로
* 2-3. PreparedStatement 실행 로직 캡슐화
* 2-4. ResultSet → 객체 매핑 분리

🎯 목표

“JdbcTemplate 왜 있는지 이해”

<br>
<br>

### STEP 3. DataSource

커넥션 생성 책임 분리

* 3-1. DataSource 인터페이스 정의
* 3-2. DriverManager 기반 구현체
* 3-3. JdbcTemplate이 DataSource만 의존하도록 변경

🎯 목표

“Connection 생성은 인프라 책임”

<br>
<br>

### STEP 4. Connection Pool

성능 & 자원 관리 단계

* 4-1. 커넥션 미리 생성
* 4-2. idle / active pool 관리
* 4-3. max size 제한
* 4-4. 커넥션 반납 처리
* 4-5. Pool 기반 DataSource 구현

🎯 목표

“왜 커넥션 풀 없으면 서비스가 죽는지”

<br>
<br>

### STEP 5. Transaction Manager

핵심 구간

* 5-1. TransactionManager 인터페이스
* 5-2. ThreadLocal로 Connection 관리
* 5-3. begin / commit / rollback
* 5-4. REQUIRED 전파 구현
* 5-5. Repository에서 직접 커넥션 못 열게 차단

🎯 목표

“트랜잭션 = 스레드 범위”

<br>
<br>

### STEP 6. @Transactional

선언적 트랜잭션

* 6-1. @MyTransactional 정의
* 6-2. 리플렉션으로 애노테이션 탐지
* 6-3. 실행 전후에 TX 처리

🎯 목표

“AOP가 왜 필요한지 체감”

<br>
<br>

### STEP 7. HTTP 서버 (Mini Tomcat 시작)

웹의 실체

* 7-1. Socket 기반 HTTP 서버
* 7-2. Request Line 파싱
* 7-3. Header / Body 파싱
* 7-4. Response 직접 작성

🎯 목표

“HTTP는 문자열 프로토콜”

<br>
<br>

### STEP 8. Thread 모델

웹 + 트랜잭션 연결 준비

* 8-1. 요청 1개 = 스레드 1개
* 8-2. ExecutorService 사용

🎯 목표

“왜 ThreadLocal이 먹히는지”

<br>
<br>

### STEP 9. Dispatcher (Servlet 역할)

Spring MVC 핵심

* 9-1. 모든 요청의 단일 진입점
* 9-2. URL → Handler 매핑
* 9-3. Controller 메서드 호출

🎯 목표

“DispatcherServlet의 본질”

<br>
<br>

### STEP 10. MVC 구조 완성

Spring MVC 뼈대

* 10-1. Controller
* 10-2. Argument Resolver (간단히)
* 10-3. Interceptor

🎯 목표

“웹 요청 흐름 완전 이해”

<br>
<br>

### STEP 11. 웹 + 트랜잭션 연결 (완성)

최종 퍼즐

* 11-1. 요청 시작
  → Transaction begin
* 11-2. Controller 실행
* 11-3. 예외 시 rollback
* 11-4. 정상 시 commit

🎯 목표

“@Transactional + Spring MVC 구조 체득”

<br>
<br>

🔥 최종 상태
```
HTTP 요청
 → Thread
   → Dispatcher
     → Interceptor
       → @Transactional
         → TransactionManager
           → Connection Pool
             → DataSource
               → JDBC
                 → MySQL
```
