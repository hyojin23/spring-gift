# Research: Wish 상품 미존재 예외 분리 리팩토링

## 결정 1: `WishProductNotFoundException` 추가

**결정**: 위시에 추가할 상품이 없을 때 전용 `WishProductNotFoundException`을 사용한다.

**이유**:

- `WishNotFoundException`은 기존 위시 항목이 없을 때 쓰는 이름이다.
- `addWish()`에서 product 조회 실패를 같은 예외로 표현하면 원인이 흐려진다.
- API 응답 code도 `WISH.NOT_FOUND`보다 `WISH.PRODUCT_NOT_FOUND`가 더 구체적이다.

**대안**:

- `ProductNotFoundException` 재사용: product 도메인 관점에서는 자연스럽지만 wish API 응답 맥락과 code가 product로 바뀔 수 있다.
- 기존 `WishNotFoundException` 유지: 변경이 적지만 의미가 맞지 않는다.

## 결정 2: HTTP status는 404 유지

**결정**: 상품 미존재도 404로 응답한다.

**이유**:

- 요청한 product resource가 존재하지 않는 상황이다.
- 기존 위시 미존재와 status는 같지만 code를 분리해 원인을 구분할 수 있다.

## 결정 3: 기존 위시 미존재 응답은 변경하지 않음

**결정**: `removeWish()`에서 wishId 조회 실패는 계속 `WishNotFoundException`과 `WISH.NOT_FOUND`를 사용한다.

**이유**:

- 이번 작업의 목적은 add wish의 product 조회 실패 의미를 바로잡는 것이다.
- 기존 API 계약을 불필요하게 변경하지 않는다.
