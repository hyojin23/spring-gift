# Research: Order 도메인 검증 강화 리팩토링

## 결정 1: `Order` 생성자에서 필수 불변조건을 검증한다

**Decision**: `option`, `memberId`, `quantity`를 `Order` 생성자에서 검증합니다.

**Rationale**: API request 검증이나 service 로직이 있더라도 도메인 객체는 스스로 유효한 상태만 허용해야 합니다. 테스트 fixture, batch, 내부 호출 등 API 외부 경로에서 잘못된 주문이 만들어지는 것을 막을 수 있습니다.

**Alternatives Considered**:

- `OrderRequest` 검증만 유지: API 경로는 보호되지만 도메인 객체 직접 생성은 보호되지 않습니다.
- DB nullable/check 제약만 추가: 저장 시점에만 실패하고 도메인 객체 자체는 잘못된 상태를 가질 수 있습니다.

## 결정 2: 실패는 `OrderValidationException`으로 표현한다

**Decision**: 주문 도메인 검증 실패는 `OrderValidationException`을 던집니다.

**Rationale**: 기존 order 예외 계층이 있으므로 범용 `IllegalArgumentException`보다 의미가 명확합니다. Product/Member/Option 도메인 검증 리팩토링과도 일관됩니다.

**Alternatives Considered**:

- `IllegalArgumentException`: 단순하지만 어떤 도메인 정책 실패인지 구분하기 어렵습니다.
- 기존 `OrderOptionNotFoundException` 재사용: 옵션 미존재 조회 실패와 생성자 null 검증 실패는 다른 상황입니다.

## 결정 3: `message`는 검증하지 않는다

**Decision**: 주문 메시지는 기존처럼 `null` 또는 blank를 허용합니다.

**Rationale**: `OrderRequest.message`에 필수 제약이 없고, `KakaoMessageClient`도 null/blank 메시지를 선택값으로 처리합니다. 이번 작업은 필수 주문 생성 조건만 보호합니다.

**Alternatives Considered**:

- blank 메시지 금지: 기존 API 계약을 변경할 수 있습니다.
- 길이 제한 추가: DB 컬럼 길이와 API 정책을 함께 검토해야 하므로 별도 작업이 적절합니다.

## 결정 4: API 응답 핸들러 추가는 이번 범위에서 제외한다

**Decision**: `OrderValidationException`에 대한 `GlobalExceptionHandler` 매핑은 이번 작업에 포함하지 않습니다.

**Rationale**: 이번 검증은 내부 도메인 생성자 방어가 목적입니다. 실제 주문 API에서는 `OrderRequest`와 service 검증이 먼저 동작하므로 `OrderValidationException`이 외부 입력 응답으로 직접 노출될 가능성은 낮습니다. 필요하면 후속 order 예외 handler 정리로 다룹니다.

**Alternatives Considered**:

- 즉시 400 handler 추가: 일관성은 좋아지지만 현재 외부 API 시나리오에서 직접 필요한 변경은 아닙니다.
