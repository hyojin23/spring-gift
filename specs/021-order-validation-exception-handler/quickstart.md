# Quickstart: Order 검증 예외 전역 처리 리팩토링

## 구현 순서

1. `GlobalExceptionHandlerTest`에 `OrderValidationException` handler 테스트를 추가합니다.
2. status가 400인지 검증합니다.
3. code가 `ORDER.INVALID`인지 검증합니다.
4. message가 예외 메시지와 같은지 검증합니다.
5. `GlobalExceptionHandler`에 `OrderValidationException` import를 추가합니다.
6. `handleOrderValidation()` 메서드를 추가합니다.
7. 관련 테스트를 실행합니다.

## 검증 명령

```powershell
.\gradlew.bat test --tests *GlobalExceptionHandler*
.\gradlew.bat test --tests *Order*
```

## 수동 확인 포인트

- `OrderValidationException`은 400으로 매핑됩니다.
- 응답 code는 `ORDER.INVALID`입니다.
- `OrderOptionNotFoundException`의 404 매핑은 유지됩니다.
- 다른 도메인 예외 handler는 변경하지 않습니다.
