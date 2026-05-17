# Implementation Plan: Option 삭제 검증 조회 최적화

**Branch**: `003-option-deletion-validation-refactor` | **Date**: 2026-05-17 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/003-option-deletion-validation-refactor/spec.md`

**Note**: 이 문서는 Option 삭제 가능 여부 검증을 목록 조회에서 count query로 변경하는 작은 리팩토링 계획입니다.

## Summary

`OptionService.deleteOption`은 마지막 옵션 삭제를 막기 위해 상품의 옵션 개수를 확인합니다. 현재 방식은 옵션 목록 전체를 조회한 뒤 `size()`로 개수를 판단할 수 있으므로, `OptionRepository.countByProductId(Long productId)`를 추가하고 삭제 검증에서 해당 메서드를 사용하도록 변경합니다. 외부 API 응답과 예외 타입은 변경하지 않습니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web, Spring Data JPA, Spring Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / AssertJ / Mockito / MockMvc  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: Web service  
**Performance Goals**: 삭제 검증 시 불필요한 Option 엔티티 목록 조회 제거  
**Constraints**: 기존 Option 삭제 정책과 API 응답 계약 유지  
**Scale/Scope**: `OptionRepository`, `OptionService`, `OptionServiceTest`, 필요 시 `OptionControllerTest`

## Constitution Check

- Domain-First Architecture: 마지막 옵션 삭제 제한은 기존처럼 서비스 계층에서 판단합니다.
- Test-Driven Stability: 변경 후 Option 관련 테스트로 회귀를 확인합니다.
- Structural and Behavioral Separation: 외부 동작은 유지하고 내부 조회 방식만 개선합니다.
- Consistent API and Error Handling: 기존 Option 예외 타입과 글로벌 핸들러 매핑을 그대로 사용합니다.
- Maintainable Simplicity: count query method로 검증 의도를 직접 표현합니다.
- Small Scoped Changes: 변경 범위는 option repository/service/test로 제한합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 API 응답 계약을 변경하지 않는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/003-option-deletion-validation-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/option/
├── OptionRepository.java
└── OptionService.java
```

```text
src/test/java/gift/option/
├── OptionControllerTest.java
└── OptionServiceTest.java
```

**Structure Decision**: 기존 단일 Spring Boot 백엔드 구조를 유지하고, 별도 패키지나 새 계층은 만들지 않습니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |
