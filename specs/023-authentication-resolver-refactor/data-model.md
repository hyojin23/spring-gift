# Data Model: AuthenticationResolver 토큰 파싱 리팩토링

## AuthenticationResolver

Authorization header를 인증된 회원으로 변환합니다.

### Public API

```java
Member extractMember(String authorization)
```

### 유지 계약

- 인증 성공 시 `Member`를 반환합니다.
- 인증 실패 시 `null`을 반환합니다.

### 추가 private method

```java
private Optional<String> extractBearerToken(String authorization)
private Optional<Member> findMemberByToken(String token)
```

### 규칙

- `authorization == null`이면 `Optional.empty()`입니다.
- `authorization.isBlank()`이면 `Optional.empty()`입니다.
- `authorization`이 `"Bearer "`로 시작하지 않으면 `Optional.empty()`입니다.
- Bearer prefix가 있으면 prefix 뒤 token만 반환합니다.
- JWT 파싱 실패는 회원 조회 없이 `Optional.empty()`입니다.
- 회원이 없으면 `Optional.empty()`입니다.

## JwtProvider

JWT token에서 email subject를 추출합니다.

### 유지 사항

- public API는 변경하지 않습니다.
- token이 유효하면 email을 반환합니다.
- token이 유효하지 않으면 JWT 관련 예외 또는 `IllegalArgumentException`이 발생할 수 있습니다.

## MemberRepository

email로 회원을 조회합니다.

### 사용 메서드

```java
Optional<Member> findByEmail(String email)
```

### 규칙

- JWT email 추출이 성공한 경우에만 호출합니다.
