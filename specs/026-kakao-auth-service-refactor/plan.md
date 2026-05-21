# Implementation Plan: KakaoAuthController 서비스 분리 리팩토링

**Branch**: `026-kakao-auth-service-refactor` | **Date**: 2026-05-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/026-kakao-auth-service-refactor/spec.md`

**Note**: 이 문서는 `KakaoAuthController`의 callback 인증 흐름을 service로 분리하기 위한 계획입니다.

## Summary

현재 `KakaoAuthController.callback()`은 카카오 access token 요청, 사용자 정보 조회, 회원 조회/생성, 카카오 token 저장, 서비스 JWT 발급을 모두 직접 수행합니다. 이 흐름을 `KakaoAuthService`로 이동해 controller는 HTTP 요청/응답 변환만 담당하게 합니다. `/login` redirect 동작과 `/callback` 응답 형식은 유지합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Web, RestClient, JJWT, Spring Data JPA  
**Storage**: Member JPA Repository  
**Testing**: JUnit 5 / AssertJ / Mockito / Spring MVC Test  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API 인증 컴포넌트  
**Performance Goals**: 성능 목표 없음, 책임 분리와 테스트 용이성 우선  
**Constraints**: API 경로/응답 형식 변경 없음, 카카오 실패 정책 변경 없음  
**Scale/Scope**: `gift.auth` controller/service/test

## Constitution Check

- Domain-First Architecture: 카카오 인증 use case를 service로 명시합니다.
- Test-Driven Stability: 신규/기존 회원 callback 흐름을 service 테스트로 고정합니다.
- Structural and Behavioral Separation: HTTP adapter와 인증 use case를 분리합니다.
- Consistent API and Error Handling: 외부 API 동작과 응답 계약은 유지합니다.
- Maintainable Simplicity: redirect URL 생성은 controller에 유지하고 callback use case만 service로 이동합니다.
- Small Scoped Changes: auth 패키지의 카카오 인증 흐름에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, behavior-preserving responsibility refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/026-kakao-auth-service-refactor/
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
├── KakaoLoginClient.java
├── KakaoLoginProperties.java
├── JwtProvider.java
└── TokenResponse.java
```

```text
src/test/java/gift/auth/
├── KakaoAuthControllerTest.java
└── KakaoAuthServiceTest.java
```

**Structure Decision**: `KakaoAuthService`는 auth 패키지에 둡니다. 별도 DTO를 추가하지 않고 service는 JWT token 문자열을 반환하며 controller가 `TokenResponse`로 변환합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `KakaoAuthController.callback()`의 현재 순서를 확인합니다.
2. `KakaoLoginClient` 응답 record 구조를 확인합니다.
3. `Member`의 카카오 access token 갱신 메서드를 확인합니다.
4. 기존 auth controller 테스트 유무를 확인합니다.

## Phase 1: Design & Contracts

1. `KakaoAuthService.loginWithKakao(String code)` 형태의 service method를 추가합니다.
2. service method는 카카오 access token 요청, user info 요청, member 저장, JWT 발급을 수행합니다.
3. controller callback은 service method를 호출하고 `TokenResponse`를 반환합니다.
4. login redirect method는 유지합니다.

## Phase 2: Task Planning Approach

1. `KakaoAuthServiceTest`로 신규 회원/기존 회원 흐름을 먼저 검증합니다.
2. `KakaoAuthService`를 추가합니다.
3. `KakaoAuthController` 의존성을 service 중심으로 변경합니다.
4. `KakaoAuthControllerTest`로 login redirect와 callback 응답을 검증합니다.
5. auth 관련 테스트와 전체 테스트를 실행합니다.

## Risk Assessment

- **API 계약 변경 위험**: controller 테스트로 status/header/body를 고정합니다.
- **회원 저장 누락 위험**: 신규/기존 회원 service 테스트에서 repository save를 검증합니다.
- **카카오 token 갱신 누락 위험**: 기존 회원 테스트에서 access token 갱신 결과를 검증합니다.
