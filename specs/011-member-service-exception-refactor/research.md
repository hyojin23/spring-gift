# Research: Member API 서비스 및 예외 처리 리팩토링

## Decision 1: Member API 비즈니스 로직은 MemberService로 이동한다

**Decision**: 회원가입과 로그인 로직을 `MemberService`로 이동합니다.

**Rationale**: controller가 repository 접근, 중복 검증, 비밀번호 검증, JWT 생성까지 직접 수행하면 HTTP layer와 business layer가 섞입니다. service를 도입하면 Product API 리팩토링과 같은 구조가 되고, 단위 테스트가 쉬워집니다.

**Alternatives considered**:

- Controller 유지: 변경은 적지만 책임 분리가 되지 않습니다.
- AuthService로 이동: 로그인과 JWT 관점에서는 자연스럽지만 회원가입까지 포함하면 member 도메인과 멀어집니다.

## Decision 2: 중복 이메일과 로그인 실패는 member 도메인 예외로 표현한다

**Decision**: `DuplicateMemberEmailException`, `InvalidMemberCredentialsException`을 추가합니다.

**Rationale**: 두 실패는 member API의 명확한 도메인 실패입니다. `IllegalArgumentException`은 범위가 넓고 global handler에서 정확한 status/code로 매핑하기 어렵습니다.

**Alternatives considered**:

- `IllegalArgumentException` 유지: controller-local handler에 묶여 표준 `ErrorResponse`를 사용하기 어렵습니다.
- 하나의 `MemberValidationException` 사용: 간단하지만 중복 이메일과 인증 실패의 status code가 다르므로 분리하는 편이 명확합니다.

## Decision 3: 로그인 실패는 401 Unauthorized로 처리한다

**Decision**: 이메일 미존재 또는 비밀번호 불일치 모두 `InvalidMemberCredentialsException`으로 처리하고, global handler에서 401 Unauthorized + `MEMBER.INVALID_CREDENTIALS`를 반환합니다.

**Rationale**: 로그인 실패는 인증 실패입니다. 또한 보안상 이메일 미존재와 비밀번호 불일치를 응답에서 구분하지 않는 것이 좋습니다.

**Alternatives considered**:

- 400 Bad Request: 인증 실패 의미가 약합니다.
- 404 Not Found: 이메일 존재 여부를 노출할 수 있습니다.

## Decision 4: MemberController의 local exception handler는 제거한다

**Decision**: `MemberController`의 `@ExceptionHandler(IllegalArgumentException.class)`를 제거하고 global handler로 이동합니다.

**Rationale**: API 예외 응답은 global handler에서 일관되게 `ErrorResponse`로 변환하는 방향과 맞습니다.

**Alternatives considered**:

- controller-local member exception handler: 적용 범위는 좁지만 Product/Option/Wish API와 handler 위치가 달라집니다.

## Decision 5: AdminMemberController는 이번 작업에서 제외한다

**Decision**: `/admin/members` HTML flow는 이번 spec 범위에서 변경하지 않습니다.

**Rationale**: Admin 화면은 view/redirect 중심이고 API는 JSON 중심입니다. 두 흐름을 동시에 바꾸면 테스트 범위와 UX 결정이 커집니다.
