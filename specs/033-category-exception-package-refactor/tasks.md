# Tasks: Category 예외 패키지 정리 리팩토링

**Input**: Design documents from `/specs/033-category-exception-package-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 category 예외 클래스 위치를 확인한다.
- [x] T002 category 예외 사용처를 검색한다.
- [x] T003 다른 도메인의 `exception` 패키지 구조를 확인한다.

## Phase 2: Implementation

- [x] T004 `gift.category.exception` 패키지를 추가한다.
- [x] T005 `CategoryNotFoundException`을 `gift.category.exception`으로 이동한다.
- [x] T006 `CategoryValidationException`을 `gift.category.exception`으로 이동한다.
- [x] T007 main 코드 import를 새 패키지로 정리한다.
- [x] T008 test 코드 import를 새 패키지로 정리한다.

## Phase 3: Validation

- [x] T009 `./gradlew.bat test --tests *Category*`를 실행한다.
- [x] T010 `./gradlew.bat test --tests *GlobalExceptionHandler*`를 실행한다.
- [x] T011 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T008
- T004 before T005-T006
- T005-T006 before T007-T008
- T007-T008 before T009-T011

## Parallel Example

```text
T007 and T008 can be performed independently after exception classes are moved.
```
