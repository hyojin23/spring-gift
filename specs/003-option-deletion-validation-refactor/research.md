# Research: Option 삭제 검증 조회 최적화

## Decision 1: Spring Data JPA derived count query 사용

**Decision**: `OptionRepository`에 `long countByProductId(Long productId)`를 추가합니다.

**Rationale**: Spring Data JPA는 method name 기반 count query를 지원합니다. 삭제 가능 여부 검증에는 옵션 엔티티 목록이 필요하지 않고 개수만 필요하므로, derived count query가 가장 단순하고 의도가 명확합니다.

**Alternatives considered**:

- `findByProductId(productId).size()` 유지: 구현은 단순하지만 옵션 목록 전체를 조회하므로 검증 목적에 비해 과합니다.
- `existsByProductId` 조합 사용: 마지막 옵션 삭제 제한은 1개 이하 여부를 알아야 하므로 exists만으로는 부족합니다.
- custom `@Query` 사용: 현재 요구사항은 단순 count이므로 derived query보다 장황합니다.

## Decision 2: API 응답 계약 유지

**Decision**: 예외 타입, HTTP status, error code, message는 변경하지 않습니다.

**Rationale**: 이번 작업은 내부 조회 방식 개선입니다. 클라이언트 관점의 동작을 바꾸면 리팩토링 범위를 벗어납니다.

**Alternatives considered**:

- 새로운 예외 타입 추가: 실패 조건이 기존과 동일하므로 불필요합니다.
- controller 응답 테스트 변경: 외부 계약이 유지되어야 하므로 기존 테스트를 유지하는 편이 좋습니다.

## Decision 3: 단위 테스트 stubbing을 count query 기준으로 변경

**Decision**: `OptionServiceTest`에서 삭제 관련 테스트는 `findByProductId` 대신 `countByProductId`를 stub합니다.

**Rationale**: service가 더 이상 목록 조회를 사용하지 않으므로 테스트도 구현 의존 stubbing을 새 흐름에 맞춰 갱신해야 합니다.

**Alternatives considered**:

- controller 통합 테스트만 의존: 내부 조회 방식 변경을 직접 검증하기 어렵습니다.
- repository slice test 추가: derived query method가 단순하고 Spring Data JPA 표준 기능이라 현재 범위에서는 과합니다.
