# Research: Wish Exception Refactor

## Decision

중앙 집중식 예외 처리 패턴을 `category` 패키지와 일치하도록 `wish` 패키지로 확장합니다. `WishController`의 controller-level status handling을 제거하고, 도메인 예외를 서비스 계층에서 발생시켜 `GlobalExceptionHandler`에서 일관된 `ErrorResponse`로 변환합니다.

## Rationale

- 현재 category 패키지는 `CategoryService`에서 `CategoryNotFoundException`을 발생시키고 `GlobalExceptionHandler`가 이를 404로 매핑합니다.
- Wish 흐름은 controller 내부에서 401/403/404를 직접 반환하고 있어 예외 처리 패턴이 일관되지 않습니다.
- 동일한 예외 처리 아키텍처를 적용하면 유지보수성과 재사용성이 향상됩니다.
- 새로운 401/403/404 매핑을 표준화된 JSON 에러 형태로 통합하면 API 소비자가 예측 가능한 에러 계약을 사용할 수 있습니다.

## Alternatives considered

- `WishController`에서 현재 방식대로 상태 코드를 직접 반환하는 방식을 유지하기
  - 단점: 중복 로직 유지, 예외 처리 구조 불일치, 예외 기반 리팩토링 목표 미충족
- `WishService`에 `WishAddResult`/`WishRemoveResult` 패턴을 계속 사용하는 방식
  - 단점: 서비스 계층과 controller 계층의 책임이 혼재됨, category 패키지의 예외 패턴과 다른 흐름이 지속됨

## Final choice

- `gift.wish.exception` 패키지를 추가하고 Wish 도메인 예외를 정의합니다.
- `gift.global.exception.ErrorResponse`를 도입하여 일관된 에러 페이로드를 정의합니다.
- `GlobalExceptionHandler`를 확장하여 `WishNotFoundException`, `UnauthorizedWishAccessException`, `AuthenticationException` 및 일반 예외를 처리합니다.
- `WishController`는 인증 실패(`401`)를 예외로 변환하고, 서비스 계층은 도메인 검증 오류를 예외로 발생시킵니다.
