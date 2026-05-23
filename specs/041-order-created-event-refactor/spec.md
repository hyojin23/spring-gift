# Feature Specification: Order Created Event Refactor

**Feature Branch**: `041-order-created-event-refactor`  
**Created**: 2026-05-23  
**Status**: Draft  
**Input**: 주문 생성 트랜잭션과 외부 카카오 알림 발송 책임 분리

## User Scenarios & Testing

### Primary User Story

사용자가 주문을 생성하면 재고 차감, 포인트 차감, 주문 저장, 위시리스트 정리는 하나의 주문 생성 트랜잭션 안에서 처리된다. 주문 저장이 성공적으로 커밋된 뒤에는 주문 생성 이벤트를 기반으로 카카오 알림 발송을 시도한다.

### Acceptance Scenarios

1. **Given** 유효한 주문 요청과 충분한 재고/포인트가 있을 때, **When** 주문을 생성하면, **Then** 주문 생성 트랜잭션은 성공하고 주문 생성 이벤트가 발행된다.
2. **Given** 주문 생성 이벤트가 커밋 이후 처리될 때, **When** 회원에게 카카오 액세스 토큰이 있으면, **Then** 카카오 메시지 발송을 시도한다.
3. **Given** 주문 생성 이벤트가 커밋 이후 처리될 때, **When** 회원에게 카카오 액세스 토큰이 없으면, **Then** 카카오 메시지 발송을 시도하지 않는다.
4. **Given** 주문 생성 중 재고 부족, 포인트 부족, 옵션 미존재 등으로 실패할 때, **When** 예외가 발생하면, **Then** 주문 생성 이벤트를 발행하지 않는다.
5. **Given** 주문 생성은 커밋되었고 카카오 메시지 발송이 실패할 때, **When** 알림 서비스에서 예외가 발생하면, **Then** 주문 생성 결과에는 영향을 주지 않고 실패 로그만 남긴다.

### Edge Cases

- 주문 저장 전 예외가 발생하면 이벤트가 발행되지 않아야 한다.
- 위시리스트 정리 중 예외가 발생하면 주문 트랜잭션은 실패하며 이벤트가 발행되지 않아야 한다.
- 이벤트 리스너는 트랜잭션 커밋 이후에만 알림 발송을 수행해야 한다.
- 이벤트 처리 실패는 이미 커밋된 주문 데이터를 롤백하지 않아야 한다.

## Requirements

### Functional Requirements

- **FR-001**: `OrderService`는 `OrderNotificationService`를 직접 호출하지 않고 주문 생성 완료 이벤트를 발행해야 한다.
- **FR-002**: 주문 생성 완료 이벤트는 주문 생성에 필요한 데이터 처리가 성공한 뒤 발행되어야 한다.
- **FR-003**: 이벤트 리스너는 `@TransactionalEventListener(phase = AFTER_COMMIT)` 방식으로 주문 생성 이벤트를 처리해야 한다.
- **FR-004**: 이벤트 리스너는 기존 `OrderNotificationService`에 카카오 알림 발송을 위임해야 한다.
- **FR-005**: 기존 카카오 알림 best effort 정책을 유지해야 한다. 즉, 알림 실패는 주문 생성 성공 여부에 영향을 주지 않는다.
- **FR-006**: 주문 생성 실패 경로에서는 이벤트가 발행되지 않아야 한다.
- **FR-007**: 기존 API 응답 형식과 HTTP 상태 코드는 변경하지 않아야 한다.

### Non-Functional Requirements

- **NFR-001**: 주문 생성 트랜잭션 안에서 외부 API 호출을 수행하지 않아야 한다.
- **NFR-002**: 이벤트 도입으로 인해 컨트롤러의 책임이 늘어나지 않아야 한다.
- **NFR-003**: 테스트는 주문 생성 이벤트 발행과 이벤트 리스너 위임을 각각 검증해야 한다.

## Out of Scope

- 카카오 메시지 재시도 큐 또는 비동기 메시지 브로커 도입
- 이벤트 저장소 또는 아웃박스 패턴 도입
- 카카오 메시지 템플릿 내용 변경
- 주문 생성 도메인 정책 변경
- 위시리스트 정리 정책 변경

## Success Criteria

- `OrderService`에서 `OrderNotificationService` 직접 의존성이 제거된다.
- 주문 생성 성공 시 `OrderCreatedEvent`가 발행된다.
- `OrderCreatedEventListener`가 커밋 이후 `OrderNotificationService`를 호출한다.
- 주문 생성 실패 시 이벤트가 발행되지 않는다.
- 전체 테스트가 통과한다.
