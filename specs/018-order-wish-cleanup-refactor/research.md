# Research: Order 위시리스트 정리 리팩토링

## 결정 1: 주문 성공 시 위시리스트에서 자동 삭제한다

**Decision**: 주문 저장이 성공하면 주문 상품을 회원 위시리스트에서 제거합니다.

**Rationale**: 위시리스트는 구매 전 관심 상품 목록입니다. 사용자가 실제로 주문한 상품이 계속 남아 있으면 목록 의미가 흐려지고, 구매 완료 후에도 같은 상품을 다시 추천하는 것처럼 보일 수 있습니다.

**Alternatives Considered**:

- 위시리스트를 그대로 둔다: 동작은 단순하지만 구매 완료 후 관심 목록이 정리되지 않습니다.
- 사용자가 직접 삭제하게 한다: 사용자에게 불필요한 후속 작업을 남깁니다.

## 결정 2: 위시가 없으면 조용히 무시한다

**Decision**: `findByMemberIdAndProductId()` 결과가 empty이면 삭제를 수행하지 않고 주문 성공을 유지합니다.

**Rationale**: 사용자는 위시리스트를 거치지 않고도 주문할 수 있습니다. 위시가 없는 것은 예외 상황이 아니라 정상 경로입니다.

**Alternatives Considered**:

- 위시가 없으면 예외 처리: 정상 주문 경로를 불필요하게 실패시킵니다.
- 삭제 쿼리만 직접 실행: 단순할 수 있으나 현재 repository에 조회 메서드가 이미 있고 테스트에서 삭제 여부를 명확히 검증하기 쉽습니다.

## 결정 3: 주문 실패 시 위시리스트를 변경하지 않는다

**Decision**: 옵션 미존재, 재고 부족, 포인트 부족 등 주문 생성이 실패하면 wish cleanup을 수행하지 않습니다.

**Rationale**: 주문이 실패한 상품은 여전히 사용자의 관심 상품일 수 있습니다. 실패했는데 위시가 사라지면 사용자 의도가 훼손됩니다.

**Alternatives Considered**:

- 옵션 조회 후 바로 cleanup: 이후 재고/포인트 실패 시 위시가 삭제되는 문제가 있습니다.
- 주문 요청 시작 시 cleanup: 요청 실패와 관계없이 위시가 삭제되어 사용자 경험이 나빠집니다.

## 결정 4: 삭제 기준은 `memberId + productId`로 한다

**Decision**: 주문한 옵션의 상품 ID와 주문 회원 ID를 기준으로 wish를 조회하고 삭제합니다.

**Rationale**: 위시리스트는 상품 단위로 관리됩니다. 주문은 옵션 단위지만, 사용자의 관심 대상은 상품이므로 `option.product.id`를 기준으로 cleanup해야 합니다.

**Alternatives Considered**:

- `memberId + optionId`: wish가 상품 단위라 repository와 도메인 모델에 맞지 않습니다.
- wish ID를 request에 포함: 주문 요청이 wish 경로를 반드시 알 필요가 없고 API coupling이 커집니다.

## 결정 5: 별도 cleanup service는 만들지 않는다

**Decision**: 이번 작업에서는 `OrderService` 내부 private method로 cleanup을 구현합니다.

**Rationale**: 정책이 작고 주문 성공 후처리에 가깝습니다. 별도 service를 만들면 의존성은 분리되지만 구조가 커집니다.

**Alternatives Considered**:

- `OrderWishCleanupService` 추가: 후처리 정책이 커지거나 여러 곳에서 재사용될 때는 유효하지만 현재는 과합니다.
