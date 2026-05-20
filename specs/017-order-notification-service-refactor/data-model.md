# Data Model: Order 알림 서비스 분리 리팩토링

## OrderNotificationService

주문 완료 알림 발송을 담당하는 service입니다.

### Public API

```java
void sendOrderCreatedMessage(Member member, Order order, Option option)
```

### 입력

| Field | Type | Description |
|-------|------|-------------|
| member | Member | 주문한 회원, 카카오 access token 확인에 사용 |
| order | Order | 저장된 주문 정보 |
| option | Option | 주문 옵션과 상품 정보 |

### 규칙

- `member.getKakaoAccessToken()`이 `null`이면 아무 작업도 하지 않습니다.
- 카카오 access token이 있으면 `KakaoMessageClient.sendToMe(token, order, option.getProduct())`를 호출합니다.
- `KakaoMessageClient` 호출 중 발생한 예외는 외부로 전파하지 않습니다.
- 주문 저장, 재고 차감, 포인트 차감은 담당하지 않습니다.

## OrderService

주문 생성 핵심 flow를 담당합니다.

### 변경 전 책임

- 옵션 조회
- 재고 차감
- 포인트 차감
- 주문 저장
- 카카오 access token 확인
- 카카오 메시지 발송
- 카카오 메시지 실패 예외 무시

### 변경 후 책임

- 옵션 조회
- 재고 차감
- 포인트 차감
- 주문 저장
- 주문 저장 후 `OrderNotificationService` 호출

### 규칙

- `KakaoMessageClient`를 직접 주입받지 않습니다.
- `sendKakaoMessageIfPossible()` 같은 알림 세부 메서드를 갖지 않습니다.
- 주문 생성 성공/실패 응답 계약은 변경하지 않습니다.

## KakaoMessageClient

실제 카카오 메시지 API 호출을 담당하는 기존 client입니다.

### 규칙

- `OrderNotificationService`에서만 주문 완료 알림 용도로 직접 사용합니다.
- 호출 실패가 주문 실패로 전파되지 않도록 알림 서비스에서 예외를 처리합니다.
