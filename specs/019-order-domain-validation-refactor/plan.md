# Implementation Plan: Order 도메인 검증 강화 리팩토링

**Branch**: `019-order-domain-validation-refactor` | **Date**: 2026-05-20 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/019-order-domain-validation-refactor/spec.md`

**Note**: 이 문서는 `Order` 생성자에 도메인 불변조건 검증을 추가하는 리팩토링 계획입니다.

## Summary

`Order`는 현재 생성자에서 `option`, `memberId`, `quantity`를 검증하지 않습니다. 주문 대상 옵션, 주문 회원 ID, 양수 수량은 주문 도메인의 필수 불변조건이므로 `Order` 생성자에서 직접 보호합니다. 검증 실패는 `OrderValidationException`으로 표현하고, 기존 주문 생성 flow와 API 응답 계약은 유지합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA, Bean Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API  
**Performance Goals**: 성능 목표 없음, 도메인 불변조건 보호 우선  
**Constraints**: message 선택값 유지, orderDateTime 자동 설정 유지, API 응답 계약 유지  
**Scale/Scope**: `Order`, `OrderValidationException`, Order domain/service tests

## Constitution Check

- Domain-First Architecture: 주문 필수 조건을 `Order` 도메인 객체가 직접 보호합니다.
- Test-Driven Stability: 정상 생성과 검증 실패 케이스를 테스트로 고정합니다.
- Structural and Behavioral Separation: API request 검증과 도메인 검증을 각각 유지합니다.
- Consistent API and Error Handling: 주문 생성 API의 기존 예외 응답을 변경하지 않습니다.
- Maintainable Simplicity: 검증 helper만 추가하고 DB 제약이나 API 계약은 건드리지 않습니다.
- Small Scoped Changes: `Order` 생성자 검증에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 정상 주문 생성 흐름을 유지합니다.

## Project Structure

### Documentation (this feature)

```text
specs/019-order-domain-validation-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/order/
├── Order.java
└── exception/
    ├── OrderException.java
    └── OrderValidationException.java
```

```text
src/test/java/gift/order/
├── OrderTest.java
├── OrderServiceTest.java
└── OrderControllerTest.java
```

**Structure Decision**: 검증 예외는 기존 order 예외 계층에 맞춰 `gift.order.exception.OrderValidationException`으로 둡니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `Order` 생성자 검증 부재를 확인합니다.
2. 기존 `OrderException` 계층을 확인합니다.
3. 기존 `OrderRequest` Bean Validation을 확인합니다.
4. 기존 `OrderServiceTest` fixture가 검증 조건을 만족하는지 확인합니다.

## Phase 1: Design & Contracts

1. `OrderValidationException`을 추가합니다.
2. `Order` 생성자에 `validateOption()`, `validateMemberId()`, `validateQuantity()`를 추가합니다.
3. `message`는 선택값으로 유지합니다.
4. 정상 생성 시 `orderDateTime = LocalDateTime.now()` 기존 동작을 유지합니다.
5. `OrderTest`를 추가해 정상/실패 케이스를 검증합니다.

## Phase 2: Task Planning Approach

1. Order domain 테스트 추가
2. OrderValidationException 추가
3. Order 생성자 검증 구현
4. 기존 Order service/controller 테스트 실행
5. 검색으로 예외/검증 구조 확인

## Risk Assessment

- **테스트 fixture 회귀 위험**: 기존 테스트 fixture가 `memberId`, `option`, `quantity`를 모두 유효하게 넘기는지 확인합니다.
- **API 응답 변경 위험**: request validation과 service flow는 변경하지 않으므로 기존 controller 테스트로 검증합니다.
- **message 정책 확대 위험**: message는 선택값이므로 이번 검증 범위에서 제외합니다.
- **DB 제약과 중복 위험**: DB 제약 추가는 별도 작업으로 남깁니다.
