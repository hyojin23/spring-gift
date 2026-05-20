# Data Model: Order 검증 예외 전역 처리 리팩토링

## OrderValidationException

주문 도메인 검증 실패를 표현하는 예외입니다.

| Field | Type | Description |
|-------|------|-------------|
| message | String | 한글 검증 실패 메시지 |

### 전역 응답 매핑

| Status | Code | Message |
|--------|------|---------|
| 400 Bad Request | ORDER.INVALID | exception.getMessage() |

## ErrorResponse

기존 공통 에러 응답 모델을 사용합니다.

| Field | Type | Description |
|-------|------|-------------|
| code | String | 클라이언트 분기용 에러 code |
| message | String | 사용자/호출자에게 전달할 메시지 |
| timestamp | Instant | 응답 생성 시각 |
| details | Map | 이번 작업에서는 사용하지 않음 |

## GlobalExceptionHandler

### 추가 메서드

```java
@ExceptionHandler(OrderValidationException.class)
public ResponseEntity<ErrorResponse> handleOrderValidation(OrderValidationException exception)
```

### 유지 메서드

```java
@ExceptionHandler(OrderOptionNotFoundException.class)
public ResponseEntity<ErrorResponse> handleOrderOptionNotFound(OrderOptionNotFoundException exception)
```

`OrderOptionNotFoundException`은 기존처럼 404와 `ORDER.OPTION_NOT_FOUND`를 유지합니다.
