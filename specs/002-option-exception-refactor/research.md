# Research: Option 패키지 예외 처리 리팩토링

## 결정 1: Option 전용 서비스 계층 도입

**Decision**: `OptionController`가 직접 수행하던 상품 조회, 옵션 조회, 중복 옵션명 검증, 마지막 옵션 삭제 제한 검사를 `OptionService`로 이동합니다.

**Rationale**: 현재 컨트롤러는 HTTP 요청 처리와 비즈니스 규칙을 동시에 담당합니다. `category` 패키지처럼 컨트롤러를 얇게 유지하면 테스트 범위가 명확해지고 예외 처리 흐름도 일관됩니다.

**Alternatives considered**:

- 컨트롤러에 로직 유지: 변경량은 적지만 예외 처리 리팩토링의 핵심 목표인 구조 일관성을 달성하지 못합니다.
- 도메인 엔티티에 모든 규칙 이동: 마지막 옵션 삭제 여부나 중복 옵션명은 repository 조회가 필요하므로 서비스 계층이 더 적합합니다.

## 결정 2: Option 도메인 예외 패키지 생성

**Decision**: `gift.option.exception` 패키지를 만들고 `OptionException`을 공통 부모로 둡니다.

**Rationale**: `Wish` 리팩토링과 같은 패턴으로 도메인별 예외를 모으면 실패 원인을 명확히 표현할 수 있고, `GlobalExceptionHandler`에서 상태 코드와 에러 코드를 안정적으로 매핑할 수 있습니다.

**Alternatives considered**:

- `IllegalArgumentException` 유지: 빠르지만 예외 의미가 불명확하고 글로벌 핸들러에서 정확한 에러 코드를 부여하기 어렵습니다.
- `category`와 같은 단일 `OptionNotFoundException`만 추가: 404에는 충분하지만 중복 옵션명, 삭제 제한, 검증 오류를 표현하기 부족합니다.

## 결정 3: 표준 JSON 에러 응답 사용

**Decision**: Option 예외는 `GlobalExceptionHandler`에서 `ErrorResponse`로 변환합니다.

**Rationale**: 최근 Wish 예외 처리와 같은 응답 형식을 사용하면 API 소비자가 에러 응답을 일관되게 처리할 수 있습니다.

**Alternatives considered**:

- 기존 `ResponseEntity<String>` 유지: 기존 테스트 작성은 단순하지만 다른 도메인과 응답 형식이 달라집니다.
- 도메인별 컨트롤러 advice 생성: 분리는 가능하지만 현재 규모에서는 글로벌 핸들러 확장이 더 단순합니다.

## 결정 4: HTTP 상태 코드 의미 유지

**Decision**: 미존재 상품/옵션은 404, 옵션명 중복/검증 실패/마지막 옵션 삭제 제한은 400으로 유지합니다.

**Rationale**: 기존 API의 의미와 클라이언트 기대를 유지하면서 응답 본문만 표준화합니다.

**Alternatives considered**:

- 중복 옵션명을 409 Conflict로 변경: 의미상 가능하지만 기존 API 계약 변경이므로 이번 리팩토링 범위에서 제외합니다.
