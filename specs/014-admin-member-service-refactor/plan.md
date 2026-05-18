# Implementation Plan: Admin Member 서비스 분리 리팩토링

**Branch**: `014-admin-member-service-refactor` | **Date**: 2026-05-18 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/014-admin-member-service-refactor/spec.md`

**Note**: 이 문서는 AdminMemberController의 repository 직접 접근을 AdminMemberService로 이동하고, 관리자 HTML flow의 오류 처리를 redirect + flash message로 정리하는 계획입니다.

## Summary

`AdminMemberController`는 현재 회원 repository 접근, 중복 이메일 검증, 회원 조회/수정, 포인트 충전, 삭제를 직접 수행합니다. `AdminMemberService`를 도입해 비즈니스 로직과 repository 접근을 이동하고, controller에는 view 이름, redirect, form model 조립만 남깁니다. 관리자 화면 오류는 JSON 응답이 아니라 `/admin/members` redirect와 flash `error`로 처리합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Thymeleaf, Spring Data JPA  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / MockMvc / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: Web service with HTML admin pages and JSON API  
**Performance Goals**: 성능 목표 없음, controller/service 책임 분리 우선  
**Constraints**: Member API error contract 변경 없음, OrderController 변경 없음, Admin HTML flow 유지  
**Scale/Scope**: `AdminMemberController`, `AdminMemberService`, admin member exception class, member list template, Admin Member MockMvc 테스트

## Constitution Check

- Domain-First Architecture: 회원 조회/수정/포인트 충전과 repository 접근은 service 계층에서 처리합니다.
- Test-Driven Stability: Admin HTML flow를 MockMvc 테스트로 확인합니다.
- Structural and Behavioral Separation: view model 조립은 controller에, 비즈니스 로직은 service에 둡니다.
- Consistent API and Error Handling: Member JSON API error handling은 이번 작업에서 변경하지 않습니다.
- Maintainable Simplicity: Admin 전용 service를 추가해 API service와 HTML form 흐름을 분리합니다.
- Small Scoped Changes: Admin member 화면 controller에 한정하고 Order flow는 변경하지 않습니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 Admin HTML flow를 Member JSON API 리팩토링과 섞지 않는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/014-admin-member-service-refactor/
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
├── AdminMemberController.java
├── AdminMemberService.java
├── Member.java
├── MemberRepository.java
└── exception/
    ├── AdminMemberNotFoundException.java
    └── MemberException.java
```

```text
src/main/resources/templates/member/
└── list.html
```

```text
src/test/java/gift/member/
├── AdminMemberControllerTest.java
├── MemberControllerTest.java
└── MemberServiceTest.java
```

**Structure Decision**: Admin member flow는 `AdminMemberService`를 별도로 둡니다. `MemberService`는 JSON API 회원가입/로그인 흐름에 맞춰져 있으므로 admin form flow와 분리합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `AdminMemberController`의 repository 직접 접근 지점을 확인합니다.
2. 회원 미존재와 포인트 충전 예외가 HTML flow에서 어떻게 보일지 결정합니다.
3. member templates의 error 표시 위치를 확인합니다.
4. 기존 Member API 테스트와 충돌하지 않는지 확인합니다.

## Phase 1: Design & Contracts

1. `AdminMemberService`의 메서드를 설계합니다.
2. admin member not found 예외를 추가합니다.
3. controller-local `@ExceptionHandler` 또는 admin 전용 advice로 redirect + flash 처리합니다.
4. member list template에 flash `error` 표시를 추가합니다.
5. Admin Member MockMvc 테스트를 추가합니다.

## Phase 2: Task Planning Approach

1. AdminMemberController 테스트 추가
2. AdminMemberService 추가
3. controller repository 직접 접근 제거
4. admin member exception handling 추가
5. template flash message 표시 추가
6. Member API 회귀 테스트 실행

## Risk Assessment

- **API handler와의 충돌**: admin 예외는 controller-local handler로 처리해 JSON global handler와 섞이지 않게 합니다.
- **포인트 예외 처리**: admin 포인트 충전 실패는 HTML flow이므로 redirect + flash로 처리합니다.
- **중복 이메일 UX**: 등록 form 복구 흐름은 기존처럼 view 반환 방식으로 유지합니다.
