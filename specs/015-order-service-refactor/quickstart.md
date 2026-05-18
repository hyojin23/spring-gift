# Quickstart: Order 서비스 분리 리팩토링

## 목표

`OrderController`의 주문 조회/생성 비즈니스 로직을 `OrderService`로 이동하고, 기존 HTTP 응답 계약을 유지합니다.

## 구현 순서

1. Order 테스트 추가
   - 주문 목록 조회 성공
   - 주문 목록 인증 실패
   - 주문 생성 성공
   - 주문 생성 인증 실패
   - 주문 생성 옵션 미존재
   - 카카오 메시지 실패에도 주문 생성 성공

2. `OrderService` 추가
   - `getOrders`
   - `createOrder`
   - `sendKakaoMessageIfPossible`

3. `OrderController` 리팩토링
   - repository/client 직접 의존 제거
   - `OrderService` 주입
   - 인증 실패 401 유지
   - 옵션 미존재 404 유지
   - 생성 성공 201 유지

4. 검증
   - Order 관련 테스트
   - Member/Option 회귀 테스트
   - controller 직접 의존성 검색

## 검증 명령

```powershell
.\gradlew.bat test --tests *Order* --tests *Member* --tests *Option*
```

## 완료 조건

- `OrderController`는 `OrderService`와 `AuthenticationResolver`만 의존합니다.
- 주문 목록/생성 응답 계약이 유지됩니다.
- 카카오 알림 실패가 주문 생성 실패로 전파되지 않습니다.
- 위시 cleanup은 이번 작업에서 구현하지 않습니다.
