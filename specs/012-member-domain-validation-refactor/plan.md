# Implementation Plan: Member 도메인 검증 강화 리팩토링

**Branch**: `012-member-domain-validation-refactor` | **Date**: 2026-05-18 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/012-member-domain-validation-refactor/spec.md`

**Note**: 이 문서는 Member 엔티티가 생성/수정 시 email/password 공통 불변 조건을 직접 검증하도록 강화하는 계획입니다.

## Summary

현재 `Member`는 일반 회원 생성자, 카카오 회원 생성자, `update()`에서 값을 검증하지 않고 그대로 할당합니다. 일반 회원은 email/password를 모두 필수로 검증하고, 카카오 회원은 email만 필수로 검증합니다. 검증 실패는 member 도메인 예외인 `MemberValidationException`으로 표현합니다. 포인트 관련 예외는 이번 작업에서 변경하지 않습니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA, Bean Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / AssertJ / Spring Boot Test / MockMvc  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API with OAuth login and HTML admin pages  
**Performance Goals**: 성능 목표 없음, 도메인 상태 무결성 우선  
**Constraints**: 카카오 회원 password null 허용, 포인트 예외 변경 제외, email 형식 검증 제외  
**Scale/Scope**: `Member`, `MemberValidationException`, `MemberTest`, member/kakao 회귀 테스트

## Constitution Check

- Domain-First Architecture: Member가 자기 기본 불변 조건을 직접 보호합니다.
- Test-Driven Stability: 생성자와 update 검증을 단위 테스트로 고정합니다.
- Structural and Behavioral Separation: 도메인 필수값 검증은 Member에, 요청 형식 검증은 DTO에 둡니다.
- Consistent API and Error Handling: member 도메인 예외 체계를 사용합니다.
- Maintainable Simplicity: 일반 회원과 카카오 회원의 검증 차이를 생성자별로 명확히 둡니다.
- Small Scoped Changes: 포인트 정책과 AdminMemberController 리팩토링은 포함하지 않습니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 카카오 회원의 password null 허용을 유지하는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/012-member-domain-validation-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/member/
├── Member.java
├── MemberRequest.java
└── exception/
    ├── MemberException.java
    └── MemberValidationException.java
```

```text
src/test/java/gift/member/
├── MemberTest.java
├── MemberControllerTest.java
└── MemberServiceTest.java
```

```text
src/main/java/gift/auth/
└── KakaoAuthController.java
```

**Structure Decision**: `MemberValidationException`은 `MemberException`의 하위 타입으로 추가합니다. API 응답 매핑은 이번 작업의 필수 범위는 아니지만, 기존 global handler 패턴과 충돌하지 않도록 member 예외 체계를 따릅니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `Member(String email, String password)` 사용 지점을 확인합니다.
2. `Member(String email)`이 카카오 로그인 flow에서 사용되는지 확인합니다.
3. `Member.update()`의 사용 지점을 확인합니다.
4. 기존 member 예외 계층을 확인합니다.

## Phase 1: Design & Contracts

1. `MemberValidationException`을 추가합니다.
2. 일반 회원 생성자 검증 메서드를 설계합니다.
3. 카카오 회원 생성자 email 검증 메서드를 설계합니다.
4. `update()`가 필드 할당 전에 검증하도록 변경합니다.
5. Member 단위 테스트를 추가합니다.
6. Member/Kakao 관련 회귀 테스트를 실행합니다.

## Phase 2: Task Planning Approach

1. Member domain test 추가
2. MemberValidationException 추가
3. Member 생성자/update 검증 구현
4. 기존 API/Kakao flow 회귀 확인
5. tasks 완료 표시

## Risk Assessment

- **카카오 로그인 회귀**: 카카오 회원은 password 없이 생성되어야 하므로 일반 회원 검증을 재사용하지 않습니다.
- **email 형식 중복 검증**: 도메인에서는 blank만 막고 형식은 `MemberRequest`/OAuth provider 결과에 맡깁니다.
- **포인트 예외와 범위 혼동**: point method의 `IllegalArgumentException`은 후속 작업으로 남깁니다.
