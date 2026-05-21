# Implementation Plan: JwtProvider 테스트 보강 리팩토링

**Branch**: `024-jwt-provider-test-refactor` | **Date**: 2026-05-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/024-jwt-provider-test-refactor/spec.md`

**Note**: 이 문서는 `JwtProvider`의 현재 생성/파싱/실패 동작을 테스트로 고정하기 위한 계획입니다.

## Summary

`JwtProvider`는 인증 핵심 컴포넌트지만 현재 직접 테스트가 없습니다. 생성한 token에서 email을 추출하는 정상 케이스와 만료, malformed, 다른 secret, null/blank token 실패 케이스를 추가합니다. production code는 변경하지 않고, 후속 토큰 예외 정리의 안전망을 마련합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: JJWT  
**Storage**: 해당 없음  
**Testing**: JUnit 5 / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API 인증 컴포넌트  
**Performance Goals**: 성능 목표 없음, 인증 핵심 동작 테스트 보강 우선  
**Constraints**: production code 변경 없음, JJWT 예외 wrapping 제외  
**Scale/Scope**: `JwtProviderTest`

## Constitution Check

- Domain-First Architecture: 인증 provider의 핵심 계약을 테스트로 명확히 합니다.
- Test-Driven Stability: 정상/실패 token 케이스를 테스트로 고정합니다.
- Structural and Behavioral Separation: production code 변경 없이 테스트만 추가합니다.
- Consistent API and Error Handling: 현재 JJWT 예외 노출 동작을 그대로 기록합니다.
- Maintainable Simplicity: 별도 auth 예외 도입은 후속 작업으로 분리합니다.
- Small Scoped Changes: `JwtProviderTest` 추가에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, behavior-preserving test refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/024-jwt-provider-test-refactor/
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
└── JwtProvider.java
```

```text
src/test/java/gift/auth/
└── JwtProviderTest.java
```

**Structure Decision**: `JwtProvider` 생성자는 secret과 expiration을 직접 받을 수 있으므로 Spring context 없이 단위 테스트로 검증합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `JwtProvider` 생성자 입력 secret/expiration을 확인합니다.
2. `createToken()`이 subject, issuedAt, expiration, signature를 설정하는지 확인합니다.
3. `getEmail()`의 JJWT 파싱 동작을 확인합니다.
4. 테스트용 secret 길이를 HS256 요구사항에 맞게 정합니다.

## Phase 1: Design & Contracts

1. `JwtProviderTest`를 추가합니다.
2. 정상 token 생성/파싱 테스트를 추가합니다.
3. expired token 테스트를 추가합니다.
4. malformed token 테스트를 추가합니다.
5. wrong secret 테스트를 추가합니다.
6. null/blank token 테스트를 추가합니다.

## Phase 2: Task Planning Approach

1. 테스트 helper secret 정의
2. provider factory helper 작성
3. 정상 케이스 테스트
4. 실패 케이스 테스트
5. JwtProvider/AuthenticationResolver 테스트 실행

## Risk Assessment

- **시간 의존 테스트 불안정 위험**: 만료 token은 음수 expiration으로 즉시 만료되도록 생성합니다.
- **secret 길이 오류 위험**: 테스트 secret은 충분히 긴 문자열을 사용합니다.
- **예외 타입 과고정 위험**: 구체 구현 예외 하나만 고정하기보다 JJWT 계층 또는 Runtime exception 범위로 검증합니다.
