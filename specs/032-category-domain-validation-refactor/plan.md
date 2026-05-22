# Implementation Plan: Category 도메인 검증 강화 리팩토링

**Branch**: `032-category-domain-validation-refactor` | **Date**: 2026-05-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/032-category-domain-validation-refactor/spec.md`

**Note**: 이 문서는 `Category` 도메인이 생성/수정 시 필수 값을 직접 검증하도록 강화하기 위한 계획입니다.

## Summary

현재 `CategoryRequest`에는 `@NotBlank`가 있지만 `Category` 도메인 생성자와 `update()`는 잘못된 상태를 허용합니다. `CategoryValidationException`을 추가하고 `Category`에서 name, color, imageUrl 필수 값을 검증합니다. GlobalExceptionHandler는 이를 400 `CATEGORY.INVALID` 응답으로 변환합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Web, JPA  
**Storage**: Category JPA Entity  
**Testing**: JUnit 5 / AssertJ / MockMvc  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API 카테고리 컴포넌트  
**Performance Goals**: 성능 목표 없음, 도메인 불변 조건 강화 우선  
**Constraints**: 기존 API 성공 응답 변경 없음, description 필수 검증 제외  
**Scale/Scope**: `gift.category`, `GlobalExceptionHandler`, 관련 테스트

## Constitution Check

- Domain-First Architecture: Category가 자기 필수 값을 직접 검증합니다.
- Test-Driven Stability: 생성/update 실패 케이스를 도메인 테스트로 고정합니다.
- Structural and Behavioral Separation: request DTO 검증과 도메인 검증을 분리해 각각 역할을 유지합니다.
- Consistent API and Error Handling: category 검증 예외도 `ErrorResponse`로 변환합니다.
- Maintainable Simplicity: 필수 값 검증에 한정하고 형식 검증은 제외합니다.
- Small Scoped Changes: Category 도메인 검증과 handler/test에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 도메인 안전망을 추가하는 validation refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/032-category-domain-validation-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/category/
├── Category.java
└── CategoryValidationException.java
```

```text
src/main/java/gift/global/
└── GlobalExceptionHandler.java
```

```text
src/test/java/gift/
├── category/CategoryTest.java
├── category/CategoryControllerTest.java
└── global/GlobalExceptionHandlerTest.java
```

**Structure Decision**: 이번 작업에서는 기존 category 예외 패키지 구조를 유지합니다. category exception package 정리는 별도 작업으로 분리합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `CategoryRequest`의 현재 bean validation을 확인합니다.
2. `Category` 생성자/update의 현재 검증 부재를 확인합니다.
3. 다른 도메인 validation exception handler naming을 확인합니다.

## Phase 1: Design & Contracts

1. `CategoryValidationException`을 추가합니다.
2. `Category` 생성자와 `update()`에서 공통 validate method를 호출합니다.
3. validate method는 name/color/imageUrl null 또는 blank를 검사합니다.
4. `GlobalExceptionHandler`에 category validation handler를 추가합니다.
5. 도메인 테스트와 handler 테스트를 추가합니다.

## Phase 2: Task Planning Approach

1. `CategoryTest`를 추가해 생성/update 실패 케이스를 작성합니다.
2. `GlobalExceptionHandlerTest`에 category validation 테스트를 추가합니다.
3. `CategoryValidationException`과 도메인 검증을 구현합니다.
4. 대상 테스트와 전체 테스트를 실행합니다.

## Risk Assessment

- **기존 fixture 실패 위험**: 테스트/시드 데이터의 category 필수 값이 모두 유효한지 확인합니다.
- **Bean validation 중복 위험**: API 요청 검증과 도메인 검증은 계층별 안전망으로 중복을 허용합니다.
- **범위 확장 위험**: color/imageUrl 형식 검증은 이번 작업에서 제외합니다.
