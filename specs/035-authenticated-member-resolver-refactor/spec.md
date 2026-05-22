# 기능 명세서: 인증 Member 추출 공통화 리팩토링

**Feature Branch**: `035-authenticated-member-resolver-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "인증된 Member 추출 공통화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 인증 필수 API의 Member 추출 정책 공통화 (우선순위: P1)

개발자는 인증이 필요한 controller에서 Authorization header를 직접 해석하거나 null member를 직접 검사하지 않고, 공통 resolver를 통해 인증된 `Member`를 받아야 합니다.

**우선순위 이유**: `OrderController`와 `WishController`는 같은 private `extractMember()` 로직을 가지고 있습니다. 인증 실패 시 `AuthenticationException`을 던지는 정책이 controller마다 반복되면 이후 정책 변경 시 여러 파일을 수정해야 합니다.

**독립적 테스트**: 새 `AuthenticatedMemberResolver`는 유효한 Authorization header에서 `Member`를 반환하고, 유효하지 않은 header/null header에서는 `AuthenticationException`을 던져야 합니다.

**승인 시나리오**:

1. **Given** 유효한 bearer token이 있을 때, **When** 공통 resolver로 member를 요청하면, **Then** 해당 member를 반환합니다.
2. **Given** 유효하지 않은 bearer token이 있을 때, **When** 공통 resolver로 member를 요청하면, **Then** `AuthenticationException`이 발생합니다.
3. **Given** Authorization header가 없을 때, **When** 공통 resolver로 member를 요청하면, **Then** `AuthenticationException`이 발생합니다.

---

### 사용자 시나리오 2 - OrderController 인증 처리 단순화 (우선순위: P1)

`OrderController`는 인증 실패 판단을 직접 하지 않고, 공통 resolver에서 반환한 인증된 `Member`만 사용해야 합니다.

**우선순위 이유**: 034 작업에서 order 인증 실패 응답은 global handler 기반으로 일관화되었습니다. 이제 controller 내부의 중복 인증 추출 로직을 제거할 수 있습니다.

**독립적 테스트**: 기존 `OrderControllerTest`의 인증 실패/성공 테스트가 모두 통과해야 합니다.

---

### 사용자 시나리오 3 - WishController 인증 처리 단순화 (우선순위: P1)

`WishController`도 인증 실패 판단을 직접 하지 않고, 공통 resolver에서 반환한 인증된 `Member`만 사용해야 합니다.

**우선순위 이유**: wish/order의 인증 필수 API가 같은 정책을 공유해야 합니다.

**독립적 테스트**: 기존 `WishControllerTest`의 인증 실패/성공 테스트가 모두 통과해야 합니다.

---

### 엣지 케이스

- `AuthenticationResolver.extractMember()`의 null 반환 정책은 유지합니다.
- 인증 실패 error code와 message는 기존 `AuthenticationException` 및 global handler 정책을 유지합니다.
- controller request/response body 형식은 변경하지 않습니다.
- JWT 토큰 파싱 정책은 변경하지 않습니다.
- 인증 선택 API가 생기더라도 이번 작업 범위에는 포함하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `AuthenticatedMemberResolver` 컴포넌트를 추가해야 합니다.
- **FR-002**: `AuthenticatedMemberResolver`는 `AuthenticationResolver.extractMember()`를 사용해야 합니다.
- **FR-003**: member가 null이면 `AuthenticationException`을 던져야 합니다.
- **FR-004**: member가 존재하면 해당 `Member`를 반환해야 합니다.
- **FR-005**: `OrderController`는 `AuthenticationResolver` 대신 `AuthenticatedMemberResolver`를 사용해야 합니다.
- **FR-006**: `WishController`는 `AuthenticationResolver` 대신 `AuthenticatedMemberResolver`를 사용해야 합니다.
- **FR-007**: `OrderController`의 private 인증 member 추출 method는 제거해야 합니다.
- **FR-008**: `WishController`의 private 인증 member 추출 method는 제거해야 합니다.
- **FR-009**: 인증 실패 응답은 기존처럼 401 `AUTH.UNAUTHORIZED`를 유지해야 합니다.
- **FR-010**: `AuthenticatedMemberResolverTest`를 추가해야 합니다.
- **FR-011**: order/wish controller 테스트는 기존 성공/실패 흐름을 유지해야 합니다.

### 주요 엔티티

- **AuthenticationResolver**: Authorization header에서 member를 찾아보고, 실패하면 null을 반환합니다.
- **AuthenticatedMemberResolver**: 인증 필수 API에서 member를 요구하고, 없으면 인증 예외를 던집니다.
- **AuthenticationException**: 인증 실패를 표현합니다.
- **OrderController**: 인증된 member로 주문 조회/생성을 처리합니다.
- **WishController**: 인증된 member로 위시 조회/추가/삭제를 처리합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `AuthenticatedMemberResolver`는 유효한 token에서 member를 반환합니다.
- **SC-002**: `AuthenticatedMemberResolver`는 invalid/null/blank Authorization header에서 `AuthenticationException`을 던집니다.
- **SC-003**: order/wish controller에 중복 private `extractMember()`가 남아 있지 않습니다.
- **SC-004**: order 인증 실패 응답은 401 `AUTH.UNAUTHORIZED`를 유지합니다.
- **SC-005**: wish 인증 실패 응답은 401 `AUTH.UNAUTHORIZED`를 유지합니다.
- **SC-006**: `./gradlew.bat test --tests *AuthenticatedMemberResolver* --tests *OrderController* --tests *WishController*`가 통과합니다.
- **SC-007**: 전체 테스트가 통과합니다.

## 가정사항

- `034-order-auth-exception-refactor`에서 order 인증 실패 응답은 global handler 기반으로 일관화되었습니다.
- `AuthenticationResolver`는 인증 선택 흐름에서 null 반환을 유지할 수 있으므로 이번 작업에서 API를 변경하지 않습니다.
- 인증 실패 예외 클래스는 기존 `gift.wish.exception.AuthenticationException`을 재사용합니다.
