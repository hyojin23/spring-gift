# Quickstart: 주문 알림 실패 로깅 리팩토링

## 실행 전 확인

```powershell
git status --short
```

## 구현 범위

1. `OrderNotificationService`에 SLF4J logger 추가
2. `catch (Exception ignored)` 제거
3. 카카오 메시지 발송 실패 시 warn 로그 추가
4. access token은 로그에 포함하지 않기
5. 기존 best effort 테스트 유지

## 검증 명령

```powershell
.\gradlew.bat test --tests *OrderNotificationService*
.\gradlew.bat test
```

## 기대 결과

- 카카오 메시지 발송 실패가 주문 흐름으로 전파되지 않습니다.
- 실패 시 warn 로그가 남습니다.
- access token이 없으면 메시지 client를 호출하지 않습니다.
- 정상 발송 흐름은 유지됩니다.
