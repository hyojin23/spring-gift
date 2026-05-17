# Research: Option 수량 도메인 검증 강화

## Decision 1: 수량 범위는 OptionRequest와 동일하게 유지

**Decision**: `Option` 생성자의 유효 수량 범위는 `OptionRequest`의 Bean Validation과 동일한 1 이상 99,999,999 이하로 둡니다.

**Rationale**: API 입력 검증과 도메인 불변식이 서로 다른 범위를 가지면 같은 개념에 대해 상충하는 규칙이 생깁니다. 현재 공개 API 계약을 유지하면서 도메인 내부 안전망을 추가하려면 같은 범위를 사용하는 것이 가장 단순합니다.

**Alternatives considered**:

- 도메인에는 최소값만 검증: 최대값을 우회할 수 있어 API 입력 검증과 도메인 상태가 달라집니다.
- `OptionRequest` 검증 제거: API 요청 단계의 빠른 검증과 명확한 응답 계약이 약해집니다.
- DB constraint로만 처리: 도메인 객체가 잘못된 상태로 생성되는 것을 막지 못합니다.

## Decision 2: 옵션명 검증은 이번 범위에서 제외

**Decision**: 이번 작업은 quantity 도메인 검증만 다루고, option name 검증은 기존 `OptionNameValidator`와 service 예외 흐름을 유지합니다.

**Rationale**: 옵션명 검증은 여러 오류 메시지를 모아 `OptionValidationException`으로 변환하는 API 계약과 연결되어 있습니다. 이를 도메인 생성자로 이동하면 예외 타입과 응답 메시지 계약까지 흔들릴 수 있으므로 별도 리팩토링으로 분리합니다.

**Alternatives considered**:

- 옵션명 검증도 생성자에 추가: 범위가 커지고 기존 에러 응답 계약을 재검토해야 합니다.
- `OptionNameValidator`를 도메인에서 직접 호출: 도메인과 API 오류 메시지 정책의 결합이 강해집니다.

## Decision 3: 수량 검증 실패는 우선 IllegalArgumentException 사용

**Decision**: 도메인 수량 검증 실패는 우선 `IllegalArgumentException`을 사용합니다.

**Rationale**: 현재 `subtractQuantity`는 이미 현재 재고 초과 차감 시 `IllegalArgumentException`을 사용합니다. 이번 작업은 도메인 불변식 강화가 목적이므로 예외 타입 통일은 별도 작업으로 남기고, 기존 스타일을 유지합니다.

**Alternatives considered**:

- 새 `OptionQuantityException` 추가: 글로벌 예외 매핑과 API 계약 변경까지 이어질 수 있습니다.
- `OptionValidationException` 재사용: 현재 예외는 옵션명 검증 실패 응답 계약과 연결되어 있어 의미가 섞입니다.
