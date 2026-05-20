# Tasks: Order 알림 서비스 분리 리팩토링

**Input**: Design documents from `/specs/017-order-notification-service-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `OrderService`의 `KakaoMessageClient` 직접 의존을 확인한다.
- [x] T002 현재 `sendKakaoMessageIfPossible()`의 token 없음 정책을 확인한다.
- [x] T003 현재 `sendKakaoMessageIfPossible()`의 예외 무시 정책을 확인한다.
- [x] T004 기존 주문 생성 성공/실패 테스트 범위를 확인한다.

## Phase 2: Tests First

- [x] T005 [P] `OrderNotificationServiceTest`를 추가한다.
- [x] T006 [P] 카카오 access token이 없으면 `KakaoMessageClient`가 호출되지 않는 테스트를 추가한다.
- [x] T007 [P] 카카오 access token이 있으면 `KakaoMessageClient.sendToMe()`가 호출되는 테스트를 추가한다.
- [x] T008 [P] 카카오 메시지 발송 실패가 예외로 전파되지 않는 테스트를 추가한다.
- [x] T009 [P] 주문 생성 성공 시 `OrderService`가 `OrderNotificationService`를 호출하는 테스트를 수정/추가한다.

## Phase 3: Notification Service

- [x] T010 `src/main/java/gift/order/OrderNotificationService.java`를 추가한다.
- [x] T011 `OrderNotificationService`에 `KakaoMessageClient`를 주입한다.
- [x] T012 `sendOrderCreatedMessage(Member member, Order order, Option option)` 메서드를 추가한다.
- [x] T013 카카오 access token이 없으면 즉시 return 하도록 구현한다.
- [x] T014 카카오 메시지 발송 실패 예외를 전파하지 않도록 구현한다.

## Phase 4: OrderService Refactor

- [x] T015 `OrderService` 생성자 의존성을 `KakaoMessageClient`에서 `OrderNotificationService`로 변경한다.
- [x] T016 주문 저장 후 `OrderNotificationService`를 호출하도록 변경한다.
- [x] T017 `OrderService`의 `sendKakaoMessageIfPossible()` 메서드를 제거한다.
- [x] T018 `OrderService`에서 `KakaoMessageClient` import/field를 제거한다.

## Phase 5: Scope Check

- [x] T019 주문 생성 성공 201 응답이 유지되는지 확인한다.
- [x] T020 주문 생성 옵션 미존재 404 응답이 유지되는지 확인한다.
- [x] T021 주문 생성 포인트 부족/재고 부족 400 응답이 유지되는지 확인한다.
- [x] T022 위시 cleanup 구현이 이번 작업에 포함되지 않았는지 확인한다.
- [x] T023 비동기 처리, retry, 로깅 정책이 이번 작업에 포함되지 않았는지 확인한다.

## Phase 6: Validation

- [x] T024 `./gradlew.bat test --tests *Order*`를 실행한다.
- [x] T025 `rg "KakaoMessageClient|sendKakaoMessageIfPossible" src/main/java/gift/order/OrderService.java`로 직접 의존이 제거되었는지 확인한다.
- [x] T026 `rg "OrderNotificationService" src/main/java/gift/order src/test/java/gift/order`로 service와 테스트 연결을 확인한다.

## Dependencies

- T001-T004 before T005-T009
- T005-T009 before T010-T018
- T010-T014 before T015-T018
- T015-T018 before T019-T026

## Parallel Example

```text
T006, T007, T008 can be written independently in OrderNotificationServiceTest.
T009 can be updated independently in OrderServiceTest.
```
