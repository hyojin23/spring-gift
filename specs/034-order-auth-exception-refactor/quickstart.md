# Quickstart: Order 인증 예외 응답 일관화 리팩토링

## 구현 순서

1. `OrderController`의 직접 401 반환 코드를 확인합니다.

```powershell
rg "ResponseEntity.status\\(401\\)|auth check" src/main/java/gift/order
```

2. `WishController`의 인증 실패 처리 방식을 참고합니다.

3. `OrderController`에 private member 추출 method를 추가합니다.

```java
private Member extractMember(String authorization) {
    Member member = authenticationResolver.extractMember(authorization);
    if (member == null) {
        throw new AuthenticationException();
    }
    return member;
}
```

4. `@RequestHeader`를 `required = false`로 변경합니다.

5. `OrderControllerTest`의 인증 실패 테스트에서 response body를 검증합니다.

```java
.andExpect(status().isUnauthorized())
.andExpect(jsonPath("$.code").value("AUTH.UNAUTHORIZED"));
```

6. 테스트를 실행합니다.

```powershell
.\gradlew.bat test --tests *OrderController*
.\gradlew.bat test
```

## 확인 포인트

- `OrderController`에 직접 빈 401 반환이 남아 있지 않아야 합니다.
- 인증 실패 응답에 `AUTH.UNAUTHORIZED` code가 포함되어야 합니다.
- 정상 주문 조회/생성 응답은 유지되어야 합니다.
