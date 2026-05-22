# Tasks: Wish 상품 미존재 예외 분리 리팩토링

**Input**: Design documents from `/specs/036-wish-product-exception-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `WishService.addWish()`의 상품 조회 실패 예외를 확인한다.
- [x] T002 기존 `WishNotFoundException` 사용처를 확인한다.
- [x] T003 `GlobalExceptionHandler`의 wish 예외 매핑을 확인한다.

## Phase 2: Tests First

- [x] T004 `WishControllerTest`에 존재하지 않는 상품 위시 추가 실패 테스트를 추가한다.
- [x] T005 새 테스트에서 404 status를 검증한다.
- [x] T006 새 테스트에서 `WISH.PRODUCT_NOT_FOUND` code를 검증한다.
- [x] T007 `GlobalExceptionHandlerTest`에 `WishProductNotFoundException` 매핑 테스트를 추가한다.

## Phase 3: Implementation

- [x] T008 `WishProductNotFoundException`을 추가한다.
- [x] T009 `WishService.addWish()`가 상품 조회 실패 시 `WishProductNotFoundException`을 던지도록 변경한다.
- [x] T010 `GlobalExceptionHandler`에 `WishProductNotFoundException` handler를 추가한다.
- [x] T011 기존 `WishNotFoundException` handler와 code를 유지한다.

## Phase 4: Validation

- [x] T012 `./gradlew.bat test --tests *Wish*`를 실행한다.
- [x] T013 `./gradlew.bat test --tests *GlobalExceptionHandler*`를 실행한다.
- [x] T014 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T007
- T004-T007 before T008-T011
- T008 before T009-T010
- T009-T011 before T012-T014

## Parallel Example

```text
T004-T006 and T007 can be written independently before implementation.
```
