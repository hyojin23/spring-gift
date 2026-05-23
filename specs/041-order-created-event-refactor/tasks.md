# Tasks: Order Created Event Refactor

## Phase 1: Event Model

- [x] T001 `OrderCreatedEvent` 추가
- [x] T002 이벤트가 주문 알림에 필요한 `Member`, `Order`, `Option`을 담도록 구성

## Phase 2: Service Refactor

- [x] T003 `OrderService`에서 `OrderNotificationService` 직접 의존 제거
- [x] T004 `OrderService`에 `ApplicationEventPublisher` 주입
- [x] T005 주문 생성 성공 후 `OrderCreatedEvent` 발행
- [x] T006 주문 생성 실패 경로에서는 이벤트가 발행되지 않도록 테스트 보강

## Phase 3: Listener

- [x] T007 `OrderCreatedEventListener` 추가
- [x] T008 `@TransactionalEventListener(phase = AFTER_COMMIT)` 적용
- [x] T009 리스너에서 `OrderNotificationService` 호출

## Phase 4: Tests

- [x] T010 `OrderServiceTest`의 직접 알림 호출 검증을 이벤트 발행 검증으로 변경
- [x] T011 `OrderCreatedEventListenerTest` 추가
- [x] T012 기존 `OrderNotificationServiceTest` 유지 및 통과 확인
- [x] T013 전체 테스트 실행

## Phase 5: Review

- [x] T014 `OrderService`의 주문 생성 책임이 트랜잭션 내부 작업과 이벤트 발행으로 제한되었는지 확인
- [x] T015 카카오 알림 실패가 주문 생성 결과에 영향을 주지 않는지 확인
