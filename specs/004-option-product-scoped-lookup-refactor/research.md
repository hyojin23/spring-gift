# Research: Option 상품 범위 조회 리팩토링

## Decision 1: Spring Data JPA derived query 사용

**Decision**: `OptionRepository`에 `Optional<Option> findByIdAndProductId(Long optionId, Long productId)`를 추가합니다.

**Rationale**: Spring Data JPA는 연관 객체의 id 조건을 method name으로 표현할 수 있습니다. 삭제 대상 옵션 조회는 option id와 product id가 함께 필요한 조건이므로 derived query method가 가장 단순하고 의도가 명확합니다.

**Alternatives considered**:

- `findById(optionId).filter(...)` 유지: 동작은 맞지만 product scope가 service filter로 숨어 있어 조회 의도가 덜 분명합니다.
- custom `@Query` 사용: 현재 조건은 단순하므로 derived query보다 장황합니다.
- `existsByIdAndProductId` 후 재조회: 존재 확인과 삭제 대상 조회가 분리되어 쿼리가 늘어납니다.

## Decision 2: 옵션 미존재 예외 유지

**Decision**: 옵션이 없거나 요청 상품에 속하지 않는 경우 모두 기존처럼 `OptionNotFoundException`을 사용합니다.

**Rationale**: 클라이언트 관점에서는 요청한 상품의 옵션 리소스가 존재하지 않는 상황입니다. 기존 HTTP 404와 `OPTION.NOT_FOUND` 계약을 유지합니다.

**Alternatives considered**:

- 다른 상품 소속 옵션에 별도 예외 추가: API 응답 계약이 달라지고 이번 리팩토링 범위를 벗어납니다.
- 403 응답으로 변경: 소유권/권한 모델이 아니라 nested resource lookup 실패에 가까우므로 기존 404가 적절합니다.

## Decision 3: service 단위 테스트 중심 검증

**Decision**: `OptionServiceTest`에서 product-scoped lookup stubbing과 실패 케이스를 검증합니다.

**Rationale**: 이번 변경의 핵심은 service가 어떤 repository method로 삭제 대상 옵션을 찾는지입니다. controller 통합 테스트는 외부 응답 계약 유지 확인에 사용하고, 조회 표현 변경은 service 테스트에서 직접 확인합니다.

**Alternatives considered**:

- controller 테스트만 의존: 내부 조회 표현 변경을 명확히 검증하기 어렵습니다.
- repository slice test 추가: derived query method가 단순하고 Spring Data JPA 표준 기능이라 현재 범위에서는 과합니다.
