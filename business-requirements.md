# 비즈니스 요구사항
✅ 실습용 비즈니스 도메인: 주문 시스템 (Order System)

## 기본 도메인

### 1. 사용자 (User)

사용자는 고유 ID를 가진다.

사용자는 이름을 가진다.

사용자는 보유 금액(balance) 을 가진다.

보유 금액은 음수가 될 수 없다.

### 2. 음식 (Food)

음식은 고유 ID를 가진다.

음식은 고유한 이름을 가진다.

음식은 가격(price)을 가진다.

음식은 재고 수량(stock)을 가진다.

재고 수량은 0 미만이 될 수 없다.

### 3. 주문 (Order)

주문은 고유 ID를 가진다.

주문은 주문 날짜(createdAt)를 가진다.

주문은 주문한 사용자(userId)를 가진다.

하나의 주문은 여러 음식(OrderItem) 을 가진다.

주문은 총 금액(totalPrice)을 가진다.

### 4. 주문 항목 (OrderItem)

주문 항목은 주문 ID를 가진다.

주문 항목은 음식 ID를 가진다.

주문 항목은 주문 수량(quantity)을 가진다.

주문 당시의 음식 가격(priceAtOrder)을 가진다.

## 기능 요구사항

### 1️⃣ 사용자 API
* `POST /users` - 사용자 생성
* `GET /users/{userId}` - 사용자 조회
* `POST /users/{userId}/balance` - 사용자 잔액 증감

### 3️⃣ 음식 API
* `POST /foods` - 음식 등록
* `GET /foods` - 음식 조회
* `POST /foods/stock` - 음식 재고 증감

### 4️⃣ 주문 API
* `POST /orders` - 주문 생성 (핵심)
```json
// 요청 예
{
    "userId": 1,
    "items": [
        { "foodId": 10, "quantity": 2 },
        { "foodId": 12, "quantity": 1 }
    ]
}
```
음식 재고 감소, 사용자 돈 감소 , 주문 + 주문 아이템 insert 👉 트랜잭션 없으면 반드시 깨진다.

* `GET /orders/{orderId}` - 주문 조회 (join 결과 매핑)
