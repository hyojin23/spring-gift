# Data Model: Option 수량 도메인 검증 강화

## Option

수량 불변식을 직접 보호해야 하는 도메인 엔티티입니다.

### Fields

- `id`: 옵션 식별자
- `product`: 옵션이 속한 상품
- `name`: 옵션 이름
- `quantity`: 옵션 재고 수량

### New Constants

- `MIN_QUANTITY`: 1
- `MAX_QUANTITY`: 99,999,999

### Invariants

- `quantity`는 생성 시 1 이상 99,999,999 이하이어야 합니다.
- 차감 수량은 1 이상이어야 합니다.
- 차감 수량은 현재 `quantity`보다 클 수 없습니다.

### Expected Behavior

```java
new Option(product, "옵션", 0);          // 실패
new Option(product, "옵션", 100000000);  // 실패
new Option(product, "옵션", 10);         // 성공
```

```java
option.subtractQuantity(0);   // 실패
option.subtractQuantity(-1);  // 실패
option.subtractQuantity(11);  // 현재 재고가 10이면 실패
option.subtractQuantity(3);   // 성공, quantity 7
```

## OptionRequest

API 입력 DTO입니다.

### Existing Validation

- `@Min(1)`
- `@Max(99_999_999)`

### Compatibility

`OptionRequest`의 Bean Validation은 유지합니다. 도메인 검증은 API 입력 검증을 대체하지 않고, 내부 생성 경로까지 보호하는 보완 검증입니다.

## Behavioral Compatibility

- DB schema는 변경하지 않습니다.
- API response schema는 변경하지 않습니다.
- 옵션명 검증 흐름은 변경하지 않습니다.
- 수량 검증 실패 예외 타입은 우선 `IllegalArgumentException`을 사용합니다.
