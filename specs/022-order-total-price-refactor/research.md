# Research: Order 총액 계산 책임 분리 리팩토링

## 결정 1: private method로 분리한다

**Decision**: 주문 총액 계산은 `OrderService` 내부 `calculateTotalPrice(Option option, int quantity)` private method로 분리합니다.

**Rationale**: 현재 계산은 `OrderService.createOrder()`에서만 사용됩니다. 별도 service나 value object를 만들기에는 정책이 단순하므로 private method가 가장 작은 변경입니다.

**Alternatives Considered**:

- 계산식 inline 유지: 클래스 수는 그대로지만 주문 생성 흐름에서 결제 총액 의미가 덜 드러납니다.
- `OrderPricingService` 추가: 후속 할인/쿠폰 정책이 있을 때는 좋지만 현재는 과합니다.
- `Order`에 총액 계산 추가: `Order`는 현재 `Product` 가격을 직접 보관하지 않고 option을 통해 접근하므로 service flow에서 계산하는 편이 변경 폭이 작습니다.

## 결정 2: 계산식과 타입은 유지한다

**Decision**: 기존 `option.getProduct().getPrice() * quantity` 계산식과 `int` 타입을 유지합니다.

**Rationale**: 이번 작업은 리팩토링이며 가격 정책 변경이 아닙니다. 타입을 `long`으로 바꾸거나 overflow 정책을 추가하면 별도 검토가 필요합니다.

**Alternatives Considered**:

- `long`으로 전환: 안정성은 좋아질 수 있지만 `Member.deductPoint(int)` 등 주변 계약 변경이 필요합니다.
- 금액 value object 도입: 과도한 구조 변경입니다.

## 결정 3: 기존 테스트로 계산 결과를 고정한다

**Decision**: 기존 `OrderServiceTest.createOrder()`의 포인트 차감 검증을 유지합니다.

**Rationale**: 해당 테스트는 상품 가격 1,000원, 수량 2개 주문 후 회원 포인트가 10,000원에서 8,000원으로 줄어드는지 확인합니다. 총액 계산이 기존과 동일하다는 충분한 회귀 테스트입니다.

**Alternatives Considered**:

- private method 직접 테스트: private method는 구현 세부사항이므로 public behavior인 주문 생성 결과로 검증하는 것이 좋습니다.
- 별도 pricing 테스트 추가: 별도 pricing 객체가 없으므로 현재는 필요하지 않습니다.
