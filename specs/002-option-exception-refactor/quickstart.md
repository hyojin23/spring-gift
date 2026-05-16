# Quickstart: Option 패키지 예외 처리 리팩토링

## 1. 현재 브랜치 확인

```powershell
git branch --show-current
```

예상 브랜치:

```text
002-option-exception-refactor
```

## 2. 구현 범위

- `OptionService`를 추가해 `OptionController`의 비즈니스 규칙을 이동합니다.
- `gift.option.exception` 패키지에 Option 도메인 예외를 추가합니다.
- `GlobalExceptionHandler`에 Option 예외 매핑을 추가합니다.
- `OptionController`의 개별 `@ExceptionHandler`와 직접 404/400 처리 흐름을 제거합니다.
- `OptionControllerTest`, `OptionServiceTest`, 필요 시 `GlobalExceptionHandlerTest`를 추가 또는 확장합니다.

## 3. 검증 명령

```powershell
.\gradlew.bat test
```

## 4. 수동 확인 포인트

- 존재하지 않는 상품의 옵션 목록 조회는 404와 표준 JSON 에러 응답을 반환합니다.
- 존재하지 않는 옵션 삭제는 404와 표준 JSON 에러 응답을 반환합니다.
- 중복 옵션명 생성은 400과 표준 JSON 에러 응답을 반환합니다.
- 마지막 옵션 삭제 시도는 400과 표준 JSON 에러 응답을 반환합니다.
- 정상 옵션 목록 조회, 생성, 삭제 응답은 기존과 동일합니다.
