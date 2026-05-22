# Data Model: Category 예외 패키지 정리 리팩토링

## CategoryNotFoundException

**변경 전 패키지**: `gift.category`  
**변경 후 패키지**: `gift.category.exception`

### 책임

- 요청한 category가 존재하지 않는 상황을 표현합니다.
- global handler에서 404 `CATEGORY.NOT_FOUND` 응답으로 변환됩니다.

### 제약

- 메시지 문구를 유지합니다.
- exception type의 의미를 변경하지 않습니다.

## CategoryValidationException

**변경 전 패키지**: `gift.category`  
**변경 후 패키지**: `gift.category.exception`

### 책임

- Category 도메인의 필수 값 검증 실패를 표현합니다.
- global handler에서 400 `CATEGORY.INVALID` 응답으로 변환됩니다.

### 제약

- 검증 메시지 문구를 유지합니다.
- 검증 조건은 변경하지 않습니다.

## Import 사용처

### Main

- `Category`
- `CategoryService`
- `GlobalExceptionHandler`

### Test

- `CategoryTest`
- `CategoryControllerTest`
- `GlobalExceptionHandlerTest`

## 관계

```text
Category
  -> CategoryValidationException

CategoryService
  -> CategoryNotFoundException

GlobalExceptionHandler
  -> CategoryNotFoundException
  -> CategoryValidationException
```
