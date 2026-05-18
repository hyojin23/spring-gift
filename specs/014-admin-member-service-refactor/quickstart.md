# Quickstart: Admin Member 서비스 분리 리팩토링

## 목표

`AdminMemberController`의 repository 직접 접근과 비즈니스 로직을 `AdminMemberService`로 이동하고, 관리자 회원 화면 오류를 HTML flow에 맞게 처리합니다.

## 구현 순서

1. Admin Member MockMvc 테스트 추가
   - 목록 조회
   - 등록 화면 조회
   - 등록 성공
   - 중복 이메일 등록 실패
   - 수정 화면 조회
   - 수정 성공
   - 포인트 충전 성공
   - 삭제 성공
   - 회원 미존재 redirect + flash
   - 포인트 충전 실패 redirect + flash

2. `AdminMemberService` 추가
   - repository 접근 이동
   - 회원 조회/생성/수정/충전/삭제 담당

3. admin member 예외 추가
   - `AdminMemberNotFoundException`

4. `AdminMemberController` 리팩토링
   - `AdminMemberService`만 주입
   - form model 조립과 redirect만 담당
   - admin 예외와 포인트 예외를 flash redirect 처리

5. template 수정
   - `member/list.html`에 flash `error` 표시 추가

## 검증 명령

```powershell
.\gradlew.bat test --tests *AdminMember* --tests *Member*
```

## 완료 조건

- `AdminMemberController`에 `MemberRepository` 직접 의존성이 없습니다.
- 관리자 회원 성공 flow는 기존 view/redirect를 유지합니다.
- 관리자 회원 오류 메시지는 한글입니다.
- 회원 미존재/포인트 충전 예외는 `/admin/members` redirect + flash `error`로 처리됩니다.
