# Data Model: Order 서비스 분리 리팩토링

## OrderService

주문 목록 조회와 주문 생성 비즈니스 흐름을 담당합니다.

**Methods**:

- `Page<OrderResponse> getOrders(Long memberId, Pageable pageable)`
- `OrderResponse createOrder(Member member, OrderRequest request)`
- `boolean existsOption(Long optionId)` 또는 `Optional<OrderResponse>` 계열 결과 표현

**Responsibilities**:

- 회원별 주문 목록 조회
- 주문 대상 옵션 조회
- 옵션 재고 차감
- 회원 포인트 차감
- 주문 저장
- 카카오 알림 best-effort 발송

## OrderController

주문 API HTTP 요청을 처리합니다.

**Responsibilities**:

- authorization header에서 회원 추출
- 인증 실패 시 401 반환
- service 호출
- 옵션 미존재 시 404 반환
- 주문 생성 성공 시 201 Created 반환

**Non-Responsibilities**:

- repository 직접 접근
- option 재고 차감 직접 수행
- member 포인트 차감 직접 수행
- kakao message client 직접 호출

## OrderResponse

주문 API 응답 DTO입니다.

**Fields**:

- `id`
- `optionId`
- `quantity`
- `orderDateTime`
- `message`

## Relationships

- `OrderController`는 `OrderService`와 `AuthenticationResolver`에 의존합니다.
- `OrderService`는 `OrderRepository`, `OptionRepository`, `MemberRepository`, `KakaoMessageClient`에 의존합니다.
- `WishRepository`는 이번 작업에서 사용하지 않습니다.
- `Option.subtractQuantity()`와 `Member.deductPoint()`는 기존 도메인 메서드를 그대로 사용합니다.
