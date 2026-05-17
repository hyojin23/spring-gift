# Implementation Plan: Member API 서비스 및 예외 처리 리팩토링

**Branch**: `011-member-service-exception-refactor` | **Date**: 2026-05-18 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/011-member-service-exception-refactor/spec.md`

**Note**: 이 문서는 Member API controller의 비즈니스 로직을 service로 이동하고, `IllegalArgumentException` 기반 실패를 member 도메인 예외와 global `ErrorResponse`로 정리하는 계획입니다.

## Summary

`MemberController`는 현재 회원가입/로그인 로직, repository 접근, JWT 발급, `IllegalArgumentException` handler를 모두 직접 가지고 있습니다. `MemberService`를 도입해 회원가입/로그인 비즈니스 로직을 이동하고, 중복 이메일과 로그인 실패를 member 도메인 예외로 표현합니다. `GlobalExceptionHandler`는 이 예외를 표준 `ErrorResponse`로 변환합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA, Bean Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / MockMvc / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API with HTML admin pages  
**Performance Goals**: 성능 목표 없음, 책임 분리와 예외 계약 일관성 우선  
**Constraints**: JWT 생성 방식 유지, 비밀번호 암호화 제외, AdminMemberController 제외  
**Scale/Scope**: `MemberController`, `MemberService`, member exception classes, `GlobalExceptionHandler`, member tests

## Constitution Check

- Domain-First Architecture: 중복 이메일과 로그인 실패를 member 도메인 예외로 표현합니다.
- Test-Driven Stability: 회원가입/로그인 성공과 실패 flow를 테스트로 고정합니다.
- Structural and Behavioral Separation: controller는 HTTP mapping에 집중하고 service가 비즈니스 로직을 담당합니다.
- Consistent API and Error Handling: member API 예외도 `ErrorResponse` JSON 계약을 사용합니다.
- Maintainable Simplicity: 기존 JWT 발급과 request DTO 검증은 유지합니다.
- Small Scoped Changes: Admin member 화면과 포인트 도메인 예외는 후속 작업으로 분리합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 Member API 리팩토링을 AdminMemberController와 섞지 않는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/011-member-service-exception-refactor/
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
├── MemberController.java
├── MemberRepository.java
├── MemberRequest.java
├── MemberService.java
└── exception/
    ├── DuplicateMemberEmailException.java
    ├── InvalidMemberCredentialsException.java
    └── MemberException.java
```

```text
src/main/java/gift/global/
└── GlobalExceptionHandler.java
```

```text
src/test/java/gift/member/
├── MemberControllerTest.java
└── MemberServiceTest.java
```

```text
src/test/java/gift/global/
└── GlobalExceptionHandlerTest.java
```

**Structure Decision**: member 예외는 `gift.member.exception` 패키지에 둡니다. API 응답 변환은 기존 global handler에 member 전용 handler 메서드를 추가합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. 현재 `MemberController`의 repository/JWT 직접 접근을 확인합니다.
2. 기존 `IllegalArgumentException` 발생 지점을 분류합니다.
3. Member API와 AdminMember HTML flow의 범위를 분리합니다.
4. 기존 `ErrorResponse` code naming pattern을 확인합니다.

## Phase 1: Design & Contracts

1. `MemberService`의 `register`와 `login` 메서드를 설계합니다.
2. member exception hierarchy를 추가합니다.
3. `GlobalExceptionHandler`에 member 예외 handler를 추가합니다.
4. controller/service 테스트를 추가합니다.
5. GlobalExceptionHandler 테스트를 보강합니다.

## Phase 2: Task Planning Approach

1. Member API 테스트 추가
2. MemberService 테스트 추가
3. member 예외 클래스 추가
4. MemberService 구현
5. MemberController 리팩토링
6. GlobalExceptionHandler member handler 추가
7. 테스트 실행

## Risk Assessment

- **응답 계약 변경 위험**: 성공 status/body는 유지하고 실패 응답만 기존 문자열 body에서 표준 `ErrorResponse`로 변경합니다.
- **Admin flow 혼입**: AdminMemberController는 이번 작업에서 변경하지 않습니다.
- **보안 메시지 노출**: 로그인 실패는 이메일 미존재와 비밀번호 불일치를 같은 메시지로 처리합니다.
