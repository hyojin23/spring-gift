# Data Model: Order 총액 계산 책임 분리 리팩토링

## OrderService

주문 생성 flow를 담당합니다.

### 추가 private method

```java
private int calculateTotalPrice(Option option, int quantity)
```

### 입력

| Field | Type | Description |
|-------|------|-------------|
| option | Option | 주문 옵션, 상품 가격을 제공 |
| quantity | int | 주문 수량 |

### 출력

| Type | Description |
|------|-------------|
| int | 상품 가격과 주문 수량을 곱한 총 주문 금액 |

### 규칙

- 계산식은 `option.getProduct().getPrice() * quantity`입니다.
- `quantity` 검증은 기존 request/domain/option 정책을 사용합니다.
- 반환값은 `member.deductPoint(totalPrice)`에 사용합니다.

## Member

포인트 차감 대상입니다.

### 유지 사항

- `deductPoint(int amount)` signature는 변경하지 않습니다.
- 포인트 부족 예외 정책은 유지합니다.

## Option/Product

총액 계산에 필요한 상품 가격을 제공합니다.

### 유지 사항

- `Option.getProduct().getPrice()`를 사용합니다.
- Product 가격 정책은 변경하지 않습니다.
