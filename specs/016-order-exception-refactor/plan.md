# Implementation Plan: Order 예외 처리 리팩토링

**Branch**: `016-order-exception-refactor` | **Date**: 2026-05-19 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/016-order-exception-refactor/spec.md`

**Note**: 이 문서는 주문 생성 실패 상황을 `Optional` 반환이 아니라 도메인 예외와 전역 예외 핸들러로 표현하기 위한 리팩토링 계획입니다.

## Summary

`OrderService.createOrder()`는 현재 옵션 미존재를 `Optional.empty()`로 반환하고, `OrderController`가 이를 404로 변환합니다. 이 구조를 `OrderOptionNotFoundException`으로 바꾸고, `GlobalExceptionHandler`에서 표준 `ErrorResponse`로 처리합니다. 주문 생성 중 발생 가능한 member 포인트 부족 예외도 전역 핸들러에 등록해 500으로 흐르지 않게 합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA, Bean Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / MockMvc / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API  
**Performance Goals**: 성능 목표 없음, 예외 의미와 응답 계약 개선 우선  
**Constraints**: 인증 실패 401 유지, 카카오 best-effort 유지, 위시 cleanup 제외  
**Scale/Scope**: `OrderService`, `OrderController`, order exception classes, `GlobalExceptionHandler`, Order/Global handler tests

## Constitution Check

- Domain-First Architecture: 주문 옵션 미존재를 order 도메인 예외로 표현합니다.
- Test-Driven Stability: 주문 생성 성공/옵션 미존재/포인트 부족/재고 부족 flow를 테스트로 고정합니다.
- Structural and Behavioral Separation: service는 예외를 발생시키고 controller는 정상 응답만 구성합니다.
- Consistent API and Error Handling: `GlobalExceptionHandler`에서 표준 `ErrorResponse`를 반환합니다.
- Maintainable Simplicity: 인증 예외 구조, 위시 cleanup, 알림 service 분리는 이번 범위에서 제외합니다.
- Small Scoped Changes: 주문 생성 예외 표현과 전역 핸들러 등록에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 기존 성공 응답과 인증 실패 응답을 유지합니다.

## Project Structure

### Documentation (this feature)

```text
specs/016-order-exception-refactor/
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
├── OrderController.java
├── OrderService.java
└── exception/
    ├── OrderException.java
    └── OrderOptionNotFoundException.java
```

```text
src/main/java/gift/global/
└── GlobalExceptionHandler.java
```

```text
src/test/java/gift/order/
├── OrderControllerTest.java
└── OrderServiceTest.java
```

```text
src/test/java/gift/global/
└── GlobalExceptionHandlerTest.java
```

**Structure Decision**: order 예외는 `gift.order.exception` 패키지에 둡니다. member/option 도메인에서 이미 발생하는 예외는 새 order 예외로 감싸지 않고 전역 핸들러에서 직접 처리합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `OrderService.createOrder()`의 `Optional` 반환과 controller 분기를 확인합니다.
2. 기존 order controller/service 테스트의 기대 응답을 확인합니다.
3. member 포인트 예외와 option 수량 예외의 전역 핸들러 등록 여부를 확인합니다.
4. 기존 `ErrorResponse` code naming pattern을 확인합니다.

## Phase 1: Design & Contracts

1. `OrderException`과 `OrderOptionNotFoundException`을 설계합니다.
2. `OrderService.createOrder()` 반환 타입을 `OrderResponse`로 변경합니다.
3. 옵션 미존재 시 `OrderOptionNotFoundException`을 던지도록 변경합니다.
4. `OrderController.createOrder()`에서 `Optional` 분기를 제거합니다.
5. `GlobalExceptionHandler`에 order/member point 예외 핸들러를 추가합니다.

## Phase 2: Task Planning Approach

1. 실패 응답 테스트 추가/수정
2. order 예외 클래스 추가
3. service/controller 리팩토링
4. global handler 등록
5. 전체 관련 테스트와 검색 검증

## Risk Assessment

- **응답 계약 변경 위험**: 옵션 미존재 응답은 기존 404를 유지하되 body가 표준 `ErrorResponse`로 추가됩니다.
- **포인트 부족 500 위험**: member 포인트 예외를 전역 핸들러에 등록해 의도된 400으로 처리합니다.
- **예외 wrapping 과다 위험**: member/option 도메인 예외는 order 예외로 감싸지 않고 기존 의미를 유지합니다.
- **카카오 알림 실패 전파 위험**: best-effort catch 정책은 변경하지 않습니다.
