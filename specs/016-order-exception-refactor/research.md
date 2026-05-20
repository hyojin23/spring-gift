# Research: Order 예외 처리 리팩토링

## 결정 1: 옵션 미존재는 `OrderOptionNotFoundException`으로 표현한다

**Decision**: `OrderService.createOrder()`에서 옵션 조회 결과가 없으면 `OrderOptionNotFoundException`을 던진다.

**Rationale**: 주문 생성 대상 옵션이 없다는 것은 주문 생성 도메인의 실패 상황입니다. `Optional.empty()`로 표현하면 controller가 실패 원인을 알아야 하고, service의 성공 반환 타입도 불필요하게 복잡해집니다.

**Alternatives Considered**:

- `Optional.empty()` 유지: 기존 동작 변경은 적지만 controller 분기가 남고 실패 의미가 약합니다.
- `OptionNotFoundException` 재사용: option 관리 API의 옵션 조회 실패와 주문 생성 대상 옵션 미존재의 API code를 구분하기 어렵습니다.

## 결정 2: `OrderService.createOrder()`는 `OrderResponse`를 직접 반환한다

**Decision**: 성공 시 `OrderResponse`를 반환하고 실패는 도메인 예외로 표현한다.

**Rationale**: 주문 생성은 성공하면 주문 응답이 반드시 존재합니다. 실패는 예외 타입으로 명확히 분리하면 controller가 정상 응답 구성에 집중할 수 있습니다.

**Alternatives Considered**:

- `Optional<OrderResponse>` 유지: 실패 원인을 표현하지 못하고 controller가 404 변환 책임을 계속 가집니다.
- custom result 타입 도입: 현재 실패 종류가 예외 처리 체계로 충분히 표현되므로 과합니다.

## 결정 3: member/option 예외는 order 예외로 감싸지 않는다

**Decision**: 포인트 부족은 `InsufficientMemberPointException`, 재고 부족은 `OptionQuantityException`을 그대로 전파하고 `GlobalExceptionHandler`에서 처리한다.

**Rationale**: 포인트와 재고 정책은 각각 member/option 도메인의 책임입니다. order service가 이 예외를 다시 order 예외로 감싸면 원래 정책 출처가 흐려집니다.

**Alternatives Considered**:

- 모든 주문 생성 실패를 `OrderException` 하위 타입으로 변환: API code는 단순해지지만 도메인 경계가 흐려집니다.
- controller에서 try/catch 처리: controller 책임이 다시 커집니다.

## 결정 4: 옵션 미존재 응답은 404 + `ErrorResponse`로 변경한다

**Decision**: 기존 404 status는 유지하되, body는 `ErrorResponse.of("ORDER.OPTION_NOT_FOUND", message)`로 제공한다.

**Rationale**: status만 있는 404보다 API 클라이언트가 실패 원인을 안정적으로 분기할 수 있습니다. 기존 전역 핸들러가 대부분 code/message 형태를 사용하므로 일관성도 높아집니다.

**Alternatives Considered**:

- body 없는 404 유지: 기존과 완전히 같지만 표준 오류 응답 체계와 어긋납니다.
- 400 반환: 요청 형식은 맞지만 참조 대상 리소스가 없으므로 404가 더 적절합니다.
