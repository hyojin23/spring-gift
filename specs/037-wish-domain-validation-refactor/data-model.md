# Data Model: Wish 도메인 검증 강화 리팩토링

## Wish

### 필드

- `id`: DB 식별자
- `memberId`: Wish 소유 회원 id
- `product`: Wish 대상 상품

### 불변 조건

- `memberId`는 null일 수 없습니다.
- `product`는 null일 수 없습니다.

### 생성 규칙

```java
public Wish(Long memberId, Product product) {
    validate(memberId, product);
    this.memberId = memberId;
    this.product = product;
}
```

## WishValidationException

### 패키지

`gift.wish.exception`

### 상속

`WishException`

### 책임

- Wish 도메인 검증 실패를 표현합니다.

### 응답 매핑

- status: 400 Bad Request
- code: `WISH.INVALID`

### 메시지 후보

- memberId null: `회원 id는 필수입니다.`
- product null: `위시 상품은 필수입니다.`

## GlobalExceptionHandler

### 추가 매핑

```java
@ExceptionHandler(WishValidationException.class)
public ResponseEntity<ErrorResponse> handleWishValidation(WishValidationException exception) {
    return error(HttpStatus.BAD_REQUEST, "WISH.INVALID", exception.getMessage());
}
```

## 관계

```text
WishService.addWish()
  -> new Wish(memberId, product)
  -> Wish.validate()
  -> WishValidationException
  -> GlobalExceptionHandler
  -> 400 WISH.INVALID
```
