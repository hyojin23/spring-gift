# Quickstart: Member 도메인 검증 강화 리팩토링

## 목표

`Member` 생성자와 `update()`에서 기본 도메인 불변 조건을 검증해, 회원이 항상 유효한 식별 정보를 가진 상태로 생성/수정되도록 합니다.

## 구현 순서

1. Member 단위 테스트 추가
   - 일반 회원 생성 시 빈 email 실패
   - 일반 회원 생성 시 빈 password 실패
   - 카카오 회원 생성 시 빈 email 실패
   - 카카오 회원 생성 시 password 없이 성공
   - update 시 빈 email/password 실패
   - update 실패 시 기존 상태 유지

2. `MemberValidationException` 추가
   - `MemberException` 하위 타입

3. `Member` 검증 구현
   - 일반 회원 생성자: email/password 검증
   - 카카오 회원 생성자: email 검증
   - update: email/password 검증 후 필드 할당

4. 회귀 테스트 실행
   - Member API
   - Kakao auth flow 관련 테스트 또는 컴파일 검증

## 검증 명령

```powershell
.\gradlew.bat test --tests *Member* --tests *Kakao*
```

## 완료 조건

- 일반 회원 생성자는 email/password를 검증합니다.
- 카카오 회원 생성자는 email만 검증하고 password null을 허용합니다.
- `Member.update()`는 검증 실패 시 기존 상태를 유지합니다.
- Member 관련 테스트가 통과합니다.
