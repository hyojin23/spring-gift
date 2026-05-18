# Implementation Plan: Order 서비스 분리 리팩토링

**Branch**: `015-order-service-refactor` | **Date**: 2026-05-19 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/015-order-service-refactor/spec.md`

**Note**: 이 문서는 OrderController의 주문 조회/생성 비즈니스 로직을 OrderService로 이동하고, 기존 HTTP 응답 계약을 유지하는 리팩토링 계획입니다.

## Summary

`OrderController`는 현재 인증 확인, 주문 목록 조회, 옵션 조회, 재고 차감, 포인트 차감, 주문 저장, 카카오 알림 발송을 직접 수행합니다. `OrderService`를 도입해 주문 비즈니스 흐름을 이동하고, controller에는 인증 결과 확인과 HTTP 응답 구성만 남깁니다. 예외 표준화, 위시 cleanup, 알림 service 분리는 후속 spec에서 다룹니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA, Bean Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / MockMvc / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API  
**Performance Goals**: 성능 목표 없음, controller/service 책임 분리 우선  
**Constraints**: 기존 401/404 응답 유지, 위시 cleanup 제외, 알림 service 분리 제외  
**Scale/Scope**: `OrderController`, `OrderService`, Order controller/service tests

## Constitution Check

- Domain-First Architecture: 주문 생성 비즈니스 흐름을 service 계층으로 이동합니다.
- Test-Driven Stability: 주문 목록/생성 성공과 실패 flow를 테스트로 고정합니다.
- Structural and Behavioral Separation: HTTP 응답 구성은 controller에, 주문 처리 단계는 service에 둡니다.
- Consistent API and Error Handling: 이번 작업에서는 기존 401/404 응답 방식을 유지합니다.
- Maintainable Simplicity: 큰 예외 표준화와 위시 cleanup은 후속 작업으로 분리합니다.
- Small Scoped Changes: Order service 분리에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 behavior-preserving refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/015-order-service-refactor/
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
├── OrderController.java
├── OrderRepository.java
├── OrderRequest.java
├── OrderResponse.java
└── OrderService.java
```

```text
src/test/java/gift/order/
├── OrderControllerTest.java
└── OrderServiceTest.java
```

**Structure Decision**: OrderService는 주문 flow를 담당하되, 인증 헤더 parsing은 기존 `AuthenticationResolver`를 사용하는 controller에 남깁니다. service는 인증된 `Member`와 `OrderRequest`를 입력으로 받습니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `OrderController`의 repository/client 직접 의존성을 확인합니다.
2. 주문 목록 조회와 주문 생성의 기존 응답 계약을 확인합니다.
3. 카카오 알림 실패가 주문 실패로 전파되지 않는 정책을 확인합니다.
4. 현재 Order 테스트 부재를 확인합니다.

## Phase 1: Design & Contracts

1. `OrderService.getOrders(memberId, pageable)`를 설계합니다.
2. `OrderService.createOrder(member, request)`를 설계합니다.
3. option 미존재는 기존 behavior를 유지하기 위해 service에서 `null` 또는 결과 타입으로 표현합니다.
4. controller는 service 결과에 따라 404 또는 201 응답을 구성합니다.
5. Order controller/service 테스트를 추가합니다.

## Phase 2: Task Planning Approach

1. Order service/controller 테스트 추가
2. OrderService 추가
3. OrderController 의존성 축소
4. Kakao best-effort 로직 이동
5. 테스트와 dependency 검색 검증

## Risk Assessment

- **응답 계약 변경 위험**: 예외 표준화는 하지 않고 기존 401/404 응답을 유지합니다.
- **Kakao 알림 실패 전파 위험**: service에서도 기존처럼 예외를 삼켜 주문 생성 성공을 유지합니다.
- **WishRepository 미사용 혼란**: cleanup은 후속 spec에서 구현하므로 이번 작업에서는 controller/service 모두에 추가하지 않습니다.
