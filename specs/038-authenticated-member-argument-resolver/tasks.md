# Tasks: 인증 Member Argument Resolver 리팩토링

**Input**: Design documents from `/specs/038-authenticated-member-argument-resolver/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 현재 인증 header 직접 처리 위치를 확인한다: `src/main/java/gift/order/OrderController.java`, `src/main/java/gift/wish/WishController.java`
- [x] T002 기존 인증 컴포넌트 동작을 확인한다: `src/main/java/gift/auth/AuthenticatedMemberResolver.java`, `src/main/java/gift/auth/AuthenticationResolver.java`
- [x] T003 기존 인증 실패 handler 매핑을 확인한다: `src/main/java/gift/global/GlobalExceptionHandler.java`

## Phase 2: Tests First

- [x] T004 `src/test/java/gift/auth/AuthenticatedMemberArgumentResolverTest.java`를 추가한다.
- [x] T005 [P] `@Authenticated Member` 파라미터를 지원하는지 테스트한다.
- [x] T006 [P] 애노테이션 없는 `Member` 파라미터를 지원하지 않는지 테스트한다.
- [x] T007 [P] `@Authenticated`가 붙었지만 `Member` 타입이 아닌 파라미터를 지원하지 않는지 테스트한다.
- [x] T008 [P] 유효한 Authorization header에서 `Member`를 반환하는지 테스트한다.
- [x] T009 [P] null 또는 invalid Authorization header에서 `AuthenticationException`이 전파되는지 테스트한다.
- [x] T010 `OrderControllerTest`에서 argument resolver 기반 인증 성공/실패 흐름이 유지되는지 확인 또는 보강한다.
- [x] T011 `WishControllerTest`에서 argument resolver 기반 인증 성공/실패 흐름이 유지되는지 확인 또는 보강한다.

## Phase 3: Implementation

- [x] T012 `src/main/java/gift/auth/Authenticated.java` 애노테이션을 추가한다.
- [x] T013 `src/main/java/gift/auth/AuthenticatedMemberArgumentResolver.java`를 추가한다.
- [x] T014 `AuthenticatedMemberArgumentResolver.supportsParameter()`가 `@Authenticated Member`만 지원하도록 구현한다.
- [x] T015 `AuthenticatedMemberArgumentResolver.resolveArgument()`가 `Authorization` header를 읽고 `AuthenticatedMemberResolver.resolve()`에 위임하도록 구현한다.
- [x] T016 `src/main/java/gift/auth/WebMvcConfig.java` 또는 적절한 MVC 설정 클래스를 추가해 argument resolver를 등록한다.
- [x] T017 `src/main/java/gift/order/OrderController.java`에서 `@RequestHeader Authorization` 파라미터와 직접 resolve 호출을 제거한다.
- [x] T018 `src/main/java/gift/order/OrderController.java`가 `@Authenticated Member member`를 사용하도록 변경한다.
- [x] T019 `src/main/java/gift/wish/WishController.java`에서 `@RequestHeader Authorization` 파라미터와 직접 resolve 호출을 제거한다.
- [x] T020 `src/main/java/gift/wish/WishController.java`가 `@Authenticated Member member`를 사용하도록 변경한다.
- [x] T021 사용하지 않는 import와 생성자 의존성을 정리한다.

## Phase 4: Validation

- [x] T022 `.\gradlew.bat test --tests *AuthenticatedMemberArgumentResolver*`를 실행한다.
- [x] T023 `.\gradlew.bat test --tests *OrderController*`를 실행한다.
- [x] T024 `.\gradlew.bat test --tests *WishController*`를 실행한다.
- [x] T025 `.\gradlew.bat test`를 실행한다.
- [x] T026 `WishController`와 `OrderController`에 `@RequestHeader(value = "Authorization", required = false)`가 남아 있지 않은지 확인한다.

## Dependencies

- T001-T003 before T004-T011
- T004-T011 before T012-T021
- T012 before T013-T015
- T013-T015 before T016
- T016 before T017-T020 controller integration validation
- T017-T021 before T022-T026

## Parallel Opportunities

- T005-T009 can be written independently after T004.
- T010 and T011 can be reviewed or updated independently.
- T017-T018 and T019-T020 can be implemented independently after T016.

## Completion Criteria

- `@Authenticated Member`로 인증 회원이 controller에 주입됩니다.
- order/wish controller의 직접 Authorization header 처리가 제거됩니다.
- 인증 실패 응답 계약이 유지됩니다.
- 관련 테스트와 전체 테스트가 통과합니다.
