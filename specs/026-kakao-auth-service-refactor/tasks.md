# Tasks: KakaoAuthController 서비스 분리 리팩토링

**Input**: Design documents from `/specs/026-kakao-auth-service-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `KakaoAuthController.callback()`의 현재 처리 순서를 확인한다.
- [x] T002 `KakaoLoginClient` 응답 record 구조를 확인한다.
- [x] T003 `Member`의 카카오 access token 갱신 메서드를 확인한다.
- [x] T004 기존 auth controller 테스트 유무를 확인한다.

## Phase 2: Tests First

- [x] T005 [P] `KakaoAuthServiceTest`를 추가한다.
- [x] T006 [P] 신규 카카오 사용자 callback 처리 시 회원 저장과 JWT 반환을 검증한다.
- [x] T007 [P] 기존 카카오 사용자 callback 처리 시 access token 갱신과 JWT 반환을 검증한다.
- [x] T008 [P] `KakaoAuthControllerTest`를 추가한다.
- [x] T009 [P] `/login` redirect URL을 검증한다.
- [x] T010 [P] `/callback`이 service token을 `TokenResponse`로 반환하는지 검증한다.

## Phase 3: Implementation

- [x] T011 `KakaoAuthService`를 추가한다.
- [x] T012 `KakaoAuthService`에 callback code 처리 메서드를 구현한다.
- [x] T013 `KakaoAuthController`에서 `KakaoLoginClient`, `MemberRepository`, `JwtProvider` 직접 의존을 제거한다.
- [x] T014 `KakaoAuthController.callback()`을 service 호출 중심으로 단순화한다.
- [x] T015 `/login` redirect 동작이 변경되지 않았는지 확인한다.

## Phase 4: Validation

- [x] T016 `./gradlew.bat test --tests *KakaoAuth*`를 실행한다.
- [x] T017 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T004 before T005-T010
- T005-T010 before T011-T014
- T011 before T012-T014
- T015 before T016-T017

## Parallel Example

```text
T006, T007 can be written independently in KakaoAuthServiceTest.
T009, T010 can be written independently in KakaoAuthControllerTest.
```
