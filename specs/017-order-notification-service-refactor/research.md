# Research: Order 알림 서비스 분리 리팩토링

## 결정 1: `OrderNotificationService`를 order 패키지에 둔다

**Decision**: 새 서비스는 `gift.order.OrderNotificationService`로 둔다.

**Rationale**: 현재 알림은 주문 완료 카카오 메시지 하나뿐이며, 독립된 notification 도메인으로 분리하기에는 범위가 작습니다. order 패키지 내부 service로 시작하면 책임은 분리하면서도 구조는 단순하게 유지할 수 있습니다.

**Alternatives Considered**:

- `gift.notification` 패키지 생성: 확장 가능성은 있지만 현재 요구보다 구조가 큽니다.
- `KakaoMessageClient`를 `OrderService`에 유지: 클래스 수는 적지만 주문 핵심 로직과 알림 정책이 계속 섞입니다.

## 결정 2: best-effort 실패 무시 정책은 알림 서비스가 가진다

**Decision**: `OrderNotificationService`가 `KakaoMessageClient` 예외를 catch 하고 전파하지 않는다.

**Rationale**: 알림 실패는 주문 생성 실패가 아니며, 이 정책은 알림 책임에 속합니다. 주문 서비스는 알림이 best-effort라는 세부 정책을 직접 구현하지 않고 호출만 합니다.

**Alternatives Considered**:

- `OrderService`에서 try/catch 유지: 주문 서비스가 외부 알림 실패 정책을 계속 알게 됩니다.
- 예외를 전파하고 controller/global handler에서 처리: 외부 API 장애가 주문 실패로 보일 수 있어 기존 정책과 다릅니다.

## 결정 3: 비동기 처리와 retry는 이번 작업에 포함하지 않는다

**Decision**: 알림 호출은 기존처럼 동기 호출로 유지하고, 실패 시 조용히 종료합니다.

**Rationale**: 이번 작업의 목적은 책임 분리입니다. 비동기, retry, outbox 같은 안정성 패턴을 함께 넣으면 동작과 운영 복잡도가 크게 바뀝니다.

**Alternatives Considered**:

- `@Async` 적용: 응답 지연을 줄일 수 있지만 thread pool 설정과 테스트 복잡도가 증가합니다.
- retry 도입: 외부 API 장애에 더 강해지지만 중복 발송과 지연 문제가 생길 수 있습니다.

## 결정 4: 실패 로깅은 후속 작업으로 남긴다

**Decision**: 기존 `Exception ignored` 정책의 동작을 보존하고, 로깅 정책은 이번 범위에서 제외합니다.

**Rationale**: 로깅은 운영 정책과 민감 정보 마스킹 기준이 필요합니다. 지금은 behavior-preserving refactor가 목적이므로 실패를 전파하지 않는 기존 동작을 우선 유지합니다.

**Alternatives Considered**:

- warning log 추가: 운영 관측성은 좋아지지만 access token, payload 등 민감 정보 노출 기준을 먼저 정해야 합니다.
