# Quickstart: Option 도메인 예외 리팩토링

## 1. 현재 브랜치 확인

```powershell
git branch --show-current
```

예상 브랜치:

```text
006-option-domain-exception-refactor
```

## 2. 구현 범위

- `gift.option.exception.OptionQuantityException`을 추가합니다.
- `Option` 수량 검증 실패에서 `IllegalArgumentException` 대신 `OptionQuantityException`을 던지도록 변경합니다.
- `GlobalExceptionHandler`에 `OptionQuantityException` handler를 추가합니다.
- error code는 `OPTION.INVALID_QUANTITY`를 사용합니다.
- `OptionTest`와 `GlobalExceptionHandlerTest`를 갱신합니다.

## 3. 검증 명령

```powershell
.\gradlew.bat test --tests *Option* --tests *GlobalExceptionHandlerTest*
```

필요 시 더 좁게 실행:

```powershell
.\gradlew.bat test --tests *OptionTest*
.\gradlew.bat test --tests *GlobalExceptionHandlerTest*
```

## 4. 수동 확인 포인트

- `src/main/java/gift/option`에 직접 `throw new IllegalArgumentException`이 남지 않습니다.
- 잘못된 Option 수량 생성은 `OptionQuantityException`을 발생시킵니다.
- 잘못된 Option 수량 차감은 `OptionQuantityException`을 발생시킵니다.
- `OptionQuantityException`은 HTTP 400과 `OPTION.INVALID_QUANTITY`로 매핑됩니다.
- 기존 Option API 응답 계약은 변경되지 않습니다.
