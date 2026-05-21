# 기능 명세서: AuthenticationResolver 토큰 파싱 리팩토링

**Feature Branch**: `023-authentication-resolver-refactor`  
**작성일**: 2026-05-21  
**상태**: 초안  
**입력**: "AuthenticationResolver 토큰 파싱/예외 처리 정리"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - Bearer 토큰 추출 규칙 명시화 (우선순위: P1)

`AuthenticationResolver`는 `Authorization` 헤더가 `Bearer ` 형식일 때만 JWT 토큰을 추출해야 합니다. 단순 문자열 `replace()`가 아니라 Bearer prefix 검사를 명시적으로 수행해야 합니다.

**우선순위 이유**: 인증 헤더 형식은 인증 정책의 일부입니다. 현재는 `replace("Bearer ", "")`로 토큰을 꺼내기 때문에 Bearer 형식이 아닌 값도 JWT 파싱 단계까지 흘러갑니다.

**독립적 테스트**: `"Bearer token"` 형식이면 `JwtProvider.getEmail("token")`이 호출되는지 검증합니다.

**승인 시나리오**:

1. **Given** `Authorization` 헤더가 `"Bearer valid-token"`일 때, **When** 회원을 추출하면, **Then** `"valid-token"`으로 JWT 이메일을 조회합니다.
2. **Then** 이메일에 해당하는 회원이 있으면 해당 회원을 반환합니다.

---

### 사용자 시나리오 2 - 헤더 없음/blank/잘못된 prefix는 인증 실패로 처리 (우선순위: P1)

`Authorization` 헤더가 없거나 blank이거나 `Bearer ` prefix가 아니면 resolver는 기존처럼 `null`을 반환해야 합니다. 이 경우 JWT 파싱을 시도하지 않아야 합니다.

**우선순위 이유**: 인증 실패 상황은 정상적인 요청 실패 경로입니다. null/blank/prefix 오류를 명시적으로 처리하면 NPE를 예외 삼킴으로 숨기지 않고 코드 의도가 분명해집니다.

**독립적 테스트**: null, blank, `"Basic token"`, `"token"` 입력에서 `jwtProvider.getEmail()`이 호출되지 않고 null이 반환되는지 검증합니다.

**승인 시나리오**:

1. **Given** `Authorization` 헤더가 null일 때, **When** 회원을 추출하면, **Then** null을 반환합니다.
2. **Given** `Authorization` 헤더가 blank일 때, **When** 회원을 추출하면, **Then** null을 반환합니다.
3. **Given** `Authorization` 헤더가 Bearer 형식이 아닐 때, **When** 회원을 추출하면, **Then** null을 반환합니다.

---

### 사용자 시나리오 3 - JWT 파싱 실패는 인증 실패로 처리 (우선순위: P1)

Bearer token이 있더라도 JWT 파싱이 실패하면 resolver는 기존처럼 null을 반환해야 합니다. 다만 모든 `Exception`을 삼키지 않고 JWT 파싱 과정에서 발생할 수 있는 예외 범위를 명확히 처리해야 합니다.

**우선순위 이유**: 잘못된 토큰은 인증 실패입니다. 하지만 DB 장애나 예상치 못한 런타임 오류까지 인증 실패로 숨기면 문제 파악이 어려워집니다.

**독립적 테스트**: `jwtProvider.getEmail()`이 JWT 예외 또는 `IllegalArgumentException`을 던지면 null을 반환하는지 검증합니다.

**승인 시나리오**:

1. **Given** Bearer token이 있지만 JWT 파싱이 실패할 때, **When** 회원을 추출하면, **Then** null을 반환합니다.
2. **Then** 회원 repository 조회는 수행되지 않습니다.

---

### 사용자 시나리오 4 - 회원 미존재는 인증 실패로 유지 (우선순위: P1)

토큰이 유효하더라도 이메일에 해당하는 회원이 없으면 resolver는 기존처럼 null을 반환해야 합니다.

**우선순위 이유**: 기존 controller들은 `extractMember()` 결과가 null이면 401을 반환합니다. 이번 작업은 기존 외부 동작을 유지해야 합니다.

**독립적 테스트**: JWT에서 email을 얻었지만 `MemberRepository.findByEmail()`이 empty를 반환하면 null을 반환하는지 검증합니다.

---

### 엣지 케이스

- 기존 `extractMember(String authorization)` public API와 null 반환 정책을 유지합니다.
- 각 controller의 `member == null` 401 처리 방식은 변경하지 않습니다.
- 이번 작업은 인증 예외를 던지는 구조로 바꾸지 않습니다.
- Bearer prefix는 `"Bearer "`를 기준으로 합니다.
- DB repository 예외까지 인증 실패로 삼키는 broad catch는 제거합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `AuthenticationResolver.extractMember()` public API를 유지해야 합니다.
- **FR-002**: 인증 실패 시 기존처럼 null을 반환해야 합니다.
- **FR-003**: Bearer token 추출을 별도 private method로 분리해야 합니다.
- **FR-004**: `Authorization` 헤더가 null 또는 blank이면 null을 반환해야 합니다.
- **FR-005**: `Authorization` 헤더가 `"Bearer "`로 시작하지 않으면 null을 반환해야 합니다.
- **FR-006**: Bearer token이 추출된 경우에만 `JwtProvider.getEmail()`을 호출해야 합니다.
- **FR-007**: JWT 파싱 실패 또는 잘못된 token 값은 null 반환으로 처리해야 합니다.
- **FR-008**: JWT에서 얻은 email에 해당하는 회원이 없으면 null을 반환해야 합니다.
- **FR-009**: 모든 `Exception`을 잡는 broad catch를 제거해야 합니다.
- **FR-010**: `AuthenticationResolverTest`를 추가해야 합니다.

### 주요 엔티티

- **AuthenticationResolver**: Authorization header를 인증된 회원으로 변환합니다.
- **JwtProvider**: JWT token에서 email을 추출합니다.
- **MemberRepository**: email로 회원을 조회합니다.
- **Member**: 인증 성공 시 반환되는 회원입니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `"Bearer token"` 입력 시 token만 `JwtProvider.getEmail()`에 전달됩니다.
- **SC-002**: null/blank/non-Bearer header 입력 시 null을 반환하고 JWT 파싱을 시도하지 않습니다.
- **SC-003**: JWT 파싱 실패 시 null을 반환하고 회원 조회를 시도하지 않습니다.
- **SC-004**: 회원 미존재 시 null을 반환합니다.
- **SC-005**: 유효한 token과 존재하는 회원이면 해당 회원을 반환합니다.
- **SC-006**: `./gradlew test --tests *AuthenticationResolver*`와 기존 인증 사용 controller 테스트가 통과합니다.

## 가정사항

- 이번 작업은 내부 파싱/예외 처리 정리이며 controller 인증 실패 응답 방식은 변경하지 않습니다.
- 인증 실패 예외를 global handler로 던지는 구조는 후속 작업에서 별도로 검토합니다.
