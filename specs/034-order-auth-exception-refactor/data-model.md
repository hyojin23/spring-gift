# Data Model: Order 인증 예외 응답 일관화 리팩토링

## AuthenticationException

### 책임

- 인증 실패를 표현합니다.
- global handler에서 401 `AUTH.UNAUTHORIZED` 응답으로 변환됩니다.

### 사용 위치

- 기존: `WishController`
- 추가 대상: `OrderController`

## OrderController

### 변경 전

- `AuthenticationResolver.extractMember()` 결과가 null이면 직접 빈 401 응답을 반환합니다.
- `ResponseEntity<?>`를 반환합니다.

### 변경 후

- 인증된 member 추출을 private method로 분리합니다.
- member가 null이면 `AuthenticationException`을 던집니다.
- 주문 목록은 `ResponseEntity<Page<OrderResponse>>` 또는 실제 응답 타입에 맞는 형태로 반환합니다.
- 주문 생성은 `ResponseEntity<OrderResponse>`를 반환합니다.

## ErrorResponse

### 인증 실패 응답

```json
{
  "code": "AUTH.UNAUTHORIZED",
  "message": "인증이 필요합니다."
}
```

정확한 message는 기존 `AuthenticationException`의 메시지를 따릅니다.

## 관계

```text
OrderController
  -> AuthenticationResolver.extractMember()
  -> AuthenticationException
  -> GlobalExceptionHandler
  -> ErrorResponse
```
