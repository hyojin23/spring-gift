# Data Model: 카카오 로그인 예외 처리 리팩토링

## KakaoLoginException

**역할**: 카카오 로그인 외부 연동 실패와 응답 이상을 표현하는 auth 도메인 예외입니다.

**속성**:

- `message`: 한글 실패 메시지
- `cause`: 원본 `RestClientException` 등 원인 예외

**규칙**:

- 카카오 API 호출 실패는 이 예외로 변환합니다.
- 카카오 응답 body 또는 필수 값 누락도 이 예외로 처리합니다.

## KakaoLoginClient

**역할**: 카카오 token API와 user info API 호출을 담당합니다.

**변경 규칙**:

- `requestAccessToken(code)`은 성공 시 `KakaoTokenResponse`를 반환합니다.
- token API 호출 실패 또는 body null은 `KakaoLoginException`을 던집니다.
- `requestUserInfo(accessToken)`은 성공 시 `KakaoUserResponse`를 반환합니다.
- user info API 호출 실패 또는 body null은 `KakaoLoginException`을 던집니다.

## KakaoAuthService

**역할**: 카카오 callback 로그인 use case를 수행합니다.

**변경 규칙**:

- access token이 null/blank이면 `KakaoLoginException`을 던집니다.
- email이 null/blank이면 `KakaoLoginException`을 던집니다.
- 유효한 응답이면 기존처럼 회원 저장과 JWT 발급을 수행합니다.

## KakaoTokenResponse

**필수 값**:

- `accessToken`

## KakaoUserResponse

**필수 값**:

- `kakaoAccount.email`
