# Tasks: Product 서비스 및 예외 처리 리팩토링

**Input**: `/specs/007-product-service-exception-refactor/`의 설계 문서  
**Prerequisites**: spec.md, plan.md, research.md, data-model.md, contracts/error-response.md, quickstart.md

**Tests**: SC-003부터 SC-007에 따라 Product service/controller/global handler 테스트 실행이 필요합니다.

**Organization**: Product API를 service/exception/global handler 구조로 분리하고 정상 API 계약을 유지하는 순서로 진행합니다.

## Format: `[ID] [P?] Description`

- **[P]**: 서로 다른 파일을 수정하거나 직접적인 의존성이 없어 병렬로 진행할 수 있는 작업
- 모든 구현 작업에는 대상 파일 경로를 명시합니다.

---

## Phase 1: Setup

**Purpose**: 현재 Product API controller 오류 분기와 기준 테스트 상태를 확인합니다.

- [x] T001 `src/main/java/gift/product/ProductController.java`의 repository 직접 접근, `orElse(null)`, `ResponseEntity.notFound()`, `@ExceptionHandler` 사용 지점을 확인합니다.
- [x] T002 `src/main/java/gift/product/AdminProductController.java`는 이번 범위에서 제외함을 확인합니다.
- [x] T003 `src/main/java/gift/product/ProductNameValidator.java`의 상품명 검증 메시지를 확인합니다.
- [x] T004 `src/main/java/gift/global/GlobalExceptionHandler.java`의 기존 handler 구조를 확인합니다.
- [x] T005 `./gradlew test --tests *Product* --tests *GlobalExceptionHandlerTest*`를 실행하여 변경 전 기준 상태를 기록합니다.

---

## Phase 2: Tests

**Purpose**: Product API 오류 응답과 service 예외 흐름을 먼저 테스트로 명확히 표현합니다.

- [x] T006 [P] `src/test/java/gift/product/ProductControllerTest.java`에 상품 미존재 조회 시 404와 `PRODUCT.NOT_FOUND`를 반환하는 테스트를 추가합니다.
- [x] T007 [P] `src/test/java/gift/product/ProductControllerTest.java`에 카테고리 미존재 생성/수정 시 404와 `PRODUCT.CATEGORY_NOT_FOUND`를 반환하는 테스트를 추가합니다.
- [x] T008 [P] `src/test/java/gift/product/ProductControllerTest.java`에 상품명 검증 실패 시 400과 `PRODUCT.INVALID_NAME`을 반환하는 테스트를 추가합니다.
- [x] T009 [P] `src/test/java/gift/product/ProductControllerTest.java`에 정상 목록/조회/생성/수정/삭제 status 유지 테스트를 추가하거나 갱신합니다.
- [x] T010 `src/test/java/gift/product/ProductServiceTest.java`를 추가하고 상품 미존재, 카테고리 미존재, 상품명 검증 실패 예외를 검증합니다.
- [x] T011 `src/test/java/gift/global/GlobalExceptionHandlerTest.java`에 Product 예외 handler 테스트를 추가합니다.

---

## Phase 3: Foundational

**Purpose**: Product service와 도메인 예외 타입을 추가합니다.

- [x] T012 [P] `src/main/java/gift/product/exception/ProductException.java`를 추가합니다.
- [x] T013 [P] `src/main/java/gift/product/exception/ProductNotFoundException.java`를 추가합니다.
- [x] T014 [P] `src/main/java/gift/product/exception/ProductCategoryNotFoundException.java`를 추가합니다.
- [x] T015 [P] `src/main/java/gift/product/exception/ProductValidationException.java`를 추가합니다.
- [x] T016 `src/main/java/gift/product/ProductService.java`를 추가하고 `ProductRepository`, `CategoryRepository`를 주입받도록 구성합니다.

---

## Phase 4: Implementation

**Purpose**: Product API 비즈니스 로직과 오류 흐름을 service/exception/global handler로 이동합니다.

- [x] T017 `src/main/java/gift/product/ProductService.java`에 상품 목록 조회, 단건 조회, 생성, 수정, 삭제 method를 구현합니다.
- [x] T018 `ProductService`에서 상품명 검증 실패 시 `ProductValidationException`을 발생시킵니다.
- [x] T019 `ProductService`에서 상품 미존재 시 `ProductNotFoundException`을 발생시킵니다.
- [x] T020 `ProductService`에서 카테고리 미존재 시 `ProductCategoryNotFoundException`을 발생시킵니다.
- [x] T021 `src/main/java/gift/product/ProductController.java`가 `ProductService`만 주입받도록 변경합니다.
- [x] T022 `ProductController`의 repository 직접 접근, `validateName`, controller-level `@ExceptionHandler`를 제거합니다.
- [x] T023 `ProductController`의 정상 응답 status와 `Location` header를 기존과 동일하게 유지합니다.
- [x] T024 `src/main/java/gift/global/GlobalExceptionHandler.java`에 Product 예외 handler를 추가합니다.

---

## Phase 5: Verification

**Purpose**: Product API 구조가 정리되었고 외부 동작이 유지되는지 확인합니다.

- [x] T025 `./gradlew test --tests *ProductServiceTest*`를 실행하여 service 단위 테스트를 확인합니다.
- [x] T026 `./gradlew test --tests *ProductControllerTest*`를 실행하여 Product API 테스트를 확인합니다.
- [x] T027 `./gradlew test --tests *GlobalExceptionHandlerTest*`를 실행하여 handler 매핑을 확인합니다.
- [x] T028 `./gradlew test --tests *Product* --tests *GlobalExceptionHandlerTest*`를 실행하여 관련 회귀 테스트를 확인합니다.
- [x] T029 `rg "ProductRepository|CategoryRepository|@ExceptionHandler|orElse\\(null\\)|ResponseEntity\\.notFound|IllegalArgumentException" src/main/java/gift/product/ProductController.java`로 controller 오류 분기가 제거되었는지 확인합니다.
- [x] T030 `rg "ProductNotFoundException|ProductCategoryNotFoundException|ProductValidationException|PRODUCT\\." src/main/java src/test/java`로 Product 예외 매핑 사용 지점을 확인합니다.

---

## Dependencies & Execution Order

- Phase 1은 의존성 없이 먼저 수행합니다.
- Phase 2는 구현 전에 진행합니다.
- T012-T016이 완료된 뒤 Phase 4를 진행합니다.
- 구현이 끝난 뒤 T025-T030으로 검증합니다.

## Parallel Opportunities

- T001-T004는 독립적인 읽기 작업이므로 병렬로 수행할 수 있습니다.
- T012-T015는 서로 다른 예외 class이므로 병렬로 진행할 수 있습니다.
- T006-T009는 controller 테스트 케이스 단위로 병렬 작성할 수 있습니다.
- T025-T027은 필요하면 독립적으로 실행할 수 있지만, 최종적으로 T028을 실행합니다.

## Implementation Strategy

1. 현재 Product API controller의 오류 분기와 정상 계약을 확인합니다.
2. Product controller/service/handler 테스트를 추가하거나 갱신합니다.
3. Product exception 타입과 ProductService를 추가합니다.
4. ProductController를 service 호출과 성공 응답 조립으로 축소합니다.
5. GlobalExceptionHandler에 Product 예외 매핑을 추가합니다.
6. Product 관련 테스트와 검색 검증으로 완료 여부를 판단합니다.

## Notes

- AdminProductController는 이번 작업에서 변경하지 않습니다.
- Bean Validation 예외 표준화는 이번 작업에서 변경하지 않습니다.
- Product 도메인 내부 검증 강화는 별도 작업으로 다룹니다.
- Category API 예외 구조는 이번 작업에서 변경하지 않습니다.
