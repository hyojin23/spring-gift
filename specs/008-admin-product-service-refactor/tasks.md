# Tasks: Admin Product 서비스 분리 리팩토링

**Input**: `/specs/008-admin-product-service-refactor/`의 설계 문서  
**Prerequisites**: spec.md, plan.md, research.md, data-model.md, quickstart.md

**Tests**: SC-003부터 SC-006에 따라 AdminProduct controller 테스트와 Product 회귀 테스트 실행이 필요합니다.

**Organization**: Admin 화면 flow를 테스트로 고정한 뒤 service 분리와 controller 축소를 진행합니다.

## Format: `[ID] [P?] Description`

- **[P]**: 서로 다른 파일을 수정하거나 직접적인 의존성이 없어 병렬로 진행할 수 있는 작업
- 모든 구현 작업에는 대상 파일 경로를 명시합니다.

---

## Phase 1: Setup

**Purpose**: 현재 AdminProductController의 repository 직접 접근과 view/form 계약을 확인합니다.

- [x] T001 `src/main/java/gift/product/AdminProductController.java`의 repository 직접 접근과 상품명 검증 흐름을 확인합니다.
- [x] T002 `src/main/resources/templates/product/list.html`, `new.html`, `edit.html`의 model attribute 사용을 확인합니다.
- [x] T003 `src/main/java/gift/product/ProductNameValidator.java`의 admin `allowKakao=true` 검증 규칙을 확인합니다.
- [x] T004 `./gradlew test --tests *Product*`를 실행하여 변경 전 기준 상태를 기록합니다.

---

## Phase 2: Tests

**Purpose**: Admin HTML view/redirect/form 복구 계약을 MockMvc 테스트로 고정합니다.

- [x] T005 `src/test/java/gift/product/AdminProductControllerTest.java`를 추가합니다.
- [x] T006 `GET /admin/products`가 `product/list` view와 `products` model을 반환하는 테스트를 추가합니다.
- [x] T007 `GET /admin/products/new`가 `product/new` view와 `categories` model을 반환하는 테스트를 추가합니다.
- [x] T008 유효한 상품 등록 form 제출 시 `/admin/products`로 redirect하는 테스트를 추가합니다.
- [x] T009 상품 등록 form 검증 실패 시 `product/new` view와 `errors`, 입력값, `categories` model을 반환하는 테스트를 추가합니다.
- [x] T010 `GET /admin/products/{id}/edit`가 `product/edit` view와 `product`, `categories` model을 반환하는 테스트를 추가합니다.
- [x] T011 유효한 상품 수정 form 제출 시 `/admin/products`로 redirect하는 테스트를 추가합니다.
- [x] T012 상품 수정 form 검증 실패 시 `product/edit` view와 `errors`, 입력값, `product`, `categories` model을 반환하는 테스트를 추가합니다.
- [x] T013 상품 삭제 form 제출 시 `/admin/products`로 redirect하는 테스트를 추가합니다.

---

## Phase 3: Implementation

**Purpose**: AdminProductService를 추가하고 AdminProductController의 책임을 view flow로 축소합니다.

- [x] T014 `src/main/java/gift/product/AdminProductService.java`를 추가하고 `ProductRepository`, `CategoryRepository`를 주입받도록 구성합니다.
- [x] T015 `AdminProductService`에 상품 목록과 카테고리 목록 조회 method를 추가합니다.
- [x] T016 `AdminProductService`에 상품 단건 조회 method를 추가합니다.
- [x] T017 `AdminProductService`에 admin 상품명 검증 method를 추가하고 `ProductNameValidator.validate(name, true)`를 사용합니다.
- [x] T018 `AdminProductService`에 상품 생성, 수정, 삭제 method를 추가합니다.
- [x] T019 `src/main/java/gift/product/AdminProductController.java`가 `AdminProductService`만 주입받도록 변경합니다.
- [x] T020 `AdminProductController`의 repository 직접 접근을 제거합니다.
- [x] T021 `AdminProductController`의 view 이름, redirect 경로, form model attribute는 기존과 동일하게 유지합니다.

---

## Phase 4: Verification

**Purpose**: Admin 화면 flow가 유지되고 Product API 동작이 깨지지 않았는지 확인합니다.

- [x] T022 `./gradlew test --tests *AdminProductControllerTest*`를 실행하여 admin flow를 확인합니다.
- [x] T023 `./gradlew test --tests *ProductControllerTest* --tests *ProductServiceTest*`를 실행하여 Product API 회귀를 확인합니다.
- [x] T024 `./gradlew test --tests *Product*`를 실행하여 Product 관련 전체 회귀 테스트를 확인합니다.
- [x] T025 `rg "ProductRepository|CategoryRepository" src/main/java/gift/product/AdminProductController.java`로 repository 직접 의존성이 제거되었는지 확인합니다.
- [x] T026 `rg "AdminProductService|allowKakao|validate\\(name, true\\)" src/main/java/gift/product src/test/java/gift/product`로 admin service와 검증 규칙 사용 지점을 확인합니다.

---

## Dependencies & Execution Order

- Phase 1은 의존성 없이 먼저 수행합니다.
- Phase 2는 구현 전에 진행합니다.
- T014 이후 T015-T018을 진행합니다.
- T019-T021은 service method가 준비된 뒤 진행합니다.
- 구현이 끝난 뒤 T022-T026으로 검증합니다.

## Parallel Opportunities

- T001-T003은 독립적인 읽기 작업이므로 병렬로 수행할 수 있습니다.
- T006-T013은 같은 테스트 파일을 수정하므로 순차적으로 처리합니다.
- T022-T023은 필요하면 독립적으로 실행할 수 있지만, 최종적으로 T024를 실행합니다.

## Implementation Strategy

1. Admin template의 model attribute 계약을 확인합니다.
2. AdminProductControllerTest로 view/redirect/form 복구 흐름을 고정합니다.
3. AdminProductService를 추가해 repository 접근과 상품 변경 로직을 이동합니다.
4. AdminProductController를 service 호출과 model 조립으로 축소합니다.
5. AdminProduct와 Product 관련 테스트로 회귀 여부를 확인합니다.

## Notes

- HTML template 파일은 이번 작업에서 변경하지 않습니다.
- Admin not found/error page 설계는 이번 작업에서 변경하지 않습니다.
- Product JSON API error response contract는 변경하지 않습니다.
- Admin 화면에는 `ErrorResponse` JSON 응답을 적용하지 않습니다.
