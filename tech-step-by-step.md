## Java 단계별 도전과제

### STEP 0. 환경 (완료)

* MySQL 실행
* JDBC 드라이버 추가
* Spring ❌ / Tomcat ❌

<br>
<br>

### STEP 1. Java - JDBC (완료)

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

### STEP 2. Spring - JdbcTemplate 만들기

노가다 제거 단계

* 2-1. 반복되는 JDBC 코드 정리
* 2-2. Connection은 놔두기 -> DataSource TrasactionManager를 거치며 차근차근 없앨 것
* 2-3. PreparedStatement 실행 로직 캡슐화
* 2-4. ResultSet → 객체 매핑 분리

🎯 목표

JdbcTemplate을 구현하며 Repository 내의 반복 작업 최소화

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

### STEP 4. Transaction Manager

핵심 구간

* 4-1. startTransaction, commit, rollback을 제공하는 TransactionManager 생성 (with ThreadLocal)
* 4-2. ThreadLocal<Connection>을 관리하는 ConnectionThreadLocal 생성
* 4-3. DataSource에서 트랜잭션을 지원하도록 구현
* 4-4. 애플리케이션 레벨에서 Connection 코드 제거
* 4-5. MyJdbcTemplate에서 커넥션을 직접 받지 않고 MyDataSource를 받도록 변경

🎯 목표

“트랜잭션의 영향을 제거해서 애플리케이션 레벨에 Connection 코드를 제거하기”

"MyJdbcTemplate 내부에서 DataSource를 직접 사용하도록 변경하기"

<br>
<br>

### STEP 5. Transactional

선언적 트랜잭션

* 5-1. @MyTransactional 정의
* 5-2. 어노테이션 감지 로직 추가
* 5-3. 트랜잭션 Proxy 로직 추가
* 5-4. 트랜잭션 적용

🎯 목표

“트랜잭션 프록시 로직 적용 과정 이해”

<br>
<br>

### STEP 6. HTTP 서버 (Mini Tomcat 시작)

웹의 실체

* 6-1. Socket 기반 HTTP 서버
* 6-2. Request Line 파싱
* 6-3. Header / Body 파싱
* 6-4. Response 직접 작성

🎯 목표

“HTTP는 문자열 프로토콜”

<br>
<br>

### STEP 7. Thread 모델

웹 + 트랜잭션 연결 준비

* 7-1. 요청 1개 = 스레드 1개
* 7-2. ExecutorService 사용

🎯 목표

“왜 ThreadLocal이 먹히는지”

<br>
<br>

### STEP 8. Dispatcher (Servlet 역할)

Spring MVC 핵심

* 8-1. 모든 요청의 단일 진입점
* 8-2. URL → Handler 매핑
* 8-3. Controller 메서드 호출

🎯 목표

“DispatcherServlet의 본질”

<br>
<br>

### STEP 9. MVC 구조 완성

Spring MVC 뼈대

* 9-1. Controller
* 9-2. Argument Resolver (간단히)
* 9-3. Interceptor

🎯 목표

“웹 요청 흐름 완전 이해”

<br>
<br>

### STEP 10. 웹 + 트랜잭션 연결 (완성)

최종 퍼즐

* 10-1. 요청 시작
  → Transaction begin
* 10-2. Controller 실행
* 10-3. 예외 시 rollback
* 10-4. 정상 시 commit

🎯 목표

“@Transactional + Spring MVC 구조 체득”

<br>
<br>

### STEP 6. Connection Pool (보류)

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
