# Tasks: Wish 패키지 예외 처리 리팩토링

**Input**: `/specs/001-wish-exception-refactor/`의 설계 문서
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/error-response.md, quickstart.md

**Tests**: FR-005와 SC-001에 따라 테스트 작성이 필요합니다.

**Organization**: 각 사용자 시나리오를 독립적으로 구현하고 검증할 수 있도록 User Story 단위로 작업을 나눕니다.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: 서로 다른 파일을 수정하거나 직접적인 의존성이 없어 병렬로 진행할 수 있는 작업
- **[Story]**: `spec.md`의 User Story 식별자입니다. 예: US1, US2, US3
- 모든 구현 작업에는 대상 파일 경로를 명시합니다.

---

## Phase 1: Setup

**Purpose**: 리팩토링 전 현재 기준 상태와 기대하는 error contract를 확인합니다.

- [ ] T001 `./gradlew test --tests *Wish*`를 실행하여 현재 Wish 관련 테스트의 존재 여부와 실패 상태를 기록합니다.
- [ ] T002 `./gradlew test --tests *Category*`를 실행하여 기준이 되는 category 동작이 정상인지 확인합니다.
- [ ] T003 `specs/001-wish-exception-refactor/contracts/error-response.md`의 error contract를 검토하고 테스트에서 검증할 error code와 message를 정리합니다.

---

## Phase 2: Foundational

**Purpose**: 모든 User Story 구현에 앞서 필요한 공통 error response 구조와 Wish 예외 타입을 추가합니다.

**CRITICAL**: 이 단계가 완료되기 전에는 controller/service 리팩토링을 시작하지 않습니다.

- [ ] T004 `src/main/java/gift/global/exception/ErrorResponse.java`를 생성하고 `code`, `message`, `timestamp`, optional `details` 필드를 정의합니다.
- [ ] T005 [P] `src/main/java/gift/wish/exception/WishException.java`를 생성하여 Wish 도메인 예외의 base runtime exception으로 사용합니다.
- [ ] T006 [P] `src/main/java/gift/wish/exception/WishNotFoundException.java`를 생성하고 존재하지 않는 Wish 항목에 대한 contract message를 정의합니다.
- [ ] T007 [P] `src/main/java/gift/wish/exception/UnauthorizedWishAccessException.java`를 생성하고 권한 없는 Wish 접근에 대한 contract message를 정의합니다.
- [ ] T008 [P] `src/main/java/gift/wish/exception/AuthenticationException.java`를 생성하고 인증 정보 누락/오류에 대한 contract message를 정의합니다.
- [ ] T009 `src/main/java/gift/global/GlobalExceptionHandler.java`에 `ErrorResponse`를 일관되게 생성하는 helper method를 추가합니다.

**Checkpoint**: Wish 동작을 변경하기 전에 공통 예외 및 응답 타입이 compile 되는지 확인합니다.

---

## Phase 3: User Story 1 - Wish 도메인 예외의 표준화된 응답 (Priority: P1) MVP

**Goal**: Wish 도메인 오류가 기존 HTTP status contract를 유지하면서 표준 JSON error payload로 응답되도록 합니다.

**Independent Test**: `/api/wishes`에서 401, 403, 404 오류를 발생시키고 HTTP status와 `ErrorResponse` JSON field를 검증합니다.

### Tests for User Story 1

- [ ] T010 [P] [US1] Authorization header가 없거나 유효하지 않을 때 401과 `AUTH.UNAUTHORIZED`를 반환하는 MockMvc test를 `src/test/java/gift/wish/WishControllerTest.java`에 추가합니다.
- [ ] T011 [P] [US1] 존재하지 않는 Wish 삭제 시 404와 `WISH.NOT_FOUND`를 반환하는 MockMvc test를 `src/test/java/gift/wish/WishControllerTest.java`에 추가합니다.
- [ ] T012 [P] [US1] 다른 사용자의 Wish 삭제 시 403과 `WISH.ACCESS_DENIED`를 반환하는 MockMvc test를 `src/test/java/gift/wish/WishControllerTest.java`에 추가합니다.
- [ ] T013 [P] [US1] Wish 예외 mapping을 검증하는 handler-level test를 `src/test/java/gift/global/GlobalExceptionHandlerTest.java`에 추가합니다.

### Implementation for User Story 1

- [ ] T014 [US1] `src/main/java/gift/global/GlobalExceptionHandler.java`에 `AuthenticationException`, `UnauthorizedWishAccessException`, `WishNotFoundException`용 `@ExceptionHandler` method를 추가합니다.
- [ ] T015 [US1] `src/main/java/gift/wish/WishController.java`의 null authentication check를 `AuthenticationException` throw 방식으로 변경합니다.
- [ ] T016 [US1] `src/main/java/gift/wish/WishService.java`에서 product를 찾지 못했을 때 `null`을 반환하지 않고 `WishNotFoundException`을 throw하도록 변경합니다.
- [ ] T017 [US1] `src/main/java/gift/wish/WishService.java`에서 Wish 미존재 및 삭제 권한 없음 상태를 `WishRemoveResult`로 반환하지 않고 `WishNotFoundException`, `UnauthorizedWishAccessException`으로 표현합니다.
- [ ] T018 [US1] `src/main/java/gift/wish/WishController.java`에서 401/403/404를 직접 반환하는 분기문을 제거하고 `GlobalExceptionHandler`가 error response를 만들도록 위임합니다.
- [ ] T019 [US1] `./gradlew test --tests *Wish*`를 실행하여 Wish error response test가 통과하는지 확인합니다.

**Checkpoint**: Wish 401/403/404 오류가 표준 JSON 형식으로 응답되며 독립적으로 테스트 가능합니다.

---

## Phase 4: User Story 2 - Category와 동일한 패키지 구조 적용 (Priority: P2)

**Goal**: Wish 예외 class와 handling 구조를 category package 패턴과 일치시키되, 새 표준 error response contract를 사용합니다.

**Independent Test**: package 위치와 import를 검토하고, 예외 처리가 `GlobalExceptionHandler`로 중앙화되었는지 확인합니다.

### Tests for User Story 2

- [ ] T020 [P] [US2] `WishService.removeWish`가 `WishNotFoundException`, `UnauthorizedWishAccessException`을 throw하는지 검증하는 unit test를 `src/test/java/gift/wish/WishServiceTest.java`에 추가합니다.
- [ ] T021 [P] [US2] `WishService.addWish`에서 product 조회 실패 시 `WishNotFoundException`을 throw하는지 검증하는 unit test를 `src/test/java/gift/wish/WishServiceTest.java`에 추가합니다.

### Implementation for User Story 2

- [ ] T022 [US2] `src/main/java/gift/wish/WishController.java`와 `src/main/java/gift/wish/WishService.java`의 import를 정리하여 Wish error는 `gift.wish.exception` package만 사용하도록 맞춥니다.
- [ ] T023 [US2] `src/main/java/gift/wish/WishController.java`와 `src/main/java/gift/wish/WishService.java`에서 `WishRemoveResult` 사용을 제거합니다.
- [ ] T024 [US2] production/test code에서 참조가 없다면 `src/main/java/gift/wish/WishRemoveResult.java`를 삭제합니다.
- [ ] T025 [US2] `src/main/java/gift/wish/WishAddResult.java`는 created/existing success flow 용도로만 유지합니다.
- [ ] T026 [US2] `rg "ResponseEntity.status\\(401\\)|ResponseEntity.status\\(403\\)|ResponseEntity.notFound" src/main/java/gift/wish`를 실행하고 남아 있는 controller-level Wish error branch를 제거합니다.

**Checkpoint**: Wish package는 전용 `exception` package를 가지며, 모든 Wish error 변환은 중앙 handler에서 처리됩니다.

---

## Phase 5: User Story 3 - 기존 Wish 기능 동작 유지 (Priority: P3)

**Goal**: 리팩토링 후에도 기존 Wish API의 성공 응답 동작과 HTTP status contract가 유지되도록 합니다.

**Independent Test**: 기존 및 신규 Wish integration test를 통과시키고, success flow와 error status code가 유지되는지 확인합니다.

### Tests for User Story 3

- [ ] T027 [P] [US3] Wish 목록 조회가 200을 반환하는 MockMvc test를 `src/test/java/gift/wish/WishControllerTest.java`에 추가하거나 유지합니다.
- [ ] T028 [P] [US3] 새 Wish 추가가 201과 `Location` header를 반환하는 MockMvc test를 `src/test/java/gift/wish/WishControllerTest.java`에 추가하거나 유지합니다.
- [ ] T029 [P] [US3] 이미 존재하는 Wish 추가가 200을 반환하는 MockMvc test를 `src/test/java/gift/wish/WishControllerTest.java`에 추가하거나 유지합니다.
- [ ] T030 [P] [US3] 본인 Wish 삭제가 204를 반환하는 MockMvc test를 `src/test/java/gift/wish/WishControllerTest.java`에 추가하거나 유지합니다.

### Implementation for User Story 3

- [ ] T031 [US3] `src/main/java/gift/wish/WishController.java`의 success response가 기존처럼 200, 201, 204를 반환하는지 확인합니다.
- [ ] T032 [US3] `src/main/java/gift/wish/WishService.java`가 기존 Wish를 중복 생성하지 않고 그대로 반환하는지 확인합니다.
- [ ] T033 [US3] `./gradlew test --tests *Wish*`를 실행하여 Wish 동작을 검증합니다.
- [ ] T034 [US3] `./gradlew test --tests *Category*`를 실행하여 category 기준 동작이 깨지지 않았는지 검증합니다.

**Checkpoint**: 리팩토링된 Wish 기능이 성공 flow와 status code 측면에서 기존 동작과 호환됩니다.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: 최종 일관성 확인, 문서 정리, 전체 검증을 수행합니다.

- [ ] T035 [P] 구현 과정에서 실제 Korean message가 달라졌다면 `specs/001-wish-exception-refactor/contracts/error-response.md`를 업데이트합니다.
- [ ] T036 [P] test command나 class name이 변경되었다면 `specs/001-wish-exception-refactor/quickstart.md`를 업데이트합니다.
- [ ] T037 아직 없다면 `src/main/java/gift/global/GlobalExceptionHandler.java`에 `INTERNAL.SERVER_ERROR`를 반환하는 generic fallback exception handler를 추가합니다.
- [ ] T038 `./gradlew test`를 실행하여 전체 regression suite를 검증합니다.
- [ ] T039 `src/main/java/gift/wish`와 `src/main/java/gift/global`에서 unused import와 더 이상 사용하지 않는 result class를 정리합니다.
- [ ] T040 동작 변경이 포함된 작업들이 테스트로 검증되었는지 확인한 뒤 feature 완료 여부를 판단합니다.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: 의존성 없음
- **Foundational (Phase 2)**: Setup 이후 진행하며 모든 User Story를 block합니다.
- **US1 (Phase 3)**: Foundational 이후 진행하며 MVP입니다.
- **US2 (Phase 4)**: Foundational 이후 시작할 수 있지만, 기존 result/status flow를 제거하므로 US1 이후 마무리하는 것이 좋습니다.
- **US3 (Phase 5)**: 최종 refactor 결과를 대상으로 regression을 확인하므로 US1/US2 구현 이후 진행합니다.
- **Polish (Phase 6)**: 필요한 User Story가 완료된 뒤 진행합니다.

### User Story Dependencies

- **US1 (P1)**: 표준화된 Wish error response를 먼저 확립해야 하므로 가장 먼저 완료합니다.
- **US2 (P2)**: foundational exception class가 준비되면 시작할 수 있고, US1 handler mapping 이후 최종 정리합니다.
- **US3 (P3)**: US1/US2 변경 이후 최종 구현 상태를 검증합니다.

### Within Each User Story

- 구현 전에 실패하는 테스트를 먼저 작성합니다.
- controller/service 리팩토링 전에 exception type을 먼저 추가합니다.
- controller의 status branch를 제거하기 전에 service의 exception flow를 먼저 변경합니다.
- 다음 User Story로 넘어가기 전에 targeted test를 실행합니다.

---

## Parallel Opportunities

- T005-T008은 각각 별도 exception class를 생성하므로 병렬 진행할 수 있습니다.
- T010-T013은 독립적인 test case이므로 병렬 작성할 수 있습니다.
- T020-T021은 exception type이 준비된 뒤 controller test와 병렬로 작성할 수 있습니다.
- T027-T030은 각각 별도의 Wish API behavior를 검증하므로 병렬 작성할 수 있습니다.
- T035-T036은 구현이 확정된 뒤 병렬로 문서 업데이트가 가능합니다.

---

## Implementation Strategy

### MVP First

1. T001-T009를 완료합니다.
2. US1 작업인 T010-T019를 완료합니다.
3. `./gradlew test --tests *Wish*`로 Wish 401/403/404 응답을 검증한 뒤 멈춰서 확인합니다.

### Incremental Delivery

1. 공통 error response와 Wish exception 기반을 만듭니다.
2. 표준화된 Wish error response를 제공합니다.
3. package/controller/service 구조를 category 패턴과 맞춥니다.
4. 기존 Wish success behavior와 전체 regression을 검증합니다.

### Notes

- 공개 Wish API status code는 유지합니다. auth는 401, unauthorized access는 403, missing resource는 404, success flow는 200/201/204입니다.
- product, member, order 예외 리팩토링으로 scope를 확장하지 않습니다.
- `addWish` 중 `Product` 조회 실패는 이번 feature의 Wish flow 안에서 404로 매핑합니다.
