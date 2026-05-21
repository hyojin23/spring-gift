# Implementation Plan: 카카오 로그인 예외 처리 리팩토링

**Branch**: `028-kakao-login-exception-refactor` | **Date**: 2026-05-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/028-kakao-login-exception-refactor/spec.md`

**Note**: 이 문서는 카카오 외부 API 호출 실패와 응답 이상을 auth 도메인 예외로 정리하기 위한 계획입니다.

## Summary

`KakaoLoginClient`는 현재 `RestClient` 호출 실패와 null 응답을 그대로 노출할 수 있습니다. `KakaoLoginException`을 추가해 카카오 token/user info API 실패를 auth 도메인 예외로 변환하고, token/email 누락 같은 응답 이상도 명확히 처리합니다. 정상 로그인 흐름과 public API는 유지합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Web RestClient, Spring Test  
**Storage**: Member JPA Repository  
**Testing**: JUnit 5 / AssertJ / MockRestServiceServer / Mockito  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API 인증 컴포넌트  
**Performance Goals**: 성능 목표 없음, 실패 의미 명확화 우선  
**Constraints**: API 경로/응답 성공 구조 변경 없음, global handler 매핑 제외  
**Scale/Scope**: `gift.auth` client/service/test

## Constitution Check

- Domain-First Architecture: 카카오 로그인 실패를 auth 도메인 예외로 표현합니다.
- Test-Driven Stability: 외부 API 실패와 응답 누락을 테스트로 고정합니다.
- Structural and Behavioral Separation: 외부 client 예외를 client 경계에서 변환합니다.
- Consistent API and Error Handling: Spring HTTP client 예외가 service로 직접 새지 않게 합니다.
- Maintainable Simplicity: 실패 타입은 우선 `KakaoLoginException` 하나로 시작합니다.
- Small Scoped Changes: 카카오 로그인 client/service와 관련 테스트에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 성공 응답 계약을 유지하는 예외 리팩토링입니다.

## Project Structure

### Documentation (this feature)

```text
specs/028-kakao-login-exception-refactor/
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
├── KakaoAuthService.java
├── KakaoLoginClient.java
└── exception/
    └── KakaoLoginException.java
```

```text
src/test/java/gift/auth/
├── KakaoAuthServiceTest.java
└── KakaoLoginClientTest.java
```

**Structure Decision**: 카카오 로그인 예외는 기존 `JwtTokenException`과 같은 `gift.auth.exception` 패키지에 둡니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `RestClient.retrieve().body()` 실패 시 발생 가능한 `RestClientException` 계열을 확인합니다.
2. token API 성공 응답 body null/blank token 케이스를 확인합니다.
3. user info API 성공 응답 body null/blank email 케이스를 확인합니다.

## Phase 1: Design & Contracts

1. `KakaoLoginException`을 추가합니다.
2. `requestAccessToken()`에서 HTTP 호출 실패를 `KakaoLoginException`으로 변환합니다.
3. `requestUserInfo()`에서 HTTP 호출 실패를 `KakaoLoginException`으로 변환합니다.
4. token response와 user info response 유효성 검증 method를 추가합니다.
5. `KakaoAuthService`는 검증된 response만 사용하도록 유지합니다.

## Phase 2: Task Planning Approach

1. `KakaoLoginClientTest`에 token/user info API 실패 테스트를 추가합니다.
2. `KakaoLoginClientTest`에 null body 테스트를 추가합니다.
3. `KakaoAuthServiceTest`에 blank access token/email 응답 실패 테스트를 추가합니다.
4. 예외 클래스를 추가하고 client/service 검증을 구현합니다.
5. 카카오 auth 테스트와 전체 테스트를 실행합니다.

## Risk Assessment

- **예외 원인 손실 위험**: HTTP client 예외는 cause로 보존합니다.
- **응답 정책 변경 위험**: global handler 연결은 이번 범위에서 제외합니다.
- **정상 흐름 변경 위험**: 기존 정상 테스트를 유지하고 전체 테스트로 확인합니다.
