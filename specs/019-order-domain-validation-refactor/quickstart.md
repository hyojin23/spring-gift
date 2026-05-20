# Quickstart: Order 도메인 검증 강화 리팩토링

## 구현 순서

1. `OrderTest`를 추가합니다.
2. 유효한 값으로 `Order`가 생성되는 테스트를 작성합니다.
3. `option == null`이면 `OrderValidationException`이 발생하는 테스트를 작성합니다.
4. `memberId == null`이면 `OrderValidationException`이 발생하는 테스트를 작성합니다.
5. `quantity == 0` 또는 음수이면 `OrderValidationException`이 발생하는 테스트를 작성합니다.
6. `OrderValidationException`을 추가합니다.
7. `Order` 생성자에 검증 helper를 추가합니다.
8. 기존 order 테스트를 실행합니다.

## 검증 명령

```powershell
.\gradlew.bat test --tests *Order*
```

## 수동 확인 포인트

- `Order` 생성자에 `option`, `memberId`, `quantity` 검증이 있습니다.
- `message`는 기존처럼 선택값입니다.
- `orderDateTime` 자동 설정은 유지됩니다.
- 정상 주문 생성 service/controller 테스트가 깨지지 않습니다.
