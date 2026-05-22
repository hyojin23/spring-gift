# Research: 주문 알림 실패 로깅 리팩토링

## Decision 1: 실패는 계속 전파하지 않음

**Decision**: 카카오 메시지 발송 실패는 기존처럼 주문 흐름으로 전파하지 않는다.

**Rationale**: 카카오 메시지는 주문 생성의 부가 기능입니다. 메시지 발송 실패 때문에 주문 생성이 실패하면 핵심 비즈니스 흐름이 외부 알림 API에 종속됩니다.

**Alternatives considered**:

- **예외 전파**: 실패를 명확히 알 수 있지만 주문 생성 안정성을 해칩니다.
- **재시도 도입**: 운영성은 좋아질 수 있지만 이번 작업 범위를 넘어섭니다.

## Decision 2: warn 로그 추가

**Decision**: 메시지 발송 실패 시 `log.warn`으로 order id와 exception을 남긴다.

**Rationale**: 실패를 완전히 숨기면 운영 중 원인을 추적할 수 없습니다. warn 로그는 주문 흐름을 깨지 않으면서 관측성을 높입니다.

**Alternatives considered**:

- **debug 로그**: 운영 장애 후보를 보기 어렵습니다.
- **error 로그**: 주문 자체는 성공하므로 error는 과할 수 있습니다.

## Decision 3: access token은 로그 제외

**Decision**: 카카오 access token은 로그에 남기지 않는다.

**Rationale**: access token은 민감정보입니다. 장애 분석에는 order id와 exception 원인만으로도 충분합니다.

**Alternatives considered**:

- **member id 포함**: 도움이 될 수 있지만 현재 method에서 order id만으로도 추적 가능하고 범위를 작게 유지합니다.

## Decision 4: 로그 검증 테스트는 선택

**Decision**: 이번 작업에서는 로그 문자열 검증보다 기존 예외 미전파 테스트를 유지하는 것을 우선한다.

**Rationale**: 로그 검증은 구현 세부사항에 테스트를 강하게 묶을 수 있습니다. 핵심 계약은 “실패해도 주문 흐름에 예외를 전파하지 않는다”입니다.
