# Tasks: Option 상품 범위 조회 리팩토링

**Input**: `/specs/004-option-product-scoped-lookup-refactor/`의 설계 문서  
**Prerequisites**: spec.md, plan.md, research.md, data-model.md, quickstart.md

**Tests**: SC-002, SC-003, SC-004, SC-005에 따라 Option service/controller 테스트 실행이 필요합니다.

**Organization**: 내부 조회 표현만 바꾸는 작은 리팩토링이므로 setup, implementation, verification 순서로 진행합니다.

## Format: `[ID] [P?] Description`

- **[P]**: 서로 다른 파일을 수정하거나 직접적인 의존성이 없어 병렬로 진행할 수 있는 작업
- 모든 구현 작업에는 대상 파일 경로를 명시합니다.

---

## Phase 1: Setup

**Purpose**: 현재 삭제 대상 옵션 조회 흐름과 테스트 기준을 확인합니다.

- [x] T001 `src/main/java/gift/option/OptionService.java`에서 삭제 대상 옵션 조회가 어떤 repository method와 filter를 사용하는지 확인합니다.
- [x] T002 `src/main/java/gift/option/OptionRepository.java`의 기존 query method 목록을 확인합니다.
- [x] T003 `src/test/java/gift/option/OptionServiceTest.java`의 삭제 관련 stubbing과 기대 예외를 확인합니다.
- [x] T004 `./gradlew test --tests *Option*`를 실행하여 변경 전 기준 상태를 기록합니다.

---

## Phase 2: Tests

**Purpose**: 다른 상품에 속한 옵션 삭제가 옵션 미존재로 처리되는지 명확히 검증합니다.

- [x] T005 `src/test/java/gift/option/OptionServiceTest.java`에 다른 상품에 속한 옵션 삭제 시 `OptionNotFoundException`이 발생하는 테스트가 있는지 확인합니다.
- [x] T006 필요한 경우 `src/test/java/gift/option/OptionServiceTest.java`에 product-scoped lookup 실패 테스트를 추가합니다.

---

## Phase 3: Implementation

**Purpose**: `findById` 후 service filter 대신 repository method로 product scope를 표현합니다.

- [x] T007 [P] `src/main/java/gift/option/OptionRepository.java`에 `Optional<Option> findByIdAndProductId(Long optionId, Long productId)`를 추가합니다.
- [x] T008 `src/main/java/gift/option/OptionService.java`의 `deleteOption`이 `optionRepository.findByIdAndProductId(optionId, productId)`를 사용하도록 변경합니다.
- [x] T009 `src/test/java/gift/option/OptionServiceTest.java`의 삭제 관련 stubbing을 product-scoped lookup 기준으로 변경합니다.

---

## Phase 4: Verification

**Purpose**: 내부 조회 표현만 변경되었고 외부 동작은 유지되는지 확인합니다.

- [x] T010 `./gradlew test --tests *OptionServiceTest*`를 실행하여 service 단위 테스트를 확인합니다.
- [x] T011 `./gradlew test --tests *OptionControllerTest*`를 실행하여 API 응답 계약이 유지되는지 확인합니다.
- [x] T012 `./gradlew test --tests *Option*`를 실행하여 Option 관련 회귀 테스트를 확인합니다.
- [x] T013 `rg "findById\\(optionId\\)|findByIdAndProductId|filter\\(" src/main/java/gift/option src/test/java/gift/option`로 삭제 대상 조회가 product-scoped repository method 기반인지 확인합니다.

---

## Dependencies & Execution Order

- Phase 1은 의존성 없이 먼저 수행합니다.
- T005-T006은 구현 전에 진행합니다.
- T007 이후 T008을 진행합니다.
- T008 이후 T009를 진행합니다.
- 구현이 끝난 뒤 T010-T013으로 검증합니다.

## Parallel Opportunities

- T001-T003은 독립적인 읽기 작업이므로 병렬로 수행할 수 있습니다.
- T007은 repository 변경이라 service/test 수정 전 독립적으로 진행할 수 있습니다.
- T010-T011은 필요하면 독립적으로 실행할 수 있지만, 최종적으로 T012를 실행합니다.

## Implementation Strategy

1. 현재 삭제 대상 옵션 조회와 테스트 흐름을 확인합니다.
2. 다른 상품의 옵션 삭제 실패 케이스가 명시적으로 검증되는지 확인합니다.
3. repository에 product-scoped lookup method를 추가합니다.
4. service 삭제 대상 조회를 새 repository method 기준으로 변경합니다.
5. mock stubbing을 새 query method 기준으로 갱신합니다.
6. Option 관련 테스트로 외부 동작 유지 여부를 확인합니다.

## Notes

- API 응답 code/message/status는 변경하지 않습니다.
- `OptionNotFoundException`의 의미와 발생 조건은 유지합니다.
- 데이터베이스 migration은 추가하지 않습니다.
