# Implementation Plan: Admin Product 예외 처리 리팩토링

**Branch**: `009-admin-product-exception-refactor` | **Date**: 2026-05-18 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/009-admin-product-exception-refactor/spec.md`

**Note**: 이 문서는 ADR-0001의 결정에 따라 관리자 상품 화면의 미존재 예외를 도메인 예외와 flash attribute redirect로 처리하는 계획입니다.

## Summary

`AdminProductService`는 현재 상품/카테고리 미존재 상황에서 `NoSuchElementException`을 발생시킵니다. 이를 관리자 상품 화면 전용 도메인 예외로 바꾸고, controller 또는 관리자 전용 handler에서 `/admin/products` redirect와 `error` flash attribute로 처리합니다. Product JSON API의 `ErrorResponse` 계약과 global handler는 변경하지 않습니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Thymeleaf, Spring Data JPA  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / MockMvc / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: Web service with HTML admin pages and JSON API  
**Performance Goals**: 성능 목표 없음, 예외 표현과 화면 UX 일관성 우선  
**Constraints**: Product API error contract 변경 없음, 별도 error page 생성 없음, 상품명 검증 실패 flow 유지  
**Scale/Scope**: `AdminProductService`, admin product exception classes, admin exception handling, product list template, Admin Product MockMvc 테스트

## Constitution Check

- Domain-First Architecture: 관리자 상품 화면의 미존재 상황을 범용 예외가 아닌 도메인 예외로 표현합니다.
- Test-Driven Stability: redirect와 flash attribute를 MockMvc 테스트로 확인합니다.
- Structural and Behavioral Separation: 관리자 HTML 예외 처리는 Product API JSON 예외 처리와 분리합니다.
- Consistent API and Error Handling: Product API의 `ErrorResponse` 계약은 변경하지 않습니다.
- Maintainable Simplicity: 별도 error page 없이 ADR에서 선택한 flash redirect 방식을 적용합니다.
- Small Scoped Changes: 관리자 상품 화면 예외 처리에 한정하고 다른 product 기능은 변경하지 않습니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 관리자 HTML flow와 Product API error flow를 섞지 않는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/009-admin-product-exception-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/product/
├── AdminProductController.java
├── AdminProductService.java
└── exception/
    ├── AdminProductCategoryNotFoundException.java
    ├── AdminProductException.java
    └── AdminProductNotFoundException.java
```

```text
src/main/resources/templates/product/
└── list.html
```

```text
src/test/java/gift/product/
└── AdminProductControllerTest.java
```

**Structure Decision**: 기존 product exception package를 사용하되, 관리자 화면 전용 예외는 `AdminProduct...` 접두사를 사용해 API용 `Product...Exception`과 구분합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. ADR-0001에서 flash attribute redirect를 선택한 이유를 확인합니다.
2. 현재 `AdminProductService`의 `NoSuchElementException` 사용 지점을 확인합니다.
3. 관리자 화면 예외 handler 위치를 결정합니다.
4. product list template에서 flash `error` 표시 위치를 확인합니다.

## Phase 1: Design & Contracts

1. 관리자 상품 예외 계층을 정의합니다.
2. `AdminProductService`에서 상품/카테고리 미존재 예외를 교체합니다.
3. 관리자 상품 예외를 `/admin/products` redirect + flash `error`로 처리합니다.
4. 상품 목록 template에 flash error 표시를 추가합니다.
5. MockMvc 테스트 시나리오를 정의합니다.

## Phase 2: Task Planning Approach

1. 예외 클래스 추가
2. service 예외 교체
3. controller 또는 handler 예외 처리 추가
4. template flash message 표시 추가
5. Admin Product controller 테스트 추가
6. Product API 관련 테스트 회귀 확인

## Risk Assessment

- **API handler와의 충돌**: 관리자 예외가 `GlobalExceptionHandler` JSON 응답으로 처리되지 않도록 controller-local handler 또는 admin 전용 advice를 사용합니다.
- **UX 혼동**: redirect 후 이유를 알 수 있도록 flash `error`를 반드시 표시합니다.
- **기존 form validation flow 회귀**: 상품명 검증 실패는 예외가 아니라 form validation 결과이므로 기존 view 반환 방식을 유지합니다.

## ADR Reference

- [ADR-0001: 관리자 상품 화면 예외 처리 방식으로 Flash Attribute Redirect 사용](../../docs/adr/0001-admin-product-exception-flash-redirect.md)
