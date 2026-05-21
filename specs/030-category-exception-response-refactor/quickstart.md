# Quickstart: Category 예외 응답 일관화 리팩토링

## 실행 전 확인

```powershell
git status --short
```

## 구현 범위

1. `GlobalExceptionHandler.handleCategoryNotFound()` 반환 타입 변경
2. category 미존재 error code `CATEGORY.NOT_FOUND` 적용
3. `GlobalExceptionHandlerTest` category 테스트 추가
4. `CategoryControllerTest` 미존재 category 응답 body 검증 추가

## 검증 명령

```powershell
.\gradlew.bat test --tests *CategoryController*
.\gradlew.bat test --tests *GlobalExceptionHandler*
.\gradlew.bat test
```

## 기대 결과

- 존재하지 않는 카테고리 요청은 404를 유지합니다.
- 응답 body에 `code=CATEGORY.NOT_FOUND`가 포함됩니다.
- 응답 body에 `message=카테고리를 찾을 수 없습니다.`가 포함됩니다.
