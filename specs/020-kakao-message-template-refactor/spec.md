# 기능 명세서: Kakao 메시지 템플릿 분리 리팩토링

**Feature Branch**: `020-kakao-message-template-refactor`  
**작성일**: 2026-05-20  
**상태**: 초안  
**입력**: "KakaoMessageClient 메시지 템플릿 분리"

## 사용자 시나리오 및 테스트 *(필수)*

### 사용자 시나리오 1 - 카카오 메시지 템플릿 생성 책임 분리 (우선순위: P1)

카카오 메시지 발송 client는 HTTP 요청 전송에 집중하고, 카카오 템플릿 JSON 생성은 별도 객체가 담당해야 합니다.

**우선순위 이유**: 현재 `KakaoMessageClient`는 HTTP 호출과 메시지 JSON 문자열 생성 책임을 동시에 가집니다. 템플릿 생성 책임을 분리하면 메시지 내용 변경을 독립적으로 테스트하고 관리할 수 있습니다.

**독립적 테스트**: `KakaoMessageTemplateBuilder`가 주문, 상품 정보를 받아 기존과 동일한 템플릿 문자열을 생성하는지 검증합니다.

**승인 시나리오**:

1. **Given** 주문, 옵션, 상품 정보가 있을 때, **When** 템플릿을 생성하면, **Then** 상품명, 옵션명, 수량, 금액이 포함됩니다.
2. **Then** 생성된 문자열은 카카오 API의 `template_object` 값으로 사용할 수 있습니다.

---

### 사용자 시나리오 2 - 주문 메시지가 있는 경우 템플릿에 메시지 포함 (우선순위: P1)

주문 메시지가 null 또는 blank가 아니면 템플릿 본문에 메시지를 포함해야 합니다.

**우선순위 이유**: 기존 카카오 메시지에는 주문자가 입력한 선물 메시지가 포함됩니다. 템플릿 생성 책임을 분리해도 이 동작이 바뀌면 안 됩니다.

**독립적 테스트**: 주문 메시지가 있는 주문으로 템플릿을 생성하면 메시지가 포함되는지 검증합니다.

**승인 시나리오**:

1. **Given** 주문 메시지가 `"선물 메시지"`일 때, **When** 템플릿을 생성하면, **Then** 템플릿에 `"선물 메시지"`가 포함됩니다.

---

### 사용자 시나리오 3 - 주문 메시지가 없는 경우 메시지 영역 생략 (우선순위: P1)

주문 메시지가 null 또는 blank이면 템플릿에서 메시지 영역을 생략해야 합니다.

**우선순위 이유**: 메시지는 선택값입니다. 메시지가 없을 때 불필요한 빈 문구가 포함되면 카카오 메시지가 어색해집니다.

**독립적 테스트**: 주문 메시지가 null 또는 blank인 주문으로 템플릿을 생성하면 메시지 prefix와 메시지 내용이 포함되지 않는지 검증합니다.

**승인 시나리오**:

1. **Given** 주문 메시지가 null일 때, **When** 템플릿을 생성하면, **Then** 메시지 영역이 포함되지 않습니다.
2. **Given** 주문 메시지가 blank일 때, **When** 템플릿을 생성하면, **Then** 메시지 영역이 포함되지 않습니다.

---

### 사용자 시나리오 4 - KakaoMessageClient의 HTTP 책임 유지 (우선순위: P2)

`KakaoMessageClient`는 기존처럼 카카오 API endpoint, Authorization header, form body 전송을 담당해야 합니다. 템플릿 생성은 분리하되 외부 호출 계약은 유지합니다.

**우선순위 이유**: 이번 작업은 책임 분리 리팩토링입니다. 카카오 API 호출 방식이나 주문 알림 발송 정책이 바뀌면 안 됩니다.

**독립적 테스트**: `KakaoMessageClient`가 template builder의 결과를 `template_object` form field로 사용하는지 검증합니다.

---

### 엣지 케이스

- 이번 작업은 템플릿 생성 책임 분리에 집중합니다.
- 카카오 API endpoint, header, request body key는 변경하지 않습니다.
- `OrderNotificationService`의 best-effort 정책은 변경하지 않습니다.
- 금액 포맷은 기존처럼 `String.format("%,d", totalPrice)`를 유지합니다.
- 템플릿 JSON 구조 자체를 더 정교한 JSON builder로 바꾸는 것은 이번 작업 범위에 포함하지 않습니다.

## 요구사항 *(필수)*

### 기능 요구사항

- **FR-001**: `KakaoMessageTemplateBuilder`를 추가해야 합니다.
- **FR-002**: `KakaoMessageTemplateBuilder`는 주문과 상품을 입력받아 카카오 템플릿 문자열을 생성해야 합니다.
- **FR-003**: 템플릿에는 상품명, 옵션명, 주문 수량, 총 금액이 포함되어야 합니다.
- **FR-004**: 총 금액 포맷은 기존처럼 천 단위 구분자를 포함해야 합니다.
- **FR-005**: 주문 메시지가 null 또는 blank가 아니면 템플릿에 메시지를 포함해야 합니다.
- **FR-006**: 주문 메시지가 null 또는 blank이면 템플릿에서 메시지 영역을 생략해야 합니다.
- **FR-007**: `KakaoMessageClient`는 템플릿 문자열 생성을 `KakaoMessageTemplateBuilder`에 위임해야 합니다.
- **FR-008**: `KakaoMessageClient`의 `sendToMe(accessToken, order, product)` public API는 유지해야 합니다.
- **FR-009**: 카카오 API endpoint, Authorization header, form field 이름 `template_object`를 유지해야 합니다.
- **FR-010**: 템플릿 builder 단위 테스트와 client 회귀 테스트를 추가/수정해야 합니다.

### 주요 엔티티

- **KakaoMessageTemplateBuilder**: 카카오 메시지 템플릿 문자열 생성을 담당합니다.
- **KakaoMessageClient**: 카카오 API HTTP 호출을 담당하며 템플릿 생성은 builder에 위임합니다.
- **Order**: 메시지, 수량, 옵션 정보를 제공합니다.
- **Product**: 상품명과 가격 정보를 제공합니다.

## 성공 기준 *(필수)*

### 측정 가능한 결과

- **SC-001**: `KakaoMessageClient`에 private `buildTemplate()` 메서드가 남지 않습니다.
- **SC-002**: `KakaoMessageTemplateBuilder` 테스트가 상품명, 옵션명, 수량, 금액 포함을 검증합니다.
- **SC-003**: 메시지가 있는 주문은 메시지 영역을 포함합니다.
- **SC-004**: 메시지가 null 또는 blank인 주문은 메시지 영역을 포함하지 않습니다.
- **SC-005**: `KakaoMessageClient.sendToMe()` public API는 변경되지 않습니다.
- **SC-006**: `./gradlew test --tests *Order*`가 통과합니다.

## 가정사항

- Order 알림 서비스 분리는 `017-order-notification-service-refactor`에서 완료되었습니다.
- 이번 작업은 카카오 메시지 템플릿 생성 책임 분리이며 HTTP 호출 정책, best-effort 정책, 비동기 처리 정책은 변경하지 않습니다.
