# Research: Member 포인트 예외 정리 리팩토링

## Decision 1: 포인트 금액 오류는 InvalidMemberPointAmountException으로 표현한다

**Decision**: 충전/차감 금액이 0 이하이면 `InvalidMemberPointAmountException`을 발생시킵니다.

**Rationale**: 충전과 차감 모두 금액은 1 이상이어야 합니다. 같은 정책 위반이므로 하나의 예외 타입으로 표현합니다.

**Alternatives considered**:

- 충전/차감별 예외 분리: 현재 메시지와 처리가 동일하므로 과합니다.
- `IllegalArgumentException` 유지: 도메인 의미가 드러나지 않습니다.

## Decision 2: 포인트 부족은 InsufficientMemberPointException으로 분리한다

**Decision**: 보유 포인트보다 큰 금액을 차감하면 `InsufficientMemberPointException`을 발생시킵니다.

**Rationale**: 포인트 부족은 금액 자체가 잘못된 것이 아니라 회원 상태가 결제를 감당하지 못하는 상황입니다. 금액 오류와 별도 예외로 구분하는 것이 명확합니다.

**Alternatives considered**:

- `InvalidMemberPointAmountException` 재사용: 실패 원인을 구분하기 어렵습니다.
- order 패키지 예외로 이동: 현재 포인트 상태와 차감 책임은 Member 메서드에 있습니다.

## Decision 3: Admin/Order controller 처리는 이번 작업에서 변경하지 않는다

**Decision**: controller의 redirect/JSON 처리 정책은 변경하지 않습니다.

**Rationale**: AdminMemberController는 HTML redirect flow이고 OrderController는 주문 API flow입니다. 예외 처리 정책까지 한 번에 바꾸면 작업 범위가 넓어집니다.

**Alternatives considered**:

- GlobalExceptionHandler에 포인트 예외 추가: Order API 응답은 좋아지지만 Admin HTML flow와 섞일 수 있습니다.
- Admin redirect/flash까지 추가: AdminMemberController 서비스 분리 작업과 함께 다루는 편이 낫습니다.

## Decision 4: 포인트 검증은 변경 전에 수행한다

**Decision**: 금액 검증과 포인트 부족 검증은 `point` 필드 변경 전에 수행합니다.

**Rationale**: 예외 발생 시 기존 포인트가 유지되어야 합니다.

## Decision 5: 포인트 예외 메시지는 한글로 통일한다

**Decision**: 포인트 금액 오류 메시지는 `"포인트 금액은 1 이상이어야 합니다."`, 포인트 부족 메시지는 `"포인트가 부족합니다."`로 사용합니다.

**Rationale**: 기존 `chargePoint()`는 영어 메시지를 사용하고 `deductPoint()`는 한글 메시지를 사용해 같은 도메인 안에서 언어가 섞여 있습니다. API 응답, 로그, 관리자 화면에서 일관되게 이해할 수 있도록 한글로 통일합니다.

**Alternatives considered**:

- 기존 메시지 유지: 예외 타입은 정리되지만 사용자-facing 메시지의 일관성 문제가 남습니다.
- 영어로 통일: 현재 프로젝트의 주요 예외 메시지가 한글 중심이므로 어색합니다.
