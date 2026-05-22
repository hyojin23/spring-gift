# Tasks: Wish 도메인 검증 강화 리팩토링

**Input**: Design documents from `/specs/037-wish-domain-validation-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `Wish` 생성자 검증 부재를 확인한다.
- [x] T002 `new Wish(...)` 테스트 fixture 사용처를 확인한다.
- [x] T003 기존 wish 예외 계층과 global handler 매핑을 확인한다.

## Phase 2: Tests First

- [x] T004 `WishTest`를 추가한다.
- [x] T005 정상 Wish 생성 테스트를 추가한다.
- [x] T006 memberId null 생성 실패 테스트를 추가한다.
- [x] T007 product null 생성 실패 테스트를 추가한다.
- [x] T008 `GlobalExceptionHandlerTest`에 `WishValidationException` 매핑 테스트를 추가한다.

## Phase 3: Implementation

- [x] T009 `WishValidationException`을 추가한다.
- [x] T010 `Wish` 생성자에서 memberId null 검증을 추가한다.
- [x] T011 `Wish` 생성자에서 product null 검증을 추가한다.
- [x] T012 `GlobalExceptionHandler`에 `WishValidationException` handler를 추가한다.
- [x] T013 `WishServiceTest`의 null product fixture를 유효한 product로 보정한다.

## Phase 4: Validation

- [x] T014 `./gradlew.bat test --tests *Wish*`를 실행한다.
- [x] T015 `./gradlew.bat test --tests *GlobalExceptionHandler*`를 실행한다.
- [x] T016 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T008
- T004-T008 before T009-T013
- T009 before T010-T012
- T010-T013 before T014-T016

## Parallel Example

```text
T006, T007, T008 can be written independently after T004.
```
