# Data Model: Order Created Event Refactor

## OrderCreatedEvent

주문 생성 완료 후 발행되는 애플리케이션 이벤트.

### Fields

- `Member member`
  - 주문한 회원
  - 카카오 액세스 토큰 확인에 사용
- `Order order`
  - 저장된 주문
  - 메시지 내용과 로그 식별자에 사용
- `Option option`
  - 주문 옵션
  - 상품 정보 조회에 사용

### Validation

- 이벤트 생성 시 `member`, `order`, `option`은 null이면 안 된다.
- 단순 record로 구현할 경우 별도 검증 없이 서비스 계층에서 유효한 객체만 전달한다.

## OrderCreatedEventListener

주문 생성 이벤트를 커밋 이후 처리하는 리스너.

### Dependencies

- `OrderNotificationService`

### Behavior

- `OrderCreatedEvent`를 수신한다.
- `OrderNotificationService#sendOrderCreatedMessage(member, order, option)`을 호출한다.
- 알림 실패 처리는 `OrderNotificationService`의 기존 정책에 맡긴다.

## OrderService Dependency Change

### Before

- `OrderNotificationService` 직접 의존

### After

- `ApplicationEventPublisher` 의존
- 주문 생성 성공 후 `OrderCreatedEvent` 발행
