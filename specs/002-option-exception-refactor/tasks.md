# Tasks: Option 패키지 예외 처리 리팩토링

**Input**: `/specs/002-option-exception-refactor/`의 설계 문서  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/error-response.md, quickstart.md

**Tests**: FR-006과 SC-001, SC-005에 따라 테스트 작성이 필요합니다.

**Organization**: 각 사용자 시나리오를 독립적으로 구현하고 검증할 수 있도록 User Story 단위로 작업을 나눕니다.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: 서로 다른 파일을 수정하거나 직접적인 의존성이 없어 병렬로 진행할 수 있는 작업
- **[Story]**: `spec.md`의 User Story 식별자입니다. 예: US1, US2, US3
- 모든 구현 작업에는 대상 파일 경로를 명시합니다.

---

## Phase 1: Setup

**Purpose**: 리팩토링 전 현재 Option 동작과 기준이 되는 category/global 예외 처리 구조를 확인합니다.

- [x] T001 `./gradlew test --tests *Option*`를 실행하여 현재 Option 관련 테스트의 존재 여부와 실패 상태를 기록합니다.
- [x] T002 `./gradlew test --tests *Category*`를 실행하여 기준이 되는 category 동작이 정상인지 확인합니다.
- [x] T003 `specs/002-option-exception-refactor/contracts/error-response.md`의 error contract를 검토하고 테스트에서 검증할 error code와 message를 정리합니다.
- [x] T004 `src/main/java/gift/option/OptionController.java`의 현재 404/400 처리 branch와 `@ExceptionHandler` 위치를 확인합니다.

---

## Phase 2: Foundational

**Purpose**: 모든 User Story 구현에 앞서 필요한 Option 서비스 계층과 도메인 예외 타입을 추가합니다.

**CRITICAL**: 이 단계가 완료되기 전에는 controller 리팩토링을 시작하지 않습니다.

- [x] T005 [P] `src/main/java/gift/option/exception/OptionException.java`를 생성하여 Option 도메인 예외의 base runtime exception으로 사용합니다.
- [x] T006 [P] `src/main/java/gift/option/exception/OptionProductNotFoundException.java`를 생성하고 상품 미존재 contract message를 정의합니다.
- [x] T007 [P] `src/main/java/gift/option/exception/OptionNotFoundException.java`를 생성하고 옵션 미존재 contract message를 정의합니다.
- [x] T008 [P] `src/main/java/gift/option/exception/DuplicateOptionNameException.java`를 생성하고 중복 옵션명 contract message를 정의합니다.
- [x] T009 [P] `src/main/java/gift/option/exception/OptionDeletionNotAllowedException.java`를 생성하고 마지막 옵션 삭제 제한 contract message를 정의합니다.
- [x] T010 [P] `src/main/java/gift/option/exception/OptionValidationException.java`를 생성하고 옵션명 검증 실패 message를 받을 수 있도록 정의합니다.
- [x] T011 `src/main/java/gift/option/OptionService.java`를 생성하고 `OptionRepository`, `ProductRepository` 의존성을 주입받도록 구성합니다.
- [x] T012 `src/main/java/gift/global/GlobalExceptionHandler.java`의 기존 `ErrorResponse` helper를 Option 예외 handler에서도 재사용할 수 있는지 확인합니다.

**Checkpoint**: Option 동작을 변경하기 전에 공통 예외 및 서비스 타입이 compile 되는지 확인합니다.

---

## Phase 3: User Story 1 - Option 도메인 예외의 표준화된 응답 (Priority: P1) MVP

**Goal**: Option 도메인 오류가 기존 HTTP status 의미를 유지하면서 표준 JSON error payload로 응답되도록 합니다.

**Independent Test**: `/api/products/{productId}/options`에서 400, 404 오류를 발생시키고 HTTP status와 `ErrorResponse` JSON field를 검증합니다.

### Tests for User Story 1

- [x] T013 [P] [US1] 존재하지 않는 상품의 옵션 목록 조회 시 404와 `OPTION.PRODUCT_NOT_FOUND`를 반환하는 MockMvc test를 `src/test/java/gift/option/OptionControllerTest.java`에 추가합니다.
- [x] T014 [P] [US1] 존재하지 않는 상품에 옵션 생성 시 404와 `OPTION.PRODUCT_NOT_FOUND`를 반환하는 MockMvc test를 `src/test/java/gift/option/OptionControllerTest.java`에 추가합니다.
- [x] T015 [P] [US1] 존재하지 않는 옵션 삭제 시 404와 `OPTION.NOT_FOUND`를 반환하는 MockMvc test를 `src/test/java/gift/option/OptionControllerTest.java`에 추가합니다.
- [x] T016 [P] [US1] 중복 옵션명 생성 시 400과 `OPTION.DUPLICATE_NAME`을 반환하는 MockMvc test를 `src/test/java/gift/option/OptionControllerTest.java`에 추가합니다.
- [x] T017 [P] [US1] 마지막 옵션 삭제 시 400과 `OPTION.DELETE_NOT_ALLOWED`를 반환하는 MockMvc test를 `src/test/java/gift/option/OptionControllerTest.java`에 추가합니다.
- [x] T018 [P] [US1] 옵션명 검증 실패 시 400과 `OPTION.INVALID_NAME`을 반환하는 MockMvc test를 `src/test/java/gift/option/OptionControllerTest.java`에 추가합니다.
- [x] T019 [P] [US1] Option 예외 mapping을 검증하는 handler-level test를 `src/test/java/gift/global/GlobalExceptionHandlerTest.java`에 추가하거나 확장합니다.

### Implementation for User Story 1

- [x] T020 [US1] `src/main/java/gift/global/GlobalExceptionHandler.java`에 `OptionProductNotFoundException`, `OptionNotFoundException`용 404 `@ExceptionHandler` method를 추가합니다.
- [x] T021 [US1] `src/main/java/gift/global/GlobalExceptionHandler.java`에 `DuplicateOptionNameException`, `OptionDeletionNotAllowedException`, `OptionValidationException`용 400 `@ExceptionHandler` method를 추가합니다.
- [x] T022 [US1] `src/main/java/gift/option/OptionService.java`에 상품 존재 여부 확인 helper를 추가하고 없으면 `OptionProductNotFoundException`을 throw하도록 구현합니다.
- [x] T023 [US1] `src/main/java/gift/option/OptionService.java`에 옵션 존재 여부와 product 소속 여부 확인 helper를 추가하고 실패 시 `OptionNotFoundException`을 throw하도록 구현합니다.
- [x] T024 [US1] `src/main/java/gift/option/OptionService.java`에 옵션명 검증 실패 시 `OptionValidationException`을 throw하도록 구현합니다.
- [x] T025 [US1] `src/main/java/gift/option/OptionService.java`에 중복 옵션명 검증 실패 시 `DuplicateOptionNameException`을 throw하도록 구현합니다.
- [x] T026 [US1] `src/main/java/gift/option/OptionService.java`에 마지막 옵션 삭제 제한 실패 시 `OptionDeletionNotAllowedException`을 throw하도록 구현합니다.
- [x] T027 [US1] `./gradlew test --tests *Option* --tests *GlobalExceptionHandlerTest*`를 실행하여 Option error response test가 통과하는지 확인합니다.

**Checkpoint**: Option 400/404 오류가 표준 JSON 형식으로 응답되며 독립적으로 테스트 가능합니다.

---

## Phase 4: User Story 2 - Category와 동일한 패키지 구조 적용 (Priority: P2)

**Goal**: Option 예외 class와 handling 구조를 category package 패턴과 일치시키되, 새 표준 error response contract를 사용합니다.

**Independent Test**: package 위치와 import를 검토하고, 예외 처리가 `GlobalExceptionHandler`로 중앙화되었는지 확인합니다.

### Tests for User Story 2

- [x] T028 [P] [US2] `OptionService.getOptions`가 상품 미존재 시 `OptionProductNotFoundException`을 throw하는 unit test를 `src/test/java/gift/option/OptionServiceTest.java`에 추가합니다.
- [x] T029 [P] [US2] `OptionService.createOption`이 중복 옵션명에서 `DuplicateOptionNameException`을 throw하는 unit test를 `src/test/java/gift/option/OptionServiceTest.java`에 추가합니다.
- [x] T030 [P] [US2] `OptionService.deleteOption`이 옵션 미존재 또는 다른 상품의 옵션에서 `OptionNotFoundException`을 throw하는 unit test를 `src/test/java/gift/option/OptionServiceTest.java`에 추가합니다.
- [x] T031 [P] [US2] `OptionService.deleteOption`이 마지막 옵션 삭제에서 `OptionDeletionNotAllowedException`을 throw하는 unit test를 `src/test/java/gift/option/OptionServiceTest.java`에 추가합니다.

### Implementation for User Story 2

- [x] T032 [US2] `src/main/java/gift/option/OptionService.java`에 `getOptions(Long productId)`를 구현하고 `OptionResponse` list를 반환하도록 합니다.
- [x] T033 [US2] `src/main/java/gift/option/OptionService.java`에 `createOption(Long productId, OptionRequest request)`를 구현하고 저장된 `OptionResponse`와 location 생성을 위한 id를 반환할 수 있게 합니다.
- [x] T034 [US2] `src/main/java/gift/option/OptionService.java`에 `deleteOption(Long productId, Long optionId)`를 구현하고 삭제 성공 시 반환값 없이 완료하도록 합니다.
- [x] T035 [US2] `src/main/java/gift/option/OptionController.java`의 repository 의존성을 제거하고 `OptionService`만 주입받도록 변경합니다.
- [x] T036 [US2] `src/main/java/gift/option/OptionController.java`의 `getOptions`, `createOption`, `deleteOption`이 서비스 호출과 성공 응답 생성만 담당하도록 변경합니다.
- [x] T037 [US2] `src/main/java/gift/option/OptionController.java`의 `validateName` method와 개별 `@ExceptionHandler(IllegalArgumentException.class)`를 제거합니다.
- [x] T038 [US2] `rg "ResponseEntity.notFound|@ExceptionHandler|orElse\\(null\\)|IllegalArgumentException" src/main/java/gift/option`를 실행하고 남아 있는 controller-level Option error branch를 제거합니다.

**Checkpoint**: Option package는 전용 `exception` package와 `OptionService`를 가지며, 모든 Option error 변환은 중앙 handler에서 처리됩니다.

---

## Phase 5: User Story 3 - 기존 Option 기능 동작 유지 (Priority: P3)

**Goal**: 리팩토링 후에도 기존 Option API의 성공 응답 동작과 HTTP status contract가 유지되도록 합니다.

**Independent Test**: 기존 및 신규 Option integration test를 통과시키고, success flow와 error status code가 유지되는지 확인합니다.

### Tests for User Story 3

- [x] T039 [P] [US3] 옵션 목록 조회가 200과 option list를 반환하는 MockMvc test를 `src/test/java/gift/option/OptionControllerTest.java`에 추가하거나 유지합니다.
- [x] T040 [P] [US3] 새 옵션 생성이 201과 `Location` header를 반환하는 MockMvc test를 `src/test/java/gift/option/OptionControllerTest.java`에 추가하거나 유지합니다.
- [x] T041 [P] [US3] 옵션 삭제가 204를 반환하는 MockMvc test를 `src/test/java/gift/option/OptionControllerTest.java`에 추가하거나 유지합니다.

### Implementation for User Story 3

- [x] T042 [US3] `src/main/java/gift/option/OptionController.java`의 success response가 기존처럼 200, 201, 204를 반환하는지 확인합니다.
- [x] T043 [US3] `src/main/java/gift/option/OptionService.java`가 기존 옵션명 검증 규칙과 중복 생성 방지 규칙을 유지하는지 확인합니다.
- [x] T044 [US3] `src/main/java/gift/option/OptionService.java`가 상품의 마지막 옵션 삭제 금지 규칙을 유지하는지 확인합니다.
- [x] T045 [US3] `./gradlew test --tests *Option*`를 실행하여 Option 동작을 검증합니다.
- [x] T046 [US3] `./gradlew test --tests *Category*`를 실행하여 category 기준 동작이 깨지지 않았는지 검증합니다.

**Checkpoint**: 리팩토링된 Option 기능이 성공 flow와 status code 측면에서 기존 동작과 호환됩니다.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: 최종 일관성 확인, 문서 정리, 전체 검증을 수행합니다.

- [x] T047 [P] 구현 과정에서 실제 Korean message나 error code가 달라졌다면 `specs/002-option-exception-refactor/contracts/error-response.md`를 업데이트합니다.
- [x] T048 [P] test command나 class name이 변경되었다면 `specs/002-option-exception-refactor/quickstart.md`를 업데이트합니다.
- [x] T049 `src/main/java/gift/option`, `src/main/java/gift/global`에서 unused import와 더 이상 사용하지 않는 controller helper를 정리합니다.
- [x] T050 `./gradlew test`를 실행하여 전체 regression suite를 검증합니다.
- [x] T051 `rg "ResponseEntity.notFound|@ExceptionHandler|orElse\\(null\\)" src/main/java/gift/option src/main/java/gift/global`를 실행하여 의도하지 않은 예외 처리 분산이 남았는지 확인합니다.
- [x] T052 동작 변경이 포함된 작업들이 테스트로 검증되었는지 확인한 뒤 feature 완료 여부를 판단합니다.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: 의존성 없음
- **Foundational (Phase 2)**: Setup 이후 진행하며 모든 User Story를 block합니다.
- **US1 (Phase 3)**: Foundational 이후 진행하며 MVP입니다.
- **US2 (Phase 4)**: Foundational 이후 시작할 수 있지만, controller/service 구조 정리는 US1 handler mapping 이후 마무리하는 것이 좋습니다.
- **US3 (Phase 5)**: 최종 refactor 결과를 대상으로 regression을 확인하므로 US1/US2 구현 이후 진행합니다.
- **Polish (Phase 6)**: 필요한 User Story가 완료된 뒤 진행합니다.

### User Story Dependencies

- **US1 (P1)**: 표준화된 Option error response를 먼저 확립해야 하므로 가장 먼저 완료합니다.
- **US2 (P2)**: foundational exception class와 service skeleton이 준비되면 시작할 수 있고, US1 handler mapping 이후 최종 정리합니다.
- **US3 (P3)**: US1/US2 변경 이후 최종 구현 상태를 검증합니다.

### Within Each User Story

- 구현 전에 실패하는 테스트를 먼저 작성합니다.
- controller 리팩토링 전에 exception type과 service method를 먼저 추가합니다.
- controller의 status branch를 제거하기 전에 service의 exception flow를 먼저 변경합니다.
- 다음 User Story로 넘어가기 전에 targeted test를 실행합니다.

---

## Parallel Opportunities

- T005-T010은 각각 별도 exception class를 생성하므로 병렬 진행할 수 있습니다.
- T013-T019는 독립적인 test case이므로 병렬 작성할 수 있습니다.
- T028-T031은 exception type이 준비된 뒤 controller test와 병렬로 작성할 수 있습니다.
- T039-T041은 각각 별도의 Option API behavior를 검증하므로 병렬 작성할 수 있습니다.
- T047-T048은 구현이 확정된 뒤 병렬로 문서 업데이트가 가능합니다.

---

## Implementation Strategy

### MVP First

1. T001-T012를 완료합니다.
2. US1 작업인 T013-T027을 완료합니다.
3. `./gradlew test --tests *Option* --tests *GlobalExceptionHandlerTest*`로 Option 400/404 응답을 검증한 뒤 멈춰서 확인합니다.

### Incremental Delivery

1. Option exception 기반과 service skeleton을 만듭니다.
2. 표준화된 Option error response를 제공합니다.
3. package/controller/service 구조를 category 패턴과 맞춥니다.
4. 기존 Option success behavior와 전체 regression을 검증합니다.

### Notes

- 공개 Option API의 성공 status code는 유지합니다. 목록 조회는 200, 생성은 201, 삭제는 204입니다.
- 미존재 상품/옵션은 404, 옵션명 중복/검증 실패/마지막 옵션 삭제 제한은 400으로 유지합니다.
- product, member, order 예외 리팩토링으로 scope를 확장하지 않습니다.
