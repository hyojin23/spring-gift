# 기능 명세서: 카카오 로그인 예외 처리 리팩토링

**Feature Branch**: `028-kakao-login-exception-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "KakaoLoginClient 예외 처리 정리"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 카카오 token API 실패를 auth 도메인 예외로 변환 (우선순위: P1)

`KakaoLoginClient.requestAccessToken(code)`는 카카오 token API 호출이 실패하면 `RestClient` 구현 예외를 그대로 노출하지 않고 auth 도메인 예외로 변환해야 합니다.

**우선순위 이유**: 카카오 API 장애나 4xx/5xx 응답은 인증 도메인의 외부 연동 실패입니다. service/controller가 Spring HTTP client의 구체 예외에 직접 의존하지 않게 해야 합니다.

**독립적 테스트**: token API가 400 또는 500을 반환하면 `KakaoLoginException`이 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** 카카오 token API가 실패할 때, **When** access token을 요청하면, **Then** 카카오 로그인 도메인 예외가 발생합니다.

---

### 사용자 시나리오 2 - 카카오 user info API 실패를 auth 도메인 예외로 변환 (우선순위: P1)

`KakaoLoginClient.requestUserInfo(accessToken)`는 카카오 user info API 호출이 실패하면 auth 도메인 예외로 변환해야 합니다.

**우선순위 이유**: access token은 받았지만 사용자 정보 조회가 실패하는 경우도 로그인 실패입니다. token API 실패와 같은 예외 기준으로 다루면 service 흐름이 단순해집니다.

**독립적 테스트**: user info API가 401 또는 500을 반환하면 `KakaoLoginException`이 발생하는지 검증합니다.

---

### 사용자 시나리오 3 - 카카오 응답 body 누락을 명확한 예외로 처리 (우선순위: P1)

카카오 API가 성공 status를 반환하더라도 응답 body가 null이거나 필수 값이 비어 있으면 카카오 로그인 도메인 예외로 처리해야 합니다.

**우선순위 이유**: 현재는 null 응답이 뒤쪽 service에서 NPE로 터질 수 있습니다. 실패 원인이 “카카오 로그인 응답이 유효하지 않다”는 의미로 드러나야 합니다.

**독립적 테스트**:

- token 응답 body가 null이면 `KakaoLoginException`
- token 응답의 access token이 null/blank이면 `KakaoLoginException`
- user info 응답의 email이 null/blank이면 `KakaoLoginException`

---

### 사용자 시나리오 4 - 정상 카카오 로그인 흐름 유지 (우선순위: P1)

정상 token/user info 응답에서는 기존처럼 회원 조회/생성, 카카오 access token 저장, 서비스 JWT 발급이 유지되어야 합니다.

**우선순위 이유**: 이번 작업은 실패 처리 정리이며 정상 로그인 흐름을 변경하면 안 됩니다.

**독립적 테스트**: 기존 `KakaoAuthServiceTest`와 `KakaoLoginClientTest` 정상 케이스가 통과해야 합니다.

---

### 엣지 케이스

- 카카오 API URI, 요청 header, 요청 body 구성은 변경하지 않습니다.
- public method `requestAccessToken()`, `requestUserInfo()`는 유지합니다.
- 카카오 실패 메시지는 한글로 작성합니다.
- global handler 응답 매핑은 이번 작업 범위에 포함하지 않습니다.
- controller 응답 정책 변경은 후속 작업에서 별도로 다룹니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: auth 패키지에 카카오 로그인 실패를 표현하는 도메인 예외를 추가해야 합니다.
- **FR-002**: `requestAccessToken()`은 `RestClient` 호출 실패를 카카오 로그인 예외로 변환해야 합니다.
- **FR-003**: `requestUserInfo()`는 `RestClient` 호출 실패를 카카오 로그인 예외로 변환해야 합니다.
- **FR-004**: token API 응답 body가 null이면 카카오 로그인 예외로 처리해야 합니다.
- **FR-005**: token API 응답의 access token이 null/blank이면 카카오 로그인 예외로 처리해야 합니다.
- **FR-006**: user info API 응답 body가 null이면 카카오 로그인 예외로 처리해야 합니다.
- **FR-007**: user info API 응답의 email이 null/blank이면 카카오 로그인 예외로 처리해야 합니다.
- **FR-008**: 기존 정상 로그인 흐름은 유지해야 합니다.
- **FR-009**: 카카오 로그인 실패 케이스 테스트를 추가해야 합니다.

### 주요 엔티티

- **KakaoLoginClient**: 카카오 OAuth token API와 user info API 호출을 담당합니다.
- **KakaoAuthService**: 카카오 callback 로그인 use case를 수행합니다.
- **KakaoLoginException**: 카카오 로그인 실패를 표현하는 auth 도메인 예외입니다.
- **KakaoTokenResponse**: 카카오 token API 응답 DTO입니다.
- **KakaoUserResponse**: 카카오 user info API 응답 DTO입니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: 카카오 token API 4xx/5xx 실패는 `KakaoLoginException`으로 변환됩니다.
- **SC-002**: 카카오 user info API 4xx/5xx 실패는 `KakaoLoginException`으로 변환됩니다.
- **SC-003**: token 응답 body/access token 누락은 `KakaoLoginException`으로 처리됩니다.
- **SC-004**: user info 응답 body/email 누락은 `KakaoLoginException`으로 처리됩니다.
- **SC-005**: `./gradlew test --tests *KakaoLoginClient* --tests *KakaoAuth*`가 통과합니다.
- **SC-006**: 전체 테스트가 통과합니다.

## 가정사항

- `027-kakao-login-client-request-refactor`에서 카카오 요청 구성은 테스트로 고정되어 있습니다.
- 이번 작업은 예외 표현 정리이며 HTTP 응답 mapping은 변경하지 않습니다.
