# Implementation Plan: Order 알림 서비스 분리 리팩토링

**Branch**: `017-order-notification-service-refactor` | **Date**: 2026-05-20 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/017-order-notification-service-refactor/spec.md`

**Note**: 이 문서는 `OrderService` 내부의 카카오 알림 발송 책임을 별도 `OrderNotificationService`로 분리하는 리팩토링 계획입니다.

## Summary

`OrderService`는 현재 주문 생성 핵심 흐름과 카카오 메시지 발송 best-effort 정책을 함께 가지고 있습니다. `OrderNotificationService`를 도입해 카카오 access token 확인, `KakaoMessageClient` 호출, 발송 실패 예외 무시 정책을 한 곳으로 옮깁니다. `OrderService`는 주문 저장 후 알림 서비스를 호출하는 역할만 수행합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA, Bean Validation  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / Spring Boot Test / MockMvc / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API  
**Performance Goals**: 성능 목표 없음, 책임 분리와 테스트 가능성 개선 우선  
**Constraints**: 주문 응답 계약 유지, best-effort 정책 유지, 비동기/재시도/로깅 제외  
**Scale/Scope**: `OrderService`, `OrderNotificationService`, Order service/controller tests

## Constitution Check

- Domain-First Architecture: 주문 생성 핵심 flow와 외부 알림 부가 책임을 분리합니다.
- Test-Driven Stability: 알림 호출, token 없음, 발송 실패 무시 정책을 테스트로 고정합니다.
- Structural and Behavioral Separation: 주문 저장은 `OrderService`, 알림 발송은 `OrderNotificationService`가 담당합니다.
- Consistent API and Error Handling: 기존 order/member/option 예외 응답을 변경하지 않습니다.
- Maintainable Simplicity: 비동기 처리, retry, 로깅은 이번 작업에서 제외합니다.
- Small Scoped Changes: 카카오 알림 책임 분리에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 기존 주문 생성 성공/실패 응답을 유지합니다.

## Project Structure

### Documentation (this feature)

```text
specs/017-order-notification-service-refactor/
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
├── KakaoMessageClient.java
├── Order.java
├── OrderController.java
├── OrderNotificationService.java
└── OrderService.java
```

```text
src/test/java/gift/order/
├── OrderControllerTest.java
├── OrderNotificationServiceTest.java
└── OrderServiceTest.java
```

**Structure Decision**: `OrderNotificationService`는 현재 order 패키지에 둡니다. 카카오 메시지는 주문 완료 알림의 구현 세부사항이고, 아직 별도 notification bounded context가 필요할 만큼 범위가 크지 않습니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `OrderService`의 `KakaoMessageClient` 직접 의존을 확인합니다.
2. `sendKakaoMessageIfPossible()`의 기존 token 없음/예외 무시 정책을 확인합니다.
3. 현재 주문 생성 성공/실패 테스트 범위를 확인합니다.
4. `KakaoMessageClient` 호출 signature를 확인합니다.

## Phase 1: Design & Contracts

1. `OrderNotificationService.sendOrderCreatedMessage(member, order, option)`를 설계합니다.
2. token 없음이면 return 하는 정책을 service 내부로 이동합니다.
3. 카카오 발송 실패 catch 정책을 service 내부로 이동합니다.
4. `OrderService`는 `OrderNotificationService`를 주입받고 주문 저장 후 호출합니다.
5. 알림 서비스 단위 테스트를 추가합니다.

## Phase 2: Task Planning Approach

1. 알림 서비스 테스트 추가
2. `OrderNotificationService` 추가
3. `OrderService` 의존성 교체
4. 기존 order 테스트 수정
5. 테스트와 dependency 검색 검증

## Risk Assessment

- **주문 생성 응답 회귀 위험**: controller 테스트로 201/400/404 응답을 유지합니다.
- **알림 실패 전파 위험**: 알림 서비스 단위 테스트로 예외 무시 정책을 고정합니다.
- **책임 과분리 위험**: 별도 notification 패키지까지 만들지 않고 order 패키지 안의 작은 service로 유지합니다.
- **후속 확장 위험**: 로깅, retry, 비동기는 정책이 정해진 뒤 별도 spec으로 진행합니다.
