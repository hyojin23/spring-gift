# 기능 명세서: Wish 도메인 검증 강화 리팩토링

**Feature Branch**: `037-wish-domain-validation-refactor`  
**작성일**: 2026-05-23  
**상태**: 초안  
**입력**: "Wish 도메인 검증 강화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - Wish 생성 시 memberId 필수 검증 (우선순위: P1)

`Wish`는 생성될 때 memberId가 null이면 생성되지 않아야 합니다.

**우선순위 이유**: Wish는 특정 회원의 위시 항목을 표현합니다. memberId 없이 생성된 Wish는 소유자를 알 수 없어 접근 권한 검증과 조회 로직이 깨질 수 있습니다.

**독립적 테스트**: `new Wish(null, product)` 호출 시 `WishValidationException`이 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** memberId가 null일 때, **When** Wish를 생성하면, **Then** wish 검증 예외가 발생합니다.

---

### 사용자 시나리오 2 - Wish 생성 시 product 필수 검증 (우선순위: P1)

`Wish`는 생성될 때 product가 null이면 생성되지 않아야 합니다.

**우선순위 이유**: Wish는 특정 상품을 찜한 항목입니다. product 없이 생성된 Wish는 응답 변환, 위시 중복 확인, 주문 후 정리 정책에서 의미가 없습니다.

**독립적 테스트**: `new Wish(memberId, null)` 호출 시 `WishValidationException`이 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** product가 null일 때, **When** Wish를 생성하면, **Then** wish 검증 예외가 발생합니다.

---

### 사용자 시나리오 3 - Wish 검증 예외 응답 일관화 (우선순위: P2)

Wish 도메인 검증 실패는 global handler에서 400 `ErrorResponse`로 변환되어야 합니다.

**우선순위 이유**: option/product/member/order/category 도메인 검증 예외처럼 wish 도메인 검증도 API 에러 응답 형식을 맞춰야 합니다.

**독립적 테스트**: `GlobalExceptionHandler`가 `WishValidationException`을 400 `WISH.INVALID` 응답으로 변환하는지 검증합니다.

---

### 사용자 시나리오 4 - 기존 WishService 테스트 fixture 보정 (우선순위: P2)

기존 테스트에서 권한 검증을 위해 product가 null인 Wish를 생성하던 fixture는 유효한 product를 가진 Wish로 바뀌어야 합니다.

**우선순위 이유**: 도메인 검증이 강화되면 테스트 fixture도 실제 도메인 불변 조건을 만족해야 합니다.

**독립적 테스트**: `WishServiceTest`의 권한 예외 테스트가 유효한 Wish fixture로도 동일하게 통과해야 합니다.

---

### 엣지 케이스

- memberId의 존재 여부를 DB에서 확인하는 로직은 이번 작업에 포함하지 않습니다.
- product의 존재 여부는 `WishService.addWish()`에서 repository 조회로 검증합니다.
- productId 중복 정책은 변경하지 않습니다.
- 기존 위시 상품 미존재 예외 `WISH.PRODUCT_NOT_FOUND`는 변경하지 않습니다.
- JPA 기본 생성자는 유지합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `WishValidationException`을 추가해야 합니다.
- **FR-002**: `WishValidationException`은 wish 예외 계층을 따라야 합니다.
- **FR-003**: `Wish` 생성자는 memberId null을 허용하지 않아야 합니다.
- **FR-004**: `Wish` 생성자는 product null을 허용하지 않아야 합니다.
- **FR-005**: 검증 실패 시 `WishValidationException`을 던져야 합니다.
- **FR-006**: global handler는 `WishValidationException`을 400 `ErrorResponse`로 변환해야 합니다.
- **FR-007**: wish 검증 실패 error code는 `WISH.INVALID`여야 합니다.
- **FR-008**: `WishTest`를 추가해야 합니다.
- **FR-009**: `WishServiceTest`의 null product fixture는 유효한 product fixture로 변경해야 합니다.
- **FR-010**: 기존 wish controller/service 테스트는 통과해야 합니다.

### 주요 엔티티

- **Wish**: 회원이 상품을 찜한 도메인 객체입니다.
- **WishValidationException**: Wish 도메인 검증 실패를 표현합니다.
- **WishService**: Wish 생성/삭제 흐름을 처리합니다.
- **GlobalExceptionHandler**: Wish 검증 예외를 HTTP 응답으로 변환합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: memberId가 null인 Wish 생성은 실패합니다.
- **SC-002**: product가 null인 Wish 생성은 실패합니다.
- **SC-003**: 정상 memberId와 product가 있으면 Wish 생성에 성공합니다.
- **SC-004**: Wish 검증 예외는 400과 `WISH.INVALID` body로 변환됩니다.
- **SC-005**: 기존 wish 상품 미존재/위시 미존재/권한 예외 테스트가 통과합니다.
- **SC-006**: `./gradlew.bat test --tests *Wish* --tests *GlobalExceptionHandler*`가 통과합니다.
- **SC-007**: 전체 테스트가 통과합니다.

## 가정사항

- `036-wish-product-exception-refactor`에서 위시에 추가할 상품 미존재 예외는 `WishProductNotFoundException`으로 분리되었습니다.
- 이번 작업은 Wish 도메인 생성 시점의 null 검증에 집중합니다.
- memberId 양수 검증은 현재 id 정책과 fixture 영향을 더 확인한 뒤 후속 작업으로 분리합니다.
