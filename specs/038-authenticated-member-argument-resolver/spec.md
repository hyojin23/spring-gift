# Feature Specification: Authenticated Member Argument Resolver

**Feature Branch**: `038-authenticated-member-argument-resolver`  
**Created**: 2026-05-23  
**Status**: Draft  
**Input**: User description: "인증 처리 중복 제거"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Controller에서 인증 회원을 직접 주입받기 (Priority: P1)

개발자는 인증이 필요한 API 컨트롤러 메서드에서 `Authorization` 헤더를 직접 받고 `AuthenticatedMemberResolver.resolve()`를 반복 호출하지 않아야 한다. 대신 컨트롤러 파라미터에 인증된 `Member`를 선언하면 프레임워크가 요청의 인증 정보를 해석해 주입해야 한다.

**Why this priority**: 현재 `WishController`와 `OrderController`는 같은 인증 처리 흐름을 각 메서드마다 반복한다. 인증 정책이 바뀌면 여러 컨트롤러를 함께 수정해야 하므로 중복 제거 효과가 가장 크다.

**Independent Test**: `WishControllerTest`와 `OrderControllerTest`에서 유효한 bearer token으로 요청했을 때 기존과 같은 응답이 반환되는지 검증한다. 컨트롤러 코드에는 `@RequestHeader("Authorization")` 기반 인증 추출 로직이 남아 있지 않아야 한다.

**Acceptance Scenarios**:

1. **Given** 유효한 bearer token이 있는 주문 목록 요청, **When** `GET /api/orders`를 호출하면, **Then** 컨트롤러는 주입받은 `Member`의 id로 주문 목록을 조회한다.
2. **Given** 유효한 bearer token이 있는 위시 목록 요청, **When** `GET /api/wishes`를 호출하면, **Then** 컨트롤러는 주입받은 `Member`의 id로 위시 목록을 조회한다.
3. **Given** 유효한 bearer token이 있는 위시 추가 요청, **When** `POST /api/wishes`를 호출하면, **Then** 컨트롤러는 주입받은 `Member`의 id와 요청 상품 id로 위시를 추가한다.

---

### User Story 2 - 인증 실패 응답 유지 (Priority: P1)

클라이언트는 인증 헤더가 없거나 유효하지 않은 경우 기존과 동일하게 401 `AUTH.UNAUTHORIZED` 응답을 받아야 한다.

**Why this priority**: 이번 작업은 구조 리팩토링이며 외부 API 계약을 바꾸면 안 된다. 인증 처리 위치가 컨트롤러에서 argument resolver로 이동해도 실패 응답 형식은 유지되어야 한다.

**Independent Test**: 인증 헤더 없음, blank header, 잘못된 bearer token으로 주문/위시 API를 호출해 401 status와 `AUTH.UNAUTHORIZED` code를 검증한다.

**Acceptance Scenarios**:

1. **Given** Authorization header가 없는 요청, **When** 인증 필수 API를 호출하면, **Then** 401 `AUTH.UNAUTHORIZED` 응답을 반환한다.
2. **Given** bearer 형식이 아닌 Authorization header, **When** 인증 필수 API를 호출하면, **Then** 401 `AUTH.UNAUTHORIZED` 응답을 반환한다.
3. **Given** 유효하지 않은 JWT bearer token, **When** 인증 필수 API를 호출하면, **Then** 401 `AUTH.UNAUTHORIZED` 응답을 반환한다.

---

### User Story 3 - 인증 필수 여부를 파라미터 선언으로 표현 (Priority: P2)

개발자는 컨트롤러 메서드 시그니처만 보고 해당 API가 인증된 회원을 요구한다는 사실을 알 수 있어야 한다. 인증 필수 파라미터에는 명시적인 애노테이션 또는 전용 타입을 사용한다.

**Why this priority**: 단순히 `Member` 타입만 보고 주입하면 향후 일반 `Member` 요청 바디나 모델 속성과 충돌할 수 있다. 인증 필수 파라미터임을 명시하면 의도가 분명하고 확장에 유리하다.

**Independent Test**: argument resolver 단위 테스트에서 지원 대상 파라미터와 비지원 파라미터를 구분하는지 검증한다.

**Acceptance Scenarios**:

1. **Given** 컨트롤러 메서드에 `@Authenticated Member member` 파라미터가 있을 때, **When** argument resolver가 파라미터 지원 여부를 확인하면, **Then** 해당 파라미터를 지원한다.
2. **Given** 애노테이션이 없는 `Member` 파라미터가 있을 때, **When** argument resolver가 파라미터 지원 여부를 확인하면, **Then** 해당 파라미터를 지원하지 않는다.

---

### Edge Cases

- Authorization header가 `null`, blank, bearer prefix 누락, 잘못된 JWT인 경우 모두 기존 인증 실패 흐름으로 처리한다.
- JWT는 유효하지만 이메일에 해당하는 회원이 없으면 인증 실패로 처리한다.
- 인증이 필요 없는 API는 이번 리팩토링으로 동작이 바뀌지 않아야 한다.
- `@Authenticated`가 붙은 파라미터 타입이 `Member`가 아니면 resolver가 지원하지 않거나 명확한 예외를 발생시켜야 한다.
- 기존 `AuthenticationResolver.extractMember()`의 null 반환 계약은 유지한다.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide an annotation for authenticated member parameters, e.g. `@Authenticated`.
- **FR-002**: System MUST provide a Spring MVC `HandlerMethodArgumentResolver` that supports only parameters marked with `@Authenticated` and typed as `Member`.
- **FR-003**: The argument resolver MUST read the request `Authorization` header and delegate member extraction to the existing authentication component.
- **FR-004**: The argument resolver MUST throw the existing authentication failure exception when no authenticated member can be resolved.
- **FR-005**: Authentication failure responses MUST remain 401 with error code `AUTH.UNAUTHORIZED`.
- **FR-006**: `WishController` MUST remove direct `Authorization` header parameters from authenticated endpoints.
- **FR-007**: `OrderController` MUST remove direct `Authorization` header parameters from authenticated endpoints.
- **FR-008**: `WishController` and `OrderController` MUST receive the authenticated member through the new argument resolver mechanism.
- **FR-009**: Existing request and response bodies for wish and order APIs MUST remain unchanged.
- **FR-010**: Existing service method behavior MUST remain unchanged except for receiving member information from a cleaner controller boundary.
- **FR-011**: Tests MUST cover resolver support rules, successful member resolution, and authentication failure.
- **FR-012**: Existing controller tests for order and wish APIs MUST continue to pass.

### Key Entities

- **Authenticated Annotation**: Marks a controller method parameter as requiring the currently authenticated `Member`.
- **AuthenticatedMemberArgumentResolver**: Converts the current HTTP request's Authorization header into a required authenticated `Member`.
- **AuthenticationResolver**: Existing component that parses Authorization header and returns a `Member` or `null`.
- **AuthenticationException**: Existing exception mapped to 401 `AUTH.UNAUTHORIZED`.
- **Member**: Authenticated domain entity passed to controllers.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: `WishController` has no `@RequestHeader(value = "Authorization", required = false)` parameters.
- **SC-002**: `OrderController` has no `@RequestHeader(value = "Authorization", required = false)` parameters.
- **SC-003**: Authenticated wish and order endpoints still return the same status codes and response bodies for successful requests.
- **SC-004**: Authenticated wish and order endpoints still return 401 `AUTH.UNAUTHORIZED` for missing or invalid authentication.
- **SC-005**: Argument resolver tests verify supported and unsupported parameter signatures.
- **SC-006**: `./gradlew.bat test --tests *AuthenticatedMemberArgumentResolver* --tests *OrderController* --tests *WishController*` passes.
- **SC-007**: `./gradlew.bat test` passes.

## Assumptions

- Existing `AuthenticationResolver` and `AuthenticatedMemberResolver` behavior is correct and should be reused rather than reimplementing JWT parsing.
- This refactor targets Spring MVC controller method arguments only.
- The API authentication contract remains header based: `Authorization: Bearer <token>`.
- Moving `AuthenticationException` from `gift.wish.exception` to `gift.auth.exception` is allowed only if imports and global exception handling remain behaviorally identical.
- Broader Spring Security adoption is outside the scope of this spec.
