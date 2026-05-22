# Quickstart: Wish 도메인 검증 강화 리팩토링

## 구현 순서

1. 기존 Wish 생성자와 테스트 fixture를 확인합니다.

```powershell
rg "new Wish\\(" src
```

2. `WishTest`를 추가합니다.

3. `WishValidationException`을 추가합니다.

```java
public class WishValidationException extends WishException {

    public WishValidationException(String message) {
        super(message);
    }
}
```

4. `Wish` 생성자에서 memberId/product null을 검증합니다.

5. `GlobalExceptionHandler`에 `WISH.INVALID` 매핑을 추가합니다.

6. product null을 쓰는 테스트 fixture를 유효한 product로 보정합니다.

7. 테스트를 실행합니다.

```powershell
.\gradlew.bat test --tests *Wish*
.\gradlew.bat test --tests *GlobalExceptionHandler*
.\gradlew.bat test
```

## 확인 포인트

- `new Wish(null, product)`가 실패해야 합니다.
- `new Wish(memberId, null)`이 실패해야 합니다.
- 기존 wish 상품 미존재/위시 미존재/권한 예외 테스트가 통과해야 합니다.
- `WISH.PRODUCT_NOT_FOUND` 응답은 유지되어야 합니다.
