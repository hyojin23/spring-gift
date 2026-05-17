# Research: Admin Product 서비스 분리 리팩토링

## Decision 1: AdminProductService를 ProductService와 분리

**Decision**: Admin 화면용 service로 `AdminProductService`를 추가합니다.

**Rationale**: `ProductService`는 JSON API 응답 DTO와 Product API error contract를 중심으로 설계되어 있습니다. Admin 화면은 `Product`, `Category`, form model, view/redirect 흐름이 필요하므로 별도 service를 두는 편이 책임이 선명합니다.

**Alternatives considered**:

- `ProductService` 재사용: DTO 변환과 API 예외 계약이 admin form 흐름과 섞일 수 있습니다.
- controller에 repository 접근 유지: 구조 개선 효과가 작고 테스트하기 어렵습니다.

## Decision 2: Form model 조립은 controller에 유지

**Decision**: `populateNewForm`, `populateEditForm` 같은 model attribute 조립은 controller에 유지합니다.

**Rationale**: model attribute는 view rendering 요구사항에 가깝습니다. service가 `Model`을 알게 되면 web layer와 강하게 결합됩니다.

**Alternatives considered**:

- service에서 `Model`을 직접 채우기: service가 Spring MVC view layer에 의존하게 됩니다.
- form model DTO 추가: 가능하지만 현재 화면 규모에서는 과한 추상화입니다.

## Decision 3: Admin error page 설계 제외

**Decision**: 상품/카테고리 미존재 상황의 HTML error page 설계는 이번 작업에서 다루지 않습니다.

**Rationale**: 이번 작업의 목적은 service 분리와 기존 정상/form validation flow 유지입니다. not found 화면 UX는 별도 controller advice나 error template 정책이 필요합니다.

**Alternatives considered**:

- Product API 예외를 admin에도 그대로 사용: `RestControllerAdvice`가 JSON `ErrorResponse`를 반환할 수 있어 HTML 화면 UX와 맞지 않습니다.
- Admin 전용 exception handler 추가: 좋은 후속 작업이지만 이번 범위를 넓힙니다.

## Decision 4: ProductNameValidator의 admin 규칙 유지

**Decision**: Admin 상품명 검증은 기존처럼 `ProductNameValidator.validate(name, true)`를 사용합니다.

**Rationale**: Admin 화면에서는 `카카오` 포함 상품명을 허용하던 기존 동작을 유지해야 합니다.
