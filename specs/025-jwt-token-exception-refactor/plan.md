# Implementation Plan: JWT 토큰 예외 리팩토링

**Branch**: `025-jwt-token-exception-refactor` | **Date**: 2026-05-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/025-jwt-token-exception-refactor/spec.md`

**Note**: 이 문서는 `JwtProvider`의 JJWT 구현 예외 노출을 auth 도메인 예외로 정리하기 위한 계획입니다.

## Summary

`JwtProvider.getEmail()`이 만료, malformed, 서명 불일치, null/blank token 실패를 auth 도메인 예외로 변환하도록 정리합니다. `AuthenticationResolver`는 새 예외를 인증 실패로 처리해 기존 null 반환 계약을 유지합니다. 기존 `JwtProviderTest`는 실패 기대 예외를 새 도메인 예외로 변경하고, resolver 테스트는 새 예외 처리 케이스를 반영합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: JJWT, Spring Boot  
**Storage**: 해당 없음  
**Testing**: JUnit 5 / AssertJ / Mockito  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API 인증 컴포넌트  
**Performance Goals**: 성능 목표 없음, 예외 표현 일관성 우선  
**Constraints**: controller 응답 방식 변경 없음, resolver null 반환 계약 유지  
**Scale/Scope**: `gift.auth` production/test

## Constitution Check

- Domain-First Architecture: JWT 실패를 auth 도메인 예외로 표현합니다.
- Test-Driven Stability: `024`에서 추가한 테스트를 기반으로 기대 예외를 변경합니다.
- Structural and Behavioral Separation: provider 예외 표현은 바꾸되 controller 외부 동작은 유지합니다.
- Consistent API and Error Handling: JJWT 구현 예외 대신 auth 도메인 예외로 통일합니다.
- Maintainable Simplicity: 토큰 실패 예외는 하나의 예외 타입으로 시작합니다.
- Small Scoped Changes: `JwtProvider`, `AuthenticationResolver`, 관련 테스트에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 인증 실패 외부 계약을 유지하는 예외 리팩토링입니다.

## Project Structure

### Documentation (this feature)

```text
specs/025-jwt-token-exception-refactor/
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
├── AuthenticationResolver.java
├── JwtProvider.java
└── exception/
    └── JwtTokenException.java
```

```text
src/test/java/gift/auth/
├── AuthenticationResolverTest.java
└── JwtProviderTest.java
```

**Structure Decision**: auth 도메인 예외는 `gift.auth.exception` 하위에 둡니다. 토큰 실패 사유별 세분화는 현재 controller 동작에 필요하지 않으므로 단일 예외로 시작합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. 현재 `JwtProvider.getEmail()`이 던지는 JJWT/입력 예외 범위를 확인합니다.
2. 기존 `JwtProviderTest` 실패 케이스를 새 도메인 예외 기준으로 바꿀 수 있는지 확인합니다.
3. `AuthenticationResolver`가 처리해야 할 예외 범위를 `JwtTokenException` 중심으로 정리합니다.

## Phase 1: Design & Contracts

1. `gift.auth.exception.JwtTokenException`을 추가합니다.
2. `JwtProvider.getEmail()`에서 null/blank token을 명시적으로 검증합니다.
3. JJWT 파싱 실패와 잘못된 token 입력을 `JwtTokenException`으로 감쌉니다.
4. `AuthenticationResolver`는 `JwtTokenException`을 인증 실패로 처리합니다.
5. `JwtProviderTest`와 `AuthenticationResolverTest`를 갱신합니다.

## Phase 2: Task Planning Approach

1. 실패 테스트 기대 예외를 먼저 auth 도메인 예외로 변경합니다.
2. 새 예외 클래스를 추가합니다.
3. `JwtProvider.getEmail()` 예외 변환을 구현합니다.
4. `AuthenticationResolver` catch 범위를 갱신합니다.
5. auth 관련 테스트와 전체 테스트를 실행합니다.

## Risk Assessment

- **외부 동작 변경 위험**: resolver null 반환 계약을 유지해 controller 응답 변경을 피합니다.
- **예외 원인 손실 위험**: `JwtTokenException` 생성 시 cause를 보존합니다.
- **테스트 과고정 위험**: JJWT 세부 예외 대신 애플리케이션 도메인 예외만 검증합니다.
