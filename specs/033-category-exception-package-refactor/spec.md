# 기능 명세서: Category 예외 패키지 정리 리팩토링

**Feature Branch**: `033-category-exception-package-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "033-category-exception-package-refactor"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - Category 예외 클래스를 exception 패키지로 모으기 (우선순위: P1)

개발자는 category 패키지의 예외 클래스를 다른 도메인 패키지처럼 `gift.category.exception`에서 찾을 수 있어야 합니다.

**우선순위 이유**: product, member, option, order, auth 패키지는 도메인 예외를 `exception` 하위 패키지로 관리하고 있습니다. category만 루트 패키지에 예외가 남아 있으면 패키지 구조가 불규칙해집니다.

**독립적 테스트**: category 관련 테스트와 global handler 테스트가 기존과 동일하게 통과해야 합니다.

**승인 시나리오**:

1. **Given** category 예외 클래스를 확인할 때, **When** 패키지 구조를 보면, **Then** `gift.category.exception` 하위에 위치합니다.
2. **Given** 존재하지 않는 category를 조회할 때, **When** service가 예외를 던지면, **Then** 기존 `CATEGORY.NOT_FOUND` 응답 동작이 유지됩니다.
3. **Given** 잘못된 category 값을 생성/수정할 때, **When** 도메인 검증 예외가 발생하면, **Then** 기존 `CATEGORY.INVALID` 응답 동작이 유지됩니다.

---

### 사용자 시나리오 2 - 기존 API 예외 응답 유지 (우선순위: P1)

예외 클래스의 패키지만 이동하고 API 응답 status, code, message는 변경하지 않아야 합니다.

**우선순위 이유**: 이번 작업은 구조 정리이며 외부 API 계약이 변경되면 안 됩니다.

**독립적 테스트**: `CategoryControllerTest`, `CategoryTest`, `GlobalExceptionHandlerTest`가 통과해야 합니다.

---

### 엣지 케이스

- 예외 메시지 문구는 변경하지 않습니다.
- `GlobalExceptionHandler`의 error code는 변경하지 않습니다.
- `CategoryService`의 비즈니스 로직은 변경하지 않습니다.
- category 요청 DTO 검증 정책은 변경하지 않습니다.
- 기존 테스트의 기대 status/body는 변경하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `CategoryNotFoundException`은 `gift.category.exception` 패키지로 이동해야 합니다.
- **FR-002**: `CategoryValidationException`은 `gift.category.exception` 패키지로 이동해야 합니다.
- **FR-003**: category 예외를 사용하는 모든 import는 새 패키지를 참조해야 합니다.
- **FR-004**: `GlobalExceptionHandler`는 이동된 category 예외를 기존과 동일하게 처리해야 합니다.
- **FR-005**: category 미존재 응답은 기존처럼 404와 `CATEGORY.NOT_FOUND` code를 유지해야 합니다.
- **FR-006**: category 검증 실패 응답은 기존처럼 400과 `CATEGORY.INVALID` code를 유지해야 합니다.
- **FR-007**: category 도메인/서비스/global handler 테스트는 통과해야 합니다.

### 주요 엔티티

- **CategoryNotFoundException**: category 미존재 상황을 표현하는 도메인 예외입니다.
- **CategoryValidationException**: category 도메인 검증 실패를 표현하는 도메인 예외입니다.
- **CategoryService**: category 조회/생성/수정 흐름에서 category 예외를 발생시킵니다.
- **GlobalExceptionHandler**: category 예외를 표준 `ErrorResponse`로 변환합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: category 예외 클래스가 `src/main/java/gift/category/exception` 아래에 위치합니다.
- **SC-002**: category 예외 import가 새 패키지로 정리됩니다.
- **SC-003**: category 미존재 예외 응답의 status/code/message가 유지됩니다.
- **SC-004**: category 검증 예외 응답의 status/code/message가 유지됩니다.
- **SC-005**: `./gradlew.bat test --tests *Category* --tests *GlobalExceptionHandler*`가 통과합니다.
- **SC-006**: 전체 테스트가 통과합니다.

## 가정사항

- `030-category-exception-response-refactor`에서 category 미존재 예외 응답은 이미 전역 handler로 일관화되었습니다.
- `032-category-domain-validation-refactor`에서 `CategoryValidationException`이 추가되었습니다.
- 이번 작업은 패키지 구조 정리에 집중하며 예외 계층의 base class 추가는 후속 작업으로 분리합니다.
