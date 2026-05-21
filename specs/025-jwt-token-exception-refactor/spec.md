# 기능 명세서: JWT 토큰 예외 리팩토링

**Feature Branch**: `025-jwt-token-exception-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "JwtProvider 토큰 예외 명확화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 잘못된 JWT는 auth 도메인 예외로 통일 (우선순위: P1)

`JwtProvider.getEmail(token)`은 만료, malformed, 서명 불일치, null/blank token 같은 파싱 실패를 JJWT 구현 예외 그대로 노출하지 않고 auth 도메인 예외로 변환해야 합니다.

**우선순위 이유**: 인증 토큰 실패는 애플리케이션의 인증 도메인 문제입니다. 외부 라이브러리 예외가 service/resolver 계층으로 직접 퍼지면 예외 처리 기준이 구현체에 묶입니다.

**독립적 테스트**: 만료 token, malformed token, 다른 secret token, null/blank token을 `getEmail()`에 전달하면 동일한 auth 도메인 예외가 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** 만료된 JWT가 있을 때, **When** 이메일을 추출하면, **Then** auth 도메인 토큰 예외가 발생합니다.
2. **Given** JWT 형식이 아닌 문자열이 있을 때, **When** 이메일을 추출하면, **Then** auth 도메인 토큰 예외가 발생합니다.
3. **Given** 다른 secret으로 서명된 JWT가 있을 때, **When** 이메일을 추출하면, **Then** auth 도메인 토큰 예외가 발생합니다.
4. **Given** null 또는 blank token이 있을 때, **When** 이메일을 추출하면, **Then** auth 도메인 토큰 예외가 발생합니다.

---

### 사용자 시나리오 2 - 유효한 JWT 동작은 유지 (우선순위: P1)

`JwtProvider.createToken(email)`로 생성한 유효한 token은 기존처럼 `getEmail(token)`에서 같은 email subject를 반환해야 합니다.

**우선순위 이유**: 이번 작업은 실패 예외 표현을 정리하는 리팩토링이며 정상 인증 흐름을 변경하면 안 됩니다.

**독립적 테스트**: 생성한 token에서 같은 email이 반환되는지 기존 테스트를 유지합니다.

---

### 사용자 시나리오 3 - AuthenticationResolver 인증 실패 계약 유지 (우선순위: P1)

`AuthenticationResolver`는 `JwtProvider`가 auth 도메인 토큰 예외를 던질 때 기존처럼 `null`을 반환해야 합니다.

**우선순위 이유**: 현재 controller들은 `extractMember()` 결과가 null이면 401을 반환합니다. 이번 작업은 내부 예외 타입을 정리하되 외부 API 응답 방식은 바꾸지 않습니다.

**독립적 테스트**: `jwtProvider.getEmail()`이 새 토큰 예외를 던지면 resolver가 null을 반환하고 회원 조회를 수행하지 않는지 검증합니다.

---

### 엣지 케이스

- `JwtProvider.getEmail()`의 성공 반환값은 변경하지 않습니다.
- `AuthenticationResolver.extractMember()`의 public API와 null 반환 정책은 유지합니다.
- controller의 401 처리 방식은 이번 작업에서 변경하지 않습니다.
- JJWT `JwtException`, `IllegalArgumentException` 등 구현체 예외는 `JwtProvider` 내부에서 auth 도메인 예외로 감쌉니다.
- 예외 메시지는 사용자에게 노출될 수 있으므로 한글로 작성합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: auth 패키지에 JWT 토큰 실패를 표현하는 도메인 예외를 추가해야 합니다.
- **FR-002**: `JwtProvider.getEmail()`은 JJWT 파싱 실패를 auth 도메인 예외로 변환해야 합니다.
- **FR-003**: `JwtProvider.getEmail()`은 null token과 blank token을 auth 도메인 예외로 처리해야 합니다.
- **FR-004**: `JwtProvider.getEmail()`의 유효 token email 추출 동작은 유지해야 합니다.
- **FR-005**: `AuthenticationResolver`는 새 auth 도메인 토큰 예외를 인증 실패로 처리해 null을 반환해야 합니다.
- **FR-006**: `AuthenticationResolver`는 broad catch를 다시 도입하지 않아야 합니다.
- **FR-007**: `JwtProviderTest`는 실패 케이스 기대 예외를 auth 도메인 예외로 변경해야 합니다.
- **FR-008**: `AuthenticationResolverTest`는 새 auth 도메인 토큰 예외 처리 케이스를 검증해야 합니다.

### 주요 엔티티

- **JwtProvider**: JWT 생성과 email subject 추출을 담당합니다.
- **Auth token exception**: JWT 파싱/검증 실패를 나타내는 auth 도메인 예외입니다.
- **AuthenticationResolver**: Authorization header를 인증된 회원으로 변환합니다.
- **JWT token**: email subject, issuedAt, expiration, signature를 포함합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: 유효 token은 기존처럼 email을 반환합니다.
- **SC-002**: 만료 token, malformed token, 다른 secret token은 auth 도메인 예외를 발생시킵니다.
- **SC-003**: null/blank token은 auth 도메인 예외를 발생시킵니다.
- **SC-004**: resolver는 auth 도메인 토큰 예외 발생 시 null을 반환합니다.
- **SC-005**: `./gradlew test --tests *JwtProvider* --tests *AuthenticationResolver*`가 통과합니다.
- **SC-006**: 전체 테스트가 통과합니다.

## 가정사항

- `024-jwt-provider-test-refactor`에서 `JwtProvider`의 현재 정상/실패 동작이 테스트로 고정되어 있습니다.
- 이번 작업은 JWT 토큰 파싱 실패 예외 표현을 정리하며, controller 응답 구조 변경은 포함하지 않습니다.
