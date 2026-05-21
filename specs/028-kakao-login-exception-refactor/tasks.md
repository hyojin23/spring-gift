# Tasks: 카카오 로그인 예외 처리 리팩토링

**Input**: Design documents from `/specs/028-kakao-login-exception-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `KakaoLoginClient`의 현재 RestClient 실패 전파 방식을 확인한다.
- [x] T002 token/user info 응답 body null 가능성을 확인한다.
- [x] T003 `KakaoAuthService`의 access token/email 사용 지점을 확인한다.

## Phase 2: Tests First

- [x] T004 [P] token API 4xx/5xx 실패 시 `KakaoLoginException` 발생 테스트를 추가한다.
- [x] T005 [P] user info API 4xx/5xx 실패 시 `KakaoLoginException` 발생 테스트를 추가한다.
- [x] T006 [P] token API body null 시 `KakaoLoginException` 발생 테스트를 추가한다.
- [x] T007 [P] user info API body null 시 `KakaoLoginException` 발생 테스트를 추가한다.
- [x] T008 [P] access token blank 시 `KakaoLoginException` 발생 테스트를 추가한다.
- [x] T009 [P] email blank 시 `KakaoLoginException` 발생 테스트를 추가한다.

## Phase 3: Implementation

- [x] T010 `gift.auth.exception.KakaoLoginException`을 추가한다.
- [x] T011 `requestAccessToken()`에서 RestClient 실패를 `KakaoLoginException`으로 변환한다.
- [x] T012 `requestUserInfo()`에서 RestClient 실패를 `KakaoLoginException`으로 변환한다.
- [x] T013 `KakaoLoginClient`에서 null response body를 검증한다.
- [x] T014 `KakaoAuthService`에서 access token null/blank를 검증한다.
- [x] T015 `KakaoAuthService`에서 email null/blank를 검증한다.
- [x] T016 정상 로그인 흐름이 변경되지 않았는지 확인한다.

## Phase 4: Validation

- [x] T017 `./gradlew.bat test --tests *KakaoLoginClient*`를 실행한다.
- [x] T018 `./gradlew.bat test --tests *KakaoAuth*`를 실행한다.
- [x] T019 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T009
- T004-T009 before T010-T016
- T010 before T011-T015
- T016 before T017-T019

## Parallel Example

```text
T004, T005, T006, T007 can be written independently in KakaoLoginClientTest.
T008, T009 can be written independently in KakaoAuthServiceTest.
```
