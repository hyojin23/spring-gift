# Data Model: Product 서비스 및 예외 처리 리팩토링

## ProductService

Product API의 비즈니스 로직과 실패 조건을 담당합니다.

### Methods

- `Page<ProductResponse> getProducts(Pageable pageable)`
- `ProductResponse getProduct(Long id)`
- `ProductResponse createProduct(ProductRequest request)`
- `ProductResponse updateProduct(Long id, ProductRequest request)`
- `void deleteProduct(Long id)`

### Responsibilities

- 상품명 검증
- 상품 존재 확인
- 생성/수정 대상 카테고리 존재 확인
- Product entity 저장/수정/삭제
- ProductResponse 변환

## Product Exceptions

### ProductException

Product 도메인 예외의 공통 기반 타입입니다.

### ProductNotFoundException

요청한 상품을 찾을 수 없을 때 발생합니다.

Recommended message:

```text
요청한 상품을 찾을 수 없습니다.
```

API mapping:

- HTTP 404
- `PRODUCT.NOT_FOUND`

### ProductCategoryNotFoundException

Product 생성/수정 대상 카테고리를 찾을 수 없을 때 발생합니다.

Recommended message:

```text
요청한 카테고리를 찾을 수 없습니다.
```

API mapping:

- HTTP 404
- `PRODUCT.CATEGORY_NOT_FOUND`

### ProductValidationException

상품명 검증 실패를 표현합니다.

Message:

- `ProductNameValidator`가 반환한 오류 메시지를 `", "`로 join한 값

API mapping:

- HTTP 400
- `PRODUCT.INVALID_NAME`

## ProductController

### Before

- `ProductRepository` 직접 접근
- `CategoryRepository` 직접 접근
- `validateName` helper 보유
- 직접 404 응답 반환
- controller-level `@ExceptionHandler(IllegalArgumentException.class)` 보유

### After

- `ProductService`만 주입
- 성공 응답 status와 `Location` header 조립
- 오류 응답은 `GlobalExceptionHandler`에 위임

## Behavioral Compatibility

- 상품 목록 조회: HTTP 200 유지
- 상품 단건 조회: HTTP 200 유지
- 상품 생성: HTTP 201과 `Location` header 유지
- 상품 수정: HTTP 200 유지
- 상품 삭제: HTTP 204 유지
- AdminProductController 변경 없음
