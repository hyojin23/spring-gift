# Tasks: 주문 알림 실패 로깅 리팩토링

**Input**: Design documents from `/specs/031-order-notification-logging-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `OrderNotificationService`의 빈 catch 위치를 확인한다.
- [x] T002 기존 `OrderNotificationServiceTest`의 best effort 테스트를 확인한다.
- [x] T003 로그에 포함할 정보와 제외할 민감 정보를 확인한다.

## Phase 2: Tests

- [x] T004 [P] access token 없음 테스트가 유지되는지 확인한다.
- [x] T005 [P] 정상 메시지 발송 테스트가 유지되는지 확인한다.
- [x] T006 [P] 메시지 발송 실패 시 예외 미전파 테스트가 유지되는지 확인한다.

## Phase 3: Implementation

- [x] T007 `OrderNotificationService`에 logger를 추가한다.
- [x] T008 `catch (Exception ignored)`를 의미 있는 변수명으로 변경한다.
- [x] T009 메시지 발송 실패 시 warn 로그를 남긴다.
- [x] T010 warn 로그에 order id를 포함한다.
- [x] T011 warn 로그에 access token이 포함되지 않도록 확인한다.

## Phase 4: Validation

- [x] T012 `./gradlew.bat test --tests *OrderNotificationService*`를 실행한다.
- [x] T013 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T006
- T004-T006 before T007-T011
- T011 before T012-T013

## Parallel Example

```text
T004, T005, T006 can be reviewed independently in OrderNotificationServiceTest.
```
