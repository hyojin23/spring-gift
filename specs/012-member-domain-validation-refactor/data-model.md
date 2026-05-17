# Data Model: Member 도메인 검증 강화 리팩토링

## Member

회원 도메인 엔티티입니다. 일반 회원과 카카오 회원 생성 시 필요한 기본 불변 조건을 직접 검증합니다.

**Fields**:

- `id`: 회원 식별자
- `email`: 회원 이메일
- `password`: 일반 회원 비밀번호, 카카오 회원은 null 가능
- `kakaoAccessToken`: 카카오 access token
- `point`: 회원 포인트

**Validation Rules**:

- 일반 회원 생성 시 `email`은 null 또는 blank일 수 없습니다.
- 일반 회원 생성 시 `password`는 null 또는 blank일 수 없습니다.
- 카카오 회원 생성 시 `email`은 null 또는 blank일 수 없습니다.
- 카카오 회원 생성 시 `password`는 null일 수 있습니다.
- `update(email, password)` 호출 시 email/password는 null 또는 blank일 수 없습니다.

**Lifecycle**:

1. 일반 회원 생성자 호출
2. email/password 검증
3. 필드 할당
4. 카카오 회원 생성자 호출
5. email 검증
6. email 필드 할당
7. `update()` 호출
8. email/password 검증
9. 필드 변경

## MemberValidationException

Member 도메인 검증 실패를 표현하는 예외입니다.

**Parent**:

- `MemberException`

**Fields**:

- `message`: 검증 실패 이유

**Usage**:

- 일반 회원 생성자 검증 실패
- 카카오 회원 생성자 검증 실패
- `Member.update()` 검증 실패

## MemberRequest

회원가입/로그인 HTTP 요청 DTO입니다.

**Validation Rules**:

- `email`: `@NotBlank`, `@Email`
- `password`: `@NotBlank`

**Relationship**:

- 요청 검증을 담당하며 Member 도메인 검증을 대체하지 않습니다.

## Relationships

- `MemberService`는 일반 회원 생성자를 사용합니다.
- `KakaoAuthController`는 카카오 회원 생성자를 사용합니다.
- `AdminMemberController`는 `Member.update()`를 사용합니다.
- 포인트 관련 method는 이번 작업에서 변경하지 않습니다.
