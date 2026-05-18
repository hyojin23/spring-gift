# Research: Admin Member 서비스 분리 리팩토링

## Decision 1: AdminMemberService를 별도로 둔다

**Decision**: 관리자 회원 화면 전용 `AdminMemberService`를 추가합니다.

**Rationale**: `MemberService`는 회원가입/로그인 API와 JWT 발급에 맞춰져 있습니다. Admin 화면은 회원 목록, form 등록/수정, 포인트 충전, 삭제 등 HTML flow가 중심이므로 별도 service가 더 단순합니다.

**Alternatives considered**:

- 기존 `MemberService` 재사용: API 인증/토큰 발급 책임과 admin CRUD 책임이 섞입니다.
- Controller 유지: 변경은 적지만 repository 직접 접근이 남습니다.

## Decision 2: Admin 회원 미존재는 admin 전용 예외로 표현한다

**Decision**: `AdminMemberNotFoundException`을 추가하고 관리자 회원 화면에서 회원을 찾지 못한 상황을 표현합니다.

**Rationale**: 관리자 HTML flow는 JSON API와 다르게 redirect + flash message가 자연스럽습니다. 예외 타입을 분리하면 handler 적용 범위를 좁힐 수 있습니다.

**Alternatives considered**:

- `IllegalArgumentException` 유지: 도메인 의미가 드러나지 않습니다.
- API용 member 예외 재사용: JSON global handler와 HTML redirect 정책이 섞일 수 있습니다.

## Decision 3: Admin 화면 오류는 redirect + flash로 처리한다

**Decision**: 회원 미존재 또는 포인트 충전 예외는 `/admin/members`로 redirect하고 flash attribute `error`에 메시지를 담습니다.

**Rationale**: 관리자 화면 사용자는 오류 후 회원 목록에서 작업을 이어가는 편이 자연스럽습니다. Product admin 예외 처리와도 일관됩니다.

**Alternatives considered**:

- error view + HTTP status: HTTP 의미론은 좋지만 현재 단순 CRUD admin flow에는 과합니다.
- JSON `ErrorResponse`: HTML 화면과 맞지 않습니다.

## Decision 4: 중복 이메일 등록 실패는 form view 반환을 유지한다

**Decision**: 중복 이메일은 `member/new` view를 반환하고 `error`, `email` model을 채웁니다.

**Rationale**: 사용자가 입력한 이메일을 수정해야 하는 form validation 성격의 오류입니다. redirect보다 기존 입력을 유지하는 view 반환이 더 적합합니다.

## Decision 5: 오류 메시지는 한글로 통일한다

**Decision**: Admin member 화면에서 표시되는 오류 메시지는 한글로 제공합니다.

**Rationale**: 기존 `Email is already registered.`, `Member not found`처럼 영어 메시지가 섞여 있습니다. 최근 member 리팩토링 방향과 맞춰 운영자 화면 메시지를 한글로 통일합니다.
