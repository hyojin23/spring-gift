# 기능 명세서: Admin Member 서비스 분리 리팩토링

**Feature Branch**: `014-admin-member-service-refactor`  
**작성일**: 2026-05-18  
**상태**: 초안  
**입력**: "AdminMemberController 리팩토링"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - Admin 회원 화면 비즈니스 로직의 서비스 계층 이동 (우선순위: P1)

관리자 회원 화면 컨트롤러는 HTML view와 form model 조립, redirect에 집중하고 회원 조회/생성/수정/포인트 충전/삭제는 admin 전용 service에서 처리해야 합니다.

**우선순위 이유**: 현재 `AdminMemberController`는 `MemberRepository`에 직접 접근하고, 중복 이메일 검증과 회원 조회/수정/포인트 충전을 직접 수행합니다. controller 책임을 줄이면 product admin 리팩토링과 구조가 일관됩니다.

**독립적 테스트**: 관리자 회원 목록/등록/수정/포인트 충전/삭제 flow가 기존 view 이름과 redirect 경로를 유지하는지 검증합니다.

**승인 시나리오**:

1. **Given** 관리자가 회원 목록에 접근할 때, **When** `/admin/members`를 요청하면, **Then** `member/list` view와 회원 목록 model을 반환합니다.
2. **Given** 관리자가 회원 등록 화면에 접근할 때, **When** `/admin/members/new`를 요청하면, **Then** `member/new` view를 반환합니다.
3. **Given** 유효한 회원 등록 form이 제출될 때, **When** `/admin/members`로 POST 요청하면, **Then** `/admin/members`로 redirect합니다.
4. **Given** 유효한 회원 수정 form이 제출될 때, **When** `/admin/members/{id}/edit`로 POST 요청하면, **Then** `/admin/members`로 redirect합니다.
5. **Given** 유효한 포인트 충전 요청이 제출될 때, **When** `/admin/members/{id}/charge-point`로 POST 요청하면, **Then** `/admin/members`로 redirect합니다.
6. **Given** 삭제 요청이 제출될 때, **When** `/admin/members/{id}/delete`로 POST 요청하면, **Then** `/admin/members`로 redirect합니다.

---

### 사용자 시나리오 2 - Admin 회원 등록 중복 이메일 UX 유지 (우선순위: P1)

관리자 회원 등록 form에서 이미 등록된 이메일을 입력하면 기존처럼 등록 화면을 다시 반환하고, 오류 메시지와 입력한 이메일을 model에 채워야 합니다. 오류 메시지는 한글로 통일합니다.

**우선순위 이유**: 관리자 HTML form flow에서는 JSON error response보다 사용자가 입력을 수정할 수 있는 form 복구 UX가 중요합니다.

**독립적 테스트**: 중복 이메일 등록 시 `member/new` view, `error`, 입력 이메일 model이 유지되는지 검증합니다.

**승인 시나리오**:

1. **Given** 이미 등록된 이메일이 있을 때, **When** 관리자 회원 등록 form을 제출하면, **Then** `member/new` view를 반환합니다.
2. **Then** model에는 한글 오류 메시지와 입력 이메일이 포함됩니다.

---

### 사용자 시나리오 3 - Admin 회원 미존재 및 포인트 예외를 HTML flow로 처리 (우선순위: P2)

관리자 회원 화면에서 존재하지 않는 회원 ID에 접근하거나 포인트 충전 정책을 위반하면 `/admin/members`로 redirect하고 flash attribute `error`에 한글 오류 메시지를 담아야 합니다.

**우선순위 이유**: Admin 화면은 HTML 기반 flow입니다. 미존재 회원이나 포인트 충전 실패 시 JSON 응답보다 목록으로 돌려보내고 오류 메시지를 표시하는 편이 현재 admin UX와 맞습니다.

**독립적 테스트**: 회원 미존재/포인트 충전 실패 요청이 `/admin/members`로 redirect되고 flash `error`를 포함하는지 검증합니다.

**승인 시나리오**:

1. **Given** 존재하지 않는 회원 ID가 있을 때, **When** 수정 화면에 접근하면, **Then** `/admin/members`로 redirect하고 flash `error`를 포함합니다.
2. **Given** 존재하지 않는 회원 ID가 있을 때, **When** 수정 또는 포인트 충전 요청을 제출하면, **Then** `/admin/members`로 redirect하고 flash `error`를 포함합니다.
3. **Given** 포인트 충전 금액이 0 이하일 때, **When** 포인트 충전 요청을 제출하면, **Then** `/admin/members`로 redirect하고 flash `error`를 포함합니다.

---

### 사용자 시나리오 4 - API Member flow와 Admin Member flow 분리 (우선순위: P2)

AdminMemberController 리팩토링은 HTML 관리자 화면에만 적용하며, Member API의 JSON `ErrorResponse` 계약은 변경하지 않습니다.

**우선순위 이유**: Member API는 JSON 응답 중심이고 Admin 화면은 view/redirect 중심입니다. 두 흐름을 섞으면 API 계약이나 관리자 UX가 의도치 않게 바뀔 수 있습니다.

**독립적 테스트**: 기존 Member API 테스트와 Admin Member controller 테스트가 모두 통과하는지 확인합니다.

---

### 엣지 케이스

- `AdminMemberController`에는 `MemberRepository` 직접 의존성이 남지 않아야 합니다.
- Admin 중복 이메일 오류 메시지는 한글이어야 합니다.
- Admin 회원 미존재 오류 메시지는 한글이어야 합니다.
- Admin 포인트 충전 오류 메시지는 한글이어야 합니다.
- Admin 화면 예외를 JSON `ErrorResponse`로 반환하지 않아야 합니다.
- Member API controller/service 구조는 이번 작업에서 변경하지 않습니다.
- OrderController의 포인트 차감 flow는 이번 작업에서 변경하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `AdminMemberService`를 추가하고 Admin 회원 화면의 회원 조회/생성/수정/포인트 충전/삭제 로직을 담당하게 해야 합니다.
- **FR-002**: `AdminMemberController`는 `AdminMemberService`만 주입받도록 변경해야 합니다.
- **FR-003**: `AdminMemberController`는 view 이름, redirect, form model 조립을 담당해야 합니다.
- **FR-004**: 중복 이메일 등록 시 기존처럼 `member/new` view를 반환하고 `error`, `email` model을 포함해야 합니다.
- **FR-005**: Admin 회원 미존재 상황은 admin member 도메인 예외로 표현해야 합니다.
- **FR-006**: Admin 회원 미존재 예외는 `/admin/members` redirect + flash `error`로 처리해야 합니다.
- **FR-007**: Admin 포인트 충전 예외는 `/admin/members` redirect + flash `error`로 처리해야 합니다.
- **FR-008**: Admin 회원 화면 오류 메시지는 한글로 통일해야 합니다.
- **FR-009**: Member API의 JSON error response 계약은 변경하지 않아야 합니다.
- **FR-010**: Admin Member controller flow를 검증하는 MockMvc 테스트를 추가해야 합니다.

### 주요 엔티티

- **AdminMemberService**: Admin 회원 화면에서 필요한 회원 조회, 회원 생성/수정/삭제, 포인트 충전을 담당합니다.
- **AdminMemberController**: HTML view와 form model 조립, redirect를 담당합니다.
- **AdminMemberNotFoundException**: 관리자 회원 화면에서 회원이 존재하지 않는 상황을 표현합니다.
- **MemberException**: 포인트 충전 예외를 포함한 member 도메인 예외의 기준 타입입니다.
- **member/list template**: flash attribute `error`가 있을 때 관리자에게 오류 메시지를 표시합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `AdminMemberController`는 `AdminMemberService`만 의존합니다.
- **SC-002**: `AdminMemberController`에 `MemberRepository` 직접 접근이 남지 않습니다.
- **SC-003**: Admin 회원 목록/등록/수정/포인트 충전/삭제 성공 flow는 기존 view/redirect 계약을 유지합니다.
- **SC-004**: 중복 이메일 등록 실패 flow는 `member/new` view와 form 복구 model을 유지합니다.
- **SC-005**: 회원 미존재 및 포인트 충전 예외는 `/admin/members` redirect와 flash `error`로 처리됩니다.
- **SC-006**: Admin 회원 화면 오류 메시지는 한글로 제공됩니다.
- **SC-007**: Member API 관련 테스트가 기존처럼 통과합니다.
- **SC-008**: `./gradlew test --tests *AdminMember* --tests *Member*`가 통과합니다.

## 가정사항

- Member API service/exception 리팩토링은 `011-member-service-exception-refactor`에서 완료되었습니다.
- Member 도메인 검증과 포인트 예외 정리는 `012`, `013` 작업에서 완료되었습니다.
- 이번 작업은 Admin member HTML flow에 집중하며, OrderController 리팩토링은 포함하지 않습니다.
