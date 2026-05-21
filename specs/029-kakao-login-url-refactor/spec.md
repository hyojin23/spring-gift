# 기능 명세서: 카카오 로그인 URL 구성 분리 리팩토링

**Feature Branch**: `029-kakao-login-url-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "KakaoAuthController 로그인 URL 구성 분리"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 카카오 로그인 URL 생성 책임 분리 (우선순위: P1)

`KakaoAuthController.login()`은 카카오 authorization URL을 직접 조립하지 않고 별도 컴포넌트가 생성한 URL로 redirect 해야 합니다.

**우선순위 이유**: controller가 `response_type`, `client_id`, `redirect_uri`, `scope`, 카카오 authorize URI를 모두 알면 HTTP adapter와 카카오 OAuth 정책이 섞입니다. URL 생성 책임을 분리하면 controller는 redirect 응답만 담당합니다.

**독립적 테스트**: URL 생성 컴포넌트가 기존과 같은 카카오 authorization URL을 생성하는지 검증합니다.

**승인 시나리오**:

1. **Given** 카카오 로그인 설정이 있을 때, **When** 로그인 URL을 생성하면, **Then** 기존과 같은 authorize URI와 query parameter를 포함합니다.

---

### 사용자 시나리오 2 - `/login` redirect 동작 유지 (우선순위: P1)

`/api/auth/kakao/login` 요청은 기존처럼 302 status와 `Location` header를 반환해야 합니다.

**우선순위 이유**: 로그인 시작 endpoint는 외부 클라이언트가 사용하는 계약입니다. 내부 URL 생성 책임을 분리해도 redirect 결과는 바뀌면 안 됩니다.

**독립적 테스트**: controller login 요청 시 URL 생성 컴포넌트가 반환한 URL이 `Location` header로 설정되는지 검증합니다.

---

### 사용자 시나리오 3 - callback 흐름 영향 없음 (우선순위: P2)

`/callback`은 기존처럼 `KakaoAuthService.login(code)`를 호출하고 `TokenResponse`를 반환해야 합니다.

**우선순위 이유**: 이번 작업은 로그인 URL 구성 분리입니다. callback 인증 use case에는 영향을 주지 않아야 합니다.

**독립적 테스트**: 기존 `KakaoAuthControllerTest.callback()`이 변경 없이 통과해야 합니다.

---

### 엣지 케이스

- 카카오 authorize URI는 기존 값을 유지합니다.
- `response_type=code`는 유지합니다.
- `scope=account_email,talk_message`는 유지합니다.
- `client_id`, `redirect_uri`는 기존 `KakaoLoginProperties` 값을 사용합니다.
- callback service 흐름과 카카오 client 요청 구성은 변경하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: 카카오 로그인 authorization URL을 생성하는 별도 컴포넌트를 추가해야 합니다.
- **FR-002**: URL 생성 컴포넌트는 `KakaoLoginProperties`를 사용해야 합니다.
- **FR-003**: URL 생성 컴포넌트는 기존 authorize URI와 query parameter를 유지해야 합니다.
- **FR-004**: `KakaoAuthController`는 `KakaoLoginProperties`와 `UriComponentsBuilder` 직접 의존을 제거해야 합니다.
- **FR-005**: `KakaoAuthController.login()`은 생성된 URL을 `Location` header로 반환해야 합니다.
- **FR-006**: `KakaoAuthController.callback()` 동작은 변경하지 않아야 합니다.
- **FR-007**: URL 생성 컴포넌트 단위 테스트를 추가해야 합니다.
- **FR-008**: controller login redirect 테스트를 갱신해야 합니다.

### 주요 엔티티

- **KakaoAuthController**: 카카오 인증 HTTP endpoint를 제공합니다.
- **KakaoLoginUrlProvider**: 카카오 authorization URL 생성을 담당합니다.
- **KakaoLoginProperties**: clientId, clientSecret, redirectUri 설정을 제공합니다.
- **KakaoAuthService**: callback 로그인 use case를 수행합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: URL 생성 컴포넌트는 기존과 같은 카카오 authorize URL을 생성합니다.
- **SC-002**: `/api/auth/kakao/login`은 URL 생성 컴포넌트가 만든 URL로 302 redirect 합니다.
- **SC-003**: `/api/auth/kakao/callback` 응답 동작은 유지됩니다.
- **SC-004**: `./gradlew test --tests *KakaoAuth* --tests *KakaoLoginUrl*`가 통과합니다.
- **SC-005**: 전체 테스트가 통과합니다.

## 가정사항

- 카카오 callback 인증 흐름은 `026-kakao-auth-service-refactor`에서 service로 분리되었습니다.
- 이번 작업은 로그인 URL 구성 책임 분리이며 OAuth scope 변경은 포함하지 않습니다.
