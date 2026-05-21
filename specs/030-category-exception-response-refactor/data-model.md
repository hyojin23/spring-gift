# Data Model: Category 예외 응답 일관화 리팩토링

## CategoryNotFoundException

**역할**: 요청한 카테고리가 존재하지 않을 때 발생합니다.

**현재 메시지**:

- `카테고리를 찾을 수 없습니다.`

**변경 규칙**:

- 예외 클래스 자체는 변경하지 않습니다.
- global handler에서 메시지를 `ErrorResponse.message`로 사용합니다.

## ErrorResponse

**역할**: API 에러 응답 body입니다.

**category 미존재 응답 값**:

- `code`: `CATEGORY.NOT_FOUND`
- `message`: `카테고리를 찾을 수 없습니다.`

## GlobalExceptionHandler

**변경 전**:

- `CategoryNotFoundException` -> 404 body 없음

**변경 후**:

- `CategoryNotFoundException` -> 404 + `ErrorResponse`

## CategoryController

**영향**:

- controller 로직은 변경하지 않습니다.
- 존재하지 않는 카테고리 요청에서 global handler 응답 body만 달라집니다.
