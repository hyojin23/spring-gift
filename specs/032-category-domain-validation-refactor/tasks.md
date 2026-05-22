# Tasks: Category 도메인 검증 강화 리팩토링

**Input**: Design documents from `/specs/032-category-domain-validation-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `CategoryRequest`의 현재 bean validation을 확인한다.
- [x] T002 `Category` 생성자/update의 현재 검증 부재를 확인한다.
- [x] T003 기존 category controller fixture가 유효한 값을 사용하는지 확인한다.

## Phase 2: Tests First

- [x] T004 [P] `CategoryTest`를 추가한다.
- [x] T005 [P] name blank 생성 실패 테스트를 추가한다.
- [x] T006 [P] color blank 생성 실패 테스트를 추가한다.
- [x] T007 [P] imageUrl blank 생성 실패 테스트를 추가한다.
- [x] T008 [P] name/color/imageUrl blank update 실패 테스트를 추가한다.
- [x] T009 [P] 정상 생성/update 테스트를 추가한다.
- [x] T010 [P] `GlobalExceptionHandlerTest`에 category validation 예외 테스트를 추가한다.

## Phase 3: Implementation

- [x] T011 `CategoryValidationException`을 추가한다.
- [x] T012 `Category` 생성자에서 공통 validate method를 호출한다.
- [x] T013 `Category.update()`에서 공통 validate method를 호출한다.
- [x] T014 name null/blank 검증을 추가한다.
- [x] T015 color null/blank 검증을 추가한다.
- [x] T016 imageUrl null/blank 검증을 추가한다.
- [x] T017 `GlobalExceptionHandler`에 `CategoryValidationException` handler를 추가한다.
- [x] T018 handler에서 `CATEGORY.INVALID` code를 사용한다.

## Phase 4: Validation

- [x] T019 `./gradlew.bat test --tests *Category*`를 실행한다.
- [x] T020 `./gradlew.bat test --tests *GlobalExceptionHandler*`를 실행한다.
- [x] T021 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T010
- T004-T010 before T011-T018
- T011 before T012-T018
- T018 before T019-T021

## Parallel Example

```text
T005, T006, T007 can be written independently for constructor validation.
T008 can cover update validation cases in one parameterized-style test or separate tests.
```
