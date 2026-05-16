# Implementation Plan: Option 패키지 예외 처리 리팩토링

**Branch**: `002-option-exception-refactor` | **Date**: 2026-05-17 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/002-option-exception-refactor/spec.md`

**Note**: 이 문서는 `/speckit.plan` 명령 실행 결과이며, Option 예외 처리 리팩토링 설계를 문서화합니다.

## Summary

`Option` 도메인의 예외 처리 구조를 `category` 패키지와 같은 중앙 집중식 예외 처리 패턴으로 맞춥니다. 현재 `OptionController`는 상품/옵션 조회 실패, 옵션명 검증, 중복 옵션명, 마지막 옵션 삭제 제한, `IllegalArgumentException` 응답 변환까지 직접 처리하고 있습니다. 이를 `OptionService`와 `gift.option.exception` 패키지로 분리하고, `GlobalExceptionHandler`에서 `ErrorResponse`를 반환하도록 변경합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web, Spring Data JPA, Spring Validation, Jackson  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / AssertJ / MockMvc  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: Web service  
**Performance Goals**: 오류 응답 일관성 및 유지보수성 우선, 추가 성능 목표 없음  
**Constraints**: 기존 Option API의 정상 응답 계약 유지, controller-level 중복 예외 로직 제거, 일관된 JSON 에러 모델 적용  
**Scale/Scope**: `gift.option` 예외 처리 리팩토링, `OptionService` 도입, 글로벌 예외 처리 확장, 관련 테스트 추가

## Constitution Check

- Domain-First Architecture: 옵션 관련 비즈니스 규칙과 실패 조건은 서비스/도메인 계층에서 판단하고, 컨트롤러는 요청/응답 흐름에 집중합니다.
- Test-Driven Stability: 변경 후 전체 테스트와 추가 Option 예외 테스트를 통해 안정성을 검증합니다.
- Structural and Behavioral Separation: 리팩토링의 주 목적은 예외 처리 구조 개선이며, 정상 API 동작은 유지합니다.
- Consistent API and Error Handling: `GlobalExceptionHandler`를 통해 공통 에러 응답 형식을 보장합니다.
- Maintainable Simplicity: controller-level `@ExceptionHandler`, `orElse(null)`, 직접 상태 반환을 제거하고 예외 기반 흐름을 단순화합니다.
- Small Scoped Changes: 변경 범위는 `gift.option`, `gift.global`, 예외 패키지 및 관련 테스트로 제한됩니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 기존 Option API의 성공 응답 계약과 HTTP 상태 의미를 유지하는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/002-option-exception-refactor/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── error-response.md
└── spec.md
```

### Source Code (repository root)

```text
src/main/java/gift/
├── global/
│   ├── GlobalExceptionHandler.java
│   └── exception/
│       └── ErrorResponse.java
├── option/
│   ├── Option.java
│   ├── OptionController.java
│   ├── OptionNameValidator.java
│   ├── OptionRepository.java
│   ├── OptionRequest.java
│   ├── OptionResponse.java
│   ├── OptionService.java
│   └── exception/
│       ├── DuplicateOptionNameException.java
│       ├── OptionDeletionNotAllowedException.java
│       ├── OptionException.java
│       ├── OptionNotFoundException.java
│       ├── OptionProductNotFoundException.java
│       └── OptionValidationException.java
└── product/
    ├── Product.java
    └── ProductRepository.java
```

```text
src/test/java/gift/
├── global/
│   └── GlobalExceptionHandlerTest.java
└── option/
    ├── OptionControllerTest.java
    └── OptionServiceTest.java
```

**Structure Decision**: 단일 Spring Boot 백엔드 애플리케이션 구조를 유지합니다. 이 기능은 `gift.option` 패키지의 서비스/예외 구조와 `gift.global` 글로벌 예외 처리 계층을 중심으로 진행됩니다.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 변경은 `gift.option` 예외 처리 일관성 확보를 위한 리팩토링 | 단순 controller-level 상태 반환 유지 시 중복과 응답 형식 불일치가 남음 |
