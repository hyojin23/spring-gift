# 기능 명세서: Product 서비스 및 예외 처리 리팩토링

**Feature Branch**: `007-product-service-exception-refactor`  
**작성일**: 2026-05-17  
**상태**: 초안  
**입력**: "Product API 예외 처리 리팩토링과 ProductService 도입을 함께 진행"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - Product API 비즈니스 로직의 서비스 계층 이동 (우선순위: P1)

Product API 컨트롤러는 요청/응답 조립에 집중하고, 상품 조회/생성/수정/삭제 및 검증은 서비스 계층에서 처리해야 합니다. 현재 `ProductController`는 `ProductRepository`, `CategoryRepository`, 상품명 검증, 상품/카테고리 존재 확인을 직접 수행하므로 이를 `ProductService`로 이동합니다.

**우선순위 이유**: 컨트롤러에 repository 접근과 비즈니스 규칙이 섞이면 예외 처리와 테스트가 분산됩니다. `ProductService`를 도입하면 API controller가 얇아지고, Option 패키지와 유사한 구조를 적용할 수 있습니다.

**독립적 테스트**: Product API 정상 조회/생성/수정/삭제가 기존 HTTP status와 응답 body를 유지하는지 검증합니다.

**승인 시나리오**:

1. **Given** 상품 목록이 존재할 때, **When** 상품 목록 API를 호출하면, **Then** 기존처럼 HTTP 200과 페이지 응답을 반환합니다.
2. **Given** 상품이 존재할 때, **When** 단건 조회 API를 호출하면, **Then** 기존처럼 HTTP 200과 상품 응답을 반환합니다.
3. **Given** 유효한 요청과 존재하는 카테고리가 있을 때, **When** 상품 생성 API를 호출하면, **Then** 기존처럼 HTTP 201과 `Location` header를 반환합니다.
4. **Given** 유효한 요청과 존재하는 상품/카테고리가 있을 때, **When** 상품 수정 API를 호출하면, **Then** 기존처럼 HTTP 200과 수정된 상품 응답을 반환합니다.
5. **Given** 상품 삭제 요청이 들어올 때, **When** 삭제 API를 호출하면, **Then** 기존처럼 HTTP 204를 반환합니다.

---

### 사용자 시나리오 2 - Product API 오류의 표준 에러 응답화 (우선순위: P2)

Product API에서 상품 미존재, 카테고리 미존재, 상품명 검증 실패가 발생하면 시스템은 `GlobalExceptionHandler`를 통해 표준 `ErrorResponse`를 반환해야 합니다.

**우선순위 이유**: Option API는 이미 도메인 예외와 global handler 기반으로 표준 에러 응답을 반환합니다. Product API도 같은 방식으로 정리해야 API 오류 응답이 일관됩니다.

**독립적 테스트**: Product API 오류 상황에서 HTTP status, error code, message, timestamp가 표준 형식으로 반환되는지 검증합니다.

**승인 시나리오**:

1. **Given** 존재하지 않는 상품 ID로 조회할 때, **When** 상품 조회 API를 호출하면, **Then** HTTP 404와 `PRODUCT.NOT_FOUND`를 반환합니다.
2. **Given** 존재하지 않는 상품 ID로 수정할 때, **When** 상품 수정 API를 호출하면, **Then** HTTP 404와 `PRODUCT.NOT_FOUND`를 반환합니다.
3. **Given** 존재하지 않는 카테고리 ID로 생성/수정할 때, **When** Product API를 호출하면, **Then** HTTP 404와 `PRODUCT.CATEGORY_NOT_FOUND`를 반환합니다.
4. **Given** 상품명 검증에 실패할 때, **When** Product API를 호출하면, **Then** HTTP 400과 `PRODUCT.INVALID_NAME`을 반환합니다.

---

### 사용자 시나리오 3 - 기존 Product API 계약 유지 (우선순위: P3)

리팩토링 후에도 Product API의 정상 응답 status와 body shape는 변경되지 않아야 합니다.

**우선순위 이유**: 이번 작업은 controller/service/exception 구조 개선이며, 클라이언트가 사용하는 정상 API 계약을 바꾸면 안 됩니다.

**독립적 테스트**: 기존 Product API 성공 flow와 대표 오류 flow가 모두 통과하는지 확인합니다.

**승인 시나리오**:

1. 상품 목록 조회는 기존처럼 page 응답을 반환합니다.
2. 상품 생성은 기존처럼 생성된 product id 기반 `Location` header를 반환합니다.
3. 상품 삭제는 기존처럼 body 없이 HTTP 204를 반환합니다.

---

### 엣지 케이스

- `ProductController`에는 repository 직접 접근이 남지 않아야 합니다.
- `ProductController`에는 개별 `@ExceptionHandler(IllegalArgumentException.class)`가 남지 않아야 합니다.
- `ProductController`에는 `ResponseEntity.notFound()`나 `orElse(null)` 기반 오류 분기가 남지 않아야 합니다.
- `AdminProductController`는 이번 작업 범위에 포함하지 않습니다.
- Bean Validation 예외 표준화는 이번 작업 범위에 포함하지 않습니다.
- Product 도메인 생성자/수정 메서드 검증 강화는 이번 작업 범위에 포함하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `ProductService`를 추가하고 Product API의 상품 조회/생성/수정/삭제 로직을 담당하게 해야 합니다.
- **FR-002**: `ProductController`는 `ProductService`만 주입받고 성공 응답 조립에 집중해야 합니다.
- **FR-003**: `gift.product.exception` 패키지에 Product 도메인 예외를 추가해야 합니다.
- **FR-004**: 상품 미존재는 `ProductNotFoundException`으로 표현해야 합니다.
- **FR-005**: 상품 생성/수정 대상 카테고리 미존재는 `ProductCategoryNotFoundException`으로 표현해야 합니다.
- **FR-006**: 상품명 검증 실패는 `ProductValidationException`으로 표현해야 합니다.
- **FR-007**: `GlobalExceptionHandler`는 Product 예외를 표준 `ErrorResponse`로 매핑해야 합니다.
- **FR-008**: Product API 정상 응답 status와 body shape는 변경하지 않아야 합니다.
- **FR-009**: Admin product 화면 controller는 이번 작업에서 변경하지 않아야 합니다.
- **FR-010**: Product API 리팩토링을 검증하는 service/controller/global handler 테스트를 추가하거나 갱신해야 합니다.

### 주요 엔티티

- **ProductService**: Product API 비즈니스 로직과 검증을 담당합니다.
- **ProductNotFoundException**: 요청한 상품을 찾을 수 없을 때 발생합니다.
- **ProductCategoryNotFoundException**: 상품 생성/수정 대상 카테고리를 찾을 수 없을 때 발생합니다.
- **ProductValidationException**: 상품명 검증 실패를 표현합니다.
- **GlobalExceptionHandler**: Product 예외를 HTTP status와 `ErrorResponse`로 변환합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `ProductController`는 `ProductService`만 의존합니다.
- **SC-002**: `ProductController`에 repository 직접 접근, 개별 `@ExceptionHandler`, `orElse(null)`, 직접 `ResponseEntity.notFound()` 오류 분기가 남지 않습니다.
- **SC-003**: 상품 미존재 오류는 HTTP 404와 `PRODUCT.NOT_FOUND`로 응답합니다.
- **SC-004**: 카테고리 미존재 오류는 HTTP 404와 `PRODUCT.CATEGORY_NOT_FOUND`로 응답합니다.
- **SC-005**: 상품명 검증 실패는 HTTP 400과 `PRODUCT.INVALID_NAME`으로 응답합니다.
- **SC-006**: Product API 정상 flow는 기존 status를 유지합니다.
- **SC-007**: `./gradlew test --tests *Product* --tests *GlobalExceptionHandlerTest*`가 통과합니다.

## 가정사항

- Option 패키지의 service/exception/global handler 구조를 Product API 리팩토링의 기준 패턴으로 사용합니다.
- `CategoryNotFoundException`의 기존 category API 응답 구조는 변경하지 않습니다.
- Product API에서 카테고리 미존재는 Product API contract에 맞춘 `ProductCategoryNotFoundException`으로 처리합니다.
- Admin product controller는 HTML form flow가 있으므로 별도 리팩토링에서 다룹니다.
- Bean Validation 오류 응답 표준화는 별도 작업으로 처리합니다.
- Product 도메인 내부 검증 강화는 별도 작업으로 처리합니다.
