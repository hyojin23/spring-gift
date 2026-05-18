# Data Model: Admin Member 서비스 분리 리팩토링

## AdminMemberService

관리자 회원 화면에서 필요한 회원 조회와 변경 로직을 담당합니다.

**Methods**:

- `List<Member> getMembers()`
- `Member getMember(Long id)`
- `boolean existsByEmail(String email)`
- `void createMember(String email, String password)`
- `void updateMember(Long id, String email, String password)`
- `void chargePoint(Long id, int amount)`
- `void deleteMember(Long id)`

**Responsibilities**:

- 회원 목록 조회
- 회원 단건 조회
- 이메일 중복 확인
- 회원 생성/수정
- 포인트 충전
- 회원 삭제

## AdminMemberNotFoundException

관리자 회원 화면에서 회원 ID로 회원을 찾을 수 없을 때 발생합니다.

**Parent**:

- `MemberException`

**Message**:

- `"회원이 존재하지 않습니다. id={id}"`

**Handling**:

- `/admin/members` redirect
- flash attribute `error`

## AdminMemberController

관리자 회원 HTML 요청을 처리합니다.

**Responsibilities**:

- view 이름 반환
- redirect 경로 반환
- form error model 조립
- admin member 예외를 flash redirect로 처리

**Non-Responsibilities**:

- repository 직접 접근
- 회원 생성/수정/포인트 충전 직접 수행

## Flash Error Message

관리자 회원 목록 화면에서 한 번 표시되는 오류 메시지입니다.

**Fields**:

- `error`: 한글 오류 메시지

## Relationships

- `AdminMemberController`는 `AdminMemberService`에 의존합니다.
- `AdminMemberService`는 `MemberRepository`에 의존합니다.
- `AdminMemberController`는 admin member 예외와 member 포인트 예외를 redirect + flash로 처리합니다.
- Member API controller/service는 이번 모델에 의존하지 않습니다.
