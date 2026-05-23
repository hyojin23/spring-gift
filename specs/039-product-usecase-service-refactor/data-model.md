# Data Model: Product 유스케이스 서비스 공통화 리팩토링

## Product

### 책임

- 상품명, 가격, 이미지 URL, 카테고리를 가진 상품 도메인입니다.
- 생성자와 `update()`에서 도메인 필수 조건을 검증합니다.

## ProductCommand

### 형태

구현 시 필요하면 상품 생성/수정 입력을 표현하는 record를 추가할 수 있습니다.

```java
public record ProductCommand(
    String name,
    int price,
    String imageUrl,
    Long categoryId
) {
}
```

### 책임

- API `ProductRequest`와 관리자 form parameter를 공통 유스케이스 입력으로 변환합니다.

### 제약

- 외부 API DTO로 노출하지 않습니다.
- product 패키지 내부 유스케이스 입력으로만 사용합니다.

## ProductService

### 현재 책임

- API 상품 목록/상세/생성/수정/삭제
- API 응답 DTO 변환
- API 상품명 검증
- 카테고리 조회

### 변경 후 기대 책임

- 상품 생성/수정 핵심 유스케이스 보유
- API 응답 DTO 변환
- API 상품명 검증 정책 유지

## AdminProductService

### 현재 책임

- 관리자 상품 목록/카테고리 목록 조회
- 관리자 상품 조회
- 관리자 상품명 검증
- 관리자 상품 생성/수정/삭제

### 변경 후 기대 책임

- 관리자 화면 보조 조회 유지
- 관리자 상품명 검증 정책 유지
- 생성/수정 저장 흐름은 공통 유스케이스를 재사용

## ProductNameValidator

### 정책

- API: `validate(name)`로 `"카카오"` 포함 제한
- Admin: `validate(name, true)`로 `"카카오"` 포함 허용

## 관계

```text
ProductController
  -> ProductService
  -> ProductRepository / CategoryRepository
  -> ProductResponse

AdminProductController
  -> AdminProductService
  -> ProductService or shared product usecase
  -> redirect/view model
```
