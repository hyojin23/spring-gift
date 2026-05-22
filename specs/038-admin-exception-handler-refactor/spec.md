# 기능 명세서: Admin 예외 처리 분리 리팩토링

**Feature Branch**: `038-admin-exception-handler-refactor`  
**작성일**: 2026-05-23  
**상태**: 초안  
**입력**: "AdminProduct/AdminMember 컨트롤러 예외 처리 방식 공통화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - AdminProductController 예외 처리 분리 (우선순위: P1)

관리자 상품 화면에서 상품/카테고리 관련 예외가 발생하면 기존처럼 flash error를 담아 `/admin/products`로 redirect되어야 하며, 이 예외 처리 코드는 controller 밖에 있어야 합니다.

**우선순위 이유**: `AdminProductController`는 요청 처리와 화면 예외 처리 정책을 함께 가지고 있습니다. controller는 화면 흐름에 집중하고, 예외 처리 정책은 별도 advice에서 담당하는 편이 책임이 선명합니다.

**독립적 테스트**: 존재하지 않는 상품 수정 화면 접근, 존재하지 않는 카테고리 상품 등록/수정 요청이 기존처럼 `/admin/products`로 redirect되고 flash error를 포함하는지 검증합니다.

**승인 시나리오**:

1. **Given** 존재하지 않는 상품 id가 있을 때, **When** 관리자 상품 수정 화면에 접근하면, **Then** `/admin/products`로 redirect되고 flash error가 전달됩니다.
2. **Given** 존재하지 않는 카테고리 id가 있을 때, **When** 관리자 상품 등록을 요청하면, **Then** `/admin/products`로 redirect되고 flash error가 전달됩니다.

---

### 사용자 시나리오 2 - AdminMemberController 예외 처리 분리 (우선순위: P1)

관리자 회원 화면에서 회원/포인트 관련 예외가 발생하면 기존처럼 flash error를 담아 `/admin/members`로 redirect되어야 하며, 이 예외 처리 코드는 controller 밖에 있어야 합니다.

**우선순위 이유**: `AdminMemberController`도 `@ExceptionHandler(MemberException.class)`를 직접 가지고 있습니다. admin 화면 예외 처리 정책을 별도 advice로 분리하면 controller가 요청 처리만 담당하게 됩니다.

**독립적 테스트**: 존재하지 않는 회원 수정/포인트 충전 요청, 유효하지 않은 포인트 충전 요청이 기존처럼 `/admin/members`로 redirect되고 flash error를 포함하는지 검증합니다.

---

### 사용자 시나리오 3 - REST API global handler와 분리 유지 (우선순위: P2)

admin 화면 예외 처리 advice는 REST API용 `GlobalExceptionHandler`와 섞이지 않아야 합니다.

**우선순위 이유**: REST API는 JSON `ErrorResponse`를 반환하고, admin 화면은 redirect + flash message를 반환합니다. 두 정책이 섞이면 응답 형식이 깨질 수 있습니다.

**독립적 테스트**: 기존 `GlobalExceptionHandlerTest`와 admin controller 테스트가 모두 통과해야 합니다.

---

### 엣지 케이스

- 예외 메시지는 변경하지 않습니다.
- redirect 경로는 기존과 동일하게 유지합니다.
- flash attribute key는 기존 `"error"`를 유지합니다.
- admin product/member의 form validation 흐름은 변경하지 않습니다.
- REST API 예외 처리 `GlobalExceptionHandler`는 변경하지 않습니다.
- 공통 helper 도입은 필요할 때 후속 작업으로 분리합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `AdminProductController`의 `@ExceptionHandler(AdminProductException.class)`를 제거해야 합니다.
- **FR-002**: `AdminProductExceptionHandler` 또는 동등한 advice를 추가해야 합니다.
- **FR-003**: `AdminProductException` 발생 시 flash attribute `"error"`에 예외 메시지를 담아야 합니다.
- **FR-004**: `AdminProductException` 발생 시 `redirect:/admin/products`를 반환해야 합니다.
- **FR-005**: `AdminMemberController`의 `@ExceptionHandler(MemberException.class)`를 제거해야 합니다.
- **FR-006**: `AdminMemberExceptionHandler` 또는 동등한 advice를 추가해야 합니다.
- **FR-007**: `MemberException` 발생 시 flash attribute `"error"`에 예외 메시지를 담아야 합니다.
- **FR-008**: `MemberException` 발생 시 `redirect:/admin/members`를 반환해야 합니다.
- **FR-009**: admin controller 테스트의 기존 redirect/flash 기대값은 유지되어야 합니다.
- **FR-010**: REST API global handler 동작은 변경하지 않아야 합니다.

### 주요 엔티티

- **AdminProductController**: 관리자 상품 화면 요청을 처리합니다.
- **AdminProductExceptionHandler**: 관리자 상품 화면 예외를 redirect + flash로 처리합니다.
- **AdminMemberController**: 관리자 회원 화면 요청을 처리합니다.
- **AdminMemberExceptionHandler**: 관리자 회원 화면 예외를 redirect + flash로 처리합니다.
- **GlobalExceptionHandler**: REST API 예외를 JSON `ErrorResponse`로 처리합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: admin product 예외 발생 시 `/admin/products` redirect와 flash error가 유지됩니다.
- **SC-002**: admin member 예외 발생 시 `/admin/members` redirect와 flash error가 유지됩니다.
- **SC-003**: `AdminProductController`에 `@ExceptionHandler`가 남아 있지 않습니다.
- **SC-004**: `AdminMemberController`에 `@ExceptionHandler`가 남아 있지 않습니다.
- **SC-005**: `./gradlew.bat test --tests *AdminProductController* --tests *AdminMemberController*`가 통과합니다.
- **SC-006**: 전체 테스트가 통과합니다.

## 가정사항

- admin 화면의 예외 처리 정책은 `redirect + flash error`로 유지합니다.
- REST API와 admin 화면 예외 처리는 서로 다른 응답 형식을 가지므로 별도 advice로 관리합니다.
- advice는 각 controller에 한정되도록 `assignableTypes`를 사용하는 방식을 우선합니다.
