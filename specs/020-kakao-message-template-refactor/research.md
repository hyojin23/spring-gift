# Research: Kakao 메시지 템플릿 분리 리팩토링

## 결정 1: `KakaoMessageTemplateBuilder`를 추가한다

**Decision**: 카카오 템플릿 문자열 생성은 `KakaoMessageTemplateBuilder`가 담당합니다.

**Rationale**: `KakaoMessageClient`는 외부 API 호출 client입니다. 템플릿 문자열 생성까지 담당하면 HTTP 전송 책임과 메시지 표현 책임이 섞입니다. builder로 분리하면 메시지 포맷 변경을 독립적으로 테스트할 수 있습니다.

**Alternatives Considered**:

- private method 유지: 클래스 수는 적지만 client 테스트 없이 템플릿만 검증하기 어렵습니다.
- `OrderNotificationService`로 이동: 알림 orchestration service에 문자열 생성 책임이 섞입니다.

## 결정 2: 문자열 템플릿 방식은 유지한다

**Decision**: 이번 작업에서는 기존 text block 기반 JSON 문자열 생성을 유지합니다.

**Rationale**: 목적은 책임 분리입니다. JSON builder나 ObjectMapper로 바꾸면 escaping, formatting, 테스트 기대값까지 함께 바뀔 수 있습니다.

**Alternatives Considered**:

- ObjectMapper로 Map 직렬화: 더 안전할 수 있지만 출력 포맷과 escape 방식이 바뀔 수 있습니다.
- 별도 DTO 도입: 카카오 템플릿 구조가 현재 단순해서 과합니다.

## 결정 3: 메시지 null/blank 처리 정책을 유지한다

**Decision**: 주문 메시지가 null 또는 blank이면 메시지 영역을 포함하지 않습니다.

**Rationale**: 기존 `buildTemplate()`은 null/blank 메시지를 생략합니다. 메시지는 선택값이므로 이 behavior를 유지해야 합니다.

**Alternatives Considered**:

- blank 메시지도 포함: 빈 선물 메시지 영역이 만들어져 사용자 경험이 어색합니다.
- message trim 적용: 기존 behavior를 바꿀 수 있어 이번 작업에서는 제외합니다.

## 결정 4: builder는 order 패키지에 둔다

**Decision**: `KakaoMessageTemplateBuilder`는 `gift.order` 패키지에 둡니다.

**Rationale**: 현재 카카오 메시지는 주문 완료 알림 전용입니다. 별도 notification 패키지를 만들기에는 범위가 작고, order 알림 흐름 안에서 관리하는 것이 단순합니다.

**Alternatives Considered**:

- `gift.notification` 패키지 생성: 장기 확장에는 좋지만 현재 기능 범위보다 큽니다.
- `gift.order.kakao` 하위 패키지 생성: 파일 수가 적어 아직 필요하지 않습니다.
