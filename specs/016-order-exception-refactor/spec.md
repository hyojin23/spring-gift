# 기능 명세서: Order 예외 처리 리팩토링

**Feature Branch**: `016-order-exception-refactor`  
**작성일**: 2026-05-19  
**상태**: 초안  
**입력**: "Order 예외 리팩토링"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 주문 대상 옵션 미존재를 도메인 예외로 표현 (우선순위: P1)

주문 생성 시 요청한 옵션 ID가 존재하지 않으면 `OrderService`는 `Optional.empty()`로 실패를 숨기지 않고 주문 도메인 예외를 발생시켜야 합니다. `OrderController`는 옵션 미존재 분기를 직접 처리하지 않고 service 호출 결과를 그대로 응답으로 변환해야 합니다.

**우선순위 이유**: 옵션 미존재는 주문 생성 실패의 명확한 도메인 상황입니다. service가 실패를 `Optional`로 표현하면 controller가 비즈니스 실패 조건을 알아야 하므로 책임이 섞입니다.

**독립적 테스트**: 존재하지 않는 옵션 ID로 주문 생성 요청을 보내면 404와 표준 `ErrorResponse`가 반환되는지 검증합니다.

**승인 시나리오**:

1. **Given** 인증된 회원과 존재하지 않는 옵션 ID가 있을 때, **When** 주문 생성을 요청하면, **Then** 404 응답을 반환합니다.
2. **Then** 응답 body는 `ORDER.OPTION_NOT_FOUND` code와 한글 메시지를 포함합니다.
3. **Then** 주문 저장, 포인트 차감, 재고 차감은 수행되지 않습니다.

---

### 사용자 시나리오 2 - 주문 생성 성공 흐름의 컨트롤러 분기 제거 (우선순위: P1)

주문 생성이 성공하면 `OrderService.createOrder()`는 `OrderResponse`를 직접 반환해야 합니다. `OrderController`는 인증 확인 후 service를 호출하고 201 Created 응답을 구성하는 역할만 수행해야 합니다.

**우선순위 이유**: `OrderService`가 성공/실패를 명확한 반환값과 예외로 구분하면 controller의 조건 분기가 줄고 주문 생성 flow를 service 테스트로 더 쉽게 검증할 수 있습니다.

**독립적 테스트**: 유효한 주문 생성 요청이 기존처럼 201 Created와 `OrderResponse`를 반환하는지 검증합니다.

**승인 시나리오**:

1. **Given** 인증된 회원과 유효한 옵션이 있을 때, **When** 주문 생성을 요청하면, **Then** 201 Created와 주문 응답을 반환합니다.
2. **Then** `Location` header는 생성된 주문 URI를 가리킵니다.

---

### 사용자 시나리오 3 - 포인트 부족 예외의 API 응답 표준화 (우선순위: P1)

주문 생성 중 회원 포인트가 부족하면 member 포인트 도메인 예외가 발생합니다. 이 예외는 전역 예외 핸들러에서 표준 `ErrorResponse`로 변환되어야 하며, 예기치 않은 500 응답으로 처리되면 안 됩니다.

**우선순위 이유**: 포인트 부족은 주문 생성 중 사용자가 만날 수 있는 정상적인 실패 상황입니다. API 사용자가 실패 원인을 이해할 수 있도록 명확한 400 계열 응답과 code를 제공해야 합니다.

**독립적 테스트**: 포인트가 부족한 회원이 주문을 생성하면 400 응답과 `MEMBER.INSUFFICIENT_POINT` code가 반환되는지 검증합니다.

**승인 시나리오**:

1. **Given** 회원 포인트가 상품 총액보다 적을 때, **When** 주문 생성을 요청하면, **Then** 400 응답을 반환합니다.
2. **Then** 응답 body는 `MEMBER.INSUFFICIENT_POINT` code와 한글 메시지를 포함합니다.
3. **Then** 주문 저장은 수행되지 않습니다.

---

### 사용자 시나리오 4 - 재고 부족 예외 응답 유지 (우선순위: P2)

주문 생성 중 옵션 재고가 부족하면 기존 option 도메인 예외가 전역 핸들러에서 처리되어야 합니다. 이번 작업은 order 예외 구조를 바꾸더라도 기존 option quantity 응답을 깨뜨리지 않아야 합니다.

**우선순위 이유**: 주문 생성은 option 재고 정책을 함께 사용합니다. order 예외를 추가하면서 이미 정리된 option 예외 응답이 회귀하면 안 됩니다.

**독립적 테스트**: 재고보다 큰 수량으로 주문 생성 요청을 보내면 기존처럼 400과 `OPTION.INVALID_QUANTITY` code가 반환되는지 검증합니다.

---

### 엣지 케이스

- 인증 실패 401 응답은 이번 작업에서 기존 방식을 유지합니다.
- 주문 옵션 미존재는 `Optional.empty()`가 아니라 주문 도메인 예외로 표현해야 합니다.
- 카카오 알림 실패는 여전히 best-effort로 삼켜야 하며 주문 실패로 전파하지 않습니다.
- 위시 cleanup 구현은 이번 작업 범위에 포함하지 않습니다.
- 포인트 부족과 재고 부족은 주문 생성 실패 상황이지만 각각 기존 member/option 도메인 정책을 재사용합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `OrderException`을 주문 도메인 예외의 상위 타입으로 추가해야 합니다.
- **FR-002**: 주문 대상 옵션 미존재는 `OrderOptionNotFoundException`으로 표현해야 합니다.
- **FR-003**: `OrderService.createOrder()`는 `Optional<OrderResponse>`가 아니라 `OrderResponse`를 반환해야 합니다.
- **FR-004**: `OrderService.createOrder()`는 옵션 미존재 시 `OrderOptionNotFoundException`을 발생시켜야 합니다.
- **FR-005**: `OrderController.createOrder()`는 옵션 미존재 분기를 직접 처리하지 않아야 합니다.
- **FR-006**: `GlobalExceptionHandler`는 `OrderOptionNotFoundException`을 404와 `ORDER.OPTION_NOT_FOUND` code로 처리해야 합니다.
- **FR-007**: `GlobalExceptionHandler`는 주문 생성 중 발생 가능한 `InsufficientMemberPointException`을 400과 `MEMBER.INSUFFICIENT_POINT` code로 처리해야 합니다.
- **FR-008**: 주문 생성 성공 시 기존 201 Created 응답을 유지해야 합니다.
- **FR-009**: 인증 실패 시 기존 401 응답을 유지해야 합니다.
- **FR-010**: 카카오 알림 실패가 주문 생성 실패로 전파되지 않아야 합니다.

### 주요 엔티티

- **OrderException**: order 패키지 예외의 공통 상위 타입입니다.
- **OrderOptionNotFoundException**: 주문 생성 대상 옵션이 존재하지 않는 상황을 표현합니다.
- **OrderService**: 주문 생성 실패를 반환값이 아니라 도메인 예외로 표현합니다.
- **OrderController**: 인증 확인과 HTTP 201 응답 구성만 담당합니다.
- **GlobalExceptionHandler**: 주문/포인트 예외를 표준 `ErrorResponse`로 변환합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: 존재하지 않는 옵션 ID로 주문 생성 시 404와 `ORDER.OPTION_NOT_FOUND` code가 반환됩니다.
- **SC-002**: 포인트 부족 주문 생성 시 400과 `MEMBER.INSUFFICIENT_POINT` code가 반환됩니다.
- **SC-003**: 재고 부족 주문 생성 시 기존 400과 `OPTION.INVALID_QUANTITY` code가 유지됩니다.
- **SC-004**: 주문 생성 성공 시 기존 201 Created와 `OrderResponse`가 유지됩니다.
- **SC-005**: `OrderService.createOrder()` 반환 타입에 `Optional`이 남지 않습니다.
- **SC-006**: `OrderController.createOrder()`에 옵션 미존재 분기가 남지 않습니다.
- **SC-007**: `./gradlew test --tests *Order* --tests *GlobalExceptionHandler* --tests *Member* --tests *Option*`가 통과합니다.

## 가정사항

- Order service 분리는 `015-order-service-refactor`에서 완료되었습니다.
- Option 수량 예외와 Member 포인트 예외는 각각 기존 도메인 예외로 정리되어 있습니다.
- 이번 작업은 order 예외 응답 표준화이며 위시 cleanup과 알림 service 분리는 후속 작업으로 진행합니다.
