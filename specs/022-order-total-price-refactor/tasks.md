# Tasks: Order 총액 계산 책임 분리 리팩토링

**Input**: Design documents from `/specs/022-order-total-price-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `OrderService.createOrder()`의 inline 가격 계산식을 확인한다.
- [x] T002 기존 `OrderServiceTest.createOrder()`가 포인트 차감 금액을 검증하는지 확인한다.
- [x] T003 주문 실패 flow 테스트 범위를 확인한다.
- [x] T004 위시 cleanup과 알림 호출 순서를 확인한다.

## Phase 2: Tests First

- [x] T005 [P] 기존 주문 생성 테스트가 `상품 가격 * 수량` 포인트 차감을 검증하는지 확인한다.
- [x] T006 [P] 기존 주문 실패 테스트가 유지되는지 확인한다.

## Phase 3: Service Refactor

- [x] T007 `OrderService`에 `calculateTotalPrice(Option option, int quantity)` private method를 추가한다.
- [x] T008 기존 계산식 `option.getProduct().getPrice() * quantity`를 private method로 이동한다.
- [x] T009 `createOrder()`에서 포인트 차감 금액으로 private method 결과를 사용한다.
- [x] T010 계산식, 반환 타입, 포인트 차감 정책을 변경하지 않는다.

## Phase 4: Scope Check

- [x] T011 주문 생성 성공 flow가 유지되는지 확인한다.
- [x] T012 주문 생성 실패 flow가 유지되는지 확인한다.
- [x] T013 위시 cleanup과 알림 호출 순서가 유지되는지 확인한다.
- [x] T014 별도 pricing service/value object가 추가되지 않았는지 확인한다.
- [x] T015 overflow/long 전환 같은 타입 정책 변경이 포함되지 않았는지 확인한다.

## Phase 5: Validation

- [x] T016 `./gradlew.bat test --tests *Order*`를 실행한다.
- [x] T017 `rg "calculateTotalPrice|deductPoint|Product\\(\\)\\.getPrice\\(\\) \\*" src/main/java/gift/order/OrderService.java`로 구현을 확인한다.

## Dependencies

- T001-T004 before T005-T006
- T005-T006 before T007-T010
- T007-T010 before T011-T017

## Parallel Example

```text
T005 and T006 can be checked independently before implementation.
```
