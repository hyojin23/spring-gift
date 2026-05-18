# Research: Order 서비스 분리 리팩토링

## Decision 1: 인증 헤더 처리는 controller에 유지한다

**Decision**: `AuthenticationResolver.extractMember(authorization)` 호출은 `OrderController`에 남기고, `OrderService`는 인증된 `Member`를 입력으로 받습니다.

**Rationale**: 인증 헤더는 HTTP layer의 관심사입니다. service는 인증된 회원 기준의 주문 작업을 처리하도록 두면 책임이 분명합니다.

**Alternatives considered**:

- service가 authorization header를 받음: service가 HTTP header를 알게 되어 layer 경계가 흐려집니다.
- 인증 예외 표준화까지 포함: 이번 작업의 service 분리 범위를 넘어섭니다.

## Decision 2: 기존 401/404 응답 계약을 유지한다

**Decision**: 인증 실패는 401 empty body, 옵션 미존재는 404 empty body를 유지합니다.

**Rationale**: 이번 작업은 behavior-preserving service 분리입니다. 예외 표준화는 후속 spec에서 다루는 편이 안전합니다.

**Alternatives considered**:

- order 도메인 예외 추가와 global handler 매핑: 의미는 좋지만 변경 범위가 큽니다.
- option 예외 재사용: 응답 계약이 바뀔 수 있습니다.

## Decision 3: 주문 생성 결과는 service에서 OrderResponse로 반환한다

**Decision**: `OrderService.createOrder`는 저장된 주문을 `OrderResponse`로 변환해 반환합니다.

**Rationale**: controller는 HTTP status와 location header만 구성하면 됩니다. response 변환은 주문 service의 결과 계약으로 두어 controller를 단순하게 합니다.

**Alternatives considered**:

- service가 Order 엔티티 반환: controller가 도메인 엔티티 변환 책임을 다시 갖습니다.
- service가 ResponseEntity 반환: service가 HTTP layer를 알게 됩니다.

## Decision 4: 카카오 알림 best-effort 정책은 service로 이동하되 유지한다

**Decision**: 카카오 access token이 있을 때만 메시지를 보내고, 발송 실패 예외는 주문 생성 실패로 전파하지 않습니다.

**Rationale**: 기존 controller의 `sendKakaoMessageIfPossible` 동작을 유지해야 합니다. 다만 주문 생성 비즈니스 흐름의 일부 후처리이므로 service로 이동합니다.

**Alternatives considered**:

- 알림 service 별도 분리: 더 깔끔하지만 이번 service 분리보다 한 단계 후속 작업이 적합합니다.
- 예외 전파: 주문 성공 여부가 외부 메시지 발송 안정성에 의존하게 됩니다.

## Decision 5: 위시 cleanup은 이번 작업에서 구현하지 않는다

**Decision**: `WishRepository` 미사용 문제와 주문 후 위시 삭제는 후속 spec에서 다룹니다.

**Rationale**: controller에는 cleanup 주석이 있지만 실제 동작은 없습니다. service 분리와 동시에 기능을 추가하면 behavior-preserving 리팩토링이 아니게 됩니다.
