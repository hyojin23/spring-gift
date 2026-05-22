# Data Model: Wish 상품 미존재 예외 분리 리팩토링

## WishProductNotFoundException

### 패키지

`gift.wish.exception`

### 상속

`WishException`

### 책임

- 위시에 추가하려는 상품이 존재하지 않는 상황을 표현합니다.

### 메시지

```text
위시에 추가할 상품을 찾을 수 없습니다.
```

### 응답 매핑

- status: 404 Not Found
- code: `WISH.PRODUCT_NOT_FOUND`

## WishNotFoundException

### 책임

- 기존 위시 항목이 존재하지 않는 상황을 표현합니다.

### 응답 매핑

- status: 404 Not Found
- code: `WISH.NOT_FOUND`

### 변경 여부

- 변경하지 않습니다.

## WishService.addWish()

### 변경 전

```java
Product product = productRepository.findById(productId)
    .orElseThrow(WishNotFoundException::new);
```

### 변경 후

```java
Product product = productRepository.findById(productId)
    .orElseThrow(WishProductNotFoundException::new);
```

## 관계

```text
WishController
  -> WishService.addWish()
  -> ProductRepository.findById()
  -> WishProductNotFoundException
  -> GlobalExceptionHandler
  -> 404 WISH.PRODUCT_NOT_FOUND
```
