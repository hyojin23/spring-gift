# Data Model: Kakao 메시지 템플릿 분리 리팩토링

## KakaoMessageTemplateBuilder

카카오 기본 메시지 API의 `template_object` 문자열을 생성합니다.

### Public API

```java
String build(Order order, Product product)
```

### 입력

| Field | Type | Description |
|-------|------|-------------|
| order | Order | 주문 수량, 옵션명, 주문 메시지 제공 |
| product | Product | 상품명, 가격 제공 |

### 출력 규칙

- `object_type`은 `"text"`를 유지합니다.
- `text`에는 상품명, 옵션명, 수량, 총 금액을 포함합니다.
- 총 금액은 `product.price * order.quantity`입니다.
- 총 금액은 천 단위 구분자를 포함합니다.
- 주문 메시지가 null 또는 blank가 아니면 메시지 영역을 포함합니다.
- 주문 메시지가 null 또는 blank이면 메시지 영역을 생략합니다.
- `button_title`은 기존 `"선물 확인하기"`를 유지합니다.

## KakaoMessageClient

카카오 API HTTP 호출을 담당합니다.

### 유지되는 API

```java
void sendToMe(String accessToken, Order order, Product product)
```

### 변경 사항

- 템플릿 생성은 `KakaoMessageTemplateBuilder`에 위임합니다.
- `template_object` form field 이름은 유지합니다.
- Authorization header와 endpoint는 유지합니다.

## OrderNotificationService

주문 완료 알림 발송 orchestration을 담당합니다.

### 유지 사항

- 카카오 access token이 없으면 client를 호출하지 않습니다.
- 카카오 메시지 발송 실패는 주문 flow로 전파하지 않습니다.
- 이번 작업에서 변경하지 않습니다.
