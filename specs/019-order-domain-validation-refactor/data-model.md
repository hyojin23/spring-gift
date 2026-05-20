# Data Model: Order 도메인 검증 강화 리팩토링

## Order

주문 도메인 객체입니다.

| Field | Type | Required | Rule |
|-------|------|----------|------|
| option | Option | Yes | null 불가 |
| memberId | Long | Yes | null 불가 |
| quantity | int | Yes | 1 이상 |
| message | String | No | 기존처럼 null/blank 허용 |
| orderDateTime | LocalDateTime | Yes | 생성 시 현재 시각으로 설정 |

### 생성 규칙

```java
new Order(option, memberId, quantity, message)
```

- `option == null`이면 `OrderValidationException`을 발생시킵니다.
- `memberId == null`이면 `OrderValidationException`을 발생시킵니다.
- `quantity < 1`이면 `OrderValidationException`을 발생시킵니다.
- 유효한 값이면 기존처럼 `orderDateTime`을 `LocalDateTime.now()`로 설정합니다.

## OrderValidationException

주문 도메인 검증 실패를 표현합니다.

| Field | Type | Description |
|-------|------|-------------|
| message | String | 한글 검증 실패 메시지 |

### 규칙

- `OrderException`을 상속합니다.
- `Order` 생성자 검증 실패에서 사용합니다.
- 범용 `IllegalArgumentException`을 사용하지 않습니다.

## OrderRequest

API 입력 DTO입니다.

### 유지 사항

- `optionId`의 `@NotNull` 검증을 유지합니다.
- `quantity`의 `@Min(1)` 검증을 유지합니다.
- `message`는 선택값으로 유지합니다.
- DTO 검증과 도메인 검증은 서로 대체하지 않고 함께 유지합니다.
