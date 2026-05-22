# 기능 명세서: 주문 알림 실패 로깅 리팩토링

**Feature Branch**: `031-order-notification-logging-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "OrderNotificationService 실패 로깅 추가"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 카카오 메시지 실패를 로그로 남김 (우선순위: P1)

주문 생성 후 카카오 메시지 발송에 실패하면 주문 흐름은 계속 성공해야 하지만, 실패 사실과 원인은 warn 로그로 남겨야 합니다.

**우선순위 이유**: 현재 `OrderNotificationService`는 예외를 완전히 삼킵니다. best effort 정책은 유지하더라도 운영 중 카카오 token 만료, 권한 누락, API 장애 등을 추적할 최소 단서가 필요합니다.

**독립적 테스트**: `KakaoMessageClient.sendToMe()`가 예외를 던져도 `sendOrderCreatedMessage()`는 예외를 전파하지 않는지 검증합니다. 로그 검증은 구현 세부사항에 과하게 묶이지 않도록 선택적으로 둡니다.

**승인 시나리오**:

1. **Given** 회원에게 카카오 access token이 있고 메시지 client가 실패할 때, **When** 주문 생성 메시지를 발송하면, **Then** 예외는 전파되지 않고 warn 로그가 남습니다.

---

### 사용자 시나리오 2 - 카카오 access token이 없으면 발송하지 않음 (우선순위: P1)

회원에게 카카오 access token이 없으면 기존처럼 메시지 발송을 시도하지 않아야 합니다.

**우선순위 이유**: 카카오 메시지는 선택 기능입니다. token이 없는 사용자의 주문 흐름에서 불필요한 client 호출이 발생하면 안 됩니다.

**독립적 테스트**: access token이 없는 회원이면 `KakaoMessageClient`가 호출되지 않는지 검증합니다.

---

### 사용자 시나리오 3 - 정상 발송 동작 유지 (우선순위: P1)

카카오 access token이 있고 client 호출이 성공하면 기존처럼 메시지를 발송해야 합니다.

**우선순위 이유**: 이번 작업은 실패 관측성 개선이며 정상 메시지 발송 경로를 변경하면 안 됩니다.

**독립적 테스트**: access token, order, product가 client에 전달되는지 검증합니다.

---

### 엣지 케이스

- 카카오 메시지 발송 실패는 주문 생성 실패로 전파하지 않습니다.
- catch 범위는 기존 best effort 정책을 유지하기 위해 넓게 유지할 수 있습니다.
- 빈 catch 또는 `ignored` 변수명은 제거합니다.
- 로그에는 최소한 order id와 실패 원인을 포함합니다.
- access token 값은 로그에 남기지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `OrderNotificationService`에 logger를 추가해야 합니다.
- **FR-002**: 카카오 메시지 발송 실패 시 warn 로그를 남겨야 합니다.
- **FR-003**: 실패 로그에는 order id를 포함해야 합니다.
- **FR-004**: 실패 로그에는 예외 cause를 포함해야 합니다.
- **FR-005**: 실패 로그에는 카카오 access token을 포함하지 않아야 합니다.
- **FR-006**: 카카오 메시지 발송 실패는 예외로 전파하지 않아야 합니다.
- **FR-007**: access token이 없으면 기존처럼 client를 호출하지 않아야 합니다.
- **FR-008**: 정상 발송 테스트는 계속 통과해야 합니다.

### 주요 엔티티

- **OrderNotificationService**: 주문 생성 후 카카오 메시지 발송을 best effort로 수행합니다.
- **KakaoMessageClient**: 카카오 메시지 API 호출을 담당합니다.
- **Member**: 카카오 access token을 보유할 수 있습니다.
- **Order**: 메시지 발송 대상 주문 정보입니다.
- **Option/Product**: 메시지에 포함되는 상품 정보를 제공합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: 메시지 client 실패 시 `sendOrderCreatedMessage()`는 예외를 던지지 않습니다.
- **SC-002**: 메시지 client 실패 시 warn 로그를 남기는 코드 경로가 존재합니다.
- **SC-003**: access token이 없으면 메시지 client를 호출하지 않습니다.
- **SC-004**: 정상 발송 시 기존처럼 `sendToMe()`를 호출합니다.
- **SC-005**: `./gradlew test --tests *OrderNotificationService*`가 통과합니다.
- **SC-006**: 전체 테스트가 통과합니다.

## 가정사항

- 주문 생성과 카카오 알림은 분리되어 있으며, 카카오 알림 실패는 주문 실패가 아닙니다.
- 로그 수집/모니터링 설정은 애플리케이션 운영 환경에서 별도로 다룹니다.
