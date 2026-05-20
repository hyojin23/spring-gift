# Tasks: Order 도메인 검증 강화 리팩토링

**Input**: Design documents from `/specs/019-order-domain-validation-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `Order` 생성자에 검증이 없는지 확인한다.
- [x] T002 기존 `OrderException` 계층을 확인한다.
- [x] T003 `OrderRequest` Bean Validation 정책을 확인한다.
- [x] T004 기존 order 테스트 fixture가 유효한 값을 사용하는지 확인한다.

## Phase 2: Tests First

- [x] T005 [P] 유효한 값으로 `Order`가 생성되는 테스트를 추가한다.
- [x] T006 [P] `option == null`이면 `OrderValidationException`이 발생하는 테스트를 추가한다.
- [x] T007 [P] `memberId == null`이면 `OrderValidationException`이 발생하는 테스트를 추가한다.
- [x] T008 [P] `quantity == 0`이면 `OrderValidationException`이 발생하는 테스트를 추가한다.
- [x] T009 [P] `quantity < 0`이면 `OrderValidationException`이 발생하는 테스트를 추가한다.
- [x] T010 [P] `message`가 null이어도 생성 가능한 테스트를 추가한다.

## Phase 3: Exception

- [x] T011 `src/main/java/gift/order/exception/OrderValidationException.java`를 추가한다.
- [x] T012 `OrderValidationException`이 `OrderException`을 상속하도록 구현한다.
- [x] T013 검증 실패 메시지를 한글로 작성한다.

## Phase 4: Domain Refactor

- [x] T014 `Order` 생성자에서 `option` null 검증을 추가한다.
- [x] T015 `Order` 생성자에서 `memberId` null 검증을 추가한다.
- [x] T016 `Order` 생성자에서 `quantity` 1 이상 검증을 추가한다.
- [x] T017 `message` 선택값 정책을 유지한다.
- [x] T018 `orderDateTime` 자동 설정 동작을 유지한다.

## Phase 5: Scope Check

- [x] T019 주문 생성 성공 201 응답이 유지되는지 확인한다.
- [x] T020 주문 생성 실패 400/404 응답이 유지되는지 확인한다.
- [x] T021 위시 cleanup과 주문 알림 호출 흐름이 유지되는지 확인한다.
- [x] T022 DB 제약 추가가 이번 작업에 포함되지 않았는지 확인한다.
- [x] T023 `GlobalExceptionHandler` 변경이 이번 작업에 포함되지 않았는지 확인한다.

## Phase 6: Validation

- [x] T024 `./gradlew.bat test --tests *Order*`를 실행한다.
- [x] T025 `rg "new Order\\(" src/test/java/gift src/main/java/gift`로 fixture가 새 검증 조건을 만족하는지 확인한다.
- [x] T026 `rg "OrderValidationException|validateOption|validateMemberId|validateQuantity" src/main/java/gift/order src/test/java/gift/order`로 구현을 확인한다.

## Dependencies

- T001-T004 before T005-T010
- T005-T010 before T011-T018
- T011-T013 before T014-T018
- T014-T018 before T019-T026

## Parallel Example

```text
T005, T006, T007, T008, T009, T010 can be written independently in OrderTest.
```
