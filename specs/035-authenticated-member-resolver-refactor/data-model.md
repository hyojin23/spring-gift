# Data Model: 인증 Member 추출 공통화 리팩토링

## AuthenticationResolver

### 책임

- Authorization header에서 bearer token을 추출합니다.
- JWT token에서 email을 얻습니다.
- email로 member를 조회합니다.
- 실패 시 null을 반환합니다.

### 변경 여부

- 이번 작업에서는 변경하지 않습니다.

## AuthenticatedMemberResolver

### 책임

- 인증 필수 API에서 사용할 `Member`를 반환합니다.
- `AuthenticationResolver.extractMember()` 결과가 null이면 `AuthenticationException`을 던집니다.

### 메서드

```java
public Member resolve(String authorization)
```

### 입력

- `authorization`: nullable Authorization header

### 출력

- 인증된 `Member`

### 예외

- `AuthenticationException`: Authorization header가 없거나 유효하지 않아 member를 찾을 수 없을 때 발생합니다.

## OrderController

### 변경 전

- `AuthenticationResolver`를 주입받습니다.
- private `extractMember()`에서 null을 검사합니다.

### 변경 후

- `AuthenticatedMemberResolver`를 주입받습니다.
- 공통 resolver가 반환한 member를 사용합니다.
- private `extractMember()`를 제거합니다.

## WishController

### 변경 전

- `AuthenticationResolver`를 주입받습니다.
- private `extractMember()`에서 null을 검사합니다.

### 변경 후

- `AuthenticatedMemberResolver`를 주입받습니다.
- 공통 resolver가 반환한 member를 사용합니다.
- private `extractMember()`를 제거합니다.

## 관계

```text
OrderController / WishController
  -> AuthenticatedMemberResolver.resolve()
  -> AuthenticationResolver.extractMember()
  -> AuthenticationException on missing member
```
