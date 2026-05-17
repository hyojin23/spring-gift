# 기능 명세서: Option 도메인 예외 리팩토링

**Feature Branch**: `006-option-domain-exception-refactor`  
**작성일**: 2026-05-17  
**상태**: 초안  
**입력**: "option 패키지 IllegalArgumentException을 전부 도메인 예외로 리팩토링하고 global handler에서 처리"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - Option 수량 예외의 도메인 예외화 (우선순위: P1)

Option 도메인 내부에서 수량 검증 실패가 발생하면 시스템은 일반 `IllegalArgumentException` 대신 Option 도메인 예외를 발생시켜야 합니다. 현재 `Option` 생성자와 `subtractQuantity`의 수량 검증은 `IllegalArgumentException`을 사용할 수 있으므로, 이를 `OptionException` 하위 예외로 교체합니다.

**우선순위 이유**: Option 도메인 오류는 Option 예외 계층으로 표현되어야 예외 의미가 명확하고, global handler에서 일관된 에러 응답으로 변환할 수 있습니다.

**독립적 테스트**: 잘못된 수량으로 `Option`을 생성하거나 수량을 차감하면 `OptionQuantityException`이 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** 생성 수량이 0 이하이거나 최대값을 초과할 때, **When** `Option`을 생성하면, **Then** `OptionQuantityException`이 발생합니다.
2. **Given** 차감 수량이 0 이하일 때, **When** `subtractQuantity`를 호출하면, **Then** `OptionQuantityException`이 발생합니다.
3. **Given** 차감 수량이 현재 재고보다 클 때, **When** `subtractQuantity`를 호출하면, **Then** `OptionQuantityException`이 발생합니다.

---

### 사용자 시나리오 2 - Option 수량 예외의 표준 에러 응답 매핑 (우선순위: P2)

Option 수량 도메인 예외는 `GlobalExceptionHandler`에서 표준 `ErrorResponse`로 변환되어야 합니다.

**우선순위 이유**: 도메인 예외가 API 경계까지 전파될 때 클라이언트는 일관된 JSON error payload를 받아야 합니다.

**독립적 테스트**: `GlobalExceptionHandler`가 `OptionQuantityException`을 HTTP 400과 `OPTION.INVALID_QUANTITY` 코드로 변환하는지 검증합니다.

**승인 시나리오**:

1. **Given** `OptionQuantityException`이 발생할 때, **When** global handler가 처리하면, **Then** HTTP 400을 반환합니다.
2. **Given** `OptionQuantityException`이 발생할 때, **When** global handler가 처리하면, **Then** error code는 `OPTION.INVALID_QUANTITY`입니다.
3. **Given** `OptionQuantityException`이 message를 포함할 때, **When** global handler가 처리하면, **Then** 응답 message는 예외 message와 같습니다.

---

### 사용자 시나리오 3 - 기존 Option API 계약 유지 (우선순위: P3)

Option 도메인 예외 타입을 정리하더라도 기존 Option API의 성공 응답과 이미 정의된 Option 예외 응답 계약은 변경하지 않습니다.

**우선순위 이유**: 이번 작업은 예외 타입과 handler 매핑의 일관성 개선입니다. 기존 클라이언트가 사용 중인 옵션 조회, 생성, 삭제 계약이 흔들리면 안 됩니다.

**독립적 테스트**: 기존 Option controller/service/domain 테스트가 동일하게 통과하는지 확인합니다.

**승인 시나리오**:

1. 옵션명 검증 실패는 기존처럼 `OPTION.INVALID_NAME` 응답을 유지합니다.
2. 중복 옵션명, 상품 미존재, 옵션 미존재, 마지막 옵션 삭제 제한 응답은 기존 코드를 유지합니다.
3. 정상 Option API 응답은 기존처럼 200, 201, 204를 반환합니다.

---

### 엣지 케이스

- `src/main/java/gift/option` 안에 직접 `throw new IllegalArgumentException`이 남지 않아야 합니다.
- 수량 오류 message는 기존 의미를 유지해야 합니다.
- Bean Validation 예외 처리는 이번 작업 범위에 포함하지 않습니다.
- 옵션명 검증 흐름은 이번 작업 범위에 포함하지 않습니다.
- order, product, member 등 다른 도메인 예외 리팩토링은 이번 작업 범위에 포함하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `gift.option.exception` 패키지에 `OptionQuantityException`을 추가해야 합니다.
- **FR-002**: `OptionQuantityException`은 `OptionException`을 상속해야 합니다.
- **FR-003**: `Option` 도메인 내부 수량 검증 실패는 `IllegalArgumentException` 대신 `OptionQuantityException`을 발생시켜야 합니다.
- **FR-004**: `GlobalExceptionHandler`는 `OptionQuantityException`을 HTTP 400과 `OPTION.INVALID_QUANTITY`로 매핑해야 합니다.
- **FR-005**: 기존 Option 예외 코드와 message 계약은 변경하지 않아야 합니다.
- **FR-006**: `OptionTest`는 수량 검증 실패 예외 타입을 `OptionQuantityException`으로 검증해야 합니다.
- **FR-007**: `GlobalExceptionHandlerTest`는 `OptionQuantityException` 매핑을 검증해야 합니다.
- **FR-008**: `src/main/java/gift/option`에서 직접 던지는 `IllegalArgumentException`이 남지 않아야 합니다.

### 주요 엔티티

- **OptionQuantityException**: Option 수량 검증 실패를 표현하는 도메인 예외입니다.
- **OptionException**: Option 도메인 예외의 공통 기반 타입입니다.
- **Option**: 생성 수량과 차감 수량 검증 실패 시 `OptionQuantityException`을 발생시킵니다.
- **GlobalExceptionHandler**: `OptionQuantityException`을 표준 `ErrorResponse`로 변환합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: 잘못된 수량으로 `Option` 생성 시 `OptionQuantityException`이 발생합니다.
- **SC-002**: 잘못된 수량으로 `subtractQuantity` 호출 시 `OptionQuantityException`이 발생합니다.
- **SC-003**: `OptionQuantityException`은 HTTP 400과 `OPTION.INVALID_QUANTITY`로 매핑됩니다.
- **SC-004**: `src/main/java/gift/option`에 직접 `throw new IllegalArgumentException`이 남지 않습니다.
- **SC-005**: `./gradlew test --tests *Option* --tests *GlobalExceptionHandlerTest*`가 통과합니다.
- **SC-006**: 외부 Option API 응답 계약의 변경이 없습니다.

## 가정사항

- Option 예외 처리 구조는 `002-option-exception-refactor`에서 이미 완료되었습니다.
- Option 수량 도메인 검증은 `005-option-quantity-domain-validation`에서 이미 완료되었습니다.
- 이번 작업은 수량 관련 `IllegalArgumentException`을 Option 도메인 예외로 교체하는 데 집중합니다.
- Bean Validation 오류 응답 표준화는 별도 작업으로 처리합니다.
- 옵션명 검증의 도메인 이동은 별도 작업으로 처리합니다.
- 데이터베이스 schema 변경은 필요하지 않습니다.
