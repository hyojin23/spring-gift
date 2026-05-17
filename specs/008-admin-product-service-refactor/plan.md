# Implementation Plan: Admin Product 서비스 분리 리팩토링

**Branch**: `008-admin-product-service-refactor` | **Date**: 2026-05-17 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/008-admin-product-service-refactor/spec.md`

**Note**: 이 문서는 AdminProductController의 repository 직접 접근을 AdminProductService로 이동하고, 기존 HTML form/view 흐름을 유지하는 리팩토링 계획입니다.

## Summary

`AdminProductController`는 현재 상품/카테고리 repository 접근, 상품명 검증, 상품 생성/수정/삭제, form model 복구를 모두 직접 수행합니다. `AdminProductService`를 도입해 repository 접근과 상품 변경 로직을 이동하고, controller에는 view 이름, redirect, form model 조립만 남깁니다. Product JSON API와 global `ErrorResponse` 정책은 변경하지 않습니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Thymeleaf, Spring Data JPA  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / MockMvc / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: Web service with HTML admin pages  
**Performance Goals**: 성능 목표 없음, controller/service 책임 분리 우선  
**Constraints**: HTML template 변경 없음, Admin view/redirect 계약 유지, Product API error contract 변경 없음  
**Scale/Scope**: `AdminProductController`, `AdminProductService`, Admin product MockMvc 테스트

## Constitution Check

- Domain-First Architecture: 상품 생성/수정/삭제와 repository 접근은 service 계층에서 처리합니다.
- Test-Driven Stability: Admin HTML flow를 MockMvc 테스트로 확인합니다.
- Structural and Behavioral Separation: view model 조립은 controller에, 비즈니스 로직은 service에 둡니다.
- Consistent API and Error Handling: JSON API error handling은 이번 작업에서 변경하지 않습니다.
- Maintainable Simplicity: Admin 전용 service를 추가해 API service와 HTML form 흐름을 분리합니다.
- Small Scoped Changes: Product admin 화면 controller에 한정하고 template/UI는 변경하지 않습니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 Admin HTML flow를 Product JSON API 리팩토링과 섞지 않는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/008-admin-product-service-refactor/
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
├── Product.java
├── ProductNameValidator.java
├── ProductRepository.java
└── ProductService.java
```

```text
src/test/java/gift/product/
├── AdminProductControllerTest.java
├── ProductControllerTest.java
└── ProductServiceTest.java
```

**Structure Decision**: Admin product flow는 `AdminProductService`를 별도로 둡니다. `ProductService`는 JSON API 응답 DTO와 Product API 예외 계약에 맞춰져 있으므로 admin form flow와 분리합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |
