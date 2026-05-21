# Data Model: KakaoAuthController 서비스 분리 리팩토링

## KakaoAuthController

**역할**: 카카오 인증 HTTP endpoint를 제공합니다.

**유지 책임**:

- `/api/auth/kakao/login` redirect 응답 생성
- `/api/auth/kakao/callback` request parameter 수신
- service 반환 token을 `TokenResponse`로 변환

**제거할 책임**:

- 카카오 access token 요청
- 카카오 사용자 정보 조회
- 회원 조회/생성/저장
- JWT 직접 발급

## KakaoAuthService

**역할**: 카카오 callback 인증 use case를 수행합니다.

**입력**:

- `String code`: 카카오 authorization code

**출력**:

- `String token`: 서비스 JWT token

**처리 규칙**:

- code로 카카오 access token을 요청합니다.
- access token으로 카카오 사용자 email을 조회합니다.
- email로 회원을 조회하고 없으면 생성합니다.
- 회원의 카카오 access token을 갱신합니다.
- 회원을 저장합니다.
- 저장된 회원 email로 서비스 JWT를 생성해 반환합니다.

## KakaoLoginClient

**역할**: 카카오 OAuth API 호출을 담당합니다.

**사용 메서드**:

- `requestAccessToken(String code)`
- `requestUserInfo(String accessToken)`

## Member

**역할**: 서비스 회원입니다.

**관련 동작**:

- 신규 카카오 사용자는 email 기반 member로 생성됩니다.
- 기존 회원은 재사용됩니다.
- `updateKakaoAccessToken()`으로 카카오 access token을 저장합니다.

## TokenResponse

**역할**: callback HTTP 응답 DTO입니다.

**규칙**:

- controller에서만 생성합니다.
- service는 `TokenResponse`에 의존하지 않습니다.
