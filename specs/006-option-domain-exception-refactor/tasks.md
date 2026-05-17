# Tasks: Option 도메인 예외 리팩토링

**Input**: `/specs/006-option-domain-exception-refactor/`의 설계 문서  
**Prerequisites**: spec.md, plan.md, research.md, data-model.md, quickstart.md

**Tests**: SC-001부터 SC-005에 따라 Option domain test, global handler test, Option 회귀 테스트 실행이 필요합니다.

**Organization**: 예외 타입 추가, 도메인 교체, global handler 매핑, 검증 순서로 진행합니다.

## Format: `[ID] [P?] Description`

- **[P]**: 서로 다른 파일을 수정하거나 직접적인 의존성이 없어 병렬로 진행할 수 있는 작업
- 모든 구현 작업에는 대상 파일 경로를 명시합니다.

---

## Phase 1: Setup

**Purpose**: 현재 Option 패키지의 `IllegalArgumentException` 사용 지점과 handler/test 기준을 확인합니다.

- [x] T001 `rg "IllegalArgumentException" src/main/java/gift/option src/test/java/gift/option`로 현재 사용 지점을 확인합니다.
- [x] T002 `src/main/java/gift/option/exception/OptionException.java`와 기존 Option 예외 구조를 확인합니다.
- [x] T003 `src/main/java/gift/global/GlobalExceptionHandler.java`의 Option 예외 handler 구조를 확인합니다.
- [x] T004 `src/test/java/gift/global/GlobalExceptionHandlerTest.java`의 Option handler 테스트 구조를 확인합니다.
- [x] T005 `./gradlew test --tests *Option* --tests *GlobalExceptionHandlerTest*`를 실행하여 변경 전 기준 상태를 기록합니다.

---

## Phase 2: Tests

**Purpose**: 수량 오류가 Option 도메인 예외와 표준 응답으로 처리되어야 함을 테스트로 명확히 표현합니다.

- [x] T006 `src/test/java/gift/option/OptionTest.java`의 수량 검증 실패 기대 예외를 `OptionQuantityException`으로 변경합니다.
- [x] T007 `src/test/java/gift/global/GlobalExceptionHandlerTest.java`에 `OptionQuantityException`이 HTTP 400으로 매핑되는 테스트를 추가합니다.
- [x] T008 `GlobalExceptionHandlerTest`에서 error code `OPTION.INVALID_QUANTITY`와 message 전달을 검증합니다.

---

## Phase 3: Implementation

**Purpose**: Option 수량 검증 실패를 Option 도메인 예외로 교체하고 global handler에 매핑합니다.

- [x] T009 [P] `src/main/java/gift/option/exception/OptionQuantityException.java`를 추가하고 `OptionException`을 상속하도록 구현합니다.
- [x] T010 `src/main/java/gift/option/Option.java`의 수량 검증 실패 예외를 `OptionQuantityException`으로 변경합니다.
- [x] T011 `src/main/java/gift/global/GlobalExceptionHandler.java`에 `OptionQuantityException` handler를 추가합니다.
- [x] T012 `GlobalExceptionHandler`의 `OptionQuantityException` handler가 HTTP 400과 `OPTION.INVALID_QUANTITY`를 반환하도록 구현합니다.

---

## Phase 4: Verification

**Purpose**: Option 도메인 예외 통일이 완료되었고 기존 API 동작은 유지되는지 확인합니다.

- [x] T013 `./gradlew test --tests *OptionTest*`를 실행하여 도메인 예외 테스트를 확인합니다.
- [x] T014 `./gradlew test --tests *GlobalExceptionHandlerTest*`를 실행하여 handler 매핑을 확인합니다.
- [x] T015 `./gradlew test --tests *Option* --tests *GlobalExceptionHandlerTest*`를 실행하여 관련 회귀 테스트를 확인합니다.
- [x] T016 `rg "throw new IllegalArgumentException" src/main/java/gift/option`로 직접 `IllegalArgumentException` throw가 남지 않았는지 확인합니다.
- [x] T017 `rg "OptionQuantityException|OPTION.INVALID_QUANTITY" src/main/java src/test/java`로 예외 타입과 error code 사용 지점을 확인합니다.

---

## Dependencies & Execution Order

- Phase 1은 의존성 없이 먼저 수행합니다.
- T006-T008은 구현 전에 진행합니다.
- T009 이후 T010-T012를 진행합니다.
- 구현이 끝난 뒤 T013-T017로 검증합니다.

## Parallel Opportunities

- T001-T004는 독립적인 읽기 작업이므로 병렬로 수행할 수 있습니다.
- T009와 T007-T008은 서로 다른 파일이므로 병렬로 진행할 수 있습니다.
- T013-T014는 필요하면 독립적으로 실행할 수 있지만, 최종적으로 T015를 실행합니다.

## Implementation Strategy

1. Option 패키지의 직접 `IllegalArgumentException` 사용 지점을 확인합니다.
2. `OptionQuantityException`을 추가합니다.
3. `Option` 수량 검증 실패 예외를 새 도메인 예외로 교체합니다.
4. `GlobalExceptionHandler`에 `OPTION.INVALID_QUANTITY` 매핑을 추가합니다.
5. domain test, handler test, Option 회귀 테스트로 확인합니다.

## Notes

- Bean Validation 예외 처리는 이번 범위에 포함하지 않습니다.
- 옵션명 검증 흐름은 이번 범위에 포함하지 않습니다.
- `OptionValidationException`의 error code는 기존 `OPTION.INVALID_NAME`을 유지합니다.
- 다른 도메인의 `IllegalArgumentException`은 이번 범위에 포함하지 않습니다.
