# Data Model: 주문 알림 실패 로깅 리팩토링

## OrderNotificationService

**역할**: 주문 생성 후 카카오 메시지 발송을 best effort로 수행합니다.

**변경 전**:

- 카카오 메시지 발송 실패를 빈 catch로 무시합니다.

**변경 후**:

- 카카오 메시지 발송 실패를 warn 로그로 남깁니다.
- 예외는 전파하지 않습니다.

## Log Event

**level**:

- `WARN`

**포함 정보**:

- order id
- exception stack trace/cause

**제외 정보**:

- kakao access token

## Member

**관련 값**:

- `kakaoAccessToken`

**규칙**:

- null이면 메시지 발송을 시도하지 않습니다.

## Order

**관련 값**:

- `id`

**규칙**:

- 실패 로그의 추적 정보로 사용합니다.
