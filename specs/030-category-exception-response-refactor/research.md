# Research: Category 예외 응답 일관화 리팩토링

## Decision 1: CategoryNotFoundException도 ErrorResponse 반환

**Decision**: `CategoryNotFoundException` handler는 404 status와 함께 `ErrorResponse` body를 반환한다.

**Rationale**: 다른 도메인 예외는 code/message body를 반환하고 있습니다. category만 body가 없으면 클라이언트의 에러 처리 로직이 예외 케이스를 가져야 합니다.

**Alternatives considered**:

- **기존 404 body 없음 유지**: REST 관점에서 가능하지만 프로젝트 내부 응답 일관성이 떨어집니다.
- **모든 404를 body 없음으로 통일**: 이미 product/option/wish/order는 ErrorResponse를 사용하므로 변경 범위가 큽니다.

## Decision 2: error code는 CATEGORY.NOT_FOUND

**Decision**: category 미존재 error code는 `CATEGORY.NOT_FOUND`로 정한다.

**Rationale**: 기존 code naming이 `DOMAIN.REASON` 형태입니다. `PRODUCT.NOT_FOUND`, `WISH.NOT_FOUND`, `OPTION.NOT_FOUND`와 맞춥니다.

**Alternatives considered**:

- **CATEGORY_NOT_FOUND**: 현재 점 구분 naming과 맞지 않습니다.
- **CATEGORY.NOT_EXIST**: 기존 not found naming과 어긋납니다.

## Decision 3: exception class 이동 제외

**Decision**: `CategoryNotFoundException`을 `gift.category.exception`으로 옮기는 작업은 이번 범위에서 제외한다.

**Rationale**: 이번 목표는 응답 일관화입니다. 패키지 이동은 import 변경과 별도 구조 정리 성격이므로 후속 작업으로 분리하는 편이 작고 안전합니다.
