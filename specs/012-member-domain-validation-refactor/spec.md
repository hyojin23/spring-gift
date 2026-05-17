# 기능 명세서: Member 도메인 검증 강화 리팩토링

**Feature Branch**: `012-member-domain-validation-refactor`  
**작성일**: 2026-05-18  
**상태**: 초안  
**입력**: "Member 도메인 검증 강화"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 일반 회원 생성 시 공통 도메인 불변 조건 보장 (우선순위: P1)

일반 회원은 생성되는 순간부터 유효한 이메일과 비밀번호를 가져야 합니다. `Member(String email, String password)` 생성자는 email과 password의 필수 조건을 직접 검증해야 합니다.

**우선순위 이유**: 현재 `Member`는 생성자에서 값을 그대로 할당하므로 request/service 계층을 우회하면 유효하지 않은 회원 객체가 만들어질 수 있습니다.

**독립적 테스트**: 일반 회원 생성자에 유효하지 않은 email/password를 전달했을 때 member 도메인 예외가 발생하는지 검증합니다.

**승인 시나리오**:

1. **Given** 이메일이 비어 있을 때, **When** 일반 회원을 생성하면, **Then** member 검증 예외가 발생합니다.
2. **Given** 비밀번호가 비어 있을 때, **When** 일반 회원을 생성하면, **Then** member 검증 예외가 발생합니다.
3. **Given** 유효한 이메일과 비밀번호가 있을 때, **When** 일반 회원을 생성하면, **Then** 회원 객체가 생성됩니다.

---

### 사용자 시나리오 2 - 카카오 회원 생성 시 email 불변 조건 보장 (우선순위: P1)

카카오 OAuth 자동 가입 회원은 password가 없을 수 있지만, email은 반드시 유효해야 합니다. `Member(String email)` 생성자는 email 필수 조건을 직접 검증해야 합니다.

**우선순위 이유**: 카카오 로그인 흐름에서는 `new Member(email)`을 사용합니다. 일반 회원과 검증 조건이 다르므로 password를 강제하지 않되, email은 동일하게 보호해야 합니다.

**독립적 테스트**: 카카오 회원 생성자에 빈 email을 전달했을 때 member 도메인 예외가 발생하고, 유효한 email이면 password 없이 생성되는지 검증합니다.

**승인 시나리오**:

1. **Given** 이메일이 비어 있을 때, **When** 카카오 회원을 생성하면, **Then** member 검증 예외가 발생합니다.
2. **Given** 유효한 이메일이 있을 때, **When** 카카오 회원을 생성하면, **Then** password 없이 회원 객체가 생성됩니다.

---

### 사용자 시나리오 3 - 회원 정보 수정 시 도메인 불변 조건 보장 (우선순위: P1)

회원 수정 후에도 일반 회원은 유효한 email과 password를 유지해야 합니다. `Member.update(email, password)`는 일반 회원 생성자와 동일한 필수 조건을 검증해야 합니다.

**우선순위 이유**: 생성 시 검증만 있으면 수정 과정에서 잘못된 값으로 회원 상태가 깨질 수 있습니다.

**독립적 테스트**: `Member.update()`에 유효하지 않은 값을 전달했을 때 member 검증 예외가 발생하고 기존 상태가 일부만 변경되지 않는지 검증합니다.

**승인 시나리오**:

1. **Given** 기존 일반 회원이 있을 때, **When** 빈 이메일로 수정하면, **Then** member 검증 예외가 발생합니다.
2. **Given** 기존 일반 회원이 있을 때, **When** 빈 비밀번호로 수정하면, **Then** member 검증 예외가 발생합니다.
3. **Given** 수정 검증이 실패할 때, **Then** 기존 email/password 상태는 유지됩니다.

---

### 사용자 시나리오 4 - 포인트 정책은 후속 리팩토링으로 분리 (우선순위: P2)

`chargePoint()`와 `deductPoint()`의 `IllegalArgumentException`은 이번 작업에서 변경하지 않습니다. 포인트 금액 오류와 포인트 부족 예외는 order/member point 흐름과 함께 후속 spec에서 다룹니다.

**우선순위 이유**: 회원 식별 정보 검증과 포인트 거래 정책은 성격이 다릅니다. 한 번에 바꾸면 영향 범위가 커지고 order 패키지와의 연결까지 고려해야 합니다.

**독립적 테스트**: 이번 작업 후에도 기존 포인트 메서드 동작은 변경되지 않습니다.

---

### 엣지 케이스

- 일반 회원 생성자는 email/password를 모두 검증합니다.
- 카카오 회원 생성자는 email만 검증하고 password null은 허용합니다.
- `Member.update()`는 일반 회원 수정 흐름으로 보고 email/password를 모두 검증합니다.
- `updateKakaoAccessToken()` 검증은 이번 작업 범위에 포함하지 않습니다.
- 포인트 관련 `IllegalArgumentException`은 이번 작업 범위에 포함하지 않습니다.
- `MemberRequest` Bean Validation은 유지합니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: 일반 회원 생성자는 email null/blank를 허용하지 않아야 합니다.
- **FR-002**: 일반 회원 생성자는 password null/blank를 허용하지 않아야 합니다.
- **FR-003**: 카카오 회원 생성자는 email null/blank를 허용하지 않아야 합니다.
- **FR-004**: 카카오 회원 생성자는 password null을 허용해야 합니다.
- **FR-005**: `Member.update()`는 email null/blank를 허용하지 않아야 합니다.
- **FR-006**: `Member.update()`는 password null/blank를 허용하지 않아야 합니다.
- **FR-007**: 검증 실패 시 member 도메인 예외를 발생시켜야 합니다.
- **FR-008**: `Member.update()` 검증 실패 시 기존 상태가 부분 변경되지 않아야 합니다.
- **FR-009**: `MemberRequest`의 Bean Validation annotation은 유지해야 합니다.
- **FR-010**: 회원가입/로그인 API 테스트가 기존처럼 통과해야 합니다.

### 주요 엔티티

- **Member**: 회원 도메인 엔티티이며 일반 회원과 카카오 회원 생성 시 필요한 공통 식별 정보 검증을 담당합니다.
- **MemberValidationException**: Member 도메인 검증 실패를 표현합니다.
- **MemberRequest**: HTTP 요청 DTO 검증을 담당하며 Member 도메인 검증과 별개로 유지됩니다.
- **MemberService**: 회원가입/로그인 API flow를 담당하며 `Member` 생성자를 사용합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: 일반 회원 생성자에 빈 email/password가 전달되면 `MemberValidationException`이 발생합니다.
- **SC-002**: 카카오 회원 생성자에 빈 email이 전달되면 `MemberValidationException`이 발생합니다.
- **SC-003**: 카카오 회원 생성자는 password 없이 회원을 생성할 수 있습니다.
- **SC-004**: `Member.update()`에 빈 email/password가 전달되면 `MemberValidationException`이 발생합니다.
- **SC-005**: `Member.update()` 검증 실패 시 기존 상태가 유지됩니다.
- **SC-006**: `./gradlew test --tests *Member* --tests *Kakao*`가 통과합니다.

## 가정사항

- Member API service/exception 리팩토링은 `011-member-service-exception-refactor`에서 완료되었습니다.
- 비밀번호 암호화는 이번 작업 범위에 포함하지 않습니다.
- email 형식 검증은 request DTO와 외부 입력 검증에 맡기고, 도메인에서는 null/blank 불변 조건에 집중합니다.
- 포인트 예외 정리는 후속 spec에서 진행합니다.
