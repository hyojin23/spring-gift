# Quickstart: Category 도메인 검증 강화 리팩토링

## 실행 전 확인

```powershell
git status --short
```

## 구현 범위

1. `CategoryValidationException` 추가
2. `Category` 생성자에서 name/color/imageUrl 검증
3. `Category.update()`에서 name/color/imageUrl 검증
4. `GlobalExceptionHandler`에 category validation handler 추가
5. `CategoryTest`와 `GlobalExceptionHandlerTest` 보강

## 검증 명령

```powershell
.\gradlew.bat test --tests *Category*
.\gradlew.bat test --tests *GlobalExceptionHandler*
.\gradlew.bat test
```

## 기대 결과

- Category는 필수 값이 비어 있는 상태로 생성되지 않습니다.
- Category는 필수 값이 비어 있는 상태로 수정되지 않습니다.
- Category 검증 예외는 400 `CATEGORY.INVALID` 응답으로 변환됩니다.
- 기존 category API 성공 테스트는 유지됩니다.
