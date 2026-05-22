# 기능 명세서: Wish 상품 미존재 예외 분리 리팩토링

**Feature Branch**: `036-wish-product-exception-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "WishService에서 위시에 추가할 상품 미존재 예외를 분리"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 위시에 추가할 상품이 없을 때 명확한 예외 응답 (우선순위: P1)

사용자가 존재하지 않는 상품을 위시에 추가하려고 하면, "위시 항목 미존재"가 아니라 "위시에 추가할 상품 미존재"를 나타내는 응답을 받아야 합니다.

**우선순위 이유**: 현재 `WishService.addWish()`는 `ProductRepository.findById(productId)`가 실패했을 때 `WishNotFoundException`을 던집니다. 하지만 이 상황은 기존 위시 항목이 없는 것이 아니라, 위시에 추가할 상품이 없는 것입니다. 예외 의미가 어긋나면 응답 code와 로그가 실제 원인을 설명하지 못합니다.

**독립적 테스트**: 존재하지 않는 productId로 `POST /api/wishes`를 호출하면 404와 `WISH.PRODUCT_NOT_FOUND` code가 반환되는지 검증합니다.

**승인 시나리오**:

1. **Given** 존재하지 않는 productId가 있을 때, **When** 위시 추가 API를 호출하면, **Then** 404와 `WISH.PRODUCT_NOT_FOUND` 응답을 반환합니다.
2. **Given** 존재하는 productId가 있을 때, **When** 위시 추가 API를 호출하면, **Then** 기존처럼 신규 생성 또는 기존 위시 응답을 반환합니다.

---

### 사용자 시나리오 2 - 기존 위시 미존재 예외 의미 유지 (우선순위: P1)

사용자가 존재하지 않는 wishId를 삭제하려고 하면 기존처럼 `WISH.NOT_FOUND` 응답을 받아야 합니다.

**우선순위 이유**: 이번 작업은 상품 미존재와 위시 미존재를 분리하는 것이며, 기존 위시 삭제 예외 응답을 바꾸면 안 됩니다.

**독립적 테스트**: 존재하지 않는 wishId로 `DELETE /api/wishes/{id}`를 호출하면 기존 404 `WISH.NOT_FOUND` 응답이 유지되는지 검증합니다.

---

### 사용자 시나리오 3 - WishService 예외 의미 명확화 (우선순위: P2)

개발자는 `WishService.addWish()`에서 상품 조회 실패와 위시 조회 실패를 서로 다른 예외 타입으로 구분할 수 있어야 합니다.

**우선순위 이유**: 예외 타입이 정확하면 service 테스트, 로그, global handler 매핑이 더 읽기 쉬워집니다.

**독립적 테스트**: `WishService.addWish()`에서 product 조회 실패 시 `WishProductNotFoundException`이 발생하는지 단위 테스트로 검증합니다.

---

### 엣지 케이스

- 기존 위시 삭제 미존재 응답은 `WISH.NOT_FOUND`를 유지합니다.
- 중복 위시 추가 시 기존 200 응답 정책은 변경하지 않습니다.
- 신규 위시 추가 시 기존 201 응답 정책은 변경하지 않습니다.
- product 패키지의 `ProductNotFoundException`은 이번 작업에서 사용하지 않습니다.
- DB 제약 조건 변경은 포함하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `WishProductNotFoundException`을 추가해야 합니다.
- **FR-002**: `WishProductNotFoundException`은 wish 예외 계층을 따라야 합니다.
- **FR-003**: `WishService.addWish()`는 상품 조회 실패 시 `WishProductNotFoundException`을 던져야 합니다.
- **FR-004**: `WishService.removeWish()`의 wish 조회 실패는 기존 `WishNotFoundException`을 유지해야 합니다.
- **FR-005**: global handler는 `WishProductNotFoundException`을 404 `ErrorResponse`로 변환해야 합니다.
- **FR-006**: 상품 미존재 error code는 `WISH.PRODUCT_NOT_FOUND`여야 합니다.
- **FR-007**: 기존 `WISH.NOT_FOUND` 응답은 유지해야 합니다.
- **FR-008**: `WishControllerTest`에 존재하지 않는 상품 위시 추가 실패 테스트를 추가해야 합니다.
- **FR-009**: `GlobalExceptionHandlerTest`에 wish 상품 미존재 예외 매핑 테스트를 추가해야 합니다.

### 주요 엔티티

- **WishService**: 위시 추가/삭제 비즈니스 흐름을 처리합니다.
- **ProductRepository**: 위시에 추가할 상품을 조회합니다.
- **WishProductNotFoundException**: 위시에 추가할 상품이 없음을 표현합니다.
- **WishNotFoundException**: 기존 위시 항목이 없음을 표현합니다.
- **GlobalExceptionHandler**: wish 예외를 표준 `ErrorResponse`로 변환합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: 존재하지 않는 productId로 위시 추가 시 404 `WISH.PRODUCT_NOT_FOUND`가 반환됩니다.
- **SC-002**: 존재하지 않는 wishId 삭제 시 기존 404 `WISH.NOT_FOUND`가 유지됩니다.
- **SC-003**: `WishService.addWish()`에서 상품 조회 실패 시 `WishProductNotFoundException`이 발생합니다.
- **SC-004**: `WishService.removeWish()`의 wish 조회 실패 예외 타입은 변경되지 않습니다.
- **SC-005**: `./gradlew.bat test --tests *Wish* --tests *GlobalExceptionHandler*`가 통과합니다.
- **SC-006**: 전체 테스트가 통과합니다.

## 가정사항

- 위시 추가 API는 인증된 member가 필요하며 인증 정책은 035 작업에서 공통 resolver로 정리되었습니다.
- 이번 작업은 wish 도메인의 예외 의미 정리에 집중합니다.
- 상품 미존재를 product 도메인 예외로 표현할 수도 있지만, API 응답 맥락은 wish 기능이므로 wish 예외로 분리합니다.
