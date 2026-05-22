# Data Model: Category 도메인 검증 강화 리팩토링

## Category

**필드**:

- `id`: DB 식별자
- `name`: 카테고리 이름, 필수
- `color`: 카테고리 색상, 필수
- `imageUrl`: 카테고리 이미지 URL, 필수
- `description`: 설명, 선택

**불변 조건**:

- `name`은 null 또는 blank일 수 없습니다.
- `color`는 null 또는 blank일 수 없습니다.
- `imageUrl`은 null 또는 blank일 수 없습니다.
- `description`은 null 또는 blank를 허용합니다.

**동작**:

- 생성자에서 필수 값을 검증합니다.
- `update()`에서 필수 값을 검증한 뒤 상태를 변경합니다.

## CategoryValidationException

**역할**: Category 도메인 필수 값 검증 실패를 표현합니다.

**메시지 후보**:

- `카테고리 이름은 필수입니다.`
- `카테고리 색상은 필수입니다.`
- `카테고리 이미지 URL은 필수입니다.`

## ErrorResponse

**category validation 응답 값**:

- `status`: 400
- `code`: `CATEGORY.INVALID`
- `message`: 예외 메시지

## CategoryRequest

**역할**: API 요청 DTO입니다.

**규칙**:

- 기존 `@NotBlank` 검증은 유지합니다.
- `toEntity()`는 검증된 request 값을 Category 생성자로 전달합니다.
