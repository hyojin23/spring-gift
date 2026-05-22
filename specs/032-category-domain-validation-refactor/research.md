# Research: Category 도메인 검증 강화 리팩토링

## Decision 1: Category가 필수 값을 직접 검증

**Decision**: `Category` 생성자와 `update()`에서 name, color, imageUrl의 null/blank를 검증한다.

**Rationale**: request DTO 검증은 API 경계의 검증입니다. 도메인 객체가 잘못된 상태를 직접 막아야 controller를 거치지 않는 내부 생성/수정 경로에서도 안전합니다.

**Alternatives considered**:

- **CategoryRequest 검증만 유지**: API 요청에는 충분하지만 도메인 불변 조건은 보장하지 못합니다.
- **JPA column nullable=false만 사용**: DB 저장 시점까지 오류가 늦게 발견되고 메시지가 도메인 친화적이지 않습니다.

## Decision 2: CategoryValidationException 추가

**Decision**: 검증 실패는 `CategoryValidationException`으로 표현한다.

**Rationale**: 다른 도메인처럼 validation 실패를 도메인 예외로 표현하면 global handler에서 일관된 error code와 message로 변환할 수 있습니다.

**Alternatives considered**:

- **IllegalArgumentException 사용**: 의미가 일반적이고 global handler에서 category 오류로 구분하기 어렵습니다.
- **기존 CategoryNotFoundException 재사용**: 미존재와 검증 실패는 다른 문제입니다.

## Decision 3: error code는 CATEGORY.INVALID

**Decision**: category 검증 실패 error code는 `CATEGORY.INVALID`로 정한다.

**Rationale**: 필수 값 검증 실패를 포괄하는 code입니다. 이후 세부 code가 필요해지면 `CATEGORY.INVALID_NAME` 등으로 분리할 수 있습니다.

**Alternatives considered**:

- **CATEGORY.INVALID_NAME**: name/color/imageUrl 모두를 다루기에는 좁습니다.
- **CATEGORY.VALIDATION_FAILED**: 의미는 명확하지만 기존 code naming보다 길고 무겁습니다.

## Decision 4: description은 검증하지 않음

**Decision**: description은 기존처럼 선택 값으로 둔다.

**Rationale**: `CategoryRequest`에서도 description에는 `@NotBlank`가 없습니다. 기존 API 계약을 유지합니다.
