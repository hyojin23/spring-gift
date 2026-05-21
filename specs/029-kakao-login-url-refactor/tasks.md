# Tasks: 카카오 로그인 URL 구성 분리 리팩토링

**Input**: Design documents from `/specs/029-kakao-login-url-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `KakaoAuthController.login()`의 현재 URL 구성 값을 확인한다.
- [x] T002 기존 `KakaoAuthControllerTest.login()` 기대값을 확인한다.
- [x] T003 provider 반환 타입과 책임 범위를 정한다.

## Phase 2: Tests First

- [x] T004 [P] `KakaoLoginUrlProviderTest`를 추가한다.
- [x] T005 [P] provider가 authorize URI를 포함한 URL을 생성하는지 검증한다.
- [x] T006 [P] provider가 response_type, client_id, redirect_uri, scope를 포함하는지 검증한다.
- [x] T007 [P] `KakaoAuthControllerTest.login()`을 provider mock 기준으로 갱신한다.
- [x] T008 [P] controller callback 테스트가 유지되는지 확인한다.

## Phase 3: Implementation

- [x] T009 `KakaoLoginUrlProvider`를 추가한다.
- [x] T010 provider에 authorize URI와 scope 상수를 정의한다.
- [x] T011 provider가 `KakaoLoginProperties`로 URL을 생성하도록 구현한다.
- [x] T012 `KakaoAuthController`에서 `KakaoLoginProperties` 직접 의존을 제거한다.
- [x] T013 `KakaoAuthController`에서 `UriComponentsBuilder` 직접 의존을 제거한다.
- [x] T014 `KakaoAuthController.login()`을 provider 호출 중심으로 단순화한다.

## Phase 4: Validation

- [x] T015 `./gradlew.bat test --tests *KakaoLoginUrl*`를 실행한다.
- [x] T016 `./gradlew.bat test --tests *KakaoAuth*`를 실행한다.
- [x] T017 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T008
- T004-T008 before T009-T014
- T009 before T010-T014
- T014 before T015-T017

## Parallel Example

```text
T005, T006 can be written independently in KakaoLoginUrlProviderTest.
T007, T008 can be updated independently in KakaoAuthControllerTest.
```
