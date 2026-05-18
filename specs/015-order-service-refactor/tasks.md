# Tasks: Order 서비스 분리 리팩토링

**Input**: Design documents from `/specs/015-order-service-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `OrderController`의 repository/client 직접 의존성을 확인한다.
- [x] T002 현재 주문 목록/생성 응답 계약을 확인한다.
- [x] T003 현재 Order 테스트 부재를 확인한다.
- [x] T004 카카오 알림 best-effort 정책을 확인한다.

## Phase 2: Tests First

- [x] T005 [P] 주문 목록 조회 성공 controller 테스트를 추가한다.
- [x] T006 [P] 주문 목록 인증 실패 controller 테스트를 추가한다.
- [x] T007 [P] 주문 생성 성공 controller 테스트를 추가한다.
- [x] T008 [P] 주문 생성 인증 실패 controller 테스트를 추가한다.
- [x] T009 [P] 주문 생성 옵션 미존재 controller 테스트를 추가한다.
- [x] T010 [P] 카카오 메시지 발송 실패에도 주문 생성이 성공하는 service 테스트를 추가한다.
- [x] T011 [P] 주문 생성 시 재고와 포인트가 차감되는 service 테스트를 추가한다.

## Phase 3: Service

- [x] T012 `src/main/java/gift/order/OrderService.java`를 추가한다.
- [x] T013 주문 목록 조회 로직을 `OrderService.getOrders`로 이동한다.
- [x] T014 옵션 조회 로직을 `OrderService`로 이동한다.
- [x] T015 옵션 재고 차감과 저장 로직을 `OrderService`로 이동한다.
- [x] T016 회원 포인트 차감과 저장 로직을 `OrderService`로 이동한다.
- [x] T017 주문 저장 로직을 `OrderService`로 이동한다.
- [x] T018 카카오 알림 best-effort 로직을 `OrderService`로 이동한다.

## Phase 4: Controller Refactor

- [x] T019 `OrderController`가 `OrderService`와 `AuthenticationResolver`만 주입받도록 변경한다.
- [x] T020 `OrderController`의 `OrderRepository`, `OptionRepository`, `MemberRepository`, `WishRepository`, `KakaoMessageClient` 직접 의존을 제거한다.
- [x] T021 인증 실패 시 기존 401 응답을 유지한다.
- [x] T022 옵션 미존재 시 기존 404 응답을 유지한다.
- [x] T023 주문 생성 성공 시 기존 201 Created 응답을 유지한다.

## Phase 5: Scope Check

- [x] T024 위시 cleanup 구현이 이번 작업에 포함되지 않았는지 확인한다.
- [x] T025 order 예외 표준화가 이번 작업에 포함되지 않았는지 확인한다.
- [x] T026 카카오 알림 service 분리가 이번 작업에 포함되지 않았는지 확인한다.

## Phase 6: Validation

- [x] T027 `./gradlew.bat test --tests *Order*`를 실행한다.
- [x] T028 `./gradlew.bat test --tests *Member* --tests *Option*`를 실행한다.
- [x] T029 `rg "OrderRepository|OptionRepository|MemberRepository|WishRepository|KakaoMessageClient" src/main/java/gift/order/OrderController.java`로 controller 직접 의존성이 제거되었는지 확인한다.

## Dependencies

- T001-T004 before T005-T011
- T005-T011 before T012-T023
- T012-T018 before T019-T023
- T019-T023 before T027-T029

## Parallel Example

```text
T005, T006, T007, T008, T009 can be written independently in OrderControllerTest.
T010 and T011 can be written independently in OrderServiceTest.
```
