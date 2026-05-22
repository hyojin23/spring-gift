# Tasks: Admin 예외 처리 분리 리팩토링

**Input**: Design documents from `/specs/038-admin-exception-handler-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `AdminProductController` 내부 `@ExceptionHandler`를 확인한다.
- [x] T002 `AdminMemberController` 내부 `@ExceptionHandler`를 확인한다.
- [x] T003 기존 admin controller 테스트의 redirect/flash 기대값을 확인한다.

## Phase 2: Implementation

- [x] T004 `AdminProductExceptionHandler`를 추가한다.
- [x] T005 `AdminProductExceptionHandler`에 `AdminProductException` handler를 추가한다.
- [x] T006 `AdminMemberExceptionHandler`를 추가한다.
- [x] T007 `AdminMemberExceptionHandler`에 `MemberException` handler를 추가한다.
- [x] T008 `AdminProductController` 내부 `@ExceptionHandler` method를 제거한다.
- [x] T009 `AdminMemberController` 내부 `@ExceptionHandler` method를 제거한다.
- [x] T010 불필요한 import를 제거한다.

## Phase 3: Validation

- [x] T011 `./gradlew.bat test --tests *AdminProductController*`를 실행한다.
- [x] T012 `./gradlew.bat test --tests *AdminMemberController*`를 실행한다.
- [x] T013 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T010
- T004-T007 before T008-T010
- T008-T010 before T011-T013

## Parallel Example

```text
T004-T005 and T006-T007 can be implemented independently.
```
