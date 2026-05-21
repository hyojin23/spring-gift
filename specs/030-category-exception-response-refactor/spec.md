# 기능 명세서: Category 예외 응답 일관화 리팩토링

**Feature Branch**: `030-category-exception-response-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "Category 예외 응답 일관화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 카테고리 미존재 404 응답에 ErrorResponse 반환 (우선순위: P1)

존재하지 않는 카테고리를 조회/수정/삭제하려 할 때 category도 다른 도메인처럼 `ErrorResponse` body를 포함한 404 응답을 반환해야 합니다.

**우선순위 이유**: 현재 `CategoryNotFoundException`만 `ResponseEntity<Void>`로 처리되어 에러 body가 없습니다. 클라이언트 입장에서는 category 오류만 code/message를 받을 수 없어 API 에러 응답 일관성이 깨집니다.

**독립적 테스트**: `GlobalExceptionHandler.handleCategoryNotFound()`가 404 status와 `CATEGORY.NOT_FOUND` code, 예외 메시지를 포함하는지 검증합니다.

**승인 시나리오**:

1. **Given** `CategoryNotFoundException`이 발생할 때, **When** global handler가 처리하면, **Then** 404와 `CATEGORY.NOT_FOUND` 에러 body를 반환합니다.

---

### 사용자 시나리오 2 - 카테고리 API 미존재 응답 body 검증 (우선순위: P1)

카테고리 API에서 존재하지 않는 id를 사용하면 404 status뿐 아니라 JSON error body가 반환되어야 합니다.

**우선순위 이유**: global handler 단위 테스트만으로는 실제 controller 응답 body까지 보장하기 어렵습니다. API 테스트에서 외부 응답 계약을 고정해야 합니다.

**독립적 테스트**: 존재하지 않는 카테고리 수정 요청이 `code=CATEGORY.NOT_FOUND`, `message=카테고리를 찾을 수 없습니다.`를 반환하는지 검증합니다.

---

### 사용자 시나리오 3 - 기존 성공 응답 유지 (우선순위: P2)

카테고리 목록 조회, 생성, 수정, 삭제 성공 흐름은 기존과 동일해야 합니다.

**우선순위 이유**: 이번 작업은 예외 응답 일관화이며 정상 API 계약을 변경하면 안 됩니다.

**독립적 테스트**: 기존 `CategoryControllerTest` 성공 케이스가 그대로 통과해야 합니다.

---

### 엣지 케이스

- `CategoryNotFoundException` 메시지는 기존 한글 메시지를 유지합니다.
- HTTP status는 기존처럼 404를 유지합니다.
- error code는 `CATEGORY.NOT_FOUND`로 통일합니다.
- category exception class의 패키지 이동은 이번 작업에 포함하지 않습니다.
- 다른 도메인 예외 응답은 변경하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `CategoryNotFoundException` handler는 `ResponseEntity<ErrorResponse>`를 반환해야 합니다.
- **FR-002**: category 미존재 error code는 `CATEGORY.NOT_FOUND`여야 합니다.
- **FR-003**: category 미존재 error message는 예외 메시지를 사용해야 합니다.
- **FR-004**: category 미존재 HTTP status는 404를 유지해야 합니다.
- **FR-005**: `GlobalExceptionHandlerTest`에 category 미존재 예외 테스트를 추가해야 합니다.
- **FR-006**: `CategoryControllerTest`의 미존재 category 응답 body 검증을 추가해야 합니다.
- **FR-007**: 기존 category 성공 API 테스트는 계속 통과해야 합니다.

### 주요 엔티티

- **CategoryNotFoundException**: 카테고리를 찾을 수 없을 때 발생하는 예외입니다.
- **GlobalExceptionHandler**: 도메인 예외를 HTTP error response로 변환합니다.
- **ErrorResponse**: API 에러 code/message 응답 DTO입니다.
- **CategoryController**: 카테고리 API endpoint를 제공합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `CategoryNotFoundException`은 404와 `CATEGORY.NOT_FOUND` body로 변환됩니다.
- **SC-002**: 존재하지 않는 카테고리 수정 API는 `code`, `message` JSON body를 반환합니다.
- **SC-003**: `./gradlew test --tests *CategoryController* --tests *GlobalExceptionHandler*`가 통과합니다.
- **SC-004**: 전체 테스트가 통과합니다.

## 가정사항

- 현재 다른 도메인 예외는 `ErrorResponse` 형태로 응답하고 있습니다.
- 이번 작업은 category 예외 응답 일관화에 한정하며 category 도메인 구조 변경은 후속 작업으로 분리합니다.
