# Data Model: Order 예외 처리 리팩토링

## OrderException

주문 도메인 예외의 공통 상위 타입입니다.

| Field | Type | Description |
|-------|------|-------------|
| message | String | 사용자/클라이언트에 전달할 한글 오류 메시지 |

### 규칙

- `RuntimeException`을 상속합니다.
- order 패키지에서 발생하는 주문 도메인 실패의 상위 타입으로 사용합니다.
- 외부 client 실패처럼 best-effort로 삼킬 예외는 이 타입으로 감싸지 않습니다.

## OrderOptionNotFoundException

주문 생성 대상 옵션이 존재하지 않는 상황을 표현합니다.

| Field | Type | Description |
|-------|------|-------------|
| optionId | Long | 존재하지 않는 주문 대상 옵션 ID |
| message | String | 한글 오류 메시지 |

### 규칙

- `OrderException`을 상속합니다.
- `OrderService.createOrder()`에서 옵션 조회 결과가 없을 때 발생합니다.
- 전역 핸들러에서 404와 `ORDER.OPTION_NOT_FOUND` code로 변환됩니다.
- 예외 발생 시 주문 저장, 포인트 차감, 재고 차감이 수행되지 않아야 합니다.

## ErrorResponse

기존 전역 예외 응답 모델을 재사용합니다.

| Field | Type | Description |
|-------|------|-------------|
| code | String | 클라이언트가 분기 가능한 오류 code |
| message | String | 한글 오류 메시지 |

### 신규/확인 대상 code

- `ORDER.OPTION_NOT_FOUND`: 주문 대상 옵션이 존재하지 않음
- `MEMBER.INSUFFICIENT_POINT`: 주문 결제에 필요한 회원 포인트 부족
- `OPTION.INVALID_QUANTITY`: 주문 수량이 옵션 재고 정책을 위반함

## OrderService.createOrder()

### 변경 전

```java
Optional<OrderResponse> createOrder(Member member, OrderRequest request)
```

### 변경 후

```java
OrderResponse createOrder(Member member, OrderRequest request)
```

### 규칙

- 성공 시 저장된 주문의 `OrderResponse`를 반환합니다.
- 옵션 미존재 시 `OrderOptionNotFoundException`을 발생시킵니다.
- 재고 부족은 `OptionQuantityException`을 그대로 전파합니다.
- 포인트 부족은 `InsufficientMemberPointException`을 그대로 전파합니다.
- 카카오 알림 실패는 기존처럼 주문 실패로 전파하지 않습니다.
