# Implementation Plan: Product 서비스 및 예외 처리 리팩토링

**Branch**: `007-product-service-exception-refactor` | **Date**: 2026-05-17 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/007-product-service-exception-refactor/spec.md`

**Note**: 이 문서는 Product API controller의 repository 직접 접근과 오류 분기를 `ProductService`, Product 도메인 예외, `GlobalExceptionHandler`로 이동하는 리팩토링 계획입니다.

## Summary

`ProductController`는 현재 상품 조회/생성/수정/삭제, 상품명 검증, 상품/카테고리 존재 확인, controller-level `IllegalArgumentException` handler를 직접 포함합니다. 이를 `ProductService`와 `gift.product.exception` 패키지로 분리하고, `GlobalExceptionHandler`에서 표준 `ErrorResponse`를 반환하도록 변경합니다. Admin product 화면 controller는 이번 범위에서 제외합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web, Spring Data JPA, Spring Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / AssertJ / Mockito / Spring Boot Test / MockMvc  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: Web service  
**Performance Goals**: 성능 목표 없음, controller/service/exception 구조 개선 우선  
**Constraints**: 기존 Product API 정상 응답 계약 유지, AdminProductController 제외, Bean Validation 예외 표준화 제외  
**Scale/Scope**: `ProductController`, `ProductService`, `gift.product.exception`, `GlobalExceptionHandler`, Product/handler 테스트

## Constitution Check

- Domain-First Architecture: Product API 비즈니스 규칙과 실패 조건은 서비스 계층에서 판단합니다.
- Test-Driven Stability: Product API 성공/실패 flow와 handler 매핑을 테스트로 확인합니다.
- Structural and Behavioral Separation: controller는 성공 응답 조립에 집중하고 오류 변환은 global handler로 이동합니다.
- Consistent API and Error Handling: Product 예외도 `ErrorResponse` 표준 형식으로 반환합니다.
- Maintainable Simplicity: Option 패키지에서 정립한 service/exception 패턴을 재사용합니다.
- Small Scoped Changes: 변경 범위는 Product API에 제한하고 admin 화면 flow는 건드리지 않습니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 AdminProductController와 Bean Validation 예외 처리를 범위에서 제외하는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/007-product-service-exception-refactor/
├── contracts/
│   └── error-response.md
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
└── product/
    ├── ProductController.java
    ├── ProductService.java
    ├── ProductNameValidator.java
    ├── ProductRepository.java
    └── exception/
        ├── ProductException.java
        ├── ProductNotFoundException.java
        ├── ProductCategoryNotFoundException.java
        └── ProductValidationException.java
```

```text
src/test/java/gift/
├── global/
│   └── GlobalExceptionHandlerTest.java
└── product/
    ├── ProductControllerTest.java
    └── ProductServiceTest.java
```

**Structure Decision**: Product API는 Option API와 같은 service/exception/global handler 구조를 따릅니다. Admin product controller는 HTML form controller이므로 별도 작업으로 분리합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |
