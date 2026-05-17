# Research: Product 도메인 검증 강화 리팩토링

## Decision 1: Product는 공통 불변 조건만 검증한다

**Decision**: `Product` 엔티티는 상품명 null/blank, 가격 0 이하, 이미지 URL null/blank, 카테고리 null을 검증합니다.

**Rationale**: 이 조건들은 API, Admin, 테스트, 내부 코드 등 어떤 경로로 Product가 생성/수정되더라도 항상 지켜져야 하는 공통 불변 조건입니다.

**Alternatives considered**:

- request DTO Bean Validation에만 의존: service를 우회하면 무효한 Product가 생성될 수 있습니다.
- service 검증에만 의존: Product 자체가 자기 상태를 보호하지 못합니다.

## Decision 2: 상품명 세부 정책은 ProductNameValidator에 유지한다

**Decision**: 길이, 허용 문자, `카카오` 포함 여부는 기존처럼 `ProductNameValidator`와 service 계층에서 처리합니다.

**Rationale**: `카카오` 포함 허용 여부는 Product의 절대 불변 조건이 아니라 API/Admin 경로별 정책입니다. Product 엔티티에 넣으면 Admin flow에서 허용되는 상품명이 도메인에서 막히게 됩니다.

**Alternatives considered**:

- Product가 `ProductNameValidator.validate(name)`을 직접 호출: API 정책이 Product 도메인에 고정되어 Admin flow와 충돌합니다.
- Product 생성자에 `allowKakao` 파라미터 추가: 도메인 생성자가 요청 경로 정책을 알게 되어 책임이 흐려집니다.

## Decision 3: 기존 ProductValidationException을 사용한다

**Decision**: Product 도메인 검증 실패는 기존 `ProductValidationException`을 사용합니다.

**Rationale**: 이미 Product 검증 실패를 표현하는 예외가 있고, global handler도 `PRODUCT.INVALID_NAME` 응답으로 처리하고 있습니다. 이번 작업의 목적은 예외 종류 추가가 아니라 Product 상태 무결성 강화입니다.

**Alternatives considered**:

- `InvalidProductException` 추가: 이름은 더 일반적이지만 기존 예외와 역할이 겹칩니다.
- `IllegalArgumentException` 사용: 도메인 예외 체계와 global handler 정책에서 벗어납니다.

## Decision 4: update 검증은 필드 할당 전에 수행한다

**Decision**: `Product.update()`는 모든 값을 검증한 뒤 필드를 변경합니다.

**Rationale**: 검증 도중 예외가 발생했을 때 일부 필드만 변경되는 상태를 피해야 합니다.

**Alternatives considered**:

- 필드별 검증과 할당을 순차 수행: 중간 실패 시 부분 변경 위험이 있습니다.
