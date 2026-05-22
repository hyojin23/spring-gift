# 기능 명세서: Category 도메인 검증 강화 리팩토링

**Feature Branch**: `032-category-domain-validation-refactor`  
**작성일**: 2026-05-22  
**상태**: 초안  
**입력**: "Category 도메인 검증 강화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - Category 생성 시 필수 값 검증 (우선순위: P1)

`Category`는 생성될 때 name, color, imageUrl이 null 또는 blank이면 생성되지 않아야 합니다.

**우선순위 이유**: 현재 요청 DTO에는 `@NotBlank`가 있지만 도메인 객체 자체는 잘못된 상태를 허용합니다. controller를 거치지 않는 service/test/내부 코드에서도 Category 불변 조건이 지켜져야 합니다.

**독립적 테스트**: `new Category(null/blank, color, imageUrl, description)` 등 필수 값 누락 케이스에서 `CategoryValidationException`이 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** name이 blank일 때, **When** Category를 생성하면, **Then** category 검증 예외가 발생합니다.
2. **Given** color가 blank일 때, **When** Category를 생성하면, **Then** category 검증 예외가 발생합니다.
3. **Given** imageUrl이 blank일 때, **When** Category를 생성하면, **Then** category 검증 예외가 발생합니다.

---

### 사용자 시나리오 2 - Category 수정 시 필수 값 검증 (우선순위: P1)

`Category.update()`는 name, color, imageUrl이 null 또는 blank인 값으로 도메인을 변경하지 않아야 합니다.

**우선순위 이유**: 생성 시점뿐 아니라 수정 시점에도 도메인이 잘못된 상태가 되지 않아야 합니다.

**독립적 테스트**: 정상 Category에 blank name/color/imageUrl로 update를 호출하면 `CategoryValidationException`이 발생하는지 검증합니다.

---

### 사용자 시나리오 3 - Category 검증 예외를 400 ErrorResponse로 변환 (우선순위: P1)

Category 도메인 검증 실패는 global handler에서 400 status와 category error code로 응답해야 합니다.

**우선순위 이유**: product/member/option/order 도메인 검증 예외처럼 category 검증 실패도 API 에러 응답 형식이 일관되어야 합니다.

**독립적 테스트**: `GlobalExceptionHandler`가 `CategoryValidationException`을 `CATEGORY.INVALID` code와 예외 메시지로 변환하는지 검증합니다.

---

### 사용자 시나리오 4 - 기존 요청 DTO 검증과 정상 API 유지 (우선순위: P2)

기존 `CategoryRequest`의 `@NotBlank` 검증과 카테고리 생성/수정 성공 흐름은 유지되어야 합니다.

**우선순위 이유**: 이번 작업은 도메인 안전망을 추가하는 것이며 기존 API 계약을 깨뜨리면 안 됩니다.

**독립적 테스트**: 기존 `CategoryControllerTest`가 계속 통과해야 합니다.

---

### 엣지 케이스

- description은 기존처럼 필수 값으로 검증하지 않습니다.
- color 형식(`#FFFFFF` 등)의 정규식 검증은 이번 작업에 포함하지 않습니다.
- imageUrl 형식 검증은 이번 작업에 포함하지 않습니다.
- `CategoryNotFoundException` 패키지 이동은 이번 작업에 포함하지 않습니다.
- `CategoryRequest`의 bean validation은 유지합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `Category` 생성자는 name null/blank를 허용하지 않아야 합니다.
- **FR-002**: `Category` 생성자는 color null/blank를 허용하지 않아야 합니다.
- **FR-003**: `Category` 생성자는 imageUrl null/blank를 허용하지 않아야 합니다.
- **FR-004**: `Category.update()`는 name null/blank를 허용하지 않아야 합니다.
- **FR-005**: `Category.update()`는 color null/blank를 허용하지 않아야 합니다.
- **FR-006**: `Category.update()`는 imageUrl null/blank를 허용하지 않아야 합니다.
- **FR-007**: 검증 실패 시 `CategoryValidationException`을 던져야 합니다.
- **FR-008**: `CategoryValidationException`은 global handler에서 400 `ErrorResponse`로 변환되어야 합니다.
- **FR-009**: category 검증 실패 error code는 `CATEGORY.INVALID`여야 합니다.
- **FR-010**: `CategoryTest`를 추가해야 합니다.
- **FR-011**: `GlobalExceptionHandlerTest`에 category 검증 예외 테스트를 추가해야 합니다.

### 주요 엔티티

- **Category**: 카테고리 도메인 객체입니다.
- **CategoryValidationException**: 카테고리 도메인 검증 실패를 표현합니다.
- **CategoryRequest**: 카테고리 API 요청 DTO입니다.
- **GlobalExceptionHandler**: category 검증 예외를 HTTP 응답으로 변환합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: name/color/imageUrl이 null 또는 blank인 Category 생성은 실패합니다.
- **SC-002**: name/color/imageUrl이 null 또는 blank인 Category update는 실패합니다.
- **SC-003**: Category 검증 예외는 400과 `CATEGORY.INVALID` body로 변환됩니다.
- **SC-004**: 기존 category controller 성공 테스트가 통과합니다.
- **SC-005**: `./gradlew test --tests *Category* --tests *GlobalExceptionHandler*`가 통과합니다.
- **SC-006**: 전체 테스트가 통과합니다.

## 가정사항

- `030-category-exception-response-refactor`에서 category 미존재 응답은 `ErrorResponse`로 일관화되었습니다.
- 이번 작업은 Category 도메인 필수 값 검증에 집중하며 color/imageUrl 형식 검증은 후속 작업으로 분리합니다.
