# Quickstart: Order 예외 처리 리팩토링

## 구현 순서

1. `OrderControllerTest`에 옵션 미존재 응답 body 검증을 추가합니다.
2. `OrderControllerTest` 또는 `OrderServiceTest`에 포인트 부족 주문 실패 검증을 추가합니다.
3. `gift.order.exception.OrderException`을 추가합니다.
4. `gift.order.exception.OrderOptionNotFoundException`을 추가합니다.
5. `OrderService.createOrder()` 반환 타입을 `OrderResponse`로 변경합니다.
6. 옵션 미존재 시 `OrderOptionNotFoundException`을 던지도록 변경합니다.
7. `OrderController.createOrder()`의 `Optional` 분기를 제거합니다.
8. `GlobalExceptionHandler`에 order/member point 예외 핸들러를 추가합니다.
9. 관련 테스트를 실행합니다.

## 검증 명령

```powershell
.\gradlew.bat test --tests *Order*
.\gradlew.bat test --tests *GlobalExceptionHandler*
.\gradlew.bat test --tests *Member* --tests *Option*
```

## 수동 확인 포인트

- 존재하지 않는 옵션 ID로 주문 생성 시 404와 `ORDER.OPTION_NOT_FOUND`가 반환됩니다.
- 포인트 부족 시 400과 `MEMBER.INSUFFICIENT_POINT`가 반환됩니다.
- 재고 부족 시 기존 `OPTION.INVALID_QUANTITY` 응답이 유지됩니다.
- 성공 주문 생성은 기존처럼 201 Created를 반환합니다.
- 카카오 알림 실패는 주문 생성 성공을 막지 않습니다.
