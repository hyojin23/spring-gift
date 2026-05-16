# Quickstart: Wish Exception Refactor

## 목표

`Wish` 도메인의 예외 처리 구조를 `category` 패키지와 동일한 중앙 집중식 예외 처리 패턴으로 리팩토링합니다.

## 주요 작업

1. `gift.wish.exception` 패키지를 생성합니다.
2. `WishNotFoundException`, `UnauthorizedWishAccessException`, `AuthenticationException` 같은 도메인 예외를 정의합니다.
3. `gift.global.exception.ErrorResponse`를 추가하여 표준 에러 페이로드를 정의합니다.
4. `GlobalExceptionHandler`를 확장하여 Wish 도메인 예외를 일관된 JSON 응답으로 매핑합니다.
5. `WishController`에서 401/403/404 직접 반환 로직을 제거하고 예외 기반 흐름으로 전환합니다.
6. `WishService`에서 도메인 검증 오류를 예외로 발생시킵니다.
7. 단위 및 통합 테스트를 작성하여 기존 Wish API 계약과 신규 에러 응답 형식을 검증합니다.

## 실행 방법

### 코드 변경 검증

- 전체 테스트 실행:
  - `./gradlew test`
- Wish 관련 테스트만 실행:
  - `./gradlew test --tests *Wish*`
- category 예외 패턴 회귀 확인:
  - `./gradlew test --tests *Category*`

### 문서

- 기능 명세서: `specs/001-wish-exception-refactor/spec.md`
- 계획 문서: `specs/001-wish-exception-refactor/plan.md`
- 설계 문서: `specs/001-wish-exception-refactor/data-model.md`
- 에러 계약: `specs/001-wish-exception-refactor/contracts/error-response.md`

## 검증 기준

- Wish API의 401/403/404 동작이 기존 계약과 일치하는지 확인합니다.
- 에러 응답이 모두 `ErrorResponse` 형태로 반환되는지 확인합니다.
- 기존 Wish 통합 테스트가 통과하는지 확인합니다.
