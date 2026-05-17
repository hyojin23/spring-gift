# Quickstart: Member API 서비스 및 예외 처리 리팩토링

## 목표

`MemberController`의 회원가입/로그인 비즈니스 로직을 `MemberService`로 이동하고, `IllegalArgumentException` 기반 실패를 member 도메인 예외와 global `ErrorResponse` 처리로 바꿉니다.

## 구현 순서

1. Member API 테스트 추가
   - 회원가입 성공
   - 로그인 성공
   - 중복 이메일 회원가입 실패
   - 존재하지 않는 이메일 로그인 실패
   - 잘못된 비밀번호 로그인 실패

2. MemberService 테스트 추가
   - 중복 이메일이면 `DuplicateMemberEmailException`
   - 이메일 미존재/비밀번호 불일치면 `InvalidMemberCredentialsException`
   - 성공 시 token 반환

3. member 예외 클래스 추가
   - `MemberException`
   - `DuplicateMemberEmailException`
   - `InvalidMemberCredentialsException`

4. `MemberService` 추가
   - `register`
   - `login`

5. `MemberController` 리팩토링
   - `MemberService`만 주입
   - local `IllegalArgumentException` handler 제거

6. `GlobalExceptionHandler` 보강
   - `DuplicateMemberEmailException` -> 400 / `MEMBER.DUPLICATE_EMAIL`
   - `InvalidMemberCredentialsException` -> 401 / `MEMBER.INVALID_CREDENTIALS`

## 검증 명령

```powershell
.\gradlew.bat test --tests *Member* --tests *GlobalExceptionHandlerTest*
```

## 완료 조건

- `MemberController`에 `MemberRepository`, `JwtProvider` 직접 의존성이 없습니다.
- `MemberController`에 `IllegalArgumentException` handler가 없습니다.
- member 실패 응답은 `ErrorResponse` JSON입니다.
- 회원가입/로그인 성공 계약은 유지됩니다.
