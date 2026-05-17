# Research: Admin Product 예외 처리 리팩토링

## Decision 1: 관리자 화면 예외는 flash attribute redirect로 처리한다

**Decision**: 상품 미존재 또는 카테고리 미존재 예외 발생 시 `/admin/products`로 redirect하고 flash attribute `error`에 메시지를 담는다.

**Rationale**: 관리자 상품 화면은 JSON API가 아니라 HTML form 기반 UI입니다. 사용자가 오류 후에도 상품 목록에서 작업을 이어갈 수 있어야 하므로 별도 error page보다 목록 redirect가 현재 UX에 더 적합합니다.

**Alternatives considered**:

- `admin/error.html` 또는 공통 error view + HTTP 404: HTTP 의미론에는 더 적합하지만 현재 관리자 CRUD 흐름에서는 사용자가 다시 목록으로 이동해야 하므로 변경 범위와 UX 측면에서 과합니다.
- Product API 예외 재사용: `GlobalExceptionHandler`의 JSON 응답 정책과 섞일 수 있어 HTML flow와 맞지 않습니다.

## Decision 2: 관리자 상품 예외는 API 상품 예외와 분리한다

**Decision**: `AdminProductNotFoundException`, `AdminProductCategoryNotFoundException`처럼 관리자 화면 전용 예외를 둔다.

**Rationale**: API 예외는 HTTP status와 JSON `ErrorResponse` contract에 맞춰져 있고, 관리자 예외는 redirect와 flash message에 맞춰져 있습니다. 같은 미존재 상황이라도 소비자가 다르므로 예외 타입을 분리하면 handler 정책을 명확히 유지할 수 있습니다.

**Alternatives considered**:

- 기존 `ProductNotFoundException` 재사용: 중복은 줄지만 API handler와 HTML handler의 책임이 섞입니다.
- `NoSuchElementException` 유지: 구현은 단순하지만 도메인 의미가 드러나지 않고 예외 처리 범위가 넓어질 수 있습니다.

## Decision 3: handler 위치는 관리자 controller 범위로 제한한다

**Decision**: controller-local `@ExceptionHandler` 또는 `@ControllerAdvice(assignableTypes = AdminProductController.class)`를 사용해 관리자 상품 화면에만 적용한다.

**Rationale**: 관리자 HTML flow에만 적용되는 redirect 정책이므로 전역 JSON handler와 분리되어야 합니다. 적용 범위를 좁히면 다른 API나 화면에 의도치 않은 영향을 주지 않습니다.

**Alternatives considered**:

- `GlobalExceptionHandler`에 추가: JSON API 정책과 HTML redirect 정책이 한 클래스에 섞입니다.
- 모든 `ProductException`을 redirect 처리: Product API 응답 계약을 깨뜨릴 수 있습니다.

## Decision 4: 상품명 검증 실패 flow는 변경하지 않는다

**Decision**: 상품명 검증 실패는 기존처럼 `product/new` 또는 `product/edit` view를 반환하고 form model을 복구한다.

**Rationale**: 상품명 검증 실패는 사용자가 입력을 수정해야 하는 form validation 결과입니다. 미존재 상품/카테고리처럼 작업 대상을 찾을 수 없는 예외와 다릅니다.

**Alternatives considered**:

- 모든 오류를 redirect + flash로 통일: 입력값과 validation errors를 잃어버려 form UX가 나빠집니다.
