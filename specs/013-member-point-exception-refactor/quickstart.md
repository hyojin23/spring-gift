# Quickstart: Member 포인트 예외 정리 리팩토링

## 목표

`Member.chargePoint()`와 `Member.deductPoint()`의 `IllegalArgumentException`을 member 도메인 예외로 교체합니다.

## 구현 순서

1. Member 포인트 테스트 추가
   - 충전 성공
   - 0 이하 금액 충전 실패
   - 차감 성공
   - 0 이하 금액 차감 실패
   - 포인트 부족 차감 실패
   - 실패 시 기존 포인트 유지

2. 포인트 예외 클래스 추가
   - `InvalidMemberPointAmountException`
   - `InsufficientMemberPointException`
   - 예외 메시지는 한글로 작성

3. `Member` 포인트 메서드 예외 교체
   - `chargePoint`
   - `deductPoint`

4. 회귀 테스트 실행

## 검증 명령

```powershell
.\gradlew.bat test --tests *Member* --tests *Order*
```

## 완료 조건

- `Member` 포인트 메서드에서 `IllegalArgumentException` 직접 throw가 제거됩니다.
- 금액 오류와 포인트 부족이 서로 다른 member 예외로 표현됩니다.
- 포인트 예외 메시지가 한글로 통일됩니다.
- Member/Order 관련 테스트가 통과합니다.
