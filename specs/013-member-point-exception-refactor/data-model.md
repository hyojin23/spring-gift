# Data Model: Member 포인트 예외 정리 리팩토링

## Member

회원 도메인 엔티티이며 포인트 충전/차감 정책을 직접 보호합니다.

**Fields**:

- `point`: 회원 보유 포인트

**Point Rules**:

- 충전 금액은 1 이상이어야 합니다.
- 차감 금액은 1 이상이어야 합니다.
- 차감 금액은 보유 포인트보다 클 수 없습니다.
- 예외 발생 시 기존 포인트는 유지되어야 합니다.

## InvalidMemberPointAmountException

포인트 충전/차감 금액이 1 미만인 상황을 표현합니다.

**Parent**:

- `MemberException`

**Message**:

- `"포인트 금액은 1 이상이어야 합니다."`

**Usage**:

- `Member.chargePoint(amount)`에서 `amount <= 0`
- `Member.deductPoint(amount)`에서 `amount <= 0`

## InsufficientMemberPointException

보유 포인트보다 큰 금액을 차감하려는 상황을 표현합니다.

**Parent**:

- `MemberException`

**Message**:

- `"포인트가 부족합니다."`

**Message Language Rule**:

- Member 포인트 예외 메시지는 한글로 작성합니다.

**Usage**:

- `Member.deductPoint(amount)`에서 `amount > point`

## Relationships

- `AdminMemberController`는 `Member.chargePoint()`를 호출합니다.
- `OrderController`는 `Member.deductPoint()`를 호출합니다.
- 두 controller의 예외 처리 정책은 이번 작업에서 변경하지 않습니다.
