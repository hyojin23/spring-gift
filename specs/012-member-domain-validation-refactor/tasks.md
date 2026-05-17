# Tasks: Member 도메인 검증 강화 리팩토링

**Input**: Design documents from `/specs/012-member-domain-validation-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `Member` 생성자와 `update()`의 필드 할당 구조를 확인한다.
- [x] T002 `Member(String email)`이 Kakao OAuth flow에서 사용되는지 확인한다.
- [x] T003 기존 member 예외 계층과 테스트 구조를 확인한다.

## Phase 2: Tests First

- [x] T004 [P] 일반 회원 생성 시 빈 email이면 `MemberValidationException`이 발생하는 테스트를 추가한다.
- [x] T005 [P] 일반 회원 생성 시 빈 password이면 `MemberValidationException`이 발생하는 테스트를 추가한다.
- [x] T006 [P] 카카오 회원 생성 시 빈 email이면 `MemberValidationException`이 발생하는 테스트를 추가한다.
- [x] T007 [P] 카카오 회원 생성 시 password 없이 생성되는 테스트를 추가한다.
- [x] T008 [P] `Member.update()`에 빈 email이면 `MemberValidationException`이 발생하는 테스트를 추가한다.
- [x] T009 [P] `Member.update()`에 빈 password이면 `MemberValidationException`이 발생하는 테스트를 추가한다.
- [x] T010 [P] `Member.update()` 검증 실패 시 기존 상태가 유지되는 테스트를 추가한다.

## Phase 3: Domain Exception

- [x] T011 `src/main/java/gift/member/exception/MemberValidationException.java`를 추가한다.

## Phase 4: Domain Validation

- [x] T012 일반 회원 생성자에서 email/password 검증을 수행한다.
- [x] T013 카카오 회원 생성자에서 email 검증을 수행한다.
- [x] T014 `Member.update()`에서 필드 할당 전 email/password 검증을 수행한다.
- [x] T015 검증 실패 시 `MemberValidationException`을 발생시킨다.

## Phase 5: Scope Check

- [x] T016 `MemberRequest` Bean Validation annotation이 유지되는지 확인한다.
- [x] T017 `chargePoint()`와 `deductPoint()`의 포인트 정책이 변경되지 않았는지 확인한다.
- [x] T018 `KakaoAuthController`의 `new Member(email)` flow가 유지되는지 확인한다.

## Phase 6: Validation

- [x] T019 `./gradlew.bat test --tests *Member*`를 실행한다.
- [x] T020 `./gradlew.bat test --tests *Kakao*`를 실행한다.
- [x] T021 `rg "new Member\\(" src/main/java src/test/java`로 생성자 사용 지점이 새 검증 정책과 맞는지 확인한다.

## Dependencies

- T001-T003 before T004-T010
- T004-T010 before T011-T015
- T011 before T012-T015
- T012-T015 before T019-T021

## Parallel Example

```text
T004, T005, T006, T007 can be written independently after Member test fixture is ready.
T016, T017, T018 can be checked independently after implementation.
```
