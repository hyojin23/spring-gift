# Tasks: Order 검증 예외 전역 처리 리팩토링

**Input**: Design documents from `/specs/021-order-validation-exception-handler/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `OrderValidationException`이 존재하는지 확인한다.
- [x] T002 `GlobalExceptionHandler`에 order validation handler가 없는지 확인한다.
- [x] T003 기존 `OrderOptionNotFoundException` handler status/code를 확인한다.
- [x] T004 기존 `GlobalExceptionHandlerTest` 작성 패턴을 확인한다.

## Phase 2: Tests First

- [x] T005 [P] `OrderValidationException`을 400으로 변환하는 handler 테스트를 추가한다.
- [x] T006 [P] 응답 code가 `ORDER.INVALID`인지 검증한다.
- [x] T007 [P] 응답 message가 예외 메시지와 같은지 검증한다.
- [x] T008 [P] 기존 `OrderOptionNotFoundException` handler 테스트가 유지되는지 확인한다.

## Phase 3: Handler

- [x] T009 `GlobalExceptionHandler`에 `OrderValidationException` import를 추가한다.
- [x] T010 `handleOrderValidation(OrderValidationException exception)` 메서드를 추가한다.
- [x] T011 `HttpStatus.BAD_REQUEST`로 응답하도록 구현한다.
- [x] T012 code를 `ORDER.INVALID`로 설정한다.
- [x] T013 message를 `exception.getMessage()`로 설정한다.

## Phase 4: Scope Check

- [x] T014 `OrderOptionNotFoundException`의 404 매핑이 유지되는지 확인한다.
- [x] T015 member/option/product/wish handler가 변경되지 않았는지 확인한다.
- [x] T016 `Order` 도메인 검증 로직이 이번 작업에서 변경되지 않았는지 확인한다.
- [x] T017 `OrderException` 상위 타입 handler가 추가되지 않았는지 확인한다.

## Phase 5: Validation

- [x] T018 `./gradlew.bat test --tests *GlobalExceptionHandler*`를 실행한다.
- [x] T019 `./gradlew.bat test --tests *Order*`를 실행한다.
- [x] T020 `rg "handleOrderValidation|ORDER.INVALID|OrderValidationException" src/main/java/gift/global src/test/java/gift/global`로 handler 연결을 확인한다.

## Dependencies

- T001-T004 before T005-T008
- T005-T008 before T009-T013
- T009-T013 before T014-T020

## Parallel Example

```text
T005, T006, T007 can be implemented together in GlobalExceptionHandlerTest.
```
