# Implementation Plan: Product 도메인 검증 강화 리팩토링

**Branch**: `010-product-domain-validation-refactor` | **Date**: 2026-05-18 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/010-product-domain-validation-refactor/spec.md`

**Note**: 이 문서는 Product 엔티티가 생성/수정 시 공통 불변 조건을 직접 검증하도록 강화하는 계획입니다.

## Summary

현재 `Product`는 생성자와 `update()`에서 값을 검증하지 않고 그대로 할당합니다. request DTO와 service 검증을 우회하면 유효하지 않은 Product 상태가 만들어질 수 있으므로, `Product` 내부에 공통 검증 메서드를 추가합니다. 상품명 세부 정책과 `카카오` 허용 여부는 경로별 정책이므로 기존 `ProductNameValidator` 호출 위치를 유지합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA, Bean Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / AssertJ / Spring Boot Test / MockMvc  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: Web service with HTML admin pages and JSON API  
**Performance Goals**: 성능 목표 없음, 도메인 상태 무결성 우선  
**Constraints**: `카카오` 정책은 service 계층에 유지, Product API/Admin flow 회귀 없음  
**Scale/Scope**: `Product`, product domain tests, existing Product/Admin Product tests

## Constitution Check

- Domain-First Architecture: Product가 자기 불변 조건을 직접 보호합니다.
- Test-Driven Stability: 생성자와 update 검증을 단위 테스트로 고정합니다.
- Structural and Behavioral Separation: 공통 불변 조건은 domain에, 경로별 정책은 service에 둡니다.
- Consistent API and Error Handling: Product API error response 계약은 변경하지 않습니다.
- Maintainable Simplicity: 생성자와 update가 하나의 private validation 메서드를 공유합니다.
- Small Scoped Changes: Product 도메인 검증에 한정하고 DB 제약이나 UI 변경은 포함하지 않습니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 공통 도메인 검증과 경로별 상품명 정책을 섞지 않는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/010-product-domain-validation-refactor/
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
├── Product.java
├── ProductNameValidator.java
├── ProductRequest.java
├── ProductService.java
└── exception/
    └── ProductValidationException.java
```

```text
src/test/java/gift/product/
├── ProductTest.java
├── ProductServiceTest.java
└── AdminProductControllerTest.java
```

**Structure Decision**: Product 검증 실패는 기존 product 예외 체계의 `ProductValidationException`을 사용합니다. 새로운 예외 타입은 실제 구분 필요가 생기기 전까지 추가하지 않습니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. 현재 `Product` 생성자와 `update()`의 무검증 할당 지점을 확인합니다.
2. `ProductRequest` Bean Validation과 `ProductService.validateName()`의 역할을 분리해 정리합니다.
3. Product 도메인에서 항상 보장해야 하는 공통 불변 조건을 확정합니다.

## Phase 1: Design & Contracts

1. `Product`에 private validation 메서드를 추가합니다.
2. 생성자와 `update()`에서 validation을 먼저 수행한 후 필드를 할당합니다.
3. 검증 실패 시 `ProductValidationException`을 발생시킵니다.
4. Product 단위 테스트를 추가합니다.
5. Product API와 Admin Product 회귀 테스트를 실행합니다.

## Phase 2: Task Planning Approach

1. Product domain test 추가
2. Product validation 구현
3. ProductNameValidator 호출 정책 유지 확인
4. 회귀 테스트 실행
5. tasks 완료 표시

## Risk Assessment

- **중복 검증**: request DTO와 domain 양쪽에서 일부 검증이 중복될 수 있습니다. 이는 외부 입력 검증과 도메인 무결성 보장이 다른 책임이므로 허용합니다.
- **Admin/API 정책 충돌**: `카카오` 정책을 Product로 옮기지 않아 충돌을 피합니다.
- **부분 변경 위험**: `update()`에서 validation을 필드 할당보다 먼저 수행합니다.
