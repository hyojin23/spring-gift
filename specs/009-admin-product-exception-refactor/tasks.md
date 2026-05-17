# Tasks: Admin Product 예외 처리 리팩토링

**Input**: Design documents from `/specs/009-admin-product-exception-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`, ADR-0001

## Phase 1: Setup

- [x] T001 현재 `AdminProductService`의 `NoSuchElementException` 사용 지점을 확인한다.
- [x] T002 현재 `AdminProductControllerTest`의 관리자 상품 flow 테스트 구조를 확인한다.
- [x] T003 현재 `product/list` template의 메시지 표시 위치를 확인한다.

## Phase 2: Tests First

- [x] T004 [P] 존재하지 않는 상품 수정 화면 접근 시 `/admin/products` redirect와 flash `error`를 검증하는 테스트를 추가한다.
- [x] T005 [P] 존재하지 않는 상품 수정 요청 시 `/admin/products` redirect와 flash `error`를 검증하는 테스트를 추가한다.
- [x] T006 [P] 존재하지 않는 카테고리로 상품 등록 요청 시 `/admin/products` redirect와 flash `error`를 검증하는 테스트를 추가한다.
- [x] T007 [P] 존재하지 않는 카테고리로 상품 수정 요청 시 `/admin/products` redirect와 flash `error`를 검증하는 테스트를 추가한다.

## Phase 3: Domain Exceptions

- [x] T008 `src/main/java/gift/product/exception/AdminProductException.java`를 추가한다.
- [x] T009 `src/main/java/gift/product/exception/AdminProductNotFoundException.java`를 추가한다.
- [x] T010 `src/main/java/gift/product/exception/AdminProductCategoryNotFoundException.java`를 추가한다.

## Phase 4: Service Refactor

- [x] T011 `AdminProductService.getProduct`의 상품 미존재 예외를 `AdminProductNotFoundException`으로 교체한다.
- [x] T012 `AdminProductService.getCategory`의 카테고리 미존재 예외를 `AdminProductCategoryNotFoundException`으로 교체한다.
- [x] T013 `AdminProductService`에서 `NoSuchElementException` import를 제거한다.

## Phase 5: Admin Exception Handling

- [x] T014 관리자 상품 예외를 처리하는 `@ExceptionHandler` 또는 admin 전용 `@ControllerAdvice`를 추가한다.
- [x] T015 예외 처리 시 `RedirectAttributes`에 `error` flash attribute를 추가한다.
- [x] T016 예외 처리 결과로 `"redirect:/admin/products"`를 반환한다.
- [x] T017 Product API용 `GlobalExceptionHandler`와 JSON error contract가 변경되지 않았는지 확인한다.

## Phase 6: View Integration

- [x] T018 `product/list` template에 flash `error` 메시지 표시 영역을 추가한다.
- [x] T019 기존 상품 목록 표시와 관리자 작업 링크가 영향을 받지 않는지 확인한다.

## Phase 7: Validation

- [x] T020 `./gradlew.bat test --tests *AdminProduct*`를 실행한다.
- [x] T021 `./gradlew.bat test --tests *Product*`를 실행한다.
- [x] T022 `rg "NoSuchElementException" src/main/java/gift/product`로 product admin service에 범용 예외가 남지 않았는지 확인한다.

## Dependencies

- T001-T003 before T004-T007
- T004-T007 before T008-T016
- T008-T010 before T011-T016
- T014-T016 before T018-T021
- T020-T022 after implementation

## Parallel Example

```text
T004, T005, T006, T007 can be written independently after current test structure is understood.
T008, T009, T010 can be added independently once exception package naming is decided.
```
