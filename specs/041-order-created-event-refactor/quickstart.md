# Quickstart: Order Created Event Refactor

## Implementation Steps

1. `gift.order.OrderCreatedEvent`를 추가한다.
2. `OrderService`의 `OrderNotificationService` 의존성을 `ApplicationEventPublisher`로 교체한다.
3. `OrderService#createOrder()`에서 주문 저장과 위시 정리 후 `OrderCreatedEvent`를 발행한다.
4. `gift.order.OrderCreatedEventListener`를 추가한다.
5. 리스너 메서드에 `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`를 적용한다.
6. 리스너에서 `OrderNotificationService`로 알림 발송을 위임한다.
7. `OrderServiceTest`를 이벤트 발행 검증 중심으로 수정한다.
8. `OrderCreatedEventListenerTest`를 추가한다.
9. 전체 테스트를 실행한다.

## Verification

```powershell
.\gradlew.bat test
```

## Expected Result

- 주문 생성 성공 테스트에서 `ApplicationEventPublisher#publishEvent()` 호출이 검증된다.
- 주문 생성 실패 테스트에서 이벤트가 발행되지 않는다.
- 이벤트 리스너 테스트에서 `OrderNotificationService` 위임이 검증된다.
- 기존 `OrderNotificationService` 테스트는 그대로 통과한다.
