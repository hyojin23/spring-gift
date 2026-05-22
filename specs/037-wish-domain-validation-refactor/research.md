# Research: Wish 도메인 검증 강화 리팩토링

## 결정 1: `Wish` 생성자에서 memberId/product null 검증

**결정**: `Wish` 생성자에서 memberId와 product가 null이면 `WishValidationException`을 던진다.

**이유**:

- Wish는 "어떤 회원이 어떤 상품을 찜했는가"를 표현하는 도메인이다.
- memberId 또는 product가 없으면 도메인 의미가 성립하지 않는다.
- service/controller를 거치지 않는 테스트나 내부 코드에서도 불변 조건이 지켜져야 한다.

**대안**:

- DTO/service에서만 검증: 도메인 객체 자체가 잘못된 상태를 허용한다.
- DB not null 제약만 추가: 런타임 예외가 persistence 시점까지 늦춰지고 메시지/응답 제어가 어렵다.

## 결정 2: `WishValidationException` 추가

**결정**: wish 도메인 검증 실패 전용 예외를 추가한다.

**이유**:

- 다른 도메인도 validation exception을 사용해 도메인 검증 실패를 명확히 표현하고 있다.
- global handler에서 `WISH.INVALID`로 일관되게 응답할 수 있다.

## 결정 3: memberId 양수 검증은 제외

**결정**: 이번 작업에서는 memberId null 검증만 한다.

**이유**:

- 현재 memberId는 primitive FK 역할을 하며 테스트 fixture에서 임의 id를 사용한다.
- 양수 검증까지 포함하면 기존 fixture 영향 범위가 커질 수 있다.
- 우선 null 검증으로 가장 명확한 불변 조건부터 강화한다.
