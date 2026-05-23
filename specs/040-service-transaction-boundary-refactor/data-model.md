# Data Model: Service 트랜잭션 경계 일관화 리팩토링

## Transaction Boundary

### 조회

```java
@Transactional(readOnly = true)
```

### 생성/수정/삭제

```java
@Transactional
```

## 적용 패턴

```java
@Service
@Transactional(readOnly = true)
public class SomeService {

    public List<Response> getItems() {
        ...
    }

    @Transactional
    public Response createItem(Request request) {
        ...
    }
}
```

## Service 분류

### DB 조회/쓰기 대상

- `CategoryService`
- `ProductService`
- `ProductUseCaseService`
- `AdminProductService`
- `OptionService`
- `WishService`
- `MemberService`
- `AdminMemberService`
- `OrderService`

### 제외 대상

- `KakaoAuthService`: 외부 카카오 로그인 흐름 중심
- `OrderNotificationService`: 외부 카카오 메시지 발송 중심

## 메서드 분류 예시

### Read

- `getProducts`
- `getProduct`
- `getCategories`
- `getWishes`
- `getOrders`
- `getMembers`

### Write

- `createProduct`
- `updateProduct`
- `deleteProduct`
- `addWish`
- `removeWish`
- `createOrder`
- `chargePoint`
- `register`
- `updateMember`
- `deleteMember`

## 성공 조건

- read 메서드는 readOnly 트랜잭션으로 실행됩니다.
- write 메서드는 쓰기 트랜잭션으로 실행됩니다.
- 기존 비즈니스 결과는 변하지 않습니다.
