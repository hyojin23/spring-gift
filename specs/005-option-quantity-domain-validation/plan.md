# Implementation Plan: Option 수량 도메인 검증 강화

**Branch**: `005-option-quantity-domain-validation` | **Date**: 2026-05-17 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/005-option-quantity-domain-validation/spec.md`

**Note**: 이 문서는 `Option` 엔티티가 수량 불변식을 직접 검증하도록 강화하는 작은 도메인 리팩토링 계획입니다.

## Summary

`OptionRequest`는 API 입력 수량을 Bean Validation으로 검증하지만, `Option` 생성자와 `subtractQuantity`는 도메인 객체가 직접 지켜야 할 수량 불변식을 충분히 표현하지 않을 수 있습니다. `Option` 생성 시 수량 범위를 검증하고, 수량 차감 시 0 이하 차감 수량을 막아 잘못된 도메인 상태를 방지합니다. 옵션명 검증과 API 응답 계약은 변경하지 않습니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web, Spring Data JPA, Spring Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / AssertJ / Spring Boot Test  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: Web service  
**Performance Goals**: 성능 목표 없음, 도메인 불변식 강화 우선  
**Constraints**: 기존 API 응답 계약 유지, 옵션명 검증 흐름 유지, 예외 타입 통일은 별도 작업으로 분리  
**Scale/Scope**: `Option`, `OptionTest`, 기존 Option 테스트 회귀 확인

## Constitution Check

- Domain-First Architecture: 수량 불변식은 `Option` 도메인 객체가 직접 보호합니다.
- Test-Driven Stability: `Option` 단위 테스트와 기존 Option 테스트로 안정성을 확인합니다.
- Structural and Behavioral Separation: API 입력 검증은 유지하고 도메인 내부 검증만 보강합니다.
- Consistent API and Error Handling: 외부 API 응답 계약은 변경하지 않습니다.
- Maintainable Simplicity: 수량 검증 상수와 helper를 `Option` 내부에 둡니다.
- Small Scoped Changes: 변경 범위는 option 도메인과 테스트로 제한합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 옵션명 검증과 예외 타입 통일을 이번 범위에 포함하지 않는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/005-option-quantity-domain-validation/
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
├── Option.java
└── OptionRequest.java
```

```text
src/test/java/gift/option/
├── OptionControllerTest.java
├── OptionServiceTest.java
└── OptionTest.java
```

**Structure Decision**: 기존 option 패키지 구조를 유지하고, 도메인 단위 테스트만 추가합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |
