# Tasks: 인증 Member 추출 공통화 리팩토링

**Input**: Design documents from `/specs/035-authenticated-member-resolver-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 order/wish controller의 중복 인증 추출 코드를 확인한다.
- [x] T002 `AuthenticationResolver.extractMember()`의 null 반환 정책을 확인한다.
- [x] T003 `AuthenticationException`의 global handler 매핑을 확인한다.

## Phase 2: Tests First

- [x] T004 `AuthenticatedMemberResolverTest`를 추가한다.
- [x] T005 유효한 Authorization header에서 member를 반환하는 테스트를 추가한다.
- [x] T006 invalid Authorization header에서 `AuthenticationException`이 발생하는 테스트를 추가한다.
- [x] T007 null/blank Authorization header에서 `AuthenticationException`이 발생하는 테스트를 추가한다.

## Phase 3: Implementation

- [x] T008 `AuthenticatedMemberResolver`를 추가한다.
- [x] T009 `OrderController`가 `AuthenticatedMemberResolver`를 사용하도록 변경한다.
- [x] T010 `WishController`가 `AuthenticatedMemberResolver`를 사용하도록 변경한다.
- [x] T011 `OrderController`의 private 인증 member 추출 method를 제거한다.
- [x] T012 `WishController`의 private 인증 member 추출 method를 제거한다.

## Phase 4: Validation

- [x] T013 `./gradlew.bat test --tests *AuthenticatedMemberResolver*`를 실행한다.
- [x] T014 `./gradlew.bat test --tests *OrderController*`를 실행한다.
- [x] T015 `./gradlew.bat test --tests *WishController*`를 실행한다.
- [x] T016 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T007
- T004-T007 before T008-T012
- T008 before T009-T012
- T009-T012 before T013-T016

## Parallel Example

```text
T005, T006, T007 can be written independently after T004.
```
