# Data Model: Admin Product 서비스 분리 리팩토링

## AdminProductService

Admin 상품 화면에서 필요한 조회와 변경 로직을 담당합니다.

### Methods

- `List<Product> getProducts()`
- `List<Category> getCategories()`
- `Product getProduct(Long id)`
- `List<String> validateName(String name)`
- `void createProduct(String name, int price, String imageUrl, Long categoryId)`
- `void updateProduct(Long id, String name, int price, String imageUrl, Long categoryId)`
- `void deleteProduct(Long id)`

### Responsibilities

- 상품 목록 조회
- 카테고리 목록 조회
- 상품 단건 조회
- admin 상품명 검증
- 상품 생성/수정/삭제

## AdminProductController

### Before

- `ProductRepository` 직접 접근
- `CategoryRepository` 직접 접근
- 상품명 검증 직접 수행
- 상품 생성/수정/삭제 직접 수행
- form validation 실패 model 조립 수행

### After

- `AdminProductService`만 주입
- view 이름과 redirect 경로 결정
- form validation 실패 model 조립 수행
- repository 접근 없음

## View Model Contract

### product/list

- `products`

### product/new

- `categories`
- validation 실패 시: `errors`, `name`, `price`, `imageUrl`, `categoryId`

### product/edit

- `product`
- `categories`
- validation 실패 시: `errors`, `name`, `price`, `imageUrl`, `categoryId`

## Behavioral Compatibility

- HTML template 파일은 변경하지 않습니다.
- 성공 redirect 경로는 `/admin/products`를 유지합니다.
- Product JSON API 응답 계약은 변경하지 않습니다.
