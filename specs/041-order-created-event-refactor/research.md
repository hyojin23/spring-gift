# Research: Order Created Event Refactor

## Decision 1: Spring Application Event 사용

- **Decision**: 주문 생성 완료 시 `ApplicationEventPublisher`로 `OrderCreatedEvent`를 발행한다.
- **Rationale**: 현재 애플리케이션은 Spring 기반이며 별도 메시지 브로커가 없다. 내부 이벤트만으로 서비스 간 직접 의존을 줄이고 트랜잭션 이후 후속 작업을 분리할 수 있다.
- **Alternatives considered**:
  - 직접 서비스 호출 유지: 단순하지만 주문 생성 트랜잭션과 외부 API 호출 책임이 섞인다.
  - 메시지 큐 도입: 안정성은 높지만 현재 요구에 비해 범위가 크다.

## Decision 2: `@TransactionalEventListener(AFTER_COMMIT)` 사용

- **Decision**: 카카오 알림은 트랜잭션 커밋 이후 이벤트 리스너에서 처리한다.
- **Rationale**: 주문 저장이 롤백될 경우 알림이 발송되지 않아야 하며, 외부 API 호출은 주문 생성 트랜잭션 시간을 늘리지 않는 편이 좋다.
- **Alternatives considered**:
  - `@EventListener`: 이벤트는 받을 수 있지만 트랜잭션 커밋 이후 보장이 없다.
  - 주문 서비스 내부 try-catch: 실패 영향은 줄일 수 있지만 트랜잭션 내부 외부 호출 문제는 남는다.

## Decision 3: 기존 `OrderNotificationService` 유지

- **Decision**: 이벤트 리스너는 기존 `OrderNotificationService`를 호출한다.
- **Rationale**: 카카오 토큰 여부 확인, 발송 실패 로깅, best effort 정책이 이미 분리되어 있다. 이번 리팩토링은 호출 시점과 의존 방향 정리에 집중한다.

## Decision 4: 이벤트 데이터는 기존 도메인 객체 기반으로 시작

- **Decision**: `OrderCreatedEvent`는 우선 `Member`, `Order`, `Option`을 담는다.
- **Rationale**: 기존 `OrderNotificationService`의 입력과 맞아 변경 범위가 작다.
- **Trade-off**: 이벤트가 영속성 엔티티에 의존한다. 장기적으로는 알림에 필요한 값만 담는 이벤트 DTO로 바꾸는 것이 더 명확할 수 있다.
