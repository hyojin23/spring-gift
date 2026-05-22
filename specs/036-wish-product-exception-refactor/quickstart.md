# Quickstart: Wish 상품 미존재 예외 분리 리팩토링

## 구현 순서

1. 현재 상품 조회 실패 예외 사용처를 확인합니다.

```powershell
rg "ProductRepository|WishNotFoundException" src/main/java/gift/wish src/test/java/gift/wish
```

2. `WishProductNotFoundException`을 추가합니다.

```java
public class WishProductNotFoundException extends WishException {

    public WishProductNotFoundException() {
        super("위시에 추가할 상품을 찾을 수 없습니다.");
    }
}
```

3. `WishService.addWish()`에서 상품 조회 실패 시 새 예외를 던지도록 변경합니다.

4. `GlobalExceptionHandler`에 handler를 추가합니다.

```java
@ExceptionHandler(WishProductNotFoundException.class)
public ResponseEntity<ErrorResponse> handleWishProductNotFound(WishProductNotFoundException exception) {
    return error(HttpStatus.NOT_FOUND, "WISH.PRODUCT_NOT_FOUND", exception.getMessage());
}
```

5. 테스트를 추가/수정합니다.

```powershell
.\gradlew.bat test --tests *Wish*
.\gradlew.bat test --tests *GlobalExceptionHandler*
.\gradlew.bat test
```

## 확인 포인트

- 위시 추가의 product 미존재는 `WISH.PRODUCT_NOT_FOUND`를 반환해야 합니다.
- 위시 삭제의 wish 미존재는 기존 `WISH.NOT_FOUND`를 유지해야 합니다.
- 중복 위시 추가와 신규 위시 추가 성공 응답은 유지되어야 합니다.
