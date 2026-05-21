# Implementation Plan: 카카오 로그인 URL 구성 분리 리팩토링

**Branch**: `029-kakao-login-url-refactor` | **Date**: 2026-05-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/029-kakao-login-url-refactor/spec.md`

**Note**: 이 문서는 `KakaoAuthController.login()`의 카카오 authorization URL 조립 책임을 별도 컴포넌트로 분리하기 위한 계획입니다.

## Summary

현재 `KakaoAuthController.login()`은 `UriComponentsBuilder`로 카카오 authorization URL을 직접 생성합니다. `KakaoLoginUrlProvider`를 추가해 authorize URI, response_type, scope, client_id, redirect_uri 구성을 담당하게 하고, controller는 provider가 반환한 URL을 302 `Location` header로 응답합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Web  
**Storage**: 해당 없음  
**Testing**: JUnit 5 / AssertJ / Spring MVC Test / Mockito  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API 인증 컴포넌트  
**Performance Goals**: 성능 목표 없음, 책임 분리와 테스트 용이성 우선  
**Constraints**: redirect URL 결과 변경 없음, callback 흐름 변경 없음  
**Scale/Scope**: `gift.auth` controller/provider/test

## Constitution Check

- Domain-First Architecture: 카카오 OAuth 로그인 URL 정책을 별도 컴포넌트로 명시합니다.
- Test-Driven Stability: 생성 URL과 controller redirect를 테스트로 고정합니다.
- Structural and Behavioral Separation: HTTP adapter와 URL 구성 정책을 분리합니다.
- Consistent API and Error Handling: 외부 endpoint와 response status/header는 유지합니다.
- Maintainable Simplicity: URL 생성 전용 provider 하나만 추가합니다.
- Small Scoped Changes: login URL 생성과 관련 테스트에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, behavior-preserving responsibility refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/029-kakao-login-url-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/auth/
├── KakaoAuthController.java
├── KakaoAuthService.java
├── KakaoLoginProperties.java
└── KakaoLoginUrlProvider.java
```

```text
src/test/java/gift/auth/
├── KakaoAuthControllerTest.java
└── KakaoLoginUrlProviderTest.java
```

**Structure Decision**: URL 생성은 `KakaoAuthService`에 넣지 않고 `KakaoLoginUrlProvider`로 분리합니다. callback login use case와 authorize URL 생성은 서로 다른 책임입니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. 현재 `KakaoAuthController.login()`의 URL 구성 값을 확인합니다.
2. 기존 `KakaoAuthControllerTest.login()` 기대값을 확인합니다.
3. URL provider의 반환 타입을 `String`으로 정합니다.

## Phase 1: Design & Contracts

1. `KakaoLoginUrlProvider`를 추가합니다.
2. provider는 `KakaoLoginProperties`를 주입받습니다.
3. provider는 authorize URI, response_type, client_id, redirect_uri, scope를 조립합니다.
4. controller는 provider를 주입받고 login method에서 provider 결과만 사용합니다.
5. callback method는 변경하지 않습니다.

## Phase 2: Task Planning Approach

1. `KakaoLoginUrlProviderTest`로 생성 URL을 검증합니다.
2. `KakaoAuthControllerTest`를 provider mock 기반으로 갱신합니다.
3. `KakaoAuthController`에서 `KakaoLoginProperties`와 `UriComponentsBuilder` 의존을 제거합니다.
4. auth 관련 테스트와 전체 테스트를 실행합니다.

## Risk Assessment

- **URL encoding 변경 위험**: 기존 redirect_uri/scope 포함 형태를 테스트로 검증합니다.
- **controller 테스트 과결합 위험**: controller는 provider 반환 URL을 Location에 넣는지만 검증합니다.
- **책임 과분리 위험**: provider는 authorize URL 생성 하나만 담당하도록 작게 유지합니다.
