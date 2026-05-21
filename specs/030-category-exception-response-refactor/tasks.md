# Tasks: Category 예외 응답 일관화 리팩토링

**Input**: Design documents from `/specs/030-category-exception-response-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `GlobalExceptionHandler.handleCategoryNotFound()`의 현재 반환 타입을 확인한다.
- [x] T002 기존 `ErrorResponse` code/message 생성 방식을 확인한다.
- [x] T003 `CategoryControllerTest`의 미존재 category 테스트를 확인한다.

## Phase 2: Tests First

- [x] T004 [P] `GlobalExceptionHandlerTest`에 category 미존재 예외 테스트를 추가한다.
- [x] T005 [P] `CategoryControllerTest.updateNotFoundCategory()`에 `code` body 검증을 추가한다.
- [x] T006 [P] `CategoryControllerTest.updateNotFoundCategory()`에 `message` body 검증을 추가한다.

## Phase 3: Implementation

- [x] T007 `handleCategoryNotFound()`가 `CategoryNotFoundException` 인자를 받도록 변경한다.
- [x] T008 `handleCategoryNotFound()` 반환 타입을 `ResponseEntity<ErrorResponse>`로 변경한다.
- [x] T009 `CATEGORY.NOT_FOUND` code를 사용한다.
- [x] T010 exception message를 error response message로 사용한다.
- [x] T011 기존 category 정상 API 동작이 변경되지 않았는지 확인한다.

## Phase 4: Validation

- [x] T012 `./gradlew.bat test --tests *CategoryController*`를 실행한다.
- [x] T013 `./gradlew.bat test --tests *GlobalExceptionHandler*`를 실행한다.
- [x] T014 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T006
- T004-T006 before T007-T010
- T011 before T012-T014

## Parallel Example

```text
T004 can be added independently in GlobalExceptionHandlerTest.
T005, T006 can be added independently in CategoryControllerTest.
```
