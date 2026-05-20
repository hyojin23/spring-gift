# Data Model: Order 위시리스트 정리 리팩토링

## Wish

회원이 관심 상품으로 등록한 항목입니다.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | wish 식별자 |
| memberId | Long | wish를 등록한 회원 ID |
| product | Product | 관심 상품 |

### 규칙

- 주문 성공 시 `memberId + product.id`가 주문 회원과 주문 상품에 일치하면 삭제 대상입니다.
- 주문 실패 시 삭제 대상이더라도 변경하지 않습니다.

## WishRepository

기존 repository를 사용합니다.

### 사용 메서드

```java
Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId)
void delete(Wish wish)
```

### 규칙

- cleanup 대상 조회 기준은 `memberId + productId`입니다.
- 조회 결과가 empty이면 `delete()`를 호출하지 않습니다.

## OrderService

주문 생성 flow와 주문 성공 후처리를 담당합니다.

### 변경 전 흐름

```text
1. option 조회
2. 재고 차감
3. 포인트 차감
4. 주문 저장
5. 주문 알림 발송
6. 응답 반환
```

### 변경 후 흐름

```text
1. option 조회
2. 재고 차감
3. 포인트 차감
4. 주문 저장
5. wish cleanup
6. 주문 알림 발송
7. 응답 반환
```

### 규칙

- 주문 저장 전 실패하면 cleanup을 수행하지 않습니다.
- cleanup 대상이 없으면 아무 작업도 하지 않습니다.
- cleanup 이후 기존처럼 `OrderNotificationService`를 호출합니다.
- controller 응답 계약은 변경하지 않습니다.
