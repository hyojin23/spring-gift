# 기능 명세서: KakaoLoginClient 요청 구성 리팩토링

**Feature Branch**: `027-kakao-login-client-request-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "KakaoLoginClient 요청 파라미터 구성 분리"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - access token 요청 파라미터 구성 분리 (우선순위: P1)

`KakaoLoginClient.requestAccessToken(code)`는 카카오 token API 호출 흐름을 보여주고, form 파라미터 구성은 별도 private method로 분리해야 합니다.

**우선순위 이유**: 현재 method 안에서 grant_type, client_id, redirect_uri, code, client_secret을 직접 조립하고 HTTP 요청까지 수행합니다. 요청 구성과 전송 책임을 분리하면 누락/변경 지점을 더 쉽게 확인할 수 있습니다.

**독립적 테스트**: 카카오 token API 요청 body에 기존과 같은 form 파라미터가 포함되는지 검증합니다.

**승인 시나리오**:

1. **Given** authorization code와 카카오 설정이 있을 때, **When** access token을 요청하면, **Then** 기존과 같은 form 파라미터로 카카오 token API를 호출합니다.

---

### 사용자 시나리오 2 - user info 요청 Authorization header 구성 분리 (우선순위: P2)

`KakaoLoginClient.requestUserInfo(accessToken)`는 사용자 정보 API 호출 흐름을 보여주고, Bearer authorization header 값 생성은 별도 private method로 분리해야 합니다.

**우선순위 이유**: `"Bearer " + accessToken` 문자열 조합도 카카오 요청 구성 규칙입니다. method로 분리하면 토큰 인증 헤더 형식이 명시됩니다.

**독립적 테스트**: 카카오 user info API 요청에 기존과 같은 `Authorization: Bearer {accessToken}` header가 포함되는지 검증합니다.

---

### 사용자 시나리오 3 - 외부 동작 유지 (우선순위: P1)

리팩토링 후에도 `KakaoAuthService`가 사용하는 `requestAccessToken()`과 `requestUserInfo()`의 public API와 응답 record는 변경되지 않아야 합니다.

**우선순위 이유**: 이번 작업은 내부 요청 구성 정리입니다. 카카오 인증 흐름의 service/controller 계약이 함께 바뀌면 변경 범위가 커집니다.

**독립적 테스트**: 기존 `KakaoAuthServiceTest`가 변경 없이 통과해야 합니다.

---

### 엣지 케이스

- 카카오 token API URI는 변경하지 않습니다.
- 카카오 user info API URI는 변경하지 않습니다.
- token 요청 content type은 form urlencoded로 유지합니다.
- `KakaoTokenResponse`, `KakaoUserResponse` record 구조는 변경하지 않습니다.
- 카카오 API 실패 예외 정책은 이번 작업에서 변경하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: access token 요청 form 파라미터 구성을 별도 private method로 분리해야 합니다.
- **FR-002**: user info 요청 Authorization header 값 생성을 별도 private method로 분리해야 합니다.
- **FR-003**: 카카오 token API URI와 user info API URI를 상수로 분리해야 합니다.
- **FR-004**: form urlencoded content type 표현은 Spring의 `MediaType` 또는 명확한 상수로 관리해야 합니다.
- **FR-005**: `requestAccessToken(String code)` public API는 유지해야 합니다.
- **FR-006**: `requestUserInfo(String accessToken)` public API는 유지해야 합니다.
- **FR-007**: `KakaoTokenResponse`, `KakaoUserResponse` record 구조는 유지해야 합니다.
- **FR-008**: 카카오 요청 구성을 검증하는 테스트를 추가해야 합니다.

### 주요 엔티티

- **KakaoLoginClient**: 카카오 OAuth token API와 user info API 호출을 담당합니다.
- **KakaoLoginProperties**: clientId, clientSecret, redirectUri 설정을 제공합니다.
- **KakaoTokenResponse**: 카카오 token API 응답 DTO입니다.
- **KakaoUserResponse**: 카카오 user info API 응답 DTO입니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: access token 요청 body에 grant_type, client_id, redirect_uri, code, client_secret이 포함됩니다.
- **SC-002**: access token 요청 content type은 form urlencoded입니다.
- **SC-003**: user info 요청은 `Authorization: Bearer {accessToken}` header를 포함합니다.
- **SC-004**: `./gradlew test --tests *KakaoLoginClient* --tests *KakaoAuth*`가 통과합니다.
- **SC-005**: 전체 테스트가 통과합니다.

## 가정사항

- `026-kakao-auth-service-refactor`에서 카카오 callback use case는 service로 분리되었습니다.
- 이번 작업은 `KakaoLoginClient` 내부 구조 리팩토링이며 API 응답 정책 변경은 포함하지 않습니다.
