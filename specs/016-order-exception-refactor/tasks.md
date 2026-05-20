# Tasks: Order 예외 처리 리팩토링

**Input**: Design documents from `/specs/016-order-exception-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `OrderService.createOrder()`의 `Optional` 반환과 옵션 미존재 분기를 확인한다.
- [x] T002 현재 `OrderController.createOrder()`의 404 분기를 확인한다.
- [x] T003 현재 `GlobalExceptionHandler`의 order/member point 예외 처리 부재를 확인한다.
- [x] T004 기존 `ErrorResponse` code naming pattern을 확인한다.

## Phase 2: Tests First

- [x] T005 [P] 주문 생성 옵션 미존재 시 404와 `ORDER.OPTION_NOT_FOUND`를 반환하는 controller 테스트를 추가/수정한다.
- [x] T006 [P] 주문 생성 포인트 부족 시 400과 `MEMBER.INSUFFICIENT_POINT`를 반환하는 controller 테스트를 추가한다.
- [x] T007 [P] 주문 생성 재고 부족 시 기존 `OPTION.INVALID_QUANTITY` 응답이 유지되는 테스트를 추가한다.
- [x] T008 [P] `OrderService.createOrder()`가 옵션 미존재 시 `OrderOptionNotFoundException`을 던지는 service 테스트를 추가한다.
- [x] T009 [P] 주문 생성 성공 시 기존 201 Created 응답이 유지되는 테스트를 확인한다.

## Phase 3: Order Exception

- [x] T010 `src/main/java/gift/order/exception/OrderException.java`를 추가한다.
- [x] T011 `src/main/java/gift/order/exception/OrderOptionNotFoundException.java`를 추가한다.
- [x] T012 주문 옵션 미존재 예외 메시지를 한글로 작성한다.

## Phase 4: Service Refactor

- [x] T013 `OrderService.createOrder()` 반환 타입을 `OrderResponse`로 변경한다.
- [x] T014 옵션 조회 결과가 없으면 `OrderOptionNotFoundException`을 발생시키도록 변경한다.
- [x] T015 성공 시 `OrderResponse.from(saved)`를 직접 반환하도록 변경한다.
- [x] T016 카카오 알림 best-effort catch 정책이 유지되는지 확인한다.

## Phase 5: Controller Refactor

- [x] T017 `OrderController.createOrder()`의 `Optional` empty 분기를 제거한다.
- [x] T018 `OrderController.createOrder()`가 service 반환값으로 201 Created를 구성하도록 변경한다.
- [x] T019 인증 실패 시 기존 401 응답이 유지되는지 확인한다.

## Phase 6: Global Handler

- [x] T020 `GlobalExceptionHandler`에 `OrderOptionNotFoundException` handler를 추가한다.
- [x] T021 `OrderOptionNotFoundException`을 404와 `ORDER.OPTION_NOT_FOUND` code로 매핑한다.
- [x] T022 `GlobalExceptionHandler`에 `InsufficientMemberPointException` handler를 추가한다.
- [x] T023 `InsufficientMemberPointException`을 400과 `MEMBER.INSUFFICIENT_POINT` code로 매핑한다.
- [x] T024 기존 `OptionQuantityException` handler가 주문 재고 부족에도 사용되는지 확인한다.

## Phase 7: Scope Check

- [x] T025 위시 cleanup 구현이 이번 작업에 포함되지 않았는지 확인한다.
- [x] T026 카카오 알림 service 분리가 이번 작업에 포함되지 않았는지 확인한다.
- [x] T027 인증 실패 구조 변경이 이번 작업에 포함되지 않았는지 확인한다.

## Phase 8: Validation

- [x] T028 `./gradlew.bat test --tests *Order*`를 실행한다.
- [x] T029 `./gradlew.bat test --tests *GlobalExceptionHandler*`를 실행한다.
- [x] T030 `./gradlew.bat test --tests *Member* --tests *Option*`를 실행한다.
- [x] T031 `rg "Optional<OrderResponse>|Optional.empty|response.isEmpty" src/main/java/gift/order`로 Optional 기반 주문 생성 실패 표현이 제거되었는지 확인한다.

## Dependencies

- T001-T004 before T005-T009
- T005-T009 before T010-T024
- T010-T012 before T013-T018
- T020-T024 before T028-T030
- T025-T027 before T031

## Parallel Example

```text
T005, T006, T007 can be written independently in OrderControllerTest.
T008 and T009 can be written independently in OrderServiceTest.
```
