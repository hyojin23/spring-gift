# 기능 명세서: Option 상품 범위 조회 리팩토링

**Feature Branch**: `004-option-product-scoped-lookup-refactor`  
**작성일**: 2026-05-17  
**상태**: 초안  
**입력**: "옵션 조회를 findById + product filter 대신 repository 메서드로 표현하기"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 삭제 대상 옵션 조회 의도 명확화 (우선순위: P1)

Option 삭제 시 시스템은 요청한 상품에 속한 옵션만 삭제해야 합니다. 현재 구현은 `findById(optionId)`로 옵션을 조회한 뒤 서비스 계층에서 product id를 filter로 비교할 수 있으므로, repository method 이름으로 "해당 상품에 속한 옵션 조회" 의도를 직접 표현하도록 개선합니다.

**우선순위 이유**: 삭제 대상 옵션 조회는 단순 옵션 ID 조회가 아니라 product scope가 포함된 조회입니다. repository method로 이를 표현하면 서비스 로직이 짧아지고, 다른 상품의 옵션을 삭제하지 않는다는 규칙이 더 선명해집니다.

**독립적 테스트**: `OptionService.deleteOption`에서 존재하지 않는 옵션 또는 요청 상품에 속하지 않는 옵션은 기존처럼 `OptionNotFoundException`이 발생하고, 요청 상품에 속한 옵션은 삭제되는지 검증합니다.

**승인 시나리오**:

1. **Given** 상품에 옵션이 2개 이상 있고 삭제 대상 옵션이 요청 상품에 속할 때, **When** 삭제를 요청하면, **Then** 기존과 동일하게 옵션이 삭제됩니다.
2. **Given** 상품에 옵션이 2개 이상이고 삭제 대상 옵션이 존재하지 않을 때, **When** 삭제를 요청하면, **Then** 기존과 동일하게 옵션 미존재 예외가 발생합니다.
3. **Given** 삭제 대상 옵션이 다른 상품에 속할 때, **When** 요청 상품의 옵션 삭제 API로 삭제를 요청하면, **Then** 기존과 동일하게 옵션 미존재 예외가 발생합니다.

---

### 사용자 시나리오 2 - 기존 API 계약 유지 (우선순위: P2)

조회 method 변경은 내부 구현만 변경하며, 외부 API의 HTTP status, error code, message, 성공 응답은 변경하지 않습니다.

**우선순위 이유**: 이 작업은 코드 의도와 유지보수성 개선을 위한 리팩토링입니다. 클라이언트가 체감하는 동작은 그대로 유지되어야 합니다.

**독립적 테스트**: 기존 Option controller/service 테스트가 동일하게 통과하는지 확인합니다.

**승인 시나리오**:

1. 존재하지 않는 옵션 삭제 요청은 기존처럼 HTTP 404와 `OPTION.NOT_FOUND` 응답으로 변환됩니다.
2. 다른 상품에 속한 옵션 삭제 요청도 기존처럼 HTTP 404와 `OPTION.NOT_FOUND` 응답으로 변환됩니다.
3. 정상 삭제 요청은 기존처럼 HTTP 204를 반환합니다.

---

### 엣지 케이스

- 상품이 존재하지 않으면 삭제 대상 옵션 조회 전에 기존처럼 상품 미존재 예외가 발생합니다.
- 상품의 옵션 개수가 1개 이하이면 삭제 대상 옵션 조회 전에 기존처럼 마지막 옵션 삭제 제한 예외가 발생합니다.
- 삭제 대상 옵션이 존재하지 않으면 옵션 미존재 예외가 발생해야 합니다.
- 삭제 대상 옵션이 존재해도 요청한 상품에 속하지 않으면 옵션 미존재 예외가 발생해야 합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `OptionRepository`는 옵션 ID와 상품 ID를 함께 사용해 옵션을 조회하는 repository method를 제공해야 합니다.
- **FR-002**: `OptionService.deleteOption`은 `findById(optionId)` 후 product id를 filter로 비교하는 방식 대신 product-scoped repository method를 사용해야 합니다.
- **FR-003**: 삭제 대상 옵션이 없거나 요청 상품에 속하지 않으면 기존과 동일하게 `OptionNotFoundException`을 발생시켜야 합니다.
- **FR-004**: Option 삭제 API의 성공/실패 HTTP 응답 계약은 변경하지 않아야 합니다.
- **FR-005**: 삭제 대상 옵션 조회 흐름을 검증하는 단위 테스트 또는 기존 테스트를 product-scoped lookup 기준으로 갱신해야 합니다.

### 주요 엔티티

- **OptionRepository**: 옵션 ID와 상품 ID를 조건으로 삭제 대상 옵션을 조회합니다.
- **OptionService**: 옵션 삭제 시 상품 존재 여부, 마지막 옵션 삭제 제한, 삭제 대상 옵션의 상품 소속 여부를 검증합니다.
- **OptionNotFoundException**: 삭제 대상 옵션이 없거나 요청 상품에 속하지 않을 때 발생하는 도메인 예외입니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `OptionService.deleteOption`은 `findById(optionId).filter(...)` 방식 대신 product-scoped repository method를 사용합니다.
- **SC-002**: 존재하지 않는 옵션 삭제 테스트가 통과합니다.
- **SC-003**: 다른 상품에 속한 옵션 삭제 시 `OptionNotFoundException`이 발생하는 테스트가 통과합니다.
- **SC-004**: 정상 옵션 삭제 테스트가 통과합니다.
- **SC-005**: `./gradlew test --tests *Option*`이 통과합니다.
- **SC-006**: 외부 API 응답 계약의 변경이 없습니다.

## 가정사항

- Option 예외 처리 구조는 `002-option-exception-refactor`에서 이미 완료되었습니다.
- Option 삭제 가능 여부 count query 리팩토링은 `003-option-deletion-validation-refactor`에서 이미 완료되었습니다.
- 이번 작업은 삭제 대상 옵션 조회의 내부 표현만 변경합니다.
- 데이터베이스 schema 변경은 필요하지 않습니다.
- product, order 등 다른 도메인의 리팩토링은 이번 범위에 포함하지 않습니다.
