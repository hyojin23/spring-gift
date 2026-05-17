# Data Model: Product 도메인 검증 강화 리팩토링

## Product

상품 도메인 엔티티입니다. 생성과 수정 시 공통 불변 조건을 직접 검증합니다.

**Fields**:

- `id`: 상품 식별자
- `name`: 상품명
- `price`: 상품 가격
- `imageUrl`: 상품 이미지 URL
- `category`: 상품 카테고리
- `options`: 상품 옵션 목록

**Validation Rules**:

- `name`은 null 또는 blank일 수 없습니다.
- `price`는 0보다 커야 합니다.
- `imageUrl`은 null 또는 blank일 수 없습니다.
- `category`는 null일 수 없습니다.

**Lifecycle**:

1. 생성자 호출
2. 공통 검증 수행
3. 필드 할당
4. `update()` 호출
5. 공통 검증 수행
6. 필드 변경

## ProductValidationException

Product 검증 실패를 표현하는 product 도메인 예외입니다.

**Fields**:

- `message`: 검증 실패 이유

**Usage**:

- Product 생성자 검증 실패
- Product `update()` 검증 실패
- Product API 상품명 정책 검증 실패

## ProductNameValidator

상품명 세부 정책을 검증하는 helper입니다.

**Responsibilities**:

- 상품명 최대 길이 검증
- 허용 문자 검증
- 경로별 `카카오` 포함 허용 여부 검증

**Constraints**:

- Product 엔티티의 공통 불변 조건과 분리됩니다.
- API에서는 `allowKakao=false` 정책을 유지합니다.
- Admin에서는 `allowKakao=true` 정책을 유지합니다.

## Relationships

- `ProductRequest`는 외부 요청 DTO 검증을 담당합니다.
- `ProductService`는 API 상품명 정책과 카테고리 조회를 담당합니다.
- `AdminProductService`는 Admin 상품명 정책과 카테고리 조회를 담당합니다.
- `Product`는 최종적으로 생성/수정되는 도메인 상태의 공통 불변 조건을 보장합니다.
