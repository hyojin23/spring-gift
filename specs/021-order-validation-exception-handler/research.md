# Research: Order 검증 예외 전역 처리 리팩토링

## 결정 1: `OrderValidationException`은 400으로 처리한다

**Decision**: 전역 핸들러에서 `OrderValidationException`을 `HttpStatus.BAD_REQUEST`로 매핑합니다.

**Rationale**: 주문 도메인 검증 실패는 잘못된 주문 상태를 생성하려는 호출자 오류입니다. 서버 내부 오류로 볼 수 없으므로 400이 적절합니다.

**Alternatives Considered**:

- 500 유지: 도메인 정책 위반을 서버 오류로 노출해 API 의미가 흐려집니다.
- 422 사용: 의미상 가능하지만 프로젝트의 기존 검증 실패 응답은 대부분 400을 사용합니다.

## 결정 2: error code는 `ORDER.INVALID`로 한다

**Decision**: 주문 검증 실패 code는 `ORDER.INVALID`를 사용합니다.

**Rationale**: Product/Option 검증 handler가 `PRODUCT.INVALID_NAME`, `OPTION.INVALID_NAME`, `OPTION.INVALID_QUANTITY` 같은 도메인 code를 사용합니다. Order는 검증 범위가 option/memberId/quantity로 나뉘지만 외부 응답에서는 단일 invalid code로 충분합니다.

**Alternatives Considered**:

- `ORDER.INVALID_QUANTITY`, `ORDER.INVALID_MEMBER_ID` 등 세분화: 세밀하지만 현재 외부 API에서 직접 발생할 가능성이 낮고 handler가 복잡해집니다.
- `ORDER.VALIDATION_FAILED`: 의미는 명확하지만 기존 code naming보다 길고 일관성이 약합니다.

## 결정 3: 예외 메시지는 그대로 사용한다

**Decision**: response message는 `exception.getMessage()`를 사용합니다.

**Rationale**: `OrderValidationException`은 이미 한글 메시지를 담고 있습니다. 기존 도메인 validation handler들도 예외 메시지를 그대로 전달합니다.

**Alternatives Considered**:

- handler에서 고정 메시지 사용: 구체적인 실패 사유가 사라집니다.
- details에 필드 정보 추가: 현재 예외가 필드 정보를 구조화해서 갖고 있지 않으므로 별도 설계가 필요합니다.

## 결정 4: `OrderException` 상위 타입 handler는 만들지 않는다

**Decision**: 이번 작업에서는 `OrderValidationException`만 명시적으로 처리합니다.

**Rationale**: `OrderOptionNotFoundException`은 이미 404로 처리되고 있습니다. 상위 타입 handler를 만들면 하위 예외 매핑 순서와 의미가 복잡해질 수 있습니다.

**Alternatives Considered**:

- `OrderException` 공통 handler: 단순하지만 not found와 validation처럼 status가 다른 예외를 구분하기 어렵습니다.
