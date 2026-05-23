# 기능 명세서: Service 트랜잭션 경계 일관화 리팩토링

**Feature Branch**: `040-service-transaction-boundary-refactor`  
**작성일**: 2026-05-23  
**상태**: 초안  
**입력**: "트랜잭션 경계 일관화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 조회 서비스는 readOnly 트랜잭션으로 실행 (우선순위: P1)

개발자는 DB 조회만 수행하는 service 메서드가 `@Transactional(readOnly = true)` 경계 안에서 실행된다는 것을 코드에서 확인할 수 있어야 합니다.

**우선순위 이유**: 일부 service는 트랜잭션이 명시되어 있고 일부는 없어 일관성이 떨어집니다. 조회 메서드에 readOnly 의도를 명시하면 JPA flush 비용과 의도치 않은 변경 가능성을 줄일 수 있습니다.

**독립적 테스트**: 기존 조회 API/controller/service 테스트가 동일하게 통과해야 합니다.

**승인 시나리오**:

1. **Given** 상품 목록 조회 요청이 있을 때, **When** service가 조회를 수행하면, **Then** 기존 응답을 유지합니다.
2. **Given** 위시 목록 조회 요청이 있을 때, **When** service가 조회를 수행하면, **Then** 기존 응답을 유지합니다.

---

### 사용자 시나리오 2 - 생성/수정/삭제 서비스는 쓰기 트랜잭션으로 실행 (우선순위: P1)

개발자는 DB 변경을 수행하는 service 메서드가 `@Transactional` 경계 안에서 실행된다는 것을 코드에서 확인할 수 있어야 합니다.

**우선순위 이유**: 생성/수정/삭제는 여러 repository 호출과 도메인 변경을 포함할 수 있습니다. service 메서드 단위로 트랜잭션을 잡으면 실패 시 롤백 경계가 명확해집니다.

**독립적 테스트**: 기존 생성/수정/삭제 테스트가 동일하게 통과해야 합니다.

---

### 사용자 시나리오 3 - 기존 동작과 응답 계약 유지 (우선순위: P1)

트랜잭션 annotation 추가는 동작 변경을 목적으로 하지 않으며, 모든 API/관리자 화면 응답은 기존과 동일해야 합니다.

**우선순위 이유**: 이번 작업은 경계 명시 리팩토링입니다. HTTP status, error code, redirect, flash message가 바뀌면 안 됩니다.

**독립적 테스트**: 전체 테스트가 통과해야 합니다.

---

### 엣지 케이스

- 외부 API client 성격의 service는 이번 작업에서 제외합니다.
- 이미 method 단위 트랜잭션이 있는 `OrderService`는 기존 의도를 유지하되 필요 시 클래스 기본값을 맞춥니다.
- `CategoryService`는 클래스 레벨 `@Transactional`이 있으므로 readOnly 기본값으로 바꾸는 경우 쓰기 메서드 override를 함께 해야 합니다.
- controller/repository에는 트랜잭션 annotation을 추가하지 않습니다.
- 테스트 코드의 트랜잭션 annotation은 이번 작업 범위가 아닙니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: DB 접근 service는 기본적으로 `@Transactional(readOnly = true)`를 사용해야 합니다.
- **FR-002**: 생성 메서드는 `@Transactional`을 명시해야 합니다.
- **FR-003**: 수정 메서드는 `@Transactional`을 명시해야 합니다.
- **FR-004**: 삭제 메서드는 `@Transactional`을 명시해야 합니다.
- **FR-005**: 조회 메서드는 클래스 기본 readOnly 또는 method readOnly로 실행되어야 합니다.
- **FR-006**: `ProductService`, `ProductUseCaseService`, `AdminProductService`의 트랜잭션 경계를 명시해야 합니다.
- **FR-007**: `WishService`의 트랜잭션 경계를 명시해야 합니다.
- **FR-008**: `OptionService`의 트랜잭션 경계를 명시해야 합니다.
- **FR-009**: `MemberService`, `AdminMemberService`의 트랜잭션 경계를 명시해야 합니다.
- **FR-010**: `CategoryService`의 기존 트랜잭션 정책을 read/write 의도에 맞게 정리해야 합니다.
- **FR-011**: `OrderService`의 기존 트랜잭션 정책은 유지하거나 더 일관된 형태로 정리해야 합니다.
- **FR-012**: 기존 테스트 결과는 변경되지 않아야 합니다.

### 주요 엔티티

- **ProductService / ProductUseCaseService / AdminProductService**: 상품 조회/변경 트랜잭션 경계를 명시합니다.
- **WishService**: 위시 조회/추가/삭제 트랜잭션 경계를 명시합니다.
- **OptionService**: 옵션 조회/생성/수정/삭제 트랜잭션 경계를 명시합니다.
- **MemberService / AdminMemberService**: 회원 가입/로그인/관리/포인트 변경 트랜잭션 경계를 명시합니다.
- **CategoryService**: 기존 클래스 레벨 트랜잭션을 read/write 의도에 맞게 정리합니다.
- **OrderService**: 기존 주문 조회/생성 트랜잭션 정책을 유지합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: DB 조회 service는 readOnly 트랜잭션 의도가 코드에 명시됩니다.
- **SC-002**: DB 변경 service는 쓰기 트랜잭션 의도가 코드에 명시됩니다.
- **SC-003**: 기존 product/option/wish/member/category/order 테스트가 통과합니다.
- **SC-004**: 기존 관리자 화면 redirect/flash 테스트가 통과합니다.
- **SC-005**: `./gradlew.bat test`가 통과합니다.

## 가정사항

- 이번 작업은 트랜잭션 경계 명시이며 비즈니스 로직을 변경하지 않습니다.
- repository 자체의 기본 트랜잭션과 별개로 service 계층의 유스케이스 경계를 코드에 드러내는 것이 목적입니다.
- 외부 API 호출 중심 service는 DB 트랜잭션 대상에서 제외합니다.
