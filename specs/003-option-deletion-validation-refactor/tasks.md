# Tasks: Option 삭제 검증 조회 최적화

**Input**: `/specs/003-option-deletion-validation-refactor/`의 설계 문서  
**Prerequisites**: spec.md, plan.md, research.md, data-model.md, quickstart.md

**Tests**: SC-002, SC-003, SC-004에 따라 Option service/controller 테스트 실행이 필요합니다.

**Organization**: 내부 조회 방식만 바꾸는 작은 리팩토링이므로 setup, implementation, verification 순서로 진행합니다.

## Format: `[ID] [P?] Description`

- **[P]**: 서로 다른 파일을 수정하거나 직접적인 의존성이 없어 병렬로 진행할 수 있는 작업
- 모든 구현 작업에는 대상 파일 경로를 명시합니다.

---

## Phase 1: Setup

**Purpose**: 현재 삭제 검증 흐름과 테스트 기준을 확인합니다.

- [x] T001 `src/main/java/gift/option/OptionService.java`에서 마지막 옵션 삭제 검증이 어떤 repository method를 사용하는지 확인합니다.
- [x] T002 `src/main/java/gift/option/OptionRepository.java`의 기존 query method 목록을 확인합니다.
- [x] T003 `src/test/java/gift/option/OptionServiceTest.java`의 삭제 관련 stubbing과 기대 예외를 확인합니다.
- [x] T004 `./gradlew test --tests *Option*`를 실행하여 변경 전 기준 상태를 기록합니다.

---

## Phase 2: Implementation

**Purpose**: 옵션 목록 전체 조회 대신 count query로 삭제 가능 여부를 검증합니다.

- [x] T005 [P] `src/main/java/gift/option/OptionRepository.java`에 `long countByProductId(Long productId)`를 추가합니다.
- [x] T006 `src/main/java/gift/option/OptionService.java`의 `validateCanDelete`가 `optionRepository.countByProductId(productId)`를 사용하도록 변경합니다.
- [x] T007 `src/test/java/gift/option/OptionServiceTest.java`의 `deleteOptionNotFound` 테스트 stubbing을 `findByProductId` 목록 조회에서 `countByProductId`로 변경합니다.
- [x] T008 `src/test/java/gift/option/OptionServiceTest.java`의 `deleteLastOption` 테스트 stubbing을 `findByProductId` 목록 조회에서 `countByProductId`로 변경합니다.

---

## Phase 3: Verification

**Purpose**: 내부 조회 방식만 변경되었고 외부 동작은 유지되는지 확인합니다.

- [x] T009 `./gradlew test --tests *OptionServiceTest*`를 실행하여 service 단위 테스트를 확인합니다.
- [x] T010 `./gradlew test --tests *OptionControllerTest*`를 실행하여 API 응답 계약이 유지되는지 확인합니다.
- [x] T011 `./gradlew test --tests *Option*`를 실행하여 Option 관련 회귀 테스트를 확인합니다.
- [x] T012 `rg "findByProductId\\(productId\\)\\.size\\(\\)|countByProductId" src/main/java/gift/option src/test/java/gift/option`로 삭제 검증이 count query 기반인지 확인합니다.

---

## Dependencies & Execution Order

- Phase 1은 의존성 없이 먼저 수행합니다.
- T005 이후 T006을 진행합니다.
- T006 이후 T007-T008을 진행합니다.
- 구현이 끝난 뒤 T009-T012로 검증합니다.

## Parallel Opportunities

- T001-T003은 독립적인 읽기 작업이므로 병렬로 수행할 수 있습니다.
- T007-T008은 같은 테스트 파일을 수정하므로 순차적으로 처리합니다.
- T009-T010은 필요하면 독립적으로 실행할 수 있지만, 최종적으로 T011을 실행합니다.

## Implementation Strategy

1. 현재 삭제 검증 테스트와 service 흐름을 확인합니다.
2. repository에 count query method를 추가합니다.
3. service 검증 로직을 count query 기반으로 변경합니다.
4. mock stubbing을 새 query method 기준으로 갱신합니다.
5. Option 관련 테스트로 외부 동작 유지 여부를 확인합니다.

## Notes

- API 응답 code/message/status는 변경하지 않습니다.
- `OptionDeletionNotAllowedException`의 의미와 발생 조건은 유지합니다.
- 데이터베이스 migration은 추가하지 않습니다.
