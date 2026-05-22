# Tasks: Order 인증 예외 응답 일관화 리팩토링

**Input**: Design documents from `/specs/034-order-auth-exception-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `OrderController`의 직접 401 반환 코드를 확인한다.
- [x] T002 `WishController`의 인증 실패 예외 처리 흐름을 확인한다.
- [x] T003 `AuthenticationException`의 global handler 매핑을 확인한다.

## Phase 2: Tests First

- [x] T004 `OrderControllerTest`의 주문 목록 인증 실패 테스트에 `AUTH.UNAUTHORIZED` body 검증을 추가한다.
- [x] T005 `OrderControllerTest`의 주문 생성 인증 실패 테스트에 `AUTH.UNAUTHORIZED` body 검증을 추가한다.
- [x] T006 Authorization header 누락 케이스를 인증 실패 응답으로 검증한다.

## Phase 3: Implementation

- [x] T007 `OrderController`에 인증 member 추출 private method를 추가한다.
- [x] T008 member가 null이면 `AuthenticationException`을 던지도록 변경한다.
- [x] T009 주문 목록 조회에서 직접 빈 401 반환을 제거한다.
- [x] T010 주문 생성에서 직접 빈 401 반환을 제거한다.
- [x] T011 `@RequestHeader`를 `required = false`로 변경한다.
- [x] T012 `ResponseEntity<?>` 반환 타입을 실제 응답 타입에 맞게 정리한다.

## Phase 4: Validation

- [x] T013 `./gradlew.bat test --tests *OrderController*`를 실행한다.
- [x] T014 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T006
- T004-T006 before T007-T012
- T007-T012 before T013-T014

## Parallel Example

```text
T004 and T005 can be updated independently before implementation.
```
