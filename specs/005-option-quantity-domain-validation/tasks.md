# Tasks: Option 수량 도메인 검증 강화

**Input**: `/specs/005-option-quantity-domain-validation/`의 설계 문서  
**Prerequisites**: spec.md, plan.md, research.md, data-model.md, quickstart.md

**Tests**: SC-001부터 SC-006에 따라 Option 도메인 단위 테스트와 기존 Option 테스트 실행이 필요합니다.

**Organization**: 도메인 불변식 강화 작업이므로 setup, tests, implementation, verification 순서로 진행합니다.

## Format: `[ID] [P?] Description`

- **[P]**: 서로 다른 파일을 수정하거나 직접적인 의존성이 없어 병렬로 진행할 수 있는 작업
- 모든 구현 작업에는 대상 파일 경로를 명시합니다.

---

## Phase 1: Setup

**Purpose**: 현재 Option 수량 검증 위치와 기존 테스트 기준을 확인합니다.

- [x] T001 `src/main/java/gift/option/Option.java`의 생성자와 `subtractQuantity` 수량 검증 흐름을 확인합니다.
- [x] T002 `src/main/java/gift/option/OptionRequest.java`의 Bean Validation 수량 범위를 확인합니다.
- [x] T003 `src/test/java/gift/option` 아래에 기존 `Option` 도메인 단위 테스트가 있는지 확인합니다.
- [x] T004 `./gradlew test --tests *Option*`를 실행하여 변경 전 기준 상태를 기록합니다.

---

## Phase 2: Tests

**Purpose**: 수량 불변식을 도메인 단위 테스트로 먼저 명확히 표현합니다.

- [x] T005 `src/test/java/gift/option/OptionTest.java`를 추가합니다.
- [x] T006 `Option` 생성 시 수량이 0 이하이면 예외가 발생하는 테스트를 추가합니다.
- [x] T007 `Option` 생성 시 수량이 99,999,999를 초과하면 예외가 발생하는 테스트를 추가합니다.
- [x] T008 정상 수량으로 `Option`을 생성할 수 있는 테스트를 추가합니다.
- [x] T009 `subtractQuantity`에 0 이하 수량을 전달하면 예외가 발생하는 테스트를 추가합니다.
- [x] T010 현재 재고보다 큰 수량을 차감하면 예외가 발생하는 테스트를 추가하거나 유지합니다.
- [x] T011 정상 수량 차감 시 `quantity`가 감소하는 테스트를 추가합니다.

---

## Phase 3: Implementation

**Purpose**: Option 도메인 객체가 수량 불변식을 직접 검증하도록 변경합니다.

- [x] T012 `src/main/java/gift/option/Option.java`에 최소 수량과 최대 수량 상수를 추가합니다.
- [x] T013 `src/main/java/gift/option/Option.java` 생성자에서 수량 범위를 검증하도록 변경합니다.
- [x] T014 `src/main/java/gift/option/Option.java`의 `subtractQuantity`가 차감 수량 1 이상 조건을 검증하도록 변경합니다.
- [x] T015 `src/main/java/gift/option/Option.java`의 현재 재고 초과 차감 검증이 기존처럼 유지되는지 확인합니다.

---

## Phase 4: Verification

**Purpose**: 도메인 검증이 추가되었고 기존 API 동작은 유지되는지 확인합니다.

- [x] T016 `./gradlew test --tests *OptionTest*`를 실행하여 도메인 단위 테스트를 확인합니다.
- [x] T017 `./gradlew test --tests *OptionServiceTest*`를 실행하여 service 테스트를 확인합니다.
- [x] T018 `./gradlew test --tests *OptionControllerTest*`를 실행하여 API 응답 계약이 유지되는지 확인합니다.
- [x] T019 `./gradlew test --tests *Option*`를 실행하여 Option 관련 회귀 테스트를 확인합니다.
- [x] T020 `rg "MIN_QUANTITY|MAX_QUANTITY|subtractQuantity|IllegalArgumentException" src/main/java/gift/option src/test/java/gift/option`로 수량 검증 위치를 확인합니다.

---

## Dependencies & Execution Order

- Phase 1은 의존성 없이 먼저 수행합니다.
- T005-T011은 구현 전에 진행합니다.
- T012-T015는 테스트 작성 후 진행합니다.
- 구현이 끝난 뒤 T016-T020으로 검증합니다.

## Parallel Opportunities

- T001-T003은 독립적인 읽기 작업이므로 병렬로 수행할 수 있습니다.
- T006-T011은 같은 테스트 파일을 수정하므로 순차적으로 처리합니다.
- T017-T018은 필요하면 독립적으로 실행할 수 있지만, 최종적으로 T019를 실행합니다.

## Implementation Strategy

1. 현재 API 입력 검증과 도메인 검증의 경계를 확인합니다.
2. `OptionTest`에 생성/차감 수량 불변식 테스트를 추가합니다.
3. `Option` 생성자와 `subtractQuantity`에 수량 검증을 추가합니다.
4. 기존 Option service/controller 테스트로 API 계약이 유지되는지 확인합니다.

## Notes

- 옵션명 검증은 이번 범위에 포함하지 않습니다.
- `OptionRequest`의 Bean Validation은 유지합니다.
- 수량 검증 실패 예외 타입은 우선 `IllegalArgumentException`을 사용합니다.
- 예외 타입 통일은 별도 리팩토링으로 다룹니다.
