# 기능 명세서: Admin Product 서비스 분리 리팩토링

**Feature Branch**: `008-admin-product-service-refactor`  
**작성일**: 2026-05-17  
**상태**: 초안  
**입력**: "AdminProductController 분리 리팩토링"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - Admin 상품 화면 비즈니스 로직의 서비스 계층 이동 (우선순위: P1)

관리자 상품 화면 컨트롤러는 HTML view와 form model 조립에 집중하고, 상품/카테고리 조회 및 상품 생성/수정/삭제는 서비스 계층에서 처리해야 합니다. 현재 `AdminProductController`는 `ProductRepository`, `CategoryRepository`, 상품명 검증, 상품/카테고리 조회를 직접 수행하므로 이를 admin 전용 service로 이동합니다.

**우선순위 이유**: Admin 화면은 JSON API와 달리 form validation 실패 시 같은 view를 다시 렌더링해야 합니다. 컨트롤러에는 view model 조립만 남기고, repository 접근과 domain 변경은 service로 분리하면 화면 흐름을 유지하면서 구조를 단순화할 수 있습니다.

**독립적 테스트**: 관리자 상품 목록/등록/수정/삭제 화면 flow가 기존 view 이름과 redirect 경로를 유지하는지 검증합니다.

**승인 시나리오**:

1. **Given** 관리자가 상품 목록에 접근할 때, **When** `/admin/products`를 요청하면, **Then** 기존처럼 `product/list` view와 상품 목록 model을 반환합니다.
2. **Given** 관리자가 상품 등록 화면에 접근할 때, **When** `/admin/products/new`를 요청하면, **Then** 기존처럼 `product/new` view와 카테고리 목록 model을 반환합니다.
3. **Given** 유효한 상품 등록 form이 제출될 때, **When** `/admin/products`로 POST 요청하면, **Then** 기존처럼 `/admin/products`로 redirect합니다.
4. **Given** 유효한 상품 수정 form이 제출될 때, **When** `/admin/products/{id}/edit`로 POST 요청하면, **Then** 기존처럼 `/admin/products`로 redirect합니다.
5. **Given** 삭제 요청이 제출될 때, **When** `/admin/products/{id}/delete`로 POST 요청하면, **Then** 기존처럼 `/admin/products`로 redirect합니다.

---

### 사용자 시나리오 2 - Admin 상품명 검증 실패 UX 유지 (우선순위: P2)

관리자 상품 등록/수정 form에서 상품명 검증에 실패하면 기존처럼 form view를 다시 반환하고, 입력값과 오류 메시지를 model에 채워야 합니다.

**우선순위 이유**: Admin 화면은 API error response가 아니라 사용자가 입력을 수정할 수 있는 HTML form UX가 중요합니다. 리팩토링 후에도 기존 form 복구 동작이 유지되어야 합니다.

**독립적 테스트**: 상품명 검증 실패 시 view 이름, `errors`, 입력값, 카테고리 목록 model이 유지되는지 검증합니다.

**승인 시나리오**:

1. **Given** 상품 등록 form의 상품명이 유효하지 않을 때, **When** POST `/admin/products` 요청하면, **Then** `product/new` view를 반환하고 `errors`와 기존 입력값을 model에 포함합니다.
2. **Given** 상품 수정 form의 상품명이 유효하지 않을 때, **When** POST `/admin/products/{id}/edit` 요청하면, **Then** `product/edit` view를 반환하고 `errors`, 기존 입력값, 상품, 카테고리 목록을 model에 포함합니다.
3. **Given** admin 상품명 검증을 수행할 때, **When** 상품명에 `카카오`가 포함되어 있어도, **Then** 기존처럼 admin flow에서는 허용됩니다.

---

### 사용자 시나리오 3 - Admin 화면 흐름과 API 리팩토링 분리 (우선순위: P3)

Admin Product 리팩토링은 HTML 화면 controller에만 적용하며, Product JSON API의 `ErrorResponse` 계약과 global handler 정책은 변경하지 않습니다.

**우선순위 이유**: Admin controller는 view/redirect 중심이고 API controller는 JSON 응답 중심입니다. 두 흐름을 섞으면 화면 UX나 API contract가 의도치 않게 바뀔 수 있습니다.

**독립적 테스트**: Admin controller 테스트와 기존 Product API 테스트가 각각 통과하는지 확인합니다.

**승인 시나리오**:

1. `ProductController`의 JSON API 응답 계약은 변경되지 않습니다.
2. `GlobalExceptionHandler`의 Product API error mapping은 변경되지 않습니다.
3. Admin 화면의 view 이름과 redirect 경로는 변경되지 않습니다.

---

### 엣지 케이스

- `AdminProductController`에는 `ProductRepository`와 `CategoryRepository` 직접 의존성이 남지 않아야 합니다.
- Admin form validation 실패 시 `categories` model이 누락되면 안 됩니다.
- Admin 수정 form validation 실패 시 `product` model이 누락되면 안 됩니다.
- Admin 화면에 `ErrorResponse` JSON 응답을 적용하지 않습니다.
- Admin not found/error page 설계는 이번 작업 범위에 포함하지 않습니다.
- Product API service/exception 구조는 이번 작업에서 변경하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `AdminProductService`를 추가하고 Admin 상품 화면의 상품/카테고리 조회 및 상품 생성/수정/삭제 로직을 담당하게 해야 합니다.
- **FR-002**: `AdminProductController`는 `AdminProductService`만 주입받도록 변경해야 합니다.
- **FR-003**: `AdminProductController`는 view 이름, redirect 경로, form model 조립을 담당해야 합니다.
- **FR-004**: 상품명 검증 실패 시 기존처럼 form view를 반환하고 `errors`와 기존 입력값을 model에 포함해야 합니다.
- **FR-005**: Admin 상품명 검증은 기존처럼 `ProductNameValidator.validate(name, true)` 규칙을 사용해야 합니다.
- **FR-006**: Admin 상품 등록/수정/삭제 성공 flow의 redirect 경로는 변경하지 않아야 합니다.
- **FR-007**: Admin product HTML template 파일은 이번 작업에서 변경하지 않아야 합니다.
- **FR-008**: Product JSON API controller와 global error response contract는 변경하지 않아야 합니다.
- **FR-009**: Admin Product controller flow를 검증하는 MockMvc 테스트를 추가해야 합니다.

### 주요 엔티티

- **AdminProductService**: Admin 상품 화면에서 필요한 상품/카테고리 조회, 상품 생성/수정/삭제, 상품명 검증을 담당합니다.
- **AdminProductController**: HTML view와 form model 조립, redirect를 담당합니다.
- **ProductNameValidator**: Admin 상품명 검증 시 `allowKakao=true` 규칙으로 사용됩니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `AdminProductController`는 `AdminProductService`만 의존합니다.
- **SC-002**: `AdminProductController`에 `ProductRepository`, `CategoryRepository` 직접 접근이 남지 않습니다.
- **SC-003**: Admin 상품 목록/등록/수정/삭제 성공 flow는 기존 view/redirect 계약을 유지합니다.
- **SC-004**: Admin 상품명 검증 실패 flow는 기존 form 복구 model을 유지합니다.
- **SC-005**: Product API 관련 테스트가 기존처럼 통과합니다.
- **SC-006**: `./gradlew test --tests *AdminProduct* --tests *Product*`가 통과합니다.

## 가정사항

- Product JSON API 리팩토링은 `007-product-service-exception-refactor`에서 완료되었습니다.
- AdminProductController는 HTML form flow이므로 `ErrorResponse` JSON 응답 표준화를 적용하지 않습니다.
- Admin not found/error page는 별도 작업으로 처리합니다.
- 이번 작업은 service 분리와 controller 책임 축소에 집중합니다.
- HTML template 구조와 UI는 변경하지 않습니다.
