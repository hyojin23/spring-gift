# Data Model: 카카오 로그인 URL 구성 분리 리팩토링

## KakaoLoginUrlProvider

**역할**: 카카오 OAuth authorization URL을 생성합니다.

**입력 의존성**:

- `KakaoLoginProperties`

**출력**:

- `String authorizationUrl`

**구성 값**:

- authorize URI: `https://kauth.kakao.com/oauth/authorize`
- response_type: `code`
- client_id: `properties.clientId()`
- redirect_uri: `properties.redirectUri()`
- scope: `account_email,talk_message`

## KakaoAuthController

**역할**: 카카오 인증 HTTP endpoint를 제공합니다.

**변경 규칙**:

- `KakaoLoginProperties`에 직접 의존하지 않습니다.
- `UriComponentsBuilder`에 직접 의존하지 않습니다.
- `/login`은 provider가 반환한 URL을 `Location` header에 담아 302 응답합니다.
- `/callback`은 기존처럼 `KakaoAuthService.login(code)`를 호출합니다.

## KakaoLoginProperties

**역할**: 카카오 로그인 설정을 제공합니다.

**사용 값**:

- `clientId`
- `redirectUri`

**비사용 값**:

- `clientSecret`: 로그인 URL 생성에는 사용하지 않고 token 요청에서 사용합니다.
