# Data Model: JwtProvider 테스트 보강 리팩토링

## JwtProvider

JWT 생성과 email subject 추출을 담당합니다.

### Constructor

```java
JwtProvider(String secret, long expiration)
```

| Field | Type | Description |
|-------|------|-------------|
| secret | String | HMAC signing key material |
| expiration | long | token 유효 시간 milliseconds |

### Public API

```java
String createToken(String email)
String getEmail(String token)
```

### 테스트할 계약

- `createToken(email)`으로 생성한 token은 `getEmail(token)`으로 같은 email을 반환합니다.
- expiration이 지난 token은 `getEmail()`에서 실패합니다.
- malformed token은 `getEmail()`에서 실패합니다.
- 다른 secret으로 생성한 token은 검증에 실패합니다.
- null/blank token은 실패합니다.

## JWT Token

### Claims

| Claim | Source | Description |
|-------|--------|-------------|
| subject | email | 회원 이메일 |
| issuedAt | now | 발급 시각 |
| expiration | now + expiration | 만료 시각 |

### Signature

- `JwtProvider`의 secret으로 서명합니다.
- 다른 secret을 가진 provider는 검증할 수 없어야 합니다.
