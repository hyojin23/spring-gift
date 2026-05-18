# Tasks: Admin Member 서비스 분리 리팩토링

**Input**: Design documents from `/specs/014-admin-member-service-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `AdminMemberController`의 `MemberRepository` 직접 접근 지점을 확인한다.
- [x] T002 현재 admin member template의 오류 메시지 표시 구조를 확인한다.
- [x] T003 기존 member/admin member 테스트 구조를 확인한다.

## Phase 2: Tests First

- [x] T004 [P] 관리자 회원 목록 화면 조회 테스트를 추가한다.
- [x] T005 [P] 관리자 회원 등록 화면 조회 테스트를 추가한다.
- [x] T006 [P] 관리자 회원 등록 성공 redirect 테스트를 추가한다.
- [x] T007 [P] 중복 이메일 등록 실패 시 `member/new` view와 한글 오류 메시지, email model을 검증하는 테스트를 추가한다.
- [x] T008 [P] 관리자 회원 수정 화면 조회 테스트를 추가한다.
- [x] T009 [P] 관리자 회원 수정 성공 redirect 테스트를 추가한다.
- [x] T010 [P] 관리자 회원 포인트 충전 성공 redirect 테스트를 추가한다.
- [x] T011 [P] 관리자 회원 삭제 성공 redirect 테스트를 추가한다.
- [x] T012 [P] 회원 미존재 시 `/admin/members` redirect와 flash `error`를 검증하는 테스트를 추가한다.
- [x] T013 [P] 포인트 충전 실패 시 `/admin/members` redirect와 flash `error`를 검증하는 테스트를 추가한다.

## Phase 3: Service and Exceptions

- [x] T014 `src/main/java/gift/member/AdminMemberService.java`를 추가한다.
- [x] T015 `src/main/java/gift/member/exception/AdminMemberNotFoundException.java`를 추가한다.
- [x] T016 `AdminMemberService`에 회원 목록/단건 조회 메서드를 추가한다.
- [x] T017 `AdminMemberService`에 회원 생성/수정/삭제 메서드를 추가한다.
- [x] T018 `AdminMemberService`에 포인트 충전 메서드를 추가한다.
- [x] T019 회원 미존재 시 `AdminMemberNotFoundException`을 발생시킨다.

## Phase 4: Controller Refactor

- [x] T020 `AdminMemberController`가 `AdminMemberService`만 주입받도록 변경한다.
- [x] T021 `AdminMemberController`의 `MemberRepository` 직접 접근을 제거한다.
- [x] T022 중복 이메일 등록 실패 시 한글 오류 메시지와 기존 form 복구를 유지한다.
- [x] T023 admin member 예외와 member point 예외를 처리하는 `@ExceptionHandler`를 추가한다.
- [x] T024 예외 처리 시 `RedirectAttributes`에 `error` flash attribute를 추가한다.
- [x] T025 예외 처리 결과로 `"redirect:/admin/members"`를 반환한다.

## Phase 5: View Integration

- [x] T026 `member/list.html`에 flash `error` 메시지 표시 영역을 추가한다.
- [x] T027 기존 회원 목록/포인트 충전/관리 버튼 표시가 영향을 받지 않는지 확인한다.

## Phase 6: Validation

- [x] T028 `./gradlew.bat test --tests *AdminMember*`를 실행한다.
- [x] T029 `./gradlew.bat test --tests *Member*`를 실행한다.
- [x] T030 `rg "MemberRepository" src/main/java/gift/member/AdminMemberController.java`로 직접 의존성이 제거되었는지 확인한다.
- [x] T031 `rg "Email is already|Member not found|Amount must" src/main/java/gift/member src/test/java/gift/member`로 admin member 영어 오류 메시지가 남지 않았는지 확인한다.

## Dependencies

- T001-T003 before T004-T013
- T004-T013 before T014-T027
- T014-T019 before T020-T025
- T023-T025 before T026-T029

## Parallel Example

```text
T004, T005, T006, T007, T008, T009, T010, T011 can be written independently after MockMvc setup is ready.
T014 and T015 can be implemented independently after naming is confirmed.
```
