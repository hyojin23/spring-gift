# ADR-0004: 주문 생성 후 알림 발송을 AFTER_COMMIT 이벤트로 분리

## 상태

Accepted

## 맥락

주문 생성 흐름에서는 옵션 수량 차감, 회원 포인트 차감, 주문 저장, 위시 정리, 카카오 메시지 발송이 함께 발생한다.

이 중 옵션 수량 차감, 포인트 차감, 주문 저장, 위시 정리는 주문 생성 트랜잭션 안에서 함께 성공하거나 실패해야 한다. 반면 카카오 메시지 발송은 외부 API 호출이며, 주문 저장이 커밋된 이후에 수행되어야 한다.

고려한 선택지는 다음과 같다.

1. `OrderService`에서 `OrderNotificationService`를 직접 호출한다.
2. 주문 생성 후 Spring application event를 발행하고 `@TransactionalEventListener(AFTER_COMMIT)`에서 알림을 발송한다.
3. `@EventListener`로 이벤트를 처리한다.
4. 메시지 큐 또는 별도 비동기 메시징 시스템을 도입한다.

## 결정

주문 생성이 성공하면 `OrderService`에서 `OrderCreatedEvent`를 발행한다. 카카오 알림 발송은 `OrderCreatedEventListener`에서 `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`로 처리한다.

예상 흐름:

1. `OrderService#createOrder`가 주문 생성 트랜잭션을 시작한다.
2. 옵션 수량 차감, 포인트 차감, 주문 저장, 위시 정리를 수행한다.
3. 주문 생성이 완료되면 `OrderCreatedEvent`를 발행한다.
4. 트랜잭션이 커밋된 후 `OrderCreatedEventListener`가 이벤트를 처리한다.
5. 리스너는 `OrderNotificationService`에 카카오 메시지 발송을 위임한다.

## 이유

주문 생성 트랜잭션과 외부 API 호출은 성격이 다르다. 주문 데이터가 롤백될 경우 알림이 발송되면 안 되며, 카카오 API 호출 실패가 주문 생성 트랜잭션의 커밋 여부에 직접 영향을 주는 것도 적절하지 않다.

`@TransactionalEventListener(AFTER_COMMIT)`를 사용하면 주문 생성이 실제로 커밋된 이후에만 알림을 처리할 수 있다. 또한 `OrderService`가 알림 서비스에 직접 의존하지 않으므로 주문 생성 책임과 후속 알림 책임을 분리할 수 있다.

메시지 큐를 도입하면 더 안정적인 비동기 처리가 가능하지만, 현재 요구사항과 프로젝트 범위에서는 Spring 내부 이벤트만으로 필요한 책임 분리를 달성할 수 있다고 판단했다.

## 결과

장점:

- 주문 생성 트랜잭션 안에서 외부 API를 직접 호출하지 않는다.
- 주문 저장이 롤백되면 알림이 발송되지 않는다.
- `OrderService`의 직접 의존이 줄고 주문 생성 책임이 명확해진다.
- 알림 발송 실패가 주문 생성 결과에 영향을 주지 않는 best effort 정책을 유지할 수 있다.

단점:

- 이벤트 기반 흐름이 추가되어 직접 호출보다 실행 흐름을 추적하기 어렵다.
- `@TransactionalEventListener`는 트랜잭션이 없는 상황에서는 기본적으로 실행되지 않으므로 사용 위치를 주의해야 한다.
- 현재 `OrderCreatedEvent`가 `Member`, `Order`, `Option` 같은 도메인 객체를 담고 있어 이벤트가 영속성 객체에 의존한다.

## 대안

### OrderService에서 직접 알림 서비스 호출

`OrderService`가 주문 저장 후 `OrderNotificationService`를 직접 호출하는 방식도 고려했다.

이 방식은 구현이 단순하고 흐름을 따라가기 쉽다. 다만 주문 생성 트랜잭션 안에 외부 API 호출 책임이 섞이고, 알림 발송 시점이 커밋 이후임을 보장하기 어렵다.

### @EventListener 사용

Spring application event를 사용하되 일반 `@EventListener`로 처리하는 방식도 고려했다.

이 방식은 서비스 간 의존을 줄일 수 있다. 다만 트랜잭션 커밋 이후 실행을 보장하지 않으므로, 주문 저장이 롤백되는 경우와 알림 발송 시점 사이의 경계를 명확히 표현하기 어렵다.

### 메시지 큐 도입

별도 메시지 큐를 도입해 주문 생성 후 알림을 비동기로 처리하는 방식도 고려했다.

이 방식은 재시도, 장애 격리, 확장성 측면에서 장점이 있다. 다만 현재 프로젝트 범위에서는 인프라와 운영 복잡도가 커지므로 이번 결정에서는 제외했다.

## 적용 범위

- `OrderService#createOrder`
- `OrderCreatedEvent`
- `OrderCreatedEventListener`
- `OrderNotificationService`를 통한 카카오 주문 알림 발송

주문 생성 자체의 도메인 정책, 카카오 메시지 템플릿, 메시지 재시도 정책에는 이 결정을 적용하지 않는다.
