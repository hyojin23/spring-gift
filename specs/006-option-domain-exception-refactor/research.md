# Research: Option 도메인 예외 리팩토링

## Decision 1: OptionQuantityException 단일 예외 사용

**Decision**: 생성 수량 범위 오류, 0 이하 차감 수량, 현재 재고 초과 차감을 `OptionQuantityException` 하나로 표현합니다.

**Rationale**: 세 실패 조건은 모두 Option 수량 불변식 위반입니다. 메시지는 각각 다르게 유지하되 예외 타입과 API error code는 `OPTION.INVALID_QUANTITY`로 묶는 것이 단순하고 일관됩니다.

**Alternatives considered**:

- 실패 조건별 예외 타입 분리: 예외 class와 handler가 늘어나며 현재 클라이언트 계약에 필요한 세분화보다 복잡합니다.
- 기존 `IllegalArgumentException` 유지: Option 도메인 예외 계층과 global handler 표준 응답 흐름에서 벗어납니다.
- `OptionValidationException` 재사용: 현재 옵션명 검증 실패와 연결된 의미가 있어 수량 오류와 섞입니다.

## Decision 2: HTTP 400과 OPTION.INVALID_QUANTITY 매핑

**Decision**: `OptionQuantityException`은 `GlobalExceptionHandler`에서 HTTP 400과 `OPTION.INVALID_QUANTITY`로 매핑합니다.

**Rationale**: 잘못된 수량은 클라이언트 입력 또는 도메인 요청 값이 유효하지 않은 상황이므로 400이 적절합니다. 기존 `OPTION.INVALID_NAME`과 구분하기 위해 수량 전용 error code를 사용합니다.

**Alternatives considered**:

- `OPTION.INVALID_NAME` 재사용: 수량 오류 의미와 맞지 않습니다.
- `OPTION.VALIDATION_FAILED` 같은 포괄 코드 사용: 현재 Option error code들이 구체적인 이름을 사용하고 있어 일관성이 떨어집니다.

## Decision 3: Bean Validation 예외 처리 제외

**Decision**: `OptionRequest`의 Bean Validation 예외 표준화는 이번 작업에서 다루지 않습니다.

**Rationale**: 이번 작업의 목적은 Option 도메인 내부의 `IllegalArgumentException` 제거입니다. API 입력 검증 실패 응답까지 함께 다루면 scope가 커지고, Spring validation exception handling 정책을 별도로 정해야 합니다.

**Alternatives considered**:

- Bean Validation handler 동시 추가: 좋은 후속 작업이지만 이번 도메인 예외 리팩토링의 최소 범위를 벗어납니다.
