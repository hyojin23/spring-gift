# 기능 명세서: Order 검증 예외 전역 처리 리팩토링

**Feature Branch**: `021-order-validation-exception-handler`  
**작성일**: 2026-05-20  
**상태**: 초안  
**입력**: "OrderValidationException global handler 매핑"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 주문 도메인 검증 예외를 표준 에러 응답으로 변환 (우선순위: P1)

`OrderValidationException`이 발생하면 전역 예외 핸들러는 500이 아니라 400 Bad Request와 표준 `ErrorResponse`를 반환해야 합니다.

**우선순위 이유**: `OrderValidationException`은 주문 도메인 정책 위반입니다. 예기치 않은 서버 오류가 아니라 클라이언트 또는 호출자가 잘못된 주문 상태를 만든 상황이므로 명확한 400 응답으로 표현해야 합니다.

**독립적 테스트**: `GlobalExceptionHandler.handleOrderValidation()` 호출 시 400 status, `ORDER.INVALID` code, 예외 메시지가 반환되는지 검증합니다.

**승인 시나리오**:

1. **Given** `OrderValidationException("주문 수량은 1 이상이어야 합니다.")`이 발생했을 때, **When** 전역 핸들러가 처리하면, **Then** 400 응답을 반환합니다.
2. **Then** 응답 body의 code는 `ORDER.INVALID`입니다.
3. **Then** 응답 body의 message는 예외 메시지와 동일합니다.

---

### 사용자 시나리오 2 - 기존 주문 옵션 미존재 응답 유지 (우선순위: P1)

주문 대상 옵션 미존재는 기존처럼 `OrderOptionNotFoundException` handler에서 404와 `ORDER.OPTION_NOT_FOUND` code로 처리되어야 합니다.

**우선순위 이유**: 주문 검증 예외 handler를 추가하면서 옵션 미존재 조회 실패 응답이 더 넓은 order 예외 처리로 흡수되면 안 됩니다.

**독립적 테스트**: 기존 `handleOrderOptionNotFound()` 테스트가 그대로 통과하는지 검증합니다.

---

### 사용자 시나리오 3 - 기존 member/option/product 예외 응답 유지 (우선순위: P2)

전역 예외 핸들러에 order 검증 handler를 추가하더라도 기존 member, option, product 예외 응답 code와 status는 변경되지 않아야 합니다.

**우선순위 이유**: 전역 핸들러는 여러 패키지의 API 응답 계약을 담당합니다. order handler 추가가 다른 도메인 예외 매핑에 영향을 주면 안 됩니다.

**독립적 테스트**: 기존 `GlobalExceptionHandlerTest`가 통과하는지 검증합니다.

---

### 엣지 케이스

- `OrderValidationException`은 400 Bad Request로 처리합니다.
- 응답 code는 `ORDER.INVALID`를 사용합니다.
- 메시지는 예외 메시지를 그대로 사용합니다.
- `OrderOptionNotFoundException`의 404 매핑은 유지합니다.
- 이번 작업은 handler 매핑만 추가하며 `Order` 도메인 검증 로직은 변경하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `GlobalExceptionHandler`에 `OrderValidationException` handler를 추가해야 합니다.
- **FR-002**: `OrderValidationException`은 400 Bad Request로 매핑해야 합니다.
- **FR-003**: `OrderValidationException` 응답 code는 `ORDER.INVALID`여야 합니다.
- **FR-004**: `OrderValidationException` 응답 message는 예외 메시지를 사용해야 합니다.
- **FR-005**: `OrderOptionNotFoundException`은 기존 404와 `ORDER.OPTION_NOT_FOUND` 매핑을 유지해야 합니다.
- **FR-006**: 기존 member/option/product/wish 예외 handler 동작을 변경하지 않아야 합니다.
- **FR-007**: `GlobalExceptionHandlerTest`에 order validation handler 테스트를 추가해야 합니다.

### 주요 엔티티

- **OrderValidationException**: 주문 도메인 검증 실패를 표현합니다.
- **GlobalExceptionHandler**: `OrderValidationException`을 표준 `ErrorResponse`로 변환합니다.
- **ErrorResponse**: code, message, timestamp를 포함하는 공통 에러 응답입니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `OrderValidationException` 처리 결과 status는 400입니다.
- **SC-002**: `OrderValidationException` 처리 결과 code는 `ORDER.INVALID`입니다.
- **SC-003**: `OrderValidationException` 처리 결과 message는 예외 메시지와 같습니다.
- **SC-004**: `OrderOptionNotFoundException` 처리 결과는 기존 404와 `ORDER.OPTION_NOT_FOUND`를 유지합니다.
- **SC-005**: `./gradlew test --tests *GlobalExceptionHandler* --tests *Order*`가 통과합니다.

## 가정사항

- `OrderValidationException`은 `019-order-domain-validation-refactor`에서 추가되었습니다.
- `OrderOptionNotFoundException` handler는 `016-order-exception-refactor`에서 추가되었습니다.
- 이번 작업은 전역 예외 매핑 추가이며 order 도메인 검증 자체는 변경하지 않습니다.
