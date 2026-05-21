# Tasks: KakaoLoginClient 요청 구성 리팩토링

**Input**: Design documents from `/specs/027-kakao-login-client-request-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `requestAccessToken()`의 현재 URI/header/body 구성을 확인한다.
- [x] T002 `requestUserInfo()`의 현재 URI/header 구성을 확인한다.
- [x] T003 `RestClient` 요청 구성을 테스트할 방식을 확인한다.

## Phase 2: Tests First

- [x] T004 [P] `KakaoLoginClientTest`를 추가한다.
- [x] T005 [P] access token 요청 URI와 method를 검증한다.
- [x] T006 [P] access token 요청 form parameter를 검증한다.
- [x] T007 [P] access token 요청 content type을 검증한다.
- [x] T008 [P] user info 요청 URI와 method를 검증한다.
- [x] T009 [P] user info 요청 Authorization header를 검증한다.

## Phase 3: Implementation

- [x] T010 카카오 token API URI를 상수로 분리한다.
- [x] T011 카카오 user info API URI를 상수로 분리한다.
- [x] T012 access token 요청 form parameter 생성 method를 분리한다.
- [x] T013 user info 요청 Bearer header 생성 method를 분리한다.
- [x] T014 token 요청 content type 표현을 정리한다.
- [x] T015 public method와 response record 구조가 변경되지 않았는지 확인한다.

## Phase 4: Validation

- [x] T016 `./gradlew.bat test --tests *KakaoLoginClient*`를 실행한다.
- [x] T017 `./gradlew.bat test --tests *KakaoAuth*`를 실행한다.
- [x] T018 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T009
- T004-T009 before T010-T015
- T010-T015 before T016-T018

## Parallel Example

```text
T005, T006, T007 can be written independently for the token request.
T008, T009 can be written independently for the user info request.
```
