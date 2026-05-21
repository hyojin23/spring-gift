# Implementation Plan: Category 예외 응답 일관화 리팩토링

**Branch**: `030-category-exception-response-refactor` | **Date**: 2026-05-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/030-category-exception-response-refactor/spec.md`

**Note**: 이 문서는 `CategoryNotFoundException`의 HTTP 응답을 다른 도메인 예외처럼 `ErrorResponse`로 통일하기 위한 계획입니다.

## Summary

`GlobalExceptionHandler.handleCategoryNotFound()`는 현재 404 status만 반환하고 body가 없습니다. 이를 `ErrorResponse.of("CATEGORY.NOT_FOUND", exception.getMessage())` 형태로 변경하고, global handler 단위 테스트와 category controller API 테스트에서 code/message body를 검증합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Web, Spring MVC Test  
**Storage**: Category JPA Repository  
**Testing**: JUnit 5 / AssertJ / MockMvc  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API 카테고리 컴포넌트  
**Performance Goals**: 성능 목표 없음, API 에러 응답 일관성 우선  
**Constraints**: HTTP status 유지, 정상 API 응답 변경 없음  
**Scale/Scope**: `GlobalExceptionHandler`, category/global tests

## Constitution Check

- Domain-First Architecture: 카테고리 미존재를 명확한 category error code로 표현합니다.
- Test-Driven Stability: handler와 controller 응답 body를 테스트로 고정합니다.
- Structural and Behavioral Separation: 도메인 예외 클래스는 유지하고 응답 변환만 변경합니다.
- Consistent API and Error Handling: 다른 도메인과 같은 `ErrorResponse` 형태로 맞춥니다.
- Maintainable Simplicity: handler method만 작게 변경합니다.
- Small Scoped Changes: category not found 응답에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, status는 유지하고 body만 일관화하는 API response refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/030-category-exception-response-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/global/
└── GlobalExceptionHandler.java
```

```text
src/test/java/gift/
├── category/CategoryControllerTest.java
└── global/GlobalExceptionHandlerTest.java
```

**Structure Decision**: `CategoryNotFoundException`은 기존 패키지와 메시지를 유지합니다. 예외 패키지 정리는 별도 spec에서 다룹니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. 현재 `handleCategoryNotFound()` 반환 타입과 status를 확인합니다.
2. 기존 `ErrorResponse` 생성 방식과 code naming을 확인합니다.
3. category controller 미존재 테스트를 확인합니다.

## Phase 1: Design & Contracts

1. `handleCategoryNotFound(CategoryNotFoundException exception)`로 예외 인자를 받습니다.
2. 반환 타입을 `ResponseEntity<ErrorResponse>`로 변경합니다.
3. status는 `HttpStatus.NOT_FOUND`, code는 `CATEGORY.NOT_FOUND`, message는 `exception.getMessage()`를 사용합니다.
4. handler 테스트와 controller 테스트를 갱신합니다.

## Phase 2: Task Planning Approach

1. `GlobalExceptionHandlerTest`에 category 미존재 테스트를 추가합니다.
2. `CategoryControllerTest.updateNotFoundCategory()`에 JSON body 검증을 추가합니다.
3. `GlobalExceptionHandler`를 변경합니다.
4. 대상 테스트와 전체 테스트를 실행합니다.

## Risk Assessment

- **클라이언트 응답 body 추가 영향**: status는 유지되며 body 추가는 에러 응답 일관성을 높입니다.
- **테스트 데이터 충돌 위험**: 기존 미존재 id `999999`를 유지합니다.
- **범위 확장 위험**: category exception 패키지 구조 변경은 제외합니다.
