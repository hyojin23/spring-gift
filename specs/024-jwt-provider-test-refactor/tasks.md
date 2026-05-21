# Tasks: JwtProvider 테스트 보강 리팩토링

**Input**: Design documents from `/specs/024-jwt-provider-test-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `JwtProvider` 생성자 입력 secret/expiration을 확인한다.
- [x] T002 `createToken()`의 subject/expiration/signature 설정을 확인한다.
- [x] T003 `getEmail()`의 JJWT 파싱 동작을 확인한다.
- [x] T004 테스트 secret 길이를 충분히 긴 값으로 정한다.

## Phase 2: Tests

- [x] T005 [P] `JwtProviderTest`를 추가한다.
- [x] T006 [P] 생성한 token에서 같은 email을 추출하는 테스트를 추가한다.
- [x] T007 [P] 만료된 token 파싱 실패 테스트를 추가한다.
- [x] T008 [P] malformed token 파싱 실패 테스트를 추가한다.
- [x] T009 [P] 다른 secret으로 생성한 token 검증 실패 테스트를 추가한다.
- [x] T010 [P] null token 파싱 실패 테스트를 추가한다.
- [x] T011 [P] blank token 파싱 실패 테스트를 추가한다.

## Phase 3: Scope Check

- [x] T012 `JwtProvider` production code가 변경되지 않았는지 확인한다.
- [x] T013 `InvalidTokenException` 같은 새 auth 예외가 추가되지 않았는지 확인한다.
- [x] T014 `AuthenticationResolver` 호출 계약이 변경되지 않았는지 확인한다.

## Phase 4: Validation

- [x] T015 `./gradlew.bat test --tests *JwtProvider*`를 실행한다.
- [x] T016 `./gradlew.bat test --tests *AuthenticationResolver*`를 실행한다.
- [x] T017 `git diff -- src/main/java/gift/auth/JwtProvider.java`로 production code 변경이 없는지 확인한다.

## Dependencies

- T001-T004 before T005-T011
- T005-T011 before T012-T017

## Parallel Example

```text
T006, T007, T008, T009, T010, T011 can be written independently in JwtProviderTest.
```
