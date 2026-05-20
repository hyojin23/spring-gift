# Implementation Plan: Order 총액 계산 책임 분리 리팩토링

**Branch**: `022-order-total-price-refactor` | **Date**: 2026-05-20 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/022-order-total-price-refactor/spec.md`

**Note**: 이 문서는 `OrderService`의 주문 총액 계산식을 이름 있는 private method로 분리하는 계획입니다.

## Summary

`OrderService.createOrder()`는 현재 `option.getProduct().getPrice() * request.quantity()` 계산식을 직접 가지고 있습니다. 계산식 자체는 유지하되 `calculateTotalPrice(option, quantity)` private method로 분리해 포인트 차감 금액의 의미를 명확히 합니다. 주문 생성 성공/실패 동작, 위시 cleanup, 알림 호출 순서는 변경하지 않습니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA  
**Storage**: 해당 없음  
**Testing**: JUnit 5 / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API  
**Performance Goals**: 성능 목표 없음, 주문 생성 flow 가독성 개선 우선  
**Constraints**: 계산식 유지, 타입 유지, 주문 응답/예외 흐름 유지  
**Scale/Scope**: `OrderService`, `OrderServiceTest`

## Constitution Check

- Domain-First Architecture: 주문 총액이라는 의미를 service 내부에서 명확히 표현합니다.
- Test-Driven Stability: 기존 포인트 차감 테스트로 계산 결과를 고정합니다.
- Structural and Behavioral Separation: 계산식 분리만 수행하고 정책 변경은 하지 않습니다.
- Consistent API and Error Handling: API 응답과 예외 흐름을 변경하지 않습니다.
- Maintainable Simplicity: 별도 가격 정책 service나 value object는 만들지 않습니다.
- Small Scoped Changes: `OrderService` 내부 private method 추출에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, behavior-preserving refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/022-order-total-price-refactor/
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
└── OrderService.java
```

```text
src/test/java/gift/order/
└── OrderServiceTest.java
```

**Structure Decision**: 총액 계산은 아직 단순하고 `OrderService` 내부 주문 생성 flow에서만 사용되므로 private method로 분리합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. 현재 `OrderService.createOrder()`의 가격 계산식을 확인합니다.
2. 기존 포인트 차감 테스트가 총액 계산을 검증하는지 확인합니다.
3. 주문 실패 flow 테스트가 유지되는지 확인합니다.
4. 위시 cleanup과 알림 호출 순서를 확인합니다.

## Phase 1: Design & Contracts

1. `calculateTotalPrice(Option option, int quantity)` private method를 설계합니다.
2. 기존 계산식 `option.getProduct().getPrice() * quantity`를 method 내부로 이동합니다.
3. `member.deductPoint(totalPrice)`가 method 결과를 사용하도록 변경합니다.
4. 기존 order 테스트를 실행합니다.

## Phase 2: Task Planning Approach

1. 기존 OrderServiceTest 검토
2. calculateTotalPrice private method 추가
3. createOrder에서 method 사용
4. order 테스트 실행
5. 검색으로 직접 계산식 위치 확인

## Risk Assessment

- **동작 변경 위험**: 계산식과 int 타입을 그대로 유지합니다.
- **과도한 추상화 위험**: 별도 service/value object를 만들지 않고 private method만 추가합니다.
- **테스트 공백 위험**: 기존 포인트 차감 테스트로 1,000원 * 2개 = 2,000원 차감을 검증합니다.
