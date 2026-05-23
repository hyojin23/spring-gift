# Tasks: Product 유스케이스 서비스 공통화 리팩토링

**Input**: Design documents from `/specs/039-product-usecase-service-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 `ProductService`의 생성/수정 흐름을 확인한다.
- [x] T002 `AdminProductService`의 생성/수정 흐름을 확인한다.
- [x] T003 API/admin 상품명 검증 정책 차이를 확인한다.
- [x] T004 기존 product/admin product 테스트의 기대 동작을 확인한다.

## Phase 2: Tests First

- [x] T005 API 상품 생성/수정 성공 테스트가 있는지 확인한다.
- [x] T006 API 카카오 상품명 제한 테스트가 있는지 확인한다.
- [x] T007 관리자 카카오 상품명 허용 테스트가 있는지 확인한다.
- [x] T008 관리자 상품/카테고리 미존재 redirect + flash 테스트가 있는지 확인한다.
- [x] T009 부족한 회귀 테스트가 있으면 추가한다.

## Phase 3: Implementation

- [x] T010 상품 생성 공통 유스케이스를 도입한다.
- [x] T011 상품 수정 공통 유스케이스를 도입한다.
- [x] T012 `ProductService.createProduct()`가 공통 생성 유스케이스를 사용하도록 변경한다.
- [x] T013 `ProductService.updateProduct()`가 공통 수정 유스케이스를 사용하도록 변경한다.
- [x] T014 `AdminProductService.createProduct()`가 공통 생성 유스케이스를 사용하도록 변경한다.
- [x] T015 `AdminProductService.updateProduct()`가 공통 수정 유스케이스를 사용하도록 변경한다.
- [x] T016 API/admin 예외 응답 계약이 유지되도록 예외 변환 경계를 정리한다.

## Phase 4: Validation

- [x] T017 `./gradlew.bat test --tests *Product*`를 실행한다.
- [x] T018 `./gradlew.bat test --tests *AdminProductController*`를 실행한다.
- [x] T019 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T004 before T005-T009
- T005-T009 before T010-T016
- T010-T011 before T012-T015
- T012-T016 before T017-T019

## Parallel Example

```text
T005-T008 can be checked independently before implementation.
```
