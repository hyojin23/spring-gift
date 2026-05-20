# Tasks: Kakao 메시지 템플릿 분리 리팩토링

**Input**: Design documents from `/specs/020-kakao-message-template-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 `KakaoMessageClient.buildTemplate()`의 템플릿 구조를 확인한다.
- [x] T002 현재 주문 메시지 null/blank 처리 정책을 확인한다.
- [x] T003 현재 금액 포맷 정책을 확인한다.
- [x] T004 `KakaoMessageClient.sendToMe()` public API와 HTTP request 구조를 확인한다.

## Phase 2: Tests First

- [x] T005 [P] `KakaoMessageTemplateBuilderTest`를 추가한다.
- [x] T006 [P] 템플릿에 상품명, 옵션명, 수량, 총 금액이 포함되는 테스트를 추가한다.
- [x] T007 [P] 주문 메시지가 있으면 템플릿에 메시지가 포함되는 테스트를 추가한다.
- [x] T008 [P] 주문 메시지가 null이면 메시지 영역이 생략되는 테스트를 추가한다.
- [x] T009 [P] 주문 메시지가 blank이면 메시지 영역이 생략되는 테스트를 추가한다.

## Phase 3: Template Builder

- [x] T010 `src/main/java/gift/order/KakaoMessageTemplateBuilder.java`를 추가한다.
- [x] T011 `build(Order order, Product product)` 메서드를 추가한다.
- [x] T012 기존 `buildTemplate()` 문자열 생성 로직을 builder로 이동한다.
- [x] T013 기존 총 금액 천 단위 포맷을 유지한다.
- [x] T014 기존 메시지 null/blank 생략 정책을 유지한다.

## Phase 4: Client Refactor

- [x] T015 `KakaoMessageClient`에 `KakaoMessageTemplateBuilder` 의존성을 추가한다.
- [x] T016 `sendToMe()`에서 builder를 통해 `template_object` 값을 생성하도록 변경한다.
- [x] T017 `KakaoMessageClient`의 private `buildTemplate()` 메서드를 제거한다.
- [x] T018 카카오 API endpoint/header/form key가 변경되지 않았는지 확인한다.

## Phase 5: Scope Check

- [x] T019 `OrderNotificationService` best-effort 정책이 변경되지 않았는지 확인한다.
- [x] T020 `KakaoMessageClient.sendToMe()` public API가 유지되는지 확인한다.
- [x] T021 JSON builder/ObjectMapper 도입이 이번 작업에 포함되지 않았는지 확인한다.
- [x] T022 비동기 처리, retry, 로깅 정책이 이번 작업에 포함되지 않았는지 확인한다.

## Phase 6: Validation

- [x] T023 `./gradlew.bat test --tests *Order*`를 실행한다.
- [x] T024 `rg "buildTemplate" src/main/java/gift/order/KakaoMessageClient.java`로 private 템플릿 메서드 제거를 확인한다.
- [x] T025 `rg "KakaoMessageTemplateBuilder" src/main/java/gift/order src/test/java/gift/order`로 builder 연결을 확인한다.

## Dependencies

- T001-T004 before T005-T009
- T005-T009 before T010-T018
- T010-T014 before T015-T018
- T015-T018 before T019-T025

## Parallel Example

```text
T006, T007, T008, T009 can be written independently in KakaoMessageTemplateBuilderTest.
```
