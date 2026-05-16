# Data Model: Option 패키지 예외 처리 리팩토링

## Option

상품에 속한 선택 옵션입니다.

**Fields**:

- `id`: 옵션 식별자
- `product`: 옵션이 속한 상품
- `name`: 옵션명
- `quantity`: 옵션 재고 수량

**Rules**:

- 옵션명은 `OptionNameValidator`의 허용 문자와 길이 제약을 만족해야 합니다.
- 같은 상품 안에서는 옵션명이 중복될 수 없습니다.
- 상품은 최소 1개의 옵션을 유지해야 하므로 마지막 옵션은 삭제할 수 없습니다.

## OptionException

Option 도메인 예외의 공통 부모입니다.

**Derived types**:

- `OptionProductNotFoundException`: 대상 상품이 존재하지 않을 때 발생합니다.
- `OptionNotFoundException`: 대상 옵션이 존재하지 않거나 해당 상품에 속하지 않을 때 발생합니다.
- `DuplicateOptionNameException`: 같은 상품에 동일한 옵션명이 이미 존재할 때 발생합니다.
- `OptionDeletionNotAllowedException`: 마지막 옵션 삭제를 시도할 때 발생합니다.
- `OptionValidationException`: 옵션명 검증 규칙을 통과하지 못할 때 발생합니다.

## ErrorResponse

API 클라이언트에 반환되는 표준 에러 응답입니다.

**Fields**:

- `code`: 클라이언트가 분기할 수 있는 안정적인 에러 코드
- `message`: 사용자 또는 개발자가 이해할 수 있는 설명
- `timestamp`: 에러 응답 생성 시각
- `details`: 추가 검증 정보가 필요할 때 사용하는 확장 필드

## OptionService

Option API의 비즈니스 규칙을 처리하는 서비스입니다.

**Responsibilities**:

- 상품 존재 여부 확인
- 옵션 목록 조회
- 옵션 생성 전 이름 검증 및 중복 확인
- 옵션 삭제 전 상품, 옵션, 최소 옵션 개수 규칙 확인
- 실패 상황에서 Option 도메인 예외 발생
