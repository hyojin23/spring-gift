# Quickstart: Order 총액 계산 책임 분리 리팩토링

## 구현 순서

1. 기존 `OrderServiceTest.createOrder()`가 포인트 차감 금액을 검증하는지 확인합니다.
2. `OrderService`에 `calculateTotalPrice(Option option, int quantity)` private method를 추가합니다.
3. 기존 inline 계산식을 private method로 이동합니다.
4. `createOrder()`에서 `member.deductPoint(calculateTotalPrice(...))` 또는 local variable을 사용하도록 변경합니다.
5. 관련 테스트를 실행합니다.

## 검증 명령

```powershell
.\gradlew.bat test --tests *Order*
```

## 수동 확인 포인트

- 주문 총액 계산식은 기존과 동일합니다.
- 별도 pricing service/value object는 추가하지 않습니다.
- 주문 생성 성공/실패 테스트가 모두 통과합니다.
