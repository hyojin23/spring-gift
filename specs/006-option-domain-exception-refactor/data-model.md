# Data Model: Option 도메인 예외 리팩토링

## OptionQuantityException

Option 수량 검증 실패를 표현하는 도메인 예외입니다.

### Type

```java
public class OptionQuantityException extends OptionException
```

### Messages

- `옵션 수량은 1 이상 99,999,999 이하이어야 합니다.`
- `차감 수량은 1 이상이어야 합니다.`
- `차감할 수량이 현재 재고보다 많습니다.`

### API Mapping

- HTTP status: `400 Bad Request`
- error code: `OPTION.INVALID_QUANTITY`
- message: exception message

## Option

### Current Quantity Rules

- 생성 수량은 1 이상 99,999,999 이하이어야 합니다.
- 차감 수량은 1 이상이어야 합니다.
- 차감 수량은 현재 재고보다 클 수 없습니다.

### Exception Behavior

```java
new Option(product, "옵션", 0);          // OptionQuantityException
new Option(product, "옵션", 100000000);  // OptionQuantityException
option.subtractQuantity(0);             // OptionQuantityException
option.subtractQuantity(11);            // 현재 재고가 10이면 OptionQuantityException
```

## GlobalExceptionHandler

### New Handler

```java
@ExceptionHandler(OptionQuantityException.class)
public ResponseEntity<ErrorResponse> handleOptionQuantity(OptionQuantityException exception) {
    return error(HttpStatus.BAD_REQUEST, "OPTION.INVALID_QUANTITY", exception.getMessage());
}
```

## Behavioral Compatibility

- DB schema는 변경하지 않습니다.
- Option API success response는 변경하지 않습니다.
- 옵션명 검증 실패 code `OPTION.INVALID_NAME`은 유지합니다.
- 다른 Option 예외 code는 유지합니다.
