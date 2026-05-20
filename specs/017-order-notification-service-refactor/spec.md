# 기능 명세서: Order 알림 서비스 분리 리팩토링

**Feature Branch**: `017-order-notification-service-refactor`  
**작성일**: 2026-05-20  
**상태**: 초안  
**입력**: "카카오 메시지 발송 책임 분리"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 주문 생성 핵심 흐름에서 알림 책임 분리 (우선순위: P1)

주문 생성 flow의 핵심 책임은 옵션 조회, 재고 차감, 포인트 차감, 주문 저장입니다. 주문 완료 후 카카오 메시지를 보내는 부가 책임은 `OrderService`가 직접 수행하지 않고 별도 `OrderNotificationService`에 위임해야 합니다.

**우선순위 이유**: `OrderService`가 주문 처리와 외부 알림 정책을 함께 가지면 책임이 커집니다. 알림 발송 정책을 분리하면 주문 생성 로직을 더 읽기 쉽고 테스트하기 쉬운 구조로 유지할 수 있습니다.

**독립적 테스트**: 주문 생성 성공 시 `OrderService`가 `OrderNotificationService`를 호출하는지 검증합니다.

**승인 시나리오**:

1. **Given** 인증된 회원과 유효한 옵션이 있을 때, **When** 주문을 생성하면, **Then** 주문은 저장됩니다.
2. **Then** `OrderService`는 저장된 주문, 회원, 옵션 정보를 `OrderNotificationService`에 전달합니다.
3. **Then** 주문 생성 응답은 기존처럼 `OrderResponse`를 반환합니다.

---

### 사용자 시나리오 2 - 카카오 access token이 없는 회원은 알림을 보내지 않음 (우선순위: P1)

회원에게 카카오 access token이 없으면 주문 알림 서비스는 카카오 메시지 client를 호출하지 않아야 합니다.

**우선순위 이유**: access token이 없는 회원에게 메시지를 보내려고 하면 불필요한 외부 호출과 예외가 발생할 수 있습니다. 기존 정책을 별도 서비스에서도 유지해야 합니다.

**독립적 테스트**: 카카오 access token이 없는 회원으로 알림 서비스를 호출하면 `KakaoMessageClient`가 호출되지 않는지 검증합니다.

**승인 시나리오**:

1. **Given** 회원의 카카오 access token이 없을 때, **When** 주문 완료 알림을 요청하면, **Then** 카카오 메시지 client는 호출되지 않습니다.

---

### 사용자 시나리오 3 - 카카오 메시지 실패는 주문 실패로 전파하지 않음 (우선순위: P1)

카카오 메시지 발송은 best-effort 정책입니다. 알림 서비스에서 카카오 메시지 client 호출이 실패해도 예외가 주문 생성 flow로 전파되면 안 됩니다.

**우선순위 이유**: 주문은 이미 저장된 핵심 거래이고, 알림 실패는 부가 기능 실패입니다. 외부 API 장애가 주문 생성 실패로 이어지면 사용자 경험과 비즈니스 안정성이 떨어집니다.

**독립적 테스트**: `KakaoMessageClient`가 예외를 던져도 `OrderNotificationService` 호출은 예외 없이 종료되는지 검증합니다.

**승인 시나리오**:

1. **Given** 회원에게 카카오 access token이 있을 때, **When** 카카오 메시지 발송이 실패하면, **Then** 알림 서비스는 예외를 전파하지 않습니다.
2. **Then** 주문 생성 API는 기존처럼 201 Created를 반환합니다.

---

### 사용자 시나리오 4 - OrderService의 외부 client 직접 의존 제거 (우선순위: P2)

`OrderService`는 `KakaoMessageClient`를 직접 주입받지 않아야 합니다. 외부 알림 client와 실패 무시 정책은 `OrderNotificationService` 내부에 있어야 합니다.

**우선순위 이유**: 주문 서비스가 외부 client를 직접 알면 알림 채널이 바뀔 때 주문 핵심 코드가 함께 변경됩니다. 알림 책임을 감싸는 service 경계를 만들면 후속 확장이 쉬워집니다.

**독립적 테스트**: `OrderService`에 `KakaoMessageClient` 직접 의존과 `sendKakaoMessageIfPossible()` 메서드가 남지 않았는지 확인합니다.

---

### 엣지 케이스

- 이번 작업은 알림 책임 분리에 집중하며 주문 생성 응답 계약을 변경하지 않습니다.
- 주문 옵션 미존재, 포인트 부족, 재고 부족 예외 처리 정책은 변경하지 않습니다.
- 카카오 알림 실패는 기존처럼 주문 실패로 전파하지 않습니다.
- 위시 cleanup 구현은 이번 작업 범위에 포함하지 않습니다.
- 알림 실패 로깅 정책 도입은 후속 작업으로 남깁니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `OrderNotificationService`를 추가해야 합니다.
- **FR-002**: `OrderNotificationService`는 주문 완료 카카오 메시지 발송 책임을 가져야 합니다.
- **FR-003**: `OrderNotificationService`는 회원의 카카오 access token이 없으면 `KakaoMessageClient`를 호출하지 않아야 합니다.
- **FR-004**: `OrderNotificationService`는 `KakaoMessageClient` 호출 실패를 주문 flow로 전파하지 않아야 합니다.
- **FR-005**: `OrderService`는 주문 저장 후 `OrderNotificationService`를 호출해야 합니다.
- **FR-006**: `OrderService`는 `KakaoMessageClient`를 직접 주입받지 않아야 합니다.
- **FR-007**: `OrderService`에는 `sendKakaoMessageIfPossible()` 같은 알림 세부 메서드가 남지 않아야 합니다.
- **FR-008**: 주문 생성 성공 시 기존 201 Created와 `OrderResponse`를 유지해야 합니다.
- **FR-009**: 주문 생성 실패 예외 응답은 기존 order/member/option 예외 처리 정책을 유지해야 합니다.
- **FR-010**: 알림 서비스 단위 테스트와 주문 서비스 회귀 테스트를 추가/수정해야 합니다.

### 주요 엔티티

- **OrderService**: 주문 생성 핵심 flow를 담당하고 주문 저장 후 알림 서비스를 호출합니다.
- **OrderNotificationService**: 주문 완료 알림 발송과 best-effort 실패 무시 정책을 담당합니다.
- **KakaoMessageClient**: 실제 카카오 메시지 API 호출을 담당합니다.
- **OrderController**: 기존처럼 인증 확인과 HTTP 응답 구성을 담당합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: 주문 생성 성공 시 `OrderNotificationService`가 호출됩니다.
- **SC-002**: 카카오 access token이 없는 회원은 `KakaoMessageClient` 호출 없이 알림 처리가 종료됩니다.
- **SC-003**: 카카오 메시지 발송 실패 시 예외가 전파되지 않습니다.
- **SC-004**: `OrderService`에 `KakaoMessageClient` 직접 의존이 남지 않습니다.
- **SC-005**: `OrderService`에 `sendKakaoMessageIfPossible` 메서드가 남지 않습니다.
- **SC-006**: 주문 생성 API의 201/400/404 응답 계약이 유지됩니다.
- **SC-007**: `./gradlew test --tests *Order*`가 통과합니다.

## 가정사항

- Order service 분리는 `015-order-service-refactor`에서 완료되었습니다.
- Order 예외 처리 리팩토링은 `016-order-exception-refactor`에서 완료되었습니다.
- 이번 작업은 알림 책임 분리이며 알림 실패 로깅, 재시도, 비동기 처리 도입은 범위에 포함하지 않습니다.
