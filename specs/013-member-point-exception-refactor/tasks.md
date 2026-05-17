# Tasks: Member 포인트 예외 정리 리팩토링

**Input**: Design documents from `/specs/013-member-point-exception-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `Member.chargePoint()`와 `Member.deductPoint()`의 현재 예외 발생 지점을 확인한다.
- [x] T002 `AdminMemberController`의 포인트 충전 호출 지점을 확인한다.
- [x] T003 `OrderController`의 포인트 차감 호출 지점을 확인한다.
- [x] T004 기존 `MemberTest` 구조를 확인한다.

## Phase 2: Tests First

- [x] T005 [P] 포인트 충전 성공 테스트를 추가한다.
- [x] T006 [P] 0 이하 금액 충전 시 `InvalidMemberPointAmountException`이 발생하는 테스트를 추가한다.
- [x] T007 [P] 포인트 차감 성공 테스트를 추가한다.
- [x] T008 [P] 0 이하 금액 차감 시 `InvalidMemberPointAmountException`이 발생하는 테스트를 추가한다.
- [x] T009 [P] 보유 포인트보다 큰 금액 차감 시 `InsufficientMemberPointException`이 발생하는 테스트를 추가한다.
- [x] T010 [P] 포인트 예외 발생 시 기존 포인트가 유지되는 테스트를 추가한다.
- [x] T011 [P] 포인트 예외 메시지가 모두 한글인지 검증하는 테스트를 추가한다.

## Phase 3: Domain Exceptions

- [x] T012 `src/main/java/gift/member/exception/InvalidMemberPointAmountException.java`를 추가한다.
- [x] T013 `src/main/java/gift/member/exception/InsufficientMemberPointException.java`를 추가한다.
- [x] T014 포인트 예외 메시지를 한글로 정의한다.

## Phase 4: Domain Refactor

- [x] T015 `Member.chargePoint()`의 금액 오류 예외를 `InvalidMemberPointAmountException`으로 교체한다.
- [x] T016 `Member.deductPoint()`의 금액 오류 예외를 `InvalidMemberPointAmountException`으로 교체한다.
- [x] T017 `Member.deductPoint()`의 포인트 부족 예외를 `InsufficientMemberPointException`으로 교체한다.
- [x] T018 포인트 변경 전에 검증이 수행되는지 확인한다.

## Phase 5: Scope Check

- [x] T019 AdminMemberController의 포인트 충전 호출 구조가 유지되는지 확인한다.
- [x] T020 OrderController의 포인트 차감 호출 구조가 유지되는지 확인한다.
- [x] T021 controller 예외 처리 정책이 이번 작업에서 변경되지 않았는지 확인한다.

## Phase 6: Validation

- [x] T022 `./gradlew.bat test --tests *Member*`를 실행한다.
- [x] T023 `./gradlew.bat test --tests *Order*`를 실행한다. *(현재 Order 테스트가 없어 Gradle이 No tests found를 반환함)*
- [x] T024 `rg "IllegalArgumentException" src/main/java/gift/member/Member.java`로 포인트 메서드에 범용 예외가 남지 않았는지 확인한다.
- [x] T025 `rg "Amount must|greater than zero" src/main/java/gift/member src/test/java/gift/member`로 영어 포인트 예외 메시지가 남지 않았는지 확인한다.

## Dependencies

- T001-T004 before T005-T011
- T005-T011 before T012-T018
- T012-T014 before T015-T017
- T015-T018 before T022-T025

## Parallel Example

```text
T005, T006, T007, T008, T009, T010 can be written independently in MemberTest.
T017, T018, T019 can be checked independently after implementation.
```
