# Implementation Plan: Option 도메인 예외 리팩토링

**Branch**: `006-option-domain-exception-refactor` | **Date**: 2026-05-17 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/006-option-domain-exception-refactor/spec.md`

**Note**: 이 문서는 Option 수량 검증 실패를 일반 예외에서 Option 도메인 예외로 교체하고, global handler에 표준 응답 매핑을 추가하는 리팩토링 계획입니다.

## Summary

`Option` 도메인의 수량 검증 실패는 현재 `IllegalArgumentException`으로 표현될 수 있습니다. 이를 `OptionQuantityException extends OptionException`으로 교체하여 Option 예외 계층에 포함시키고, `GlobalExceptionHandler`에서 HTTP 400과 `OPTION.INVALID_QUANTITY`로 변환합니다. Bean Validation, 옵션명 검증, 다른 도메인 예외는 변경하지 않습니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web, Spring Data JPA, Spring Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / AssertJ / Spring Boot Test / MockMvc  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: Web service  
**Performance Goals**: 성능 목표 없음, 예외 의미와 응답 일관성 개선  
**Constraints**: 기존 Option API 응답 계약 유지, Bean Validation 예외 처리 제외, 옵션명 검증 흐름 유지  
**Scale/Scope**: `Option`, `gift.option.exception`, `GlobalExceptionHandler`, `OptionTest`, `GlobalExceptionHandlerTest`, Option 회귀 테스트

## Constitution Check

- Domain-First Architecture: Option 수량 오류는 Option 도메인 예외로 표현합니다.
- Test-Driven Stability: domain test와 handler test로 예외 타입 및 응답 매핑을 검증합니다.
- Structural and Behavioral Separation: 예외 타입과 handler 매핑을 개선하되 기존 API 성공 동작은 유지합니다.
- Consistent API and Error Handling: `GlobalExceptionHandler`에서 표준 `ErrorResponse`를 반환합니다.
- Maintainable Simplicity: 수량 예외는 하나의 `OptionQuantityException`으로 묶습니다.
- Small Scoped Changes: 변경 범위는 Option 수량 예외와 global handler 매핑으로 제한합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 Bean Validation과 옵션명 검증을 이번 범위에 포함하지 않는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/006-option-domain-exception-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/
├── global/
│   └── GlobalExceptionHandler.java
└── option/
    ├── Option.java
    └── exception/
        ├── OptionException.java
        └── OptionQuantityException.java
```

```text
src/test/java/gift/
├── global/
│   └── GlobalExceptionHandlerTest.java
└── option/
    └── OptionTest.java
```

**Structure Decision**: 기존 Option 예외 패키지와 global handler 구조를 유지하고, 수량 예외 타입만 추가합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |
