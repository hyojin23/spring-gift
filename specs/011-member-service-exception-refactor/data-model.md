# Data Model: Member API 서비스 및 예외 처리 리팩토링

## MemberService

회원가입과 로그인 API의 비즈니스 로직을 담당합니다.

**Methods**:

- `TokenResponse register(MemberRequest request)`
- `TokenResponse login(MemberRequest request)`

**Responsibilities**:

- 이메일 중복 여부 확인
- 회원 저장
- 로그인 대상 회원 조회
- 비밀번호 일치 여부 확인
- JWT token 발급

## MemberException

member 도메인/API 예외의 기준 타입입니다.

**Fields**:

- `message`: API error response에 사용할 메시지

## DuplicateMemberEmailException

이미 등록된 이메일로 회원가입하려는 상황을 표현합니다.

**Message**:

- `"이미 등록된 이메일입니다."`

**HTTP Mapping**:

- Status: 400 Bad Request
- Code: `MEMBER.DUPLICATE_EMAIL`

## InvalidMemberCredentialsException

이메일 또는 비밀번호가 유효하지 않은 로그인 실패 상황을 표현합니다.

**Message**:

- `"이메일 또는 비밀번호가 올바르지 않습니다."`

**HTTP Mapping**:

- Status: 401 Unauthorized
- Code: `MEMBER.INVALID_CREDENTIALS`

## MemberController

회원가입/로그인 HTTP 요청을 처리합니다.

**Responsibilities**:

- request validation
- service 호출
- 성공 status code 반환

**Non-Responsibilities**:

- repository 직접 접근
- JWT 직접 생성
- `IllegalArgumentException` 직접 처리

## Relationships

- `MemberController`는 `MemberService`에 의존합니다.
- `MemberService`는 `MemberRepository`와 `JwtProvider`에 의존합니다.
- `GlobalExceptionHandler`는 `MemberException` 하위 타입을 `ErrorResponse`로 변환합니다.
- `AdminMemberController`는 이번 작업 범위에서 변경하지 않습니다.
