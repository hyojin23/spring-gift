# Research: Product 서비스 및 예외 처리 리팩토링

## Decision 1: ProductService 도입과 예외 리팩토링을 함께 진행

**Decision**: Product API의 repository 직접 접근 제거와 Product 도메인 예외 도입을 같은 작업으로 진행합니다.

**Rationale**: `ProductController`에서 오류 분기를 제거하려면 상품/카테고리 조회와 상품명 검증을 수행할 service 계층이 필요합니다. service 없이 예외만 추가하면 controller가 계속 비즈니스 흐름을 갖게 됩니다.

**Alternatives considered**:

- 예외 class만 먼저 추가: controller 책임이 줄지 않아 구조 개선 효과가 작습니다.
- service만 먼저 추가: 오류 응답 표준화가 미뤄지고 controller-level handler가 남습니다.

## Decision 2: Product API 전용 카테고리 미존재 예외 사용

**Decision**: Product API에서 카테고리 미존재는 `ProductCategoryNotFoundException`으로 표현합니다.

**Rationale**: 기존 `CategoryNotFoundException`은 category API에서 `ResponseEntity<Void>` 404로 처리됩니다. Product API는 표준 `ErrorResponse`를 반환해야 하므로 Product API contract에 맞춘 예외가 필요합니다.

**Alternatives considered**:

- `CategoryNotFoundException` 재사용: 기존 global handler가 body 없는 404를 반환하므로 Product error contract와 맞지 않습니다.
- category 예외 구조 전체 리팩토링: 범위가 product API를 벗어납니다.

## Decision 3: AdminProductController 제외

**Decision**: `AdminProductController`는 이번 Product API 리팩토링에서 변경하지 않습니다.

**Rationale**: Admin controller는 HTML template/form 흐름을 가지며, form validation 실패 시 model을 채워 같은 view를 반환합니다. JSON API의 service/exception/global handler 패턴과 섞으면 scope가 커지고 화면 UX가 바뀔 수 있습니다.

**Alternatives considered**:

- Admin controller까지 동시에 service화: 가능하지만 form error 처리와 redirect/view 흐름을 별도로 설계해야 합니다.
- Admin controller 예외만 교체: HTML 화면에 `ErrorResponse`를 적용하는 것은 적절하지 않을 수 있습니다.

## Decision 4: Bean Validation 예외 표준화 제외

**Decision**: `@Valid ProductRequest`의 Bean Validation 실패 응답 표준화는 이번 작업에서 제외합니다.

**Rationale**: 이번 작업은 Product API의 수동 오류 분기와 상품명 검증을 Product 도메인 예외로 옮기는 것입니다. Spring validation exception handling 정책은 전체 API 공통 작업으로 분리하는 것이 좋습니다.
