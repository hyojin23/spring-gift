# Implementation Plan: AuthenticationResolver 토큰 파싱 리팩토링

**Branch**: `023-authentication-resolver-refactor` | **Date**: 2026-05-21 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/023-authentication-resolver-refactor/spec.md`

**Note**: 이 문서는 `AuthenticationResolver`의 Bearer token 추출과 JWT 파싱 실패 처리를 명확히 하는 리팩토링 계획입니다.

## Summary

`AuthenticationResolver.extractMember()`는 현재 `authorization.replace("Bearer ", "")`와 broad `catch (Exception)`으로 인증 실패를 처리합니다. 기존 null 반환 정책은 유지하되, Bearer token 추출을 private method로 분리하고 null/blank/non-Bearer 헤더를 명시적으로 처리합니다. JWT 파싱 실패만 인증 실패로 처리하고, 예상치 못한 오류까지 무조건 삼키는 구조를 제거합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot, JJWT, Spring Data JPA  
**Storage**: MemberRepository  
**Testing**: JUnit 5 / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API  
**Performance Goals**: 성능 목표 없음, 인증 파싱 의미 명확화 우선  
**Constraints**: `extractMember()` public API 유지, 인증 실패 null 반환 유지, controller 변경 제외  
**Scale/Scope**: `AuthenticationResolver`, `AuthenticationResolverTest`

## Constitution Check

- Domain-First Architecture: 인증 resolver의 책임을 token 추출과 회원 조회로 명확히 나눕니다.
- Test-Driven Stability: 정상/실패 header와 JWT 파싱 실패를 테스트로 고정합니다.
- Structural and Behavioral Separation: controller 응답 정책은 변경하지 않고 resolver 내부 구조만 정리합니다.
- Consistent API and Error Handling: 기존 null 기반 인증 실패 계약을 유지합니다.
- Maintainable Simplicity: 예외 기반 인증 구조로 확장하지 않고 작은 리팩토링에 한정합니다.
- Small Scoped Changes: AuthenticationResolver와 테스트에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, behavior-preserving refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/023-authentication-resolver-refactor/
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
└── AuthenticationResolver.java
```

```text
src/test/java/gift/auth/
└── AuthenticationResolverTest.java
```

**Structure Decision**: 새 class를 만들지 않고 `AuthenticationResolver` 내부 private method로 token 추출과 token 기반 회원 조회를 분리합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. 현재 `replace("Bearer ", "")` 기반 token 추출을 확인합니다.
2. 현재 broad `catch (Exception)` 동작을 확인합니다.
3. `JwtProvider.getEmail()` 실패 시 발생 가능한 예외 계층을 확인합니다.
4. 기존 controller의 null 기반 401 처리 방식을 확인합니다.

## Phase 1: Design & Contracts

1. `extractBearerToken(String authorization)` private method를 설계합니다.
2. null/blank/non-Bearer 헤더는 `Optional.empty()`로 처리합니다.
3. `findMemberByToken(String token)` private method를 설계합니다.
4. JWT 파싱 실패는 `Optional.empty()`로 처리합니다.
5. `extractMember()`는 최종적으로 `orElse(null)`로 기존 계약을 유지합니다.

## Phase 2: Task Planning Approach

1. AuthenticationResolverTest 추가
2. 정상 Bearer token 테스트 추가
3. null/blank/non-Bearer 실패 테스트 추가
4. JWT 파싱 실패/회원 미존재 테스트 추가
5. AuthenticationResolver 리팩토링
6. 관련 auth/order/wish controller 테스트 실행

## Risk Assessment

- **외부 동작 변경 위험**: null 반환 계약을 유지하고 controller 변경을 하지 않습니다.
- **예외 처리 범위 축소 위험**: DB 장애 같은 예상치 못한 오류는 더 이상 인증 실패로 숨기지 않습니다. 이는 의도된 내부 품질 개선입니다.
- **Bearer prefix 호환성 위험**: 기존 정책상 `"Bearer "`를 사용하고 있으므로 동일 prefix를 기준으로 합니다.
