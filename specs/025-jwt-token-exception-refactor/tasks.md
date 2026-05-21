# Tasks: JWT 토큰 예외 리팩토링

**Input**: Design documents from `/specs/025-jwt-token-exception-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `JwtProvider.getEmail()`의 현재 실패 예외 범위를 확인한다.
- [x] T002 `JwtProviderTest`의 현재 실패 케이스를 확인한다.
- [x] T003 `AuthenticationResolver`의 현재 catch 범위를 확인한다.

## Phase 2: Tests First

- [x] T004 [P] `JwtProviderTest`의 만료 token 기대 예외를 auth 도메인 예외로 변경한다.
- [x] T005 [P] `JwtProviderTest`의 malformed token 기대 예외를 auth 도메인 예외로 변경한다.
- [x] T006 [P] `JwtProviderTest`의 다른 secret token 기대 예외를 auth 도메인 예외로 변경한다.
- [x] T007 [P] `JwtProviderTest`의 null/blank token 기대 예외를 auth 도메인 예외로 변경한다.
- [x] T008 [P] `AuthenticationResolverTest`에 auth 도메인 토큰 예외 발생 시 null 반환 테스트를 추가한다.

## Phase 3: Implementation

- [x] T009 `gift.auth.exception.JwtTokenException`을 추가한다.
- [x] T010 `JwtProvider.getEmail()`에서 null/blank token을 `JwtTokenException`으로 처리한다.
- [x] T011 `JwtProvider.getEmail()`에서 JJWT 파싱/검증 실패를 `JwtTokenException`으로 감싼다.
- [x] T012 `AuthenticationResolver`가 `JwtTokenException`을 인증 실패로 처리하도록 변경한다.
- [x] T013 broad catch 또는 JJWT 구현 예외 의존이 남지 않았는지 확인한다.

## Phase 4: Validation

- [x] T014 `./gradlew.bat test --tests *JwtProvider*`를 실행한다.
- [x] T015 `./gradlew.bat test --tests *AuthenticationResolver*`를 실행한다.
- [x] T016 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T008
- T004-T008 before T009-T013
- T009 before T010-T012
- T013 before T014-T016

## Parallel Example

```text
T004, T005, T006, T007 can be updated independently in JwtProviderTest.
T008 can be updated independently in AuthenticationResolverTest.
```
