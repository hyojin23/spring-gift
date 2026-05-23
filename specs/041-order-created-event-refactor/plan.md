# Implementation Plan: Order Created Event Refactor

**Branch**: `041-order-created-event-refactor`  
**Spec**: `specs/041-order-created-event-refactor/spec.md`

## Summary

`OrderService`가 주문 생성 트랜잭션 안에서 카카오 알림 서비스를 직접 호출하던 구조를 주문 생성 이벤트 발행 구조로 변경한다. 카카오 알림은 트랜잭션 커밋 이후 이벤트 리스너가 처리한다.

## Technical Context

- Language: Java
- Framework: Spring Boot, Spring Data JPA
- Test: JUnit 5, Mockito, AssertJ
- Current behavior:
  - `OrderService#createOrder()`에서 주문 저장 후 `OrderNotificationService#sendOrderCreatedMessage()`를 직접 호출한다.
  - `OrderNotificationService`는 카카오 메시지 발송 실패를 로그로 남기고 예외를 삼킨다.

## Proposed Design

1. `OrderCreatedEvent`를 추가한다.
   - 주문 알림에 필요한 `Member`, `Order`, `Option`을 담는다.
   - 초기 리팩토링 범위를 줄이기 위해 기존 `OrderNotificationService` 시그니처와 맞춘다.

2. `OrderService`는 `ApplicationEventPublisher`를 주입받는다.
   - 주문 저장과 위시리스트 정리 후 `OrderCreatedEvent`를 발행한다.
   - `OrderNotificationService` 직접 의존성을 제거한다.

3. `OrderCreatedEventListener`를 추가한다.
   - `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`로 이벤트를 처리한다.
   - `OrderNotificationService`에 알림 발송을 위임한다.

4. 테스트를 조정한다.
   - `OrderServiceTest`: 주문 생성 성공 시 이벤트 발행 검증, 실패 시 이벤트 미발행 검증
   - `OrderCreatedEventListenerTest`: 이벤트 처리 시 알림 서비스 위임 검증
   - `OrderNotificationServiceTest`: 기존 best effort 정책 유지

## Implementation Notes

- 이벤트 발행 위치는 `cleanupWish()` 이후로 둔다.
- 이벤트 리스너는 커밋 이후 실행되므로 외부 API 실패가 주문 트랜잭션에 영향을 주지 않는다.
- 단위 테스트에서는 Spring 트랜잭션 이벤트 실행 자체보다 `OrderService`의 이벤트 발행과 리스너의 위임 책임을 분리해 검증한다.

## Risks

- `@TransactionalEventListener`는 트랜잭션이 없으면 기본적으로 실행되지 않는다. 실제 런타임에서는 `OrderService#createOrder()`가 `@Transactional`로 실행되므로 커밋 이후 처리된다.
- 이벤트에 JPA 엔티티를 담으면 리스너에서 지연 로딩 문제가 생길 수 있다. 현재 알림에 필요한 `Product`는 `Option`을 통해 접근하므로 기존 동작 범위에서는 유지한다. 필요하면 후속 리팩토링으로 이벤트 DTO를 값 기반으로 변경한다.
