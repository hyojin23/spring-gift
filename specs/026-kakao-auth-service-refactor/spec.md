# 기능 명세서: KakaoAuthController 서비스 분리 리팩토링

**Feature Branch**: `026-kakao-auth-service-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "KakaoAuthController 서비스 분리"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 카카오 로그인 redirect 동작 유지 (우선순위: P1)

사용자가 `/api/auth/kakao/login`을 호출하면 기존처럼 카카오 OAuth authorization URL로 302 redirect 되어야 합니다.

**우선순위 이유**: 로그인 시작 URL은 외부 동작입니다. 서비스 분리 중 redirect URL 파라미터가 바뀌면 실제 로그인 흐름이 깨질 수 있습니다.

**독립적 테스트**: login 요청 결과가 302이고 `Location` 헤더에 response_type, client_id, redirect_uri, scope가 포함되는지 검증합니다.

**승인 시나리오**:

1. **Given** 카카오 OAuth 설정이 있을 때, **When** `/login`을 호출하면, **Then** 카카오 authorization URL로 redirect 됩니다.

---

### 사용자 시나리오 2 - callback 인증 흐름을 서비스가 처리 (우선순위: P1)

`/api/auth/kakao/callback?code=...` 요청의 토큰 교환, 사용자 정보 조회, 회원 조회/생성, 카카오 access token 저장, 서비스 JWT 발급 흐름은 `KakaoAuthService`가 담당해야 합니다.

**우선순위 이유**: controller가 외부 HTTP 요청, persistence, JWT 발급을 모두 알면 테스트와 변경이 어려워집니다. callback 흐름은 인증 use case로 묶어 서비스에서 검증하는 편이 좋습니다.

**독립적 테스트**: service에 authorization code를 전달하면 Kakao access token을 요청하고, 사용자 email로 회원을 저장한 뒤 JWT token 문자열을 반환하는지 검증합니다.

**승인 시나리오**:

1. **Given** 신규 카카오 사용자 email이 있을 때, **When** callback code를 처리하면, **Then** 새 회원을 저장하고 서비스 JWT를 반환합니다.
2. **Given** 기존 회원 email이 있을 때, **When** callback code를 처리하면, **Then** 기존 회원의 카카오 access token을 갱신하고 서비스 JWT를 반환합니다.

---

### 사용자 시나리오 3 - controller는 HTTP 변환만 담당 (우선순위: P2)

`KakaoAuthController.callback()`은 request parameter code를 service에 전달하고, service가 반환한 token을 `TokenResponse`로 감싸 응답해야 합니다.

**우선순위 이유**: controller를 얇게 유지하면 인증 흐름 테스트는 service 단위에서 빠르게 검증하고, controller 테스트는 HTTP 응답 형태에 집중할 수 있습니다.

**독립적 테스트**: controller callback 요청 시 service가 호출되고 응답 body에 token이 담기는지 검증합니다.

---

### 엣지 케이스

- `/api/auth/kakao/login`의 redirect URL과 scope 값은 변경하지 않습니다.
- `/api/auth/kakao/callback`의 응답 형식 `TokenResponse`는 유지합니다.
- 회원이 없으면 기존처럼 email 기반 `Member`를 생성합니다.
- 회원이 있으면 기존 회원을 재사용하고 카카오 access token을 갱신합니다.
- 카카오 API 실패 예외 정책은 이번 작업에서 새로 정의하지 않고 기존 전파 동작을 유지합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `KakaoAuthService`를 추가해야 합니다.
- **FR-002**: `KakaoAuthService`는 authorization code로 카카오 access token을 요청해야 합니다.
- **FR-003**: `KakaoAuthService`는 카카오 access token으로 사용자 email을 조회해야 합니다.
- **FR-004**: `KakaoAuthService`는 email로 회원을 조회하고 없으면 생성해야 합니다.
- **FR-005**: `KakaoAuthService`는 회원의 카카오 access token을 갱신하고 저장해야 합니다.
- **FR-006**: `KakaoAuthService`는 저장된 회원 email로 서비스 JWT를 생성해 반환해야 합니다.
- **FR-007**: `KakaoAuthController.callback()`은 service 호출과 `TokenResponse` 생성만 담당해야 합니다.
- **FR-008**: `KakaoAuthController.login()`의 redirect 동작은 유지해야 합니다.
- **FR-009**: service 단위 테스트를 추가해야 합니다.
- **FR-010**: controller HTTP 동작을 검증하는 테스트를 추가하거나 기존 테스트가 있으면 갱신해야 합니다.

### 주요 엔티티

- **KakaoAuthController**: 카카오 로그인 API의 HTTP 요청/응답을 담당합니다.
- **KakaoAuthService**: 카카오 callback 인증 use case를 수행합니다.
- **KakaoLoginClient**: 카카오 token/userinfo API 호출을 담당합니다.
- **MemberRepository**: email 기준 회원 조회와 저장을 담당합니다.
- **JwtProvider**: 서비스 JWT 발급을 담당합니다.
- **TokenResponse**: callback 응답으로 반환하는 token DTO입니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `/api/auth/kakao/login`은 기존과 같은 카카오 authorization URL로 302 redirect 됩니다.
- **SC-002**: 신규 카카오 사용자 callback 처리 시 회원이 저장되고 JWT token이 반환됩니다.
- **SC-003**: 기존 카카오 사용자 callback 처리 시 기존 회원의 카카오 access token이 갱신되고 JWT token이 반환됩니다.
- **SC-004**: controller callback은 service가 반환한 token을 `TokenResponse`로 응답합니다.
- **SC-005**: `./gradlew test --tests *KakaoAuth*`가 통과합니다.
- **SC-006**: 전체 테스트가 통과합니다.

## 가정사항

- 카카오 API 호출 실패와 외부 client 예외 처리는 후속 spec에서 별도로 다룹니다.
- 이번 작업은 책임 분리 리팩토링이며 API 경로, HTTP status, 응답 body 구조는 변경하지 않습니다.
