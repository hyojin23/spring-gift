# Tasks: Order 위시리스트 정리 리팩토링

**Input**: Design documents from `/specs/018-order-wish-cleanup-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 주문 생성 flow에 wish cleanup이 없는지 확인한다.
- [x] T002 `WishRepository.findByMemberIdAndProductId(memberId, productId)` 사용 가능 여부를 확인한다.
- [x] T003 위시 삭제 기준을 `memberId + productId`로 확정한다.
- [x] T004 주문 실패 시 cleanup을 수행하지 않는 정책을 확정한다.

## Phase 2: Tests First

- [x] T005 [P] 주문 성공 시 위시가 있으면 `WishRepository.delete()`가 호출되는 service 테스트를 추가한다.
- [x] T006 [P] 주문 성공 시 위시가 없으면 `WishRepository.delete()`가 호출되지 않는 service 테스트를 추가한다.
- [x] T007 [P] 옵션 미존재로 주문 실패 시 wish 조회/삭제가 호출되지 않는 테스트를 확인 또는 추가한다.
- [x] T008 [P] 재고 부족으로 주문 실패 시 wish 조회/삭제가 호출되지 않는 테스트를 추가한다.
- [x] T009 [P] 포인트 부족으로 주문 실패 시 wish 조회/삭제가 호출되지 않는 테스트를 추가한다.
- [x] T010 [P] 기존 주문 생성 성공/실패 controller 테스트가 유지되는지 확인한다.

## Phase 3: Service Refactor

- [x] T011 `OrderService`에 `WishRepository` 의존성을 추가한다.
- [x] T012 주문 저장 후 cleanup 메서드를 호출하도록 변경한다.
- [x] T013 cleanup 메서드에서 `memberId + productId`로 wish를 조회한다.
- [x] T014 조회 결과가 있으면 `WishRepository.delete()`를 호출한다.
- [x] T015 조회 결과가 없으면 아무 작업도 하지 않도록 구현한다.
- [x] T016 cleanup 이후 `OrderNotificationService` 호출을 유지한다.

## Phase 4: Scope Check

- [x] T017 주문 생성 성공 201 응답이 유지되는지 확인한다.
- [x] T018 주문 생성 옵션 미존재 404 응답이 유지되는지 확인한다.
- [x] T019 포인트 부족/재고 부족 400 응답이 유지되는지 확인한다.
- [x] T020 알림 서비스 분리 구조가 유지되는지 확인한다.
- [x] T021 위시 cleanup 실패 처리, 비동기 처리, 이벤트 처리가 이번 작업에 포함되지 않았는지 확인한다.

## Phase 5: Validation

- [x] T022 `./gradlew.bat test --tests *Order*`를 실행한다.
- [x] T023 `./gradlew.bat test --tests *Wish*`를 실행한다.
- [x] T024 `rg "WishRepository|cleanupWish|findByMemberIdAndProductId" src/main/java/gift/order/OrderService.java`로 cleanup 구현을 확인한다.
- [x] T025 `rg "WishRepository" src/main/java/gift/order/OrderController.java`로 controller 직접 의존이 없는지 확인한다.

## Dependencies

- T001-T004 before T005-T010
- T005-T010 before T011-T016
- T011-T016 before T017-T025

## Parallel Example

```text
T005, T006, T008, T009 can be written independently in OrderServiceTest.
T010 can be checked independently with existing OrderControllerTest.
```
