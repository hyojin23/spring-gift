# Quickstart: Order 위시리스트 정리 리팩토링

## 구현 순서

1. `OrderServiceTest`에 위시가 있는 상품 주문 시 wish 삭제 테스트를 추가합니다.
2. `OrderServiceTest`에 위시가 없는 상품 주문 시 delete 미호출 테스트를 추가합니다.
3. `OrderServiceTest`에 주문 실패 시 wish cleanup 미호출 테스트를 추가합니다.
4. `OrderService`에 `WishRepository`를 주입합니다.
5. 주문 저장 후 `cleanupWish(member.getId(), option.getProduct().getId())`를 호출합니다.
6. `cleanupWish()`에서 `findByMemberIdAndProductId()` 결과가 있으면 삭제합니다.
7. cleanup 이후 기존 `OrderNotificationService` 호출 순서를 유지합니다.
8. 관련 테스트를 실행합니다.

## 검증 명령

```powershell
.\gradlew.bat test --tests *Order*
.\gradlew.bat test --tests *Wish*
```

## 수동 확인 포인트

- 주문 성공 후 위시가 있으면 삭제됩니다.
- 주문 성공 후 위시가 없으면 delete가 호출되지 않습니다.
- 주문 실패 시 wish 조회와 삭제가 수행되지 않습니다.
- 삭제 기준은 `memberId + productId`입니다.
- 주문 생성 API 응답 계약은 기존과 같습니다.
