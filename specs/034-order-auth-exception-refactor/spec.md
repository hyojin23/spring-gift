# 기능 명세서: Order 인증 예외 응답 일관화 리팩토링

**Feature Branch**: `034-order-auth-exception-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "OrderController 인증 실패 처리 일관화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 주문 목록 인증 실패 응답 일관화 (우선순위: P1)

인증되지 않은 사용자가 주문 목록을 조회하면, 다른 API와 동일하게 전역 예외 응답 형식으로 401 응답을 받아야 합니다.

**우선순위 이유**: `WishController`는 인증 실패 시 `AuthenticationException`을 던지고 global handler가 `AUTH.UNAUTHORIZED` 응답을 만듭니다. 반면 `OrderController`는 직접 빈 401 응답을 반환해서 API 에러 응답 형식이 다릅니다.

**독립적 테스트**: 유효하지 않은 Authorization header로 `GET /api/orders`를 호출하면 status 401과 `AUTH.UNAUTHORIZED` code가 반환되는지 검증합니다.

**승인 시나리오**:

1. **Given** 유효하지 않은 bearer token이 있을 때, **When** 주문 목록을 조회하면, **Then** 401과 `AUTH.UNAUTHORIZED` body를 반환합니다.
2. **Given** Authorization header가 없을 때, **When** 주문 목록을 조회하면, **Then** 401과 `AUTH.UNAUTHORIZED` body를 반환합니다.

---

### 사용자 시나리오 2 - 주문 생성 인증 실패 응답 일관화 (우선순위: P1)

인증되지 않은 사용자가 주문 생성을 요청하면, 전역 예외 응답 형식으로 401 응답을 받아야 합니다.

**우선순위 이유**: 주문 생성은 인증이 필수인 API이며, 실패 응답이 다른 인증 필요 API와 동일해야 클라이언트가 일관되게 처리할 수 있습니다.

**독립적 테스트**: 유효하지 않은 Authorization header로 `POST /api/orders`를 호출하면 status 401과 `AUTH.UNAUTHORIZED` code가 반환되는지 검증합니다.

---

### 사용자 시나리오 3 - 정상 주문 API 동작 유지 (우선순위: P2)

유효한 인증 정보가 있는 주문 목록 조회와 주문 생성 흐름은 기존과 동일하게 동작해야 합니다.

**우선순위 이유**: 이번 작업은 인증 실패 처리 방식만 바꾸는 리팩토링이며 정상 API 계약을 깨뜨리면 안 됩니다.

**독립적 테스트**: 기존 `OrderControllerTest`의 정상 조회/생성 테스트가 통과해야 합니다.

---

### 엣지 케이스

- invalid token은 `AuthenticationResolver`에서 null member로 해석될 수 있습니다.
- Authorization header가 없는 요청도 인증 실패로 처리합니다.
- error code는 기존 global handler의 `AUTH.UNAUTHORIZED`를 사용합니다.
- 주문 도메인/서비스 예외 처리는 변경하지 않습니다.
- 인증 공통 resolver 구조 변경은 이번 작업에 포함하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `OrderController`는 인증 실패 시 직접 빈 401 응답을 반환하지 않아야 합니다.
- **FR-002**: `OrderController`는 인증 실패 시 `AuthenticationException`을 던져야 합니다.
- **FR-003**: 주문 목록 조회 인증 실패는 401 `ErrorResponse`로 변환되어야 합니다.
- **FR-004**: 주문 생성 인증 실패는 401 `ErrorResponse`로 변환되어야 합니다.
- **FR-005**: 인증 실패 error code는 `AUTH.UNAUTHORIZED`여야 합니다.
- **FR-006**: 인증 실패 message는 기존 `AuthenticationException` 메시지를 사용해야 합니다.
- **FR-007**: `OrderController`의 반환 타입은 불필요한 wildcard 사용을 줄이고 실제 응답 타입에 맞게 정리해야 합니다.
- **FR-008**: 기존 정상 주문 조회/생성 응답은 유지해야 합니다.
- **FR-009**: `OrderControllerTest`는 인증 실패 응답 body를 검증해야 합니다.

### 주요 엔티티

- **OrderController**: 주문 API 요청을 받고 인증된 member를 주문 service에 전달합니다.
- **AuthenticationResolver**: Authorization header에서 member를 추출합니다.
- **AuthenticationException**: 인증 실패를 표현합니다.
- **GlobalExceptionHandler**: 인증 실패를 401 `ErrorResponse`로 변환합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `GET /api/orders` 인증 실패 응답은 401과 `AUTH.UNAUTHORIZED` body를 반환합니다.
- **SC-002**: `POST /api/orders` 인증 실패 응답은 401과 `AUTH.UNAUTHORIZED` body를 반환합니다.
- **SC-003**: `OrderController`에 직접 `ResponseEntity.status(401).build()`가 남아 있지 않습니다.
- **SC-004**: 기존 주문 조회/생성 성공 테스트가 통과합니다.
- **SC-005**: `./gradlew.bat test --tests *OrderController*`가 통과합니다.
- **SC-006**: 전체 테스트가 통과합니다.

## 가정사항

- `AuthenticationException`은 이미 global handler에서 401 `AUTH.UNAUTHORIZED`로 처리됩니다.
- `AuthenticationResolver.extractMember()`의 null 반환 정책은 이번 작업에서 변경하지 않습니다.
- 인증 추출 공통화는 후속 작업으로 분리합니다.
