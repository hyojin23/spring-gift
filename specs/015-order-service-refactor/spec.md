# 기능 명세서: Order 서비스 분리 리팩토링

**Feature Branch**: `015-order-service-refactor`  
**작성일**: 2026-05-19  
**상태**: 초안  
**입력**: "OrderService 분리 리팩토링"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 주문 목록 조회 비즈니스 로직의 서비스 계층 이동 (우선순위: P1)

주문 목록 조회에서 인증된 회원의 주문을 조회하는 로직은 `OrderService`에서 처리해야 합니다. `OrderController`는 인증 헤더에서 회원을 추출하고 service에 위임한 뒤 HTTP 응답을 반환해야 합니다.

**우선순위 이유**: 현재 `OrderController`가 repository를 직접 사용해 주문 목록을 조회합니다. controller 책임을 줄이고 주문 조회 로직을 테스트 가능한 service 계층으로 이동해야 합니다.

**독립적 테스트**: 인증된 회원의 주문 목록 조회가 기존처럼 200 OK와 `OrderResponse` page를 반환하는지 검증합니다.

**승인 시나리오**:

1. **Given** 인증된 회원이 있을 때, **When** 주문 목록을 조회하면, **Then** 200 OK와 회원의 주문 목록을 반환합니다.
2. **Given** 인증되지 않은 요청일 때, **When** 주문 목록을 조회하면, **Then** 기존처럼 401 응답을 반환합니다.

---

### 사용자 시나리오 2 - 주문 생성 비즈니스 로직의 서비스 계층 이동 (우선순위: P1)

주문 생성 flow의 option 조회, 재고 차감, 포인트 차감, 주문 저장, 카카오 알림 발송은 `OrderService`에서 처리해야 합니다. `OrderController`는 request를 service에 위임하고 생성 응답만 구성해야 합니다.

**우선순위 이유**: 현재 `OrderController`는 주문 생성의 모든 비즈니스 단계를 직접 수행합니다. 이후 예외 처리, 위시 cleanup, 알림 분리를 진행하려면 먼저 service 경계를 만들어야 합니다.

**독립적 테스트**: 인증된 회원의 주문 생성 요청이 기존처럼 201 Created와 `OrderResponse`를 반환하고, 재고/포인트 차감이 수행되는지 검증합니다.

**승인 시나리오**:

1. **Given** 인증된 회원과 유효한 옵션이 있을 때, **When** 주문 생성 요청을 보내면, **Then** 201 Created와 주문 응답을 반환합니다.
2. **Then** 옵션 재고가 요청 수량만큼 차감됩니다.
3. **Then** 회원 포인트가 상품 가격과 수량에 따라 차감됩니다.
4. **Given** 인증되지 않은 요청일 때, **When** 주문 생성 요청을 보내면, **Then** 기존처럼 401 응답을 반환합니다.
5. **Given** 존재하지 않는 옵션 ID가 있을 때, **When** 주문 생성 요청을 보내면, **Then** 기존처럼 404 응답을 반환합니다.

---

### 사용자 시나리오 3 - 카카오 알림 best-effort 정책 유지 (우선순위: P2)

주문 생성 후 카카오 access token이 있는 회원에게 알림을 보내되, 알림 실패가 주문 생성 실패로 이어지면 안 됩니다.

**우선순위 이유**: 현재 `sendKakaoMessageIfPossible()`은 예외를 삼키는 best-effort 정책입니다. service로 이동하더라도 이 동작이 바뀌면 안 됩니다.

**독립적 테스트**: 카카오 메시지 발송 중 예외가 발생해도 주문 생성 응답이 성공하는지 검증합니다.

**승인 시나리오**:

1. **Given** 회원에게 카카오 access token이 없을 때, **When** 주문을 생성하면, **Then** 카카오 메시지를 보내지 않고 주문 생성은 성공합니다.
2. **Given** 카카오 메시지 발송이 실패할 때, **When** 주문을 생성하면, **Then** 주문 생성은 성공합니다.

---

### 사용자 시나리오 4 - OrderController 책임 축소 (우선순위: P2)

`OrderController`는 `OrderService`와 `AuthenticationResolver`만 사용해야 하며, 주문 생성에 필요한 repository나 외부 client를 직접 주입받지 않아야 합니다.

**우선순위 이유**: controller가 repository/client를 직접 알면 서비스 계층 분리 효과가 떨어집니다. HTTP layer와 business layer를 분리해야 후속 리팩토링이 쉬워집니다.

**독립적 테스트**: `OrderController`에 `OrderRepository`, `OptionRepository`, `MemberRepository`, `WishRepository`, `KakaoMessageClient` 직접 의존이 남지 않았는지 확인합니다.

---

### 엣지 케이스

- 이번 작업은 주문 동작을 유지하는 service 분리에 집중합니다.
- 주문 예외를 도메인 예외로 바꾸는 작업은 후속 spec에서 다룹니다.
- 위시 cleanup 구현은 후속 `order-wish-cleanup` spec에서 다룹니다.
- 카카오 알림 책임을 별도 notification service로 분리하는 작업은 후속 spec에서 다룹니다.
- 기존 401/404 응답 방식은 이번 작업에서 유지합니다.
- `WishRepository`는 실제 cleanup 구현 전까지 `OrderService`로 옮기지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `OrderService`를 추가하고 주문 목록 조회와 주문 생성 비즈니스 로직을 담당하게 해야 합니다.
- **FR-002**: `OrderController`는 `OrderService`와 `AuthenticationResolver`에만 의존해야 합니다.
- **FR-003**: `OrderController`는 인증 실패 시 기존처럼 401 응답을 반환해야 합니다.
- **FR-004**: `OrderController`는 존재하지 않는 옵션 주문 시 기존처럼 404 응답을 반환해야 합니다.
- **FR-005**: `OrderService.getOrders(memberId, pageable)`는 회원의 주문 목록을 `OrderResponse` page로 반환해야 합니다.
- **FR-006**: `OrderService.createOrder(member, request)`는 재고 차감, 포인트 차감, 주문 저장을 수행해야 합니다.
- **FR-007**: 주문 생성 성공 시 기존처럼 201 Created와 `OrderResponse` body를 반환해야 합니다.
- **FR-008**: 카카오 알림 발송 실패는 주문 생성 실패로 전파되지 않아야 합니다.
- **FR-009**: 주문 생성 flow에서 위시 cleanup은 이번 작업에서 구현하지 않아야 합니다.
- **FR-010**: Order controller/service 테스트를 추가해야 합니다.

### 주요 엔티티

- **OrderService**: 주문 목록 조회, 주문 생성, 재고 차감, 포인트 차감, 주문 저장, 카카오 알림 best-effort 호출을 담당합니다.
- **OrderController**: 인증된 회원 확인, service 호출, HTTP 응답 구성을 담당합니다.
- **OrderRepository**: 주문 조회/저장을 담당합니다.
- **OptionRepository**: 주문 대상 옵션 조회와 재고 변경 저장에 사용됩니다.
- **MemberRepository**: 포인트 차감 후 회원 저장에 사용됩니다.
- **KakaoMessageClient**: 주문 생성 후 best-effort 알림 발송에 사용됩니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `OrderController`에는 `OrderRepository`, `OptionRepository`, `MemberRepository`, `WishRepository`, `KakaoMessageClient` 직접 의존이 남지 않습니다.
- **SC-002**: 주문 목록 조회 성공/인증 실패 flow는 기존 응답을 유지합니다.
- **SC-003**: 주문 생성 성공/인증 실패/옵션 미존재 flow는 기존 응답을 유지합니다.
- **SC-004**: 주문 생성 시 옵션 재고와 회원 포인트가 차감됩니다.
- **SC-005**: 카카오 메시지 발송 실패는 주문 생성 실패로 이어지지 않습니다.
- **SC-006**: `./gradlew test --tests *Order* --tests *Member* --tests *Option*`가 통과합니다.

## 가정사항

- Member 포인트 예외 리팩토링은 `013-member-point-exception-refactor`에서 완료되었습니다.
- Option 수량 예외 리팩토링은 기존 option 작업에서 완료되었습니다.
- 이번 작업은 service 분리이며 예외 응답 표준화, 위시 cleanup, 알림 service 분리는 후속 작업으로 진행합니다.
