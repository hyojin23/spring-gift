# Tasks: AuthenticationResolver 토큰 파싱 리팩토링

**Input**: Design documents from `/specs/023-authentication-resolver-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `AuthenticationResolver`의 `replace("Bearer ", "")` token 추출을 확인한다.
- [x] T002 현재 broad `catch (Exception)` 동작을 확인한다.
- [x] T003 기존 controller의 null 기반 401 처리 계약을 확인한다.
- [x] T004 auth 테스트 패키지 부재를 확인한다.

## Phase 2: Tests First

- [x] T005 [P] `AuthenticationResolverTest`를 추가한다.
- [x] T006 [P] 유효한 Bearer token과 존재하는 회원이면 회원을 반환하는 테스트를 추가한다.
- [x] T007 [P] null header면 null을 반환하고 JWT 파싱을 시도하지 않는 테스트를 추가한다.
- [x] T008 [P] blank header면 null을 반환하고 JWT 파싱을 시도하지 않는 테스트를 추가한다.
- [x] T009 [P] non-Bearer header면 null을 반환하고 JWT 파싱을 시도하지 않는 테스트를 추가한다.
- [x] T010 [P] JWT 파싱 실패 시 null을 반환하고 회원 조회를 시도하지 않는 테스트를 추가한다.
- [x] T011 [P] 회원 미존재 시 null을 반환하는 테스트를 추가한다.

## Phase 3: Resolver Refactor

- [x] T012 `extractBearerToken(String authorization)` private method를 추가한다.
- [x] T013 null/blank authorization을 `Optional.empty()`로 처리한다.
- [x] T014 `"Bearer "` prefix가 없으면 `Optional.empty()`로 처리한다.
- [x] T015 Bearer prefix 뒤 token만 추출한다.
- [x] T016 `findMemberByToken(String token)` private method를 추가한다.
- [x] T017 JWT 파싱 실패를 null 반환 흐름으로 처리한다.
- [x] T018 broad `catch (Exception)`을 제거한다.
- [x] T019 `extractMember()` public API와 null 반환 계약을 유지한다.

## Phase 4: Scope Check

- [x] T020 controller의 `member == null` 401 처리 방식을 변경하지 않았는지 확인한다.
- [x] T021 인증 실패 예외/global handler 구조가 이번 작업에 포함되지 않았는지 확인한다.
- [x] T022 Bearer prefix 대소문자/trim 정책 변경이 포함되지 않았는지 확인한다.

## Phase 5: Validation

- [x] T023 `./gradlew.bat test --tests *AuthenticationResolver*`를 실행한다.
- [x] T024 `./gradlew.bat test --tests *Order*`를 실행한다.
- [x] T025 `./gradlew.bat test --tests *Wish*`를 실행한다.
- [x] T026 `rg "replace\\(\"Bearer \"|catch \\(Exception" src/main/java/gift/auth/AuthenticationResolver.java`로 기존 구현 제거를 확인한다.
- [x] T027 `rg "extractBearerToken|findMemberByToken" src/main/java/gift/auth src/test/java/gift/auth`로 리팩토링 구조를 확인한다.

## Dependencies

- T001-T004 before T005-T011
- T005-T011 before T012-T019
- T012-T019 before T020-T027

## Parallel Example

```text
T006, T007, T008, T009, T010, T011 can be written independently in AuthenticationResolverTest.
```
