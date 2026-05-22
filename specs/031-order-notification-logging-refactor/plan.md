# Implementation Plan: 주문 알림 실패 로깅 리팩토링

**Branch**: `031-order-notification-logging-refactor` | **Date**: 2026-05-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/031-order-notification-logging-refactor/spec.md`

**Note**: 이 문서는 주문 생성 후 카카오 메시지 발송 실패를 조용히 삼키지 않고 warn 로그로 남기기 위한 계획입니다.

## Summary

`OrderNotificationService`는 카카오 메시지 발송 실패를 `catch (Exception ignored)`로 완전히 무시하고 있습니다. 주문 성공을 유지하는 best effort 정책은 유지하되, 실패 원인을 추적할 수 있도록 logger를 추가하고 warn 로그를 남깁니다. 기존 `OrderNotificationServiceTest`의 성공/skip/failure 정책 테스트는 유지합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Logging(SLF4J)  
**Storage**: 해당 없음  
**Testing**: JUnit 5 / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: 주문 알림 컴포넌트  
**Performance Goals**: 성능 목표 없음, 운영 관측성 개선 우선  
**Constraints**: 주문 생성 흐름 변경 없음, 카카오 메시지 실패 전파 금지  
**Scale/Scope**: `OrderNotificationService`, 관련 테스트

## Constitution Check

- Domain-First Architecture: 주문 알림의 best effort 정책을 코드와 테스트로 명확히 유지합니다.
- Test-Driven Stability: 실패해도 예외가 전파되지 않는 정책을 테스트로 유지합니다.
- Structural and Behavioral Separation: 주문 생성 성공과 알림 실패를 분리합니다.
- Consistent API and Error Handling: 실패는 API 에러로 전파하지 않고 로그로 관측합니다.
- Maintainable Simplicity: logger 추가와 catch block 정리에 한정합니다.
- Small Scoped Changes: order notification service에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, behavior-preserving observability refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/031-order-notification-logging-refactor/
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
└── OrderNotificationService.java
```

```text
src/test/java/gift/order/
└── OrderNotificationServiceTest.java
```

**Structure Decision**: 별도 logging service를 만들지 않고 `OrderNotificationService`에 SLF4J logger를 추가합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. 현재 `catch (Exception ignored)` 위치를 확인합니다.
2. 기존 `OrderNotificationServiceTest`가 best effort 정책을 검증하는지 확인합니다.
3. 로그에 포함할 정보와 제외할 민감 정보를 정합니다.

## Phase 1: Design & Contracts

1. `Logger`를 추가합니다.
2. catch 변수명을 `exception`으로 바꾸고 warn 로그를 남깁니다.
3. 로그에는 `order.getId()`를 포함합니다.
4. 로그에는 카카오 access token을 포함하지 않습니다.
5. 기존 예외 미전파 동작을 유지합니다.

## Phase 2: Task Planning Approach

1. 기존 failure 테스트가 예외 미전파를 검증하는지 확인합니다.
2. 필요한 경우 failure 테스트 display name 또는 검증을 보강합니다.
3. service에 logger를 추가하고 빈 catch를 제거합니다.
4. 대상 테스트와 전체 테스트를 실행합니다.

## Risk Assessment

- **민감정보 로그 노출 위험**: access token은 로그에 포함하지 않습니다.
- **테스트 과결합 위험**: 로그 문자열 자체 검증은 피하고, 예외 미전파 정책을 중심으로 검증합니다.
- **best effort 정책 훼손 위험**: catch에서 예외를 다시 던지지 않습니다.
