# Data Model: JWT 토큰 예외 리팩토링

## JwtProvider

**역할**: JWT token 생성과 email subject 추출을 담당합니다.

**현재 필드**:

- `SecretKey key`: JWT 서명/검증 key
- `long expiration`: token 만료 시간 밀리초

**변경 규칙**:

- `createToken(email)` 정상 동작은 유지합니다.
- `getEmail(token)`은 유효 token이면 subject email을 반환합니다.
- `getEmail(token)`은 token이 null/blank이거나 파싱/검증에 실패하면 `JwtTokenException`을 던집니다.
- 원인이 되는 JJWT 예외는 cause로 보존합니다.

## JwtTokenException

**역할**: JWT token 파싱/검증 실패를 표현하는 auth 도메인 예외입니다.

**속성**:

- `message`: 사용자/로그에서 이해 가능한 한글 메시지
- `cause`: 원본 JJWT 또는 입력 검증 예외

**불변 조건**:

- token 실패는 `JwtProvider` 밖으로 JJWT 구현 예외 그대로 노출하지 않습니다.
- 메시지는 한글로 유지합니다.

## AuthenticationResolver

**역할**: Authorization header에서 인증된 `Member`를 찾습니다.

**변경 규칙**:

- Bearer token 추출 규칙은 유지합니다.
- `JwtProvider.getEmail()`이 `JwtTokenException`을 던지면 인증 실패로 보고 `Optional.empty()` 흐름을 유지합니다.
- public method `extractMember(String authorization)`은 기존처럼 실패 시 null을 반환합니다.
