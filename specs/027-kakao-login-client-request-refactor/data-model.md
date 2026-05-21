# Data Model: KakaoLoginClient 요청 구성 리팩토링

## KakaoLoginClient

**역할**: 카카오 OAuth token API와 user info API를 호출합니다.

**유지 public API**:

- `requestAccessToken(String code)`
- `requestUserInfo(String accessToken)`

**추가 내부 구성 요소**:

- `KAKAO_TOKEN_URI`: 카카오 token API endpoint
- `KAKAO_USER_INFO_URI`: 카카오 user info API endpoint
- `createAccessTokenRequestParams(String code)`: form body 구성
- `bearerToken(String accessToken)`: Authorization header 값 구성

## Access Token Request Params

**필드**:

- `grant_type`: `authorization_code`
- `client_id`: 카카오 REST API key
- `redirect_uri`: 카카오 redirect URI
- `code`: authorization code
- `client_secret`: 카카오 client secret

**규칙**:

- 기존 key 이름과 값 매핑을 유지합니다.
- form urlencoded body로 전송합니다.

## User Info Request Header

**필드**:

- `Authorization`: `Bearer {accessToken}`

**규칙**:

- 기존 Bearer prefix 형식을 유지합니다.

## Response Records

**KakaoTokenResponse**:

- `accessToken`: `access_token` JSON property에서 매핑됩니다.

**KakaoUserResponse**:

- `kakaoAccount`: `kakao_account` JSON property에서 매핑됩니다.
- `email()`은 `kakaoAccount.email()`을 반환합니다.
