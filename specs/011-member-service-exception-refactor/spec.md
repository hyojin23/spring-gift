# 기능 명세서: Member API 서비스 및 예외 처리 리팩토링

**Feature Branch**: `011-member-service-exception-refactor`  
**작성일**: 2026-05-18  
**상태**: 초안  
**입력**: "member 패키지에서 Member API 서비스/예외 리팩토링"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - MemberController의 비즈니스 로직 서비스 계층 이동 (우선순위: P1)

회원가입과 로그인 API의 repository 접근, 중복 이메일 검증, 비밀번호 검증, 토큰 생성 로직은 `MemberService`에서 처리해야 합니다. `MemberController`는 HTTP 요청/응답 매핑에 집중하고 service를 호출해야 합니다.

**우선순위 이유**: 현재 `MemberController`가 repository와 JWT 생성 로직을 직접 처리해 controller 책임이 큽니다. Product API 리팩토링과 같은 구조로 정리하면 테스트와 예외 처리가 쉬워집니다.

**독립적 테스트**: 회원가입/로그인 성공 요청이 기존 status code와 `TokenResponse` body를 유지하는지 검증합니다.

**승인 시나리오**:

1. **Given** 등록되지 않은 이메일과 비밀번호가 있을 때, **When** 회원가입 API를 호출하면, **Then** 201 Created와 token 응답을 반환합니다.
2. **Given** 등록된 회원의 이메일과 올바른 비밀번호가 있을 때, **When** 로그인 API를 호출하면, **Then** 200 OK와 token 응답을 반환합니다.
3. **Given** `MemberController`가 생성될 때, **When** 의존성을 확인하면, **Then** `MemberService`와만 협력하고 `MemberRepository`, `JwtProvider`를 직접 사용하지 않습니다.

---

### 사용자 시나리오 2 - 회원가입 중복 이메일 예외를 도메인 예외로 표현 (우선순위: P1)

이미 등록된 이메일로 회원가입을 요청하면 범용 `IllegalArgumentException`이 아니라 member 도메인 예외가 발생해야 합니다.

**우선순위 이유**: 중복 이메일은 회원 도메인의 명확한 실패 상황입니다. 전용 예외를 사용하면 global handler에서 일관된 JSON error response로 변환할 수 있습니다.

**독립적 테스트**: 중복 이메일 회원가입 요청이 400 Bad Request와 member error response를 반환하는지 검증합니다.

**승인 시나리오**:

1. **Given** 이미 등록된 이메일이 있을 때, **When** 회원가입 API를 호출하면, **Then** 400 Bad Request를 반환합니다.
2. **Then** 응답 body는 `ErrorResponse` 형식이며 code는 `MEMBER.DUPLICATE_EMAIL`입니다.

---

### 사용자 시나리오 3 - 로그인 실패 예외를 도메인 예외로 표현 (우선순위: P1)

존재하지 않는 이메일 또는 잘못된 비밀번호로 로그인하면 범용 `IllegalArgumentException`이 아니라 member 인증 실패 예외가 발생해야 합니다.

**우선순위 이유**: 로그인 실패는 API에서 자주 발생하는 인증 실패 상황입니다. 응답 메시지는 보안상 이메일 미존재와 비밀번호 불일치를 구분하지 않고 동일하게 유지하는 편이 좋습니다.

**독립적 테스트**: 존재하지 않는 이메일과 잘못된 비밀번호 모두 동일한 401 Unauthorized error response를 반환하는지 검증합니다.

**승인 시나리오**:

1. **Given** 등록되지 않은 이메일이 있을 때, **When** 로그인 API를 호출하면, **Then** 401 Unauthorized와 `MEMBER.INVALID_CREDENTIALS` error response를 반환합니다.
2. **Given** 등록된 이메일이지만 비밀번호가 다를 때, **When** 로그인 API를 호출하면, **Then** 401 Unauthorized와 `MEMBER.INVALID_CREDENTIALS` error response를 반환합니다.

---

### 사용자 시나리오 4 - 기존 요청 검증과 API 계약 유지 (우선순위: P2)

`MemberRequest`의 Bean Validation은 유지하고, 잘못된 request body는 기존 Spring validation 흐름에 맡깁니다. 이번 작업은 controller 내부 비즈니스 예외와 service 분리에 집중합니다.

**우선순위 이유**: 요청 DTO 검증과 도메인/서비스 예외는 서로 다른 책임입니다. 리팩토링 중 API 성공 응답 계약과 request validation annotation을 깨뜨리면 안 됩니다.

**독립적 테스트**: 기존 회원가입/로그인 성공 flow와 DTO validation annotation이 유지되는지 확인합니다.

**승인 시나리오**:

1. `MemberRequest`의 `@NotBlank`, `@Email` annotation은 유지됩니다.
2. 회원가입 성공 응답은 기존처럼 201 Created와 `TokenResponse`입니다.
3. 로그인 성공 응답은 기존처럼 200 OK와 `TokenResponse`입니다.

---

### 엣지 케이스

- `MemberController`에 `MemberRepository` 직접 의존성이 남지 않아야 합니다.
- `MemberController`에 `JwtProvider` 직접 의존성이 남지 않아야 합니다.
- `MemberController`의 지역 `@ExceptionHandler(IllegalArgumentException.class)`는 제거되어야 합니다.
- 로그인 실패 응답은 이메일 미존재와 비밀번호 불일치 이유를 구분하지 않습니다.
- AdminMemberController의 HTML flow는 이번 작업 범위에 포함하지 않습니다.
- Member 도메인의 포인트 예외 리팩토링은 이번 작업 범위에 포함하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `MemberService`를 추가하고 회원가입과 로그인 비즈니스 로직을 담당하게 해야 합니다.
- **FR-002**: `MemberController`는 `MemberService`만 주입받도록 변경해야 합니다.
- **FR-003**: 중복 이메일 회원가입은 `DuplicateMemberEmailException`으로 표현해야 합니다.
- **FR-004**: 로그인 실패는 `InvalidMemberCredentialsException`으로 표현해야 합니다.
- **FR-005**: member 도메인 예외는 `GlobalExceptionHandler`에서 `ErrorResponse` JSON으로 변환해야 합니다.
- **FR-006**: 중복 이메일 응답은 400 Bad Request와 `MEMBER.DUPLICATE_EMAIL` code를 사용해야 합니다.
- **FR-007**: 로그인 실패 응답은 401 Unauthorized와 `MEMBER.INVALID_CREDENTIALS` code를 사용해야 합니다.
- **FR-008**: 회원가입/로그인 성공 응답의 status code와 `TokenResponse` body는 유지해야 합니다.
- **FR-009**: `MemberController`의 `IllegalArgumentException` handler를 제거해야 합니다.
- **FR-010**: Member API controller/service 테스트를 추가해야 합니다.

### 주요 엔티티

- **MemberService**: 회원가입, 로그인, 중복 이메일 검증, 비밀번호 검증, JWT 발급을 담당합니다.
- **MemberController**: HTTP 요청을 받아 service를 호출하고 `ResponseEntity<TokenResponse>`를 반환합니다.
- **MemberException**: member 도메인/API 예외의 기준 타입입니다.
- **DuplicateMemberEmailException**: 이미 등록된 이메일로 회원가입하는 상황을 표현합니다.
- **InvalidMemberCredentialsException**: 이메일 또는 비밀번호가 유효하지 않은 로그인 실패 상황을 표현합니다.
- **GlobalExceptionHandler**: member 예외를 표준 `ErrorResponse`로 변환합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `MemberController`는 `MemberService`만 의존합니다.
- **SC-002**: `MemberController`에는 `MemberRepository`, `JwtProvider` 직접 접근이 남지 않습니다.
- **SC-003**: `MemberController`에는 `@ExceptionHandler(IllegalArgumentException.class)`가 남지 않습니다.
- **SC-004**: 회원가입 중복 이메일 요청은 400 + `MEMBER.DUPLICATE_EMAIL` 응답을 반환합니다.
- **SC-005**: 로그인 실패 요청은 401 + `MEMBER.INVALID_CREDENTIALS` 응답을 반환합니다.
- **SC-006**: 회원가입/로그인 성공 flow는 기존 계약을 유지합니다.
- **SC-007**: `./gradlew test --tests *Member* --tests *GlobalExceptionHandlerTest*`가 통과합니다.

## 가정사항

- JWT 발급 방식은 변경하지 않습니다.
- 비밀번호 암호화는 이번 리팩토링 범위에 포함하지 않습니다.
- AdminMemberController 리팩토링은 후속 spec에서 다룹니다.
- Member 포인트 도메인 예외 리팩토링은 후속 spec에서 다룹니다.
