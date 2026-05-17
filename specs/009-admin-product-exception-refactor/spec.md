# 기능 명세서: Admin Product 예외 처리 리팩토링

**Feature Branch**: `009-admin-product-exception-refactor`  
**작성일**: 2026-05-18  
**상태**: 초안  
**입력**: "ADR에서 결정한 대로 flash attribute로 작업"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 관리자 상품 미존재 예외를 도메인 예외로 표현 (우선순위: P1)

관리자 상품 수정 화면 접근 또는 수정 요청에서 존재하지 않는 상품 ID가 전달되면, `AdminProductService`는 범용 `NoSuchElementException` 대신 관리자 상품 도메인 예외를 발생시켜야 합니다.

**우선순위 이유**: 범용 예외는 예외 발생 의도를 코드에서 드러내기 어렵고, HTML 관리자 화면 전용 처리와 API 예외 처리를 구분하기 어렵습니다.

**독립적 테스트**: 존재하지 않는 상품 수정 화면 요청이 `/admin/products`로 redirect되고 flash attribute에 오류 메시지가 포함되는지 검증합니다.

**승인 시나리오**:

1. **Given** 존재하지 않는 상품 ID가 있을 때, **When** 관리자가 `/admin/products/{id}/edit`에 접근하면, **Then** `/admin/products`로 redirect하고 flash attribute `error`에 상품 미존재 메시지를 담습니다.
2. **Given** 존재하지 않는 상품 ID가 있을 때, **When** 관리자가 `/admin/products/{id}/edit`로 수정 요청을 제출하면, **Then** `/admin/products`로 redirect하고 flash attribute `error`에 상품 미존재 메시지를 담습니다.

---

### 사용자 시나리오 2 - 관리자 카테고리 미존재 예외를 도메인 예외로 표현 (우선순위: P1)

관리자 상품 등록 또는 수정 요청에서 존재하지 않는 카테고리 ID가 전달되면, `AdminProductService`는 범용 `NoSuchElementException` 대신 관리자 카테고리 도메인 예외를 발생시켜야 합니다.

**우선순위 이유**: 카테고리 미존재는 관리자 상품 화면의 입력 데이터 오류이며, 사용자가 상품 목록으로 돌아가 다시 작업할 수 있도록 HTML flow에 맞게 처리되어야 합니다.

**독립적 테스트**: 존재하지 않는 카테고리 ID로 상품 등록/수정을 요청하면 `/admin/products`로 redirect되고 flash attribute에 오류 메시지가 포함되는지 검증합니다.

**승인 시나리오**:

1. **Given** 존재하지 않는 카테고리 ID가 있을 때, **When** 관리자가 상품 등록 요청을 제출하면, **Then** `/admin/products`로 redirect하고 flash attribute `error`에 카테고리 미존재 메시지를 담습니다.
2. **Given** 존재하지 않는 카테고리 ID가 있을 때, **When** 관리자가 상품 수정 요청을 제출하면, **Then** `/admin/products`로 redirect하고 flash attribute `error`에 카테고리 미존재 메시지를 담습니다.

---

### 사용자 시나리오 3 - 관리자 HTML 예외 처리와 Product API 예외 처리 분리 (우선순위: P2)

관리자 상품 화면 예외는 HTML redirect와 flash message로 처리하고, Product JSON API의 `ErrorResponse` 계약과 global handler 정책은 변경하지 않습니다.

**우선순위 이유**: API는 HTTP status와 JSON body가 중요하지만, 관리자 화면은 view/redirect 흐름과 사용자가 작업을 이어갈 수 있는 UX가 중요합니다.

**독립적 테스트**: 관리자 상품 예외 처리 테스트와 기존 Product API 예외 처리 테스트가 모두 통과하는지 확인합니다.

**승인 시나리오**:

1. **Given** 관리자 상품 화면에서 미존재 예외가 발생할 때, **When** 예외 핸들러가 처리하면, **Then** JSON `ErrorResponse`가 아니라 redirect + flash attribute를 사용합니다.
2. **Given** Product JSON API에서 미존재 예외가 발생할 때, **When** global exception handler가 처리하면, **Then** 기존 JSON error response 계약을 유지합니다.

---

### 사용자 시나리오 4 - 관리자 상품 목록 화면에서 오류 메시지 표시 (우선순위: P2)

관리자 상품 화면 예외가 redirect된 후 상품 목록 화면은 flash attribute의 `error` 메시지를 표시해야 합니다.

**우선순위 이유**: redirect만 발생하면 사용자는 왜 상품 목록으로 돌아왔는지 알 수 없습니다. flash message가 있어야 실패 원인을 한 번 확인할 수 있습니다.

**독립적 테스트**: flash attribute `error`가 있는 상태로 상품 목록 화면을 렌더링하면 오류 메시지 model이 유지되는지 검증합니다.

**승인 시나리오**:

1. **Given** flash attribute `error`가 있을 때, **When** `/admin/products` 목록 화면을 렌더링하면, **Then** 화면에서 오류 메시지를 표시할 수 있습니다.

---

### 엣지 케이스

- 관리자 상품 화면 예외 처리에서 `NoSuchElementException`을 직접 사용하지 않습니다.
- 관리자 상품 예외를 Product API용 `ProductNotFoundException`으로 재사용하지 않습니다.
- 관리자 상품 화면 예외가 `GlobalExceptionHandler`의 JSON 응답으로 처리되지 않도록 합니다.
- 상품명 검증 실패 flow는 기존처럼 form view를 반환하고 redirect하지 않습니다.
- 존재하지 않는 상품 수정 화면 접근은 `product/edit` view를 렌더링하지 않습니다.
- 존재하지 않는 카테고리로 상품 등록/수정 시 저장이 수행되지 않아야 합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `AdminProductService`의 상품 미존재 상황은 관리자 상품 도메인 예외로 표현해야 합니다.
- **FR-002**: `AdminProductService`의 카테고리 미존재 상황은 관리자 상품 도메인 예외로 표현해야 합니다.
- **FR-003**: 관리자 상품 도메인 예외는 `/admin/products`로 redirect되어야 합니다.
- **FR-004**: redirect 시 `RedirectAttributes`에 `error` flash attribute를 추가해야 합니다.
- **FR-005**: 상품 목록 화면은 flash attribute `error`를 표시할 수 있어야 합니다.
- **FR-006**: Product JSON API 예외 응답 계약은 변경하지 않아야 합니다.
- **FR-007**: 상품명 검증 실패 flow는 기존 form 복구 동작을 유지해야 합니다.
- **FR-008**: 관리자 상품 예외 처리 flow를 검증하는 MockMvc 테스트를 추가해야 합니다.
- **FR-009**: 기존 Admin Product 성공 flow 테스트는 계속 통과해야 합니다.

### 주요 엔티티

- **AdminProductService**: 관리자 상품 화면의 상품/카테고리 조회와 변경을 담당하며, 미존재 상황에서 관리자 상품 도메인 예외를 발생시킵니다.
- **AdminProductNotFoundException**: 관리자 상품 화면에서 상품이 존재하지 않는 상황을 표현합니다.
- **AdminProductCategoryNotFoundException**: 관리자 상품 화면에서 카테고리가 존재하지 않는 상황을 표현합니다.
- **AdminProductController 또는 AdminProductExceptionHandler**: 관리자 상품 도메인 예외를 redirect + flash attribute 방식으로 처리합니다.
- **product/list template**: flash attribute `error`가 있을 때 관리자에게 오류 메시지를 표시합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `AdminProductService`에는 `NoSuchElementException` import와 직접 throw가 남지 않습니다.
- **SC-002**: 존재하지 않는 상품 수정 화면 요청은 `/admin/products`로 redirect됩니다.
- **SC-003**: 존재하지 않는 상품 수정 요청은 `/admin/products`로 redirect됩니다.
- **SC-004**: 존재하지 않는 카테고리로 상품 등록 요청 시 `/admin/products`로 redirect됩니다.
- **SC-005**: 존재하지 않는 카테고리로 상품 수정 요청 시 `/admin/products`로 redirect됩니다.
- **SC-006**: 위 redirect 응답은 flash attribute `error`를 포함합니다.
- **SC-007**: Product API 관련 테스트가 기존처럼 통과합니다.
- **SC-008**: `./gradlew test --tests *AdminProduct* --tests *Product*`가 통과합니다.

## 가정사항

- `docs/adr/0001-admin-product-exception-flash-redirect.md`에서 관리자 상품 화면 예외 처리 방식으로 flash attribute redirect를 선택했습니다.
- Admin Product service 분리는 `008-admin-product-service-refactor`에서 완료되었습니다.
- Product JSON API 예외 처리 리팩토링은 `007-product-service-exception-refactor`에서 완료되었습니다.
- 이번 작업은 관리자 상품 화면의 미존재 예외 처리에 집중하며, 새로운 error page는 만들지 않습니다.
- 삭제 요청의 상품 미존재 처리 정책은 repository delete 동작과 기존 flow를 우선 유지하고, 별도 요구가 생기면 후속 spec에서 다룹니다.
