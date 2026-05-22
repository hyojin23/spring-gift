# Research: Order 인증 예외 응답 일관화 리팩토링

## 결정 1: 인증 실패는 `AuthenticationException`으로 표현

**결정**: `OrderController`에서 member 추출 결과가 null이면 `AuthenticationException`을 던진다.

**이유**:

- `WishController`가 이미 같은 정책을 사용하고 있다.
- global handler가 `AuthenticationException`을 401 `AUTH.UNAUTHORIZED`로 변환하고 있다.
- controller마다 직접 401 응답을 만들면 응답 body가 달라지고 중복이 생긴다.

**대안**:

- 기존처럼 빈 401 유지: 변경량은 적지만 API 에러 응답 형식이 불일치한다.
- `AuthenticationResolver`에서 바로 예외 던지기: 더 근본적이지만 wish/order/auth 테스트 영향 범위가 커진다.

## 결정 2: Authorization header는 optional로 받고 내부에서 인증 실패 처리

**결정**: order endpoint의 `@RequestHeader`를 `required = false`로 변경한다.

**이유**:

- header 누락도 인증 실패로 통일해서 `AUTH.UNAUTHORIZED` body를 반환할 수 있다.
- `AuthenticationResolver.extractMember(null)`은 이미 null을 반환하는 정책을 가진다.
- wish controller와 같은 형태가 된다.

## 결정 3: 공통 인증 추출 컴포넌트는 후속 작업으로 분리

**결정**: 이번 작업에서는 `OrderController` 내부 private method로만 정리한다.

**이유**:

- 이번 목적은 order 인증 실패 응답 일관화다.
- wish/order 공통화를 함께 하면 변경 범위가 커지고 테스트 영향도 커진다.
- 이후 `AuthenticatedMemberResolver` 같은 별도 spec으로 확장할 수 있다.
