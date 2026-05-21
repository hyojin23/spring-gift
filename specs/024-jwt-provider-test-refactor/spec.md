# 기능 명세서: JwtProvider 테스트 보강 리팩토링

**Feature Branch**: `024-jwt-provider-test-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "JwtProvider 테스트 추가"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 생성한 JWT에서 이메일을 추출 (우선순위: P1)

`JwtProvider.createToken(email)`로 생성한 토큰은 같은 `JwtProvider`의 `getEmail(token)`으로 email subject를 추출할 수 있어야 합니다.

**우선순위 이유**: JWT 생성과 파싱은 인증 흐름의 핵심입니다. 이 기본 동작이 테스트로 고정되어야 이후 토큰 예외 정리나 설정 변경을 안전하게 진행할 수 있습니다.

**독립적 테스트**: `"member@example.com"`으로 토큰을 생성한 뒤 `getEmail()` 결과가 같은 이메일인지 검증합니다.

**승인 시나리오**:

1. **Given** 유효한 email이 있을 때, **When** 토큰을 생성하고 다시 파싱하면, **Then** 같은 email이 반환됩니다.

---

### 사용자 시나리오 2 - 만료된 토큰은 파싱 실패 (우선순위: P1)

만료 시간이 지난 JWT는 `getEmail(token)`에서 실패해야 합니다.

**우선순위 이유**: 만료 검증은 JWT 인증의 기본 보안 정책입니다. expiration 설정이 정상적으로 적용되는지 테스트로 고정해야 합니다.

**독립적 테스트**: 음수 expiration으로 생성한 만료 토큰을 파싱하면 JWT 예외가 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** 이미 만료된 토큰이 있을 때, **When** email을 추출하면, **Then** JWT 파싱 예외가 발생합니다.

---

### 사용자 시나리오 3 - 잘못된 형식의 토큰은 파싱 실패 (우선순위: P1)

JWT 형식이 아닌 문자열은 `getEmail(token)`에서 실패해야 합니다.

**우선순위 이유**: 잘못된 token 값은 인증 실패로 이어져야 합니다. provider의 현재 실패 동작을 테스트로 고정해 resolver와의 계약을 명확히 합니다.

**독립적 테스트**: `"invalid-token"` 같은 값을 `getEmail()`에 전달하면 예외가 발생하는지 검증합니다.

---

### 사용자 시나리오 4 - 다른 secret으로 생성한 토큰은 검증 실패 (우선순위: P1)

서로 다른 secret을 사용하는 `JwtProvider`끼리는 토큰을 검증할 수 없어야 합니다.

**우선순위 이유**: 서명 검증은 JWT 보안의 핵심입니다. 잘못된 secret으로 검증이 성공하면 인증 위조 위험이 있습니다.

**독립적 테스트**: provider A가 만든 token을 provider B로 파싱하면 JWT 예외가 발생하는지 검증합니다.

---

### 사용자 시나리오 5 - null/blank token은 실패 (우선순위: P2)

`getEmail()`에 null 또는 blank token을 넘기면 실패해야 합니다.

**우선순위 이유**: resolver가 null/blank header를 먼저 거르더라도, provider의 잘못된 입력 동작을 테스트로 명확히 해두면 후속 예외 정리의 기준점이 됩니다.

---

### 엣지 케이스

- 이번 작업은 테스트 보강에 집중하며 `JwtProvider` production 코드는 변경하지 않습니다.
- JJWT 라이브러리 예외를 auth 도메인 예외로 감싸는 작업은 후속 spec에서 다룹니다.
- secret 길이 정책은 JJWT `Keys.hmacShaKeyFor()`의 현재 동작을 유지합니다.
- 테스트 secret은 HS256에 충분한 길이로 사용합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `JwtProviderTest`를 추가해야 합니다.
- **FR-002**: 생성한 token에서 email을 추출하는 테스트를 추가해야 합니다.
- **FR-003**: 만료된 token 파싱 실패 테스트를 추가해야 합니다.
- **FR-004**: 잘못된 형식의 token 파싱 실패 테스트를 추가해야 합니다.
- **FR-005**: 다른 secret으로 생성된 token 검증 실패 테스트를 추가해야 합니다.
- **FR-006**: null token 파싱 실패 테스트를 추가해야 합니다.
- **FR-007**: blank token 파싱 실패 테스트를 추가해야 합니다.
- **FR-008**: `JwtProvider` production 코드는 변경하지 않아야 합니다.

### 주요 엔티티

- **JwtProvider**: JWT 생성과 email subject 추출을 담당합니다.
- **JWT token**: email subject, issuedAt, expiration, signature를 포함합니다.
- **secret**: JWT 서명/검증에 사용하는 HMAC secret입니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: 생성한 token에서 동일한 email을 추출합니다.
- **SC-002**: 만료 token은 파싱 실패합니다.
- **SC-003**: 잘못된 형식의 token은 파싱 실패합니다.
- **SC-004**: 다른 secret으로 생성한 token은 검증 실패합니다.
- **SC-005**: null/blank token은 파싱 실패합니다.
- **SC-006**: `./gradlew test --tests *JwtProvider* --tests *AuthenticationResolver*`가 통과합니다.

## 가정사항

- `AuthenticationResolver` 토큰 파싱 정리는 `023-authentication-resolver-refactor`에서 완료되었습니다.
- 이번 작업은 테스트 보강이며 `InvalidTokenException` 같은 auth 도메인 예외 도입은 후속 작업으로 진행합니다.
