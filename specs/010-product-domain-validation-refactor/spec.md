# 기능 명세서: Product 도메인 검증 강화 리팩토링

**Feature Branch**: `010-product-domain-validation-refactor`  
**작성일**: 2026-05-18  
**상태**: 초안  
**입력**: "Product 도메인 검증 강화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - Product 생성 시 공통 도메인 불변 조건 보장 (우선순위: P1)

상품은 생성되는 순간부터 유효한 상태여야 합니다. `Product` 생성자는 상품명, 가격, 이미지 URL, 카테고리의 공통 불변 조건을 직접 검증해야 합니다.

**우선순위 이유**: 현재 `Product`는 생성자에서 값을 그대로 할당하므로 service나 request 계층을 우회하면 유효하지 않은 상품 객체가 만들어질 수 있습니다.

**독립적 테스트**: `Product` 생성자에 유효하지 않은 값을 전달했을 때 product 도메인 예외가 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** 상품명이 비어 있을 때, **When** `Product`를 생성하면, **Then** 상품 검증 예외가 발생합니다.
2. **Given** 가격이 0 이하일 때, **When** `Product`를 생성하면, **Then** 상품 검증 예외가 발생합니다.
3. **Given** 이미지 URL이 비어 있을 때, **When** `Product`를 생성하면, **Then** 상품 검증 예외가 발생합니다.
4. **Given** 카테고리가 없을 때, **When** `Product`를 생성하면, **Then** 상품 검증 예외가 발생합니다.

---

### 사용자 시나리오 2 - Product 수정 시 공통 도메인 불변 조건 보장 (우선순위: P1)

상품 수정 후에도 상품은 유효한 상태를 유지해야 합니다. `Product.update()`는 생성자와 동일한 공통 불변 조건을 검증해야 합니다.

**우선순위 이유**: 생성 시 검증만 있어도 수정 시 잘못된 값으로 엔티티 상태가 깨질 수 있습니다. 생성과 수정은 동일한 도메인 불변 조건을 공유해야 합니다.

**독립적 테스트**: `Product.update()`에 유효하지 않은 값을 전달했을 때 product 도메인 예외가 발생하고 기존 상태가 의도치 않게 변경되지 않는지 검증합니다.

**승인 시나리오**:

1. **Given** 기존 상품이 있을 때, **When** 빈 상품명으로 수정하면, **Then** 상품 검증 예외가 발생합니다.
2. **Given** 기존 상품이 있을 때, **When** 0 이하 가격으로 수정하면, **Then** 상품 검증 예외가 발생합니다.
3. **Given** 기존 상품이 있을 때, **When** 빈 이미지 URL로 수정하면, **Then** 상품 검증 예외가 발생합니다.
4. **Given** 기존 상품이 있을 때, **When** 카테고리 없이 수정하면, **Then** 상품 검증 예외가 발생합니다.

---

### 사용자 시나리오 3 - 경로별 상품명 정책 유지 (우선순위: P2)

상품명 길이, 허용 문자, `카카오` 포함 여부 같은 정책은 현재처럼 service 계층의 `ProductNameValidator` 사용 정책을 유지합니다.

**우선순위 이유**: Admin 상품 화면은 `카카오` 포함 상품명을 허용하고, Product API는 담당 MD 협의가 필요한 상품명을 제한합니다. 이 정책은 요청 경로별 정책이므로 Product 엔티티의 공통 불변 조건으로 넣으면 admin/API 흐름이 충돌할 수 있습니다.

**독립적 테스트**: 기존 Product API 테스트와 Admin Product 테스트가 모두 통과하는지 확인합니다.

**승인 시나리오**:

1. **Given** Product API에서 `카카오` 포함 상품명이 전달될 때, **When** 상품 생성/수정 요청을 처리하면, **Then** 기존처럼 API 정책에 따라 검증 실패합니다.
2. **Given** Admin 상품 화면에서 `카카오` 포함 상품명이 전달될 때, **When** 상품 생성/수정 요청을 처리하면, **Then** 기존처럼 admin 정책에 따라 허용됩니다.

---

### 엣지 케이스

- Product 도메인 검증은 request DTO의 Bean Validation을 대체하지 않습니다.
- `ProductNameValidator`의 경로별 `allowKakao` 정책을 Product 엔티티로 옮기지 않습니다.
- Product 생성자와 `update()`는 동일한 공통 검증 규칙을 사용해야 합니다.
- `Product.update()` 검증 실패 시 기존 필드 값이 일부만 변경되는 상태가 없어야 합니다.
- Admin 상품 화면의 form validation 실패 flow는 기존처럼 view를 반환해야 합니다.
- Product API의 JSON error response 계약은 변경하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `Product` 생성자는 상품명 null/blank를 허용하지 않아야 합니다.
- **FR-002**: `Product` 생성자는 0 이하 가격을 허용하지 않아야 합니다.
- **FR-003**: `Product` 생성자는 이미지 URL null/blank를 허용하지 않아야 합니다.
- **FR-004**: `Product` 생성자는 null 카테고리를 허용하지 않아야 합니다.
- **FR-005**: `Product.update()`는 생성자와 동일한 공통 도메인 검증을 수행해야 합니다.
- **FR-006**: 검증 실패 시 product 도메인 예외를 발생시켜야 합니다.
- **FR-007**: 상품명 길이, 허용 문자, `카카오` 포함 여부 검증 정책은 기존 service 계층 흐름을 유지해야 합니다.
- **FR-008**: `ProductRequest`의 Bean Validation annotation은 유지해야 합니다.
- **FR-009**: Product 도메인 검증 단위 테스트를 추가해야 합니다.
- **FR-010**: 기존 Product API 및 Admin Product 테스트가 계속 통과해야 합니다.

### 주요 엔티티

- **Product**: 상품의 이름, 가격, 이미지 URL, 카테고리를 가지며 생성/수정 시 공통 불변 조건을 직접 검증합니다.
- **ProductValidationException**: Product 검증 실패를 표현하는 product 도메인 예외입니다.
- **ProductNameValidator**: 상품명 세부 정책을 검증하며, API/Admin 경로별 `allowKakao` 정책을 유지합니다.
- **ProductRequest**: HTTP 요청 DTO 검증을 담당하며 Product 도메인 검증과 별개로 유지됩니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `Product` 생성자에 유효하지 않은 값이 전달되면 `ProductValidationException`이 발생합니다.
- **SC-002**: `Product.update()`에 유효하지 않은 값이 전달되면 `ProductValidationException`이 발생합니다.
- **SC-003**: `Product.update()` 검증 실패 시 기존 상품 상태가 부분 변경되지 않습니다.
- **SC-004**: `ProductNameValidator.validate(name)`와 `ProductNameValidator.validate(name, true)` 호출 정책은 유지됩니다.
- **SC-005**: `./gradlew test --tests *Product* --tests *AdminProduct*`가 통과합니다.

## 가정사항

- Product API 예외 처리 구조는 `007-product-service-exception-refactor`에서 정리되었습니다.
- Admin Product service 분리와 예외 처리는 `008`, `009` 작업에서 진행되었습니다.
- 이번 작업은 Product 엔티티의 공통 불변 조건 강화에 집중합니다.
- DB 제약 추가는 별도 spec에서 다룹니다.
