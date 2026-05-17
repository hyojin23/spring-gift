# Tasks: Member API 서비스 및 예외 처리 리팩토링

**Input**: Design documents from `/specs/011-member-service-exception-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `MemberController`의 repository/JWT 직접 접근을 확인한다.
- [x] T002 현재 `MemberController`의 `IllegalArgumentException` 사용 지점을 확인한다.
- [x] T003 기존 member 테스트 부재와 global handler 테스트 구조를 확인한다.

## Phase 2: Tests First

- [x] T004 [P] 회원가입 성공 시 201 Created와 token 응답을 반환하는 controller 테스트를 추가한다.
- [x] T005 [P] 로그인 성공 시 200 OK와 token 응답을 반환하는 controller 테스트를 추가한다.
- [x] T006 [P] 중복 이메일 회원가입 실패 시 400 + `MEMBER.DUPLICATE_EMAIL` 응답을 반환하는 controller 테스트를 추가한다.
- [x] T007 [P] 존재하지 않는 이메일 로그인 실패 시 401 + `MEMBER.INVALID_CREDENTIALS` 응답을 반환하는 controller 테스트를 추가한다.
- [x] T008 [P] 잘못된 비밀번호 로그인 실패 시 401 + `MEMBER.INVALID_CREDENTIALS` 응답을 반환하는 controller 테스트를 추가한다.
- [x] T009 [P] `MemberService` 회원가입/로그인 성공 및 실패 단위 테스트를 추가한다.
- [x] T010 [P] `GlobalExceptionHandlerTest`에 member 예외 매핑 테스트를 추가한다.

## Phase 3: Exceptions

- [x] T011 `src/main/java/gift/member/exception/MemberException.java`를 추가한다.
- [x] T012 `src/main/java/gift/member/exception/DuplicateMemberEmailException.java`를 추가한다.
- [x] T013 `src/main/java/gift/member/exception/InvalidMemberCredentialsException.java`를 추가한다.

## Phase 4: Service Refactor

- [x] T014 `MemberService`를 추가한다.
- [x] T015 회원가입 로직을 `MemberService.register`로 이동한다.
- [x] T016 로그인 로직을 `MemberService.login`으로 이동한다.
- [x] T017 중복 이메일 검증 실패 시 `DuplicateMemberEmailException`을 발생시킨다.
- [x] T018 이메일 미존재 또는 비밀번호 불일치 시 `InvalidMemberCredentialsException`을 발생시킨다.
- [x] T019 JWT token 생성은 기존 `JwtProvider`를 사용해 유지한다.

## Phase 5: Controller and Handler

- [x] T020 `MemberController`가 `MemberService`만 주입받도록 변경한다.
- [x] T021 `MemberController`의 repository/JWT 직접 접근을 제거한다.
- [x] T022 `MemberController`의 `@ExceptionHandler(IllegalArgumentException.class)`를 제거한다.
- [x] T023 `GlobalExceptionHandler`에 `DuplicateMemberEmailException` handler를 추가한다.
- [x] T024 `GlobalExceptionHandler`에 `InvalidMemberCredentialsException` handler를 추가한다.

## Phase 6: Validation

- [x] T025 `./gradlew.bat test --tests *Member*`를 실행한다.
- [x] T026 `./gradlew.bat test --tests *GlobalExceptionHandlerTest*`를 실행한다.
- [x] T027 `rg "IllegalArgumentException" src/main/java/gift/member/MemberController.java`로 controller에 범용 예외가 남지 않았는지 확인한다.
- [x] T028 `rg "MemberRepository|JwtProvider" src/main/java/gift/member/MemberController.java`로 controller 직접 의존성이 제거되었는지 확인한다.

## Dependencies

- T001-T003 before T004-T010
- T004-T010 before T011-T024
- T011-T013 before T014-T024
- T014-T019 before T020-T022
- T023-T024 before T006-T008 pass
- T025-T028 after implementation

## Parallel Example

```text
T004, T005, T006, T007, T008 can be written independently after controller test setup is chosen.
T011, T012, T013 can be added independently once exception package naming is confirmed.
```
