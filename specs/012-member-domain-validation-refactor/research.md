# Research: Member 도메인 검증 강화 리팩토링

## Decision 1: 일반 회원은 email/password를 모두 도메인에서 검증한다

**Decision**: `Member(String email, String password)`는 email과 password의 null/blank를 검증합니다.

**Rationale**: 일반 회원은 로그인에 비밀번호가 필요합니다. request DTO 검증을 우회해도 유효하지 않은 일반 회원이 만들어지지 않도록 Member가 기본 불변 조건을 보호해야 합니다.

**Alternatives considered**:

- `MemberRequest` 검증에만 의존: service를 우회하면 무효한 Member가 생성될 수 있습니다.
- service 검증에만 의존: Member 자체가 자기 상태를 보호하지 못합니다.

## Decision 2: 카카오 회원은 email만 검증하고 password null을 허용한다

**Decision**: `Member(String email)`은 email만 검증하고 password는 설정하지 않습니다.

**Rationale**: 현재 Kakao OAuth 자동 가입 flow는 `new Member(email)`을 사용합니다. 카카오 회원은 서비스 비밀번호가 없을 수 있으므로 일반 회원과 같은 password 필수 검증을 적용하면 안 됩니다.

**Alternatives considered**:

- 모든 Member에 password 필수 적용: 카카오 로그인 flow가 깨집니다.
- 카카오 회원 생성자를 제거: OAuth flow 변경 범위가 커집니다.

## Decision 3: Member.update는 일반 회원 수정 flow로 본다

**Decision**: `Member.update(email, password)`는 email/password를 모두 검증합니다.

**Rationale**: 현재 `AdminMemberController`의 회원 수정 flow에서 email/password를 함께 수정합니다. 이 메서드는 일반 회원 수정 의미에 가깝기 때문에 password 필수 검증을 적용합니다.

**Alternatives considered**:

- password null 허용: 일반 회원이 로그인 불가능한 상태로 수정될 수 있습니다.
- update 메서드 분리: 더 명확하지만 이번 작업보다 큰 리팩토링이 됩니다.

## Decision 4: MemberValidationException을 추가한다

**Decision**: 검증 실패는 `MemberValidationException`으로 표현합니다.

**Rationale**: 011 작업에서 member 예외 계층을 도입했습니다. 도메인 검증 실패도 이 체계에 맞춰 표현하면 `IllegalArgumentException` 사용을 줄이고 의도를 명확히 할 수 있습니다.

**Alternatives considered**:

- `IllegalArgumentException` 사용: 도메인 예외 체계와 맞지 않습니다.
- 기존 `DuplicateMemberEmailException` 재사용: 의미가 다릅니다.

## Decision 5: 포인트 예외는 이번 작업에서 제외한다

**Decision**: `chargePoint()`와 `deductPoint()`의 예외는 후속 spec에서 다룹니다.

**Rationale**: 포인트 충전/차감은 주문 결제 flow와 연결될 수 있는 별도 정책입니다. 회원 식별 정보 검증과 한 번에 바꾸면 영향 범위가 커집니다.
