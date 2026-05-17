# Data Model: Admin Product 예외 처리 리팩토링

## AdminProductException

관리자 상품 화면에서 발생한 도메인 예외의 기준 타입입니다.

**Fields**:

- `message`: 관리자 화면에 flash attribute로 전달할 오류 메시지

**Validation Rules**:

- 메시지는 관리자 사용자가 원인을 이해할 수 있어야 합니다.
- JSON API error response contract에 의존하지 않습니다.

## AdminProductNotFoundException

관리자 상품 화면에서 상품 ID로 상품을 찾을 수 없을 때 발생합니다.

**Fields**:

- `id`: 찾을 수 없었던 상품 ID
- `message`: `"상품이 존재하지 않습니다. id={id}"`

**Lifecycle**:

1. `AdminProductService.getProduct(id)`에서 repository 조회 실패
2. `AdminProductNotFoundException` 발생
3. 관리자 예외 handler에서 `/admin/products` redirect
4. flash attribute `error`로 메시지 전달

## AdminProductCategoryNotFoundException

관리자 상품 등록/수정 요청에서 카테고리 ID로 카테고리를 찾을 수 없을 때 발생합니다.

**Fields**:

- `id`: 찾을 수 없었던 카테고리 ID
- `message`: `"카테고리가 존재하지 않습니다. id={id}"`

**Lifecycle**:

1. `AdminProductService.getCategory(id)`에서 repository 조회 실패
2. `AdminProductCategoryNotFoundException` 발생
3. 관리자 예외 handler에서 `/admin/products` redirect
4. flash attribute `error`로 메시지 전달

## Flash Error Message

redirect 후 관리자 상품 목록 화면에서 한 번 표시되는 오류 메시지입니다.

**Fields**:

- `error`: 예외 메시지 문자열

**Constraints**:

- redirect 응답에 flash attribute로 전달합니다.
- 목록 화면에서 표시할 수 있어야 합니다.
- 다음 요청 이후에는 유지되지 않습니다.

## Relationships

- `AdminProductService`는 상품/카테고리 미존재 상황에서 `AdminProductException` 하위 타입을 발생시킵니다.
- `AdminProductController` 또는 admin 전용 handler는 `AdminProductException`을 처리합니다.
- `product/list` template은 flash `error`를 표시합니다.
- `ProductController`와 `GlobalExceptionHandler`의 JSON API contract는 이 모델에 의존하지 않습니다.
