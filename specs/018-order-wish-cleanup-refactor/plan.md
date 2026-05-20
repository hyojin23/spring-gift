# Implementation Plan: Order 위시리스트 정리 리팩토링

**Branch**: `018-order-wish-cleanup-refactor` | **Date**: 2026-05-20 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/018-order-wish-cleanup-refactor/spec.md`

**Note**: 이 문서는 주문 성공 후 주문 상품을 회원 위시리스트에서 정리하는 동작을 `OrderService`에 추가하기 위한 계획입니다.

## Summary

주문 성공 후 `memberId + productId` 기준으로 위시리스트 항목을 조회하고, 존재하면 삭제합니다. 위시가 없으면 아무 작업도 하지 않습니다. 옵션 미존재, 재고 부족, 포인트 부족 등 주문 실패 flow에서는 위시리스트를 변경하지 않습니다. 주문 저장 이후 cleanup을 수행하고, 이후 기존 `OrderNotificationService`를 호출합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA, Bean Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / MockMvc / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API  
**Performance Goals**: 성능 목표 없음, 주문 성공 후 데이터 정합성 개선 우선  
**Constraints**: 주문 실패 시 wish 유지, 기존 API 응답 계약 유지, 알림 서비스 분리 유지  
**Scale/Scope**: `OrderService`, `WishRepository` 사용, Order/Wish tests

## Constitution Check

- Domain-First Architecture: 주문 성공 후 구매 완료 상품을 위시리스트에서 정리하는 정책을 service 계층에 둡니다.
- Test-Driven Stability: 위시 있음/없음/주문 실패 flow를 테스트로 고정합니다.
- Structural and Behavioral Separation: HTTP layer는 변경하지 않고 주문 service에서 business cleanup을 처리합니다.
- Consistent API and Error Handling: 기존 order/member/option 예외 응답을 유지합니다.
- Maintainable Simplicity: 별도 cleanup service는 만들지 않고 작은 정책으로 `OrderService`에 둡니다.
- Small Scoped Changes: 주문 성공 후 wish cleanup에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주문 성공/실패 응답 계약을 유지합니다.

## Project Structure

### Documentation (this feature)

```text
specs/018-order-wish-cleanup-refactor/
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
src/main/java/gift/wish/
├── Wish.java
└── WishRepository.java
```

```text
src/test/java/gift/order/
├── OrderControllerTest.java
└── OrderServiceTest.java
```

**Structure Decision**: cleanup은 주문 성공에 따른 후처리 정책이므로 `OrderService`에 둡니다. 위시 도메인의 조회/삭제 기능은 기존 `WishRepository`를 사용합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. 현재 주문 생성 flow에서 wish cleanup이 없는지 확인합니다.
2. `WishRepository.findByMemberIdAndProductId()` 사용 가능 여부를 확인합니다.
3. 기존 주문 생성 성공/실패 응답 테스트를 확인합니다.
4. 위시 삭제 기준을 `memberId + productId`로 확정합니다.

## Phase 1: Design & Contracts

1. `OrderService`에 `WishRepository`를 주입합니다.
2. 주문 저장 후 `cleanupWish(member.getId(), option.getProduct().getId())`를 호출합니다.
3. cleanup은 wish가 있으면 삭제하고 없으면 무시합니다.
4. 주문 실패 flow에서는 cleanup 메서드가 호출되지 않도록 현재 순서를 유지합니다.
5. cleanup 이후 `OrderNotificationService`를 호출합니다.

## Phase 2: Task Planning Approach

1. Order service 테스트에 wish 있음/없음/실패 케이스 추가
2. `OrderService`에 `WishRepository` 의존성 추가
3. 주문 저장 후 cleanup 구현
4. 기존 order/wish 테스트 실행
5. 검색으로 cleanup 순서와 repository 사용 확인

## Risk Assessment

- **실패 주문에서 wish 삭제 위험**: cleanup을 주문 저장 이후에만 호출하고 실패 테스트로 고정합니다.
- **위시 없음 주문 실패 위험**: `Optional.ifPresent()`로 삭제 대상이 없으면 무시합니다.
- **응답 계약 변경 위험**: controller 응답 로직은 변경하지 않고 기존 controller 테스트를 유지합니다.
- **책임 과분리 위험**: 작은 후처리 정책이라 별도 service를 만들지 않습니다.
