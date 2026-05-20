# Quickstart: Order 알림 서비스 분리 리팩토링

## 구현 순서

1. `OrderNotificationServiceTest`를 추가합니다.
2. token 없는 회원은 `KakaoMessageClient`를 호출하지 않는 테스트를 작성합니다.
3. token 있는 회원은 `KakaoMessageClient.sendToMe()`를 호출하는 테스트를 작성합니다.
4. 카카오 메시지 발송 실패가 예외로 전파되지 않는 테스트를 작성합니다.
5. `OrderNotificationService`를 추가합니다.
6. `OrderService` 생성자 의존성을 `KakaoMessageClient`에서 `OrderNotificationService`로 바꿉니다.
7. `OrderService`의 `sendKakaoMessageIfPossible()`를 제거합니다.
8. 주문 저장 후 `orderNotificationService.sendOrderCreatedMessage(member, saved, option)`을 호출합니다.
9. 기존 `OrderServiceTest` mock 의존성을 수정합니다.
10. 관련 테스트를 실행합니다.

## 검증 명령

```powershell
.\gradlew.bat test --tests *Order*
```

## 수동 확인 포인트

- `OrderService`에 `KakaoMessageClient` 직접 의존이 없습니다.
- `OrderService`에 `sendKakaoMessageIfPossible` 메서드가 없습니다.
- 카카오 access token이 없는 회원은 카카오 client 호출 없이 종료됩니다.
- 카카오 메시지 발송 실패는 주문 생성 실패로 이어지지 않습니다.
- 주문 생성 API의 성공/실패 응답은 기존과 같습니다.
