# 기능 명세서: Order 도메인 검증 강화 리팩토링

**Feature Branch**: `019-order-domain-validation-refactor`  
**작성일**: 2026-05-20  
**상태**: 초안  
**입력**: "Order 도메인 검증 강화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 주문 옵션 필수 조건을 도메인에서 보호 (우선순위: P1)

주문은 반드시 주문 대상 옵션을 가져야 합니다. `Order` 생성자는 `option`이 `null`이면 주문 도메인 검증 예외를 발생시켜야 합니다.

**우선순위 이유**: 주문 대상 없이 생성된 주문은 의미가 없습니다. request/service 계층에서 검증하더라도 도메인 객체가 스스로 불변조건을 보호해야 합니다.

**독립적 테스트**: `new Order(null, memberId, quantity, message)` 호출 시 `OrderValidationException`이 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** 주문 옵션이 없을 때, **When** `Order`를 생성하면, **Then** `OrderValidationException`이 발생합니다.
2. **Then** 예외 메시지는 한글로 제공됩니다.

---

### 사용자 시나리오 2 - 주문 회원 ID 필수 조건을 도메인에서 보호 (우선순위: P1)

주문은 반드시 주문 회원 ID를 가져야 합니다. `Order` 생성자는 `memberId`가 `null`이면 주문 도메인 검증 예외를 발생시켜야 합니다.

**우선순위 이유**: 현재 `Order`는 primitive FK 방식으로 회원 ID를 보관합니다. 회원 ID가 없으면 주문 소유자를 식별할 수 없으므로 도메인 생성 시점에 차단해야 합니다.

**독립적 테스트**: `new Order(option, null, quantity, message)` 호출 시 `OrderValidationException`이 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** 회원 ID가 없을 때, **When** `Order`를 생성하면, **Then** `OrderValidationException`이 발생합니다.
2. **Then** 예외 메시지는 한글로 제공됩니다.

---

### 사용자 시나리오 3 - 주문 수량 양수 조건을 도메인에서 보호 (우선순위: P1)

주문 수량은 1 이상이어야 합니다. `OrderRequest`의 Bean Validation과 별개로 `Order` 생성자도 0 이하 수량을 허용하지 않아야 합니다.

**우선순위 이유**: 도메인 객체가 잘못된 수량으로 생성되면 API 외부 경로나 테스트 fixture에서 불완전한 주문이 만들어질 수 있습니다. 주문 수량 정책은 `Order`가 직접 보호해야 합니다.

**독립적 테스트**: 0 이하 수량으로 `Order`를 생성하면 `OrderValidationException`이 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** 수량이 0일 때, **When** `Order`를 생성하면, **Then** `OrderValidationException`이 발생합니다.
2. **Given** 수량이 음수일 때, **When** `Order`를 생성하면, **Then** `OrderValidationException`이 발생합니다.
3. **Then** 예외 메시지는 한글로 제공됩니다.

---

### 사용자 시나리오 4 - 기존 주문 생성 flow 유지 (우선순위: P2)

유효한 주문 생성 flow는 기존처럼 성공해야 합니다. `OrderService`가 이미 옵션 조회, 재고 차감, 포인트 차감을 수행한 뒤 `Order`를 생성하므로, 도메인 검증 추가가 정상 주문 생성을 깨뜨리면 안 됩니다.

**우선순위 이유**: 이번 작업은 도메인 불변조건 보강입니다. 정상 주문 API 응답, 예외 응답, 위시 cleanup, 알림 호출 흐름은 유지되어야 합니다.

**독립적 테스트**: 기존 `OrderServiceTest`와 `OrderControllerTest`가 통과하는지 검증합니다.

---

### 엣지 케이스

- `message`는 선택값이므로 이번 작업에서 필수 검증하지 않습니다.
- `orderDateTime`은 생성 시점에 자동 설정되는 기존 동작을 유지합니다.
- `OrderRequest` Bean Validation은 유지하되 도메인 검증으로 대체하지 않습니다.
- 주문 생성 실패 응답 정책은 변경하지 않습니다.
- DB 제약 추가는 이번 작업 범위에 포함하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `OrderValidationException`을 추가해야 합니다.
- **FR-002**: `OrderValidationException`은 `OrderException` 하위 타입이어야 합니다.
- **FR-003**: `Order` 생성자는 `option`이 `null`이면 `OrderValidationException`을 발생시켜야 합니다.
- **FR-004**: `Order` 생성자는 `memberId`가 `null`이면 `OrderValidationException`을 발생시켜야 합니다.
- **FR-005**: `Order` 생성자는 `quantity`가 1 미만이면 `OrderValidationException`을 발생시켜야 합니다.
- **FR-006**: 검증 실패 메시지는 한글로 제공해야 합니다.
- **FR-007**: 유효한 `Order` 생성 시 `orderDateTime`이 기존처럼 설정되어야 합니다.
- **FR-008**: 주문 생성 API의 기존 201/400/404 응답 계약을 유지해야 합니다.
- **FR-009**: 위시 cleanup과 주문 알림 호출 흐름을 변경하지 않아야 합니다.
- **FR-010**: `Order` 도메인 테스트를 추가해야 합니다.

### 주요 엔티티

- **Order**: 주문 도메인 객체이며 옵션, 회원 ID, 수량 불변조건을 직접 보호합니다.
- **OrderValidationException**: 주문 도메인 검증 실패를 표현합니다.
- **OrderService**: 유효한 주문 생성 flow를 유지합니다.
- **OrderRequest**: API 입력 검증을 담당하며 도메인 검증과 함께 유지됩니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `option == null`로 `Order` 생성 시 `OrderValidationException`이 발생합니다.
- **SC-002**: `memberId == null`로 `Order` 생성 시 `OrderValidationException`이 발생합니다.
- **SC-003**: `quantity <= 0`으로 `Order` 생성 시 `OrderValidationException`이 발생합니다.
- **SC-004**: 유효한 값으로 `Order` 생성 시 필드와 `orderDateTime`이 정상 설정됩니다.
- **SC-005**: 주문 생성 service/controller 테스트가 기존처럼 통과합니다.
- **SC-006**: `./gradlew test --tests *Order*`가 통과합니다.

## 가정사항

- Order service 분리는 `015-order-service-refactor`에서 완료되었습니다.
- Order 예외 처리 리팩토링은 `016-order-exception-refactor`에서 완료되었습니다.
- Order 알림 서비스 분리는 `017-order-notification-service-refactor`에서 완료되었습니다.
- Order wish cleanup은 `018-order-wish-cleanup-refactor`에서 완료되었습니다.
