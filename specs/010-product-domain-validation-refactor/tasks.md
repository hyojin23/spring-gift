# Tasks: Product 도메인 검증 강화 리팩토링

**Input**: Design documents from `/specs/010-product-domain-validation-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `Product` 생성자와 `update()`의 필드 할당 구조를 확인한다.
- [x] T002 현재 `ProductService`와 `AdminProductService`의 `ProductNameValidator` 호출 정책을 확인한다.
- [x] T003 기존 product 테스트 구조를 확인한다.

## Phase 2: Tests First

- [x] T004 [P] 빈 상품명으로 Product 생성 시 `ProductValidationException`이 발생하는 테스트를 추가한다.
- [x] T005 [P] 0 이하 가격으로 Product 생성 시 `ProductValidationException`이 발생하는 테스트를 추가한다.
- [x] T006 [P] 빈 이미지 URL로 Product 생성 시 `ProductValidationException`이 발생하는 테스트를 추가한다.
- [x] T007 [P] null 카테고리로 Product 생성 시 `ProductValidationException`이 발생하는 테스트를 추가한다.
- [x] T008 [P] 유효하지 않은 값으로 Product 수정 시 `ProductValidationException`이 발생하는 테스트를 추가한다.
- [x] T009 [P] Product 수정 검증 실패 시 기존 상태가 유지되는 테스트를 추가한다.

## Phase 3: Domain Validation

- [x] T010 `Product`에 공통 검증 private 메서드를 추가한다.
- [x] T011 `Product` 생성자에서 필드 할당 전 공통 검증을 수행한다.
- [x] T012 `Product.update()`에서 필드 할당 전 공통 검증을 수행한다.
- [x] T013 검증 실패 시 `ProductValidationException`을 발생시킨다.

## Phase 4: Policy Separation Check

- [x] T014 `ProductNameValidator.validate(name)` API 호출 정책이 유지되는지 확인한다.
- [x] T015 `ProductNameValidator.validate(name, true)` Admin 호출 정책이 유지되는지 확인한다.
- [x] T016 `ProductRequest` Bean Validation annotation이 유지되는지 확인한다.

## Phase 5: Validation

- [x] T017 `./gradlew.bat test --tests *Product*`를 실행한다.
- [x] T018 `./gradlew.bat test --tests *AdminProduct*`를 실행한다.
- [x] T019 `Product` 생성자와 `update()`가 동일한 검증 메서드를 사용하는지 확인한다.

## Dependencies

- T001-T003 before T004-T009
- T004-T009 before T010-T013
- T010 before T011-T012
- T013 before T017-T018
- T014-T016 before final review

## Parallel Example

```text
T004, T005, T006, T007 can be written independently after Product test fixture is ready.
T014, T015, T016 can be checked independently after implementation.
```
