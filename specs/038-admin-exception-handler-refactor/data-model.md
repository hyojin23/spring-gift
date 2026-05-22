# Data Model: Admin 예외 처리 분리 리팩토링

## AdminProductExceptionHandler

### 패키지

`gift.product`

### 책임

- `AdminProductController`에서 발생한 `AdminProductException`을 처리합니다.
- flash attribute `"error"`에 예외 메시지를 담습니다.
- `redirect:/admin/products`를 반환합니다.

### 적용 범위

```java
@ControllerAdvice(assignableTypes = AdminProductController.class)
```

## AdminMemberExceptionHandler

### 패키지

`gift.member`

### 책임

- `AdminMemberController`에서 발생한 `MemberException`을 처리합니다.
- flash attribute `"error"`에 예외 메시지를 담습니다.
- `redirect:/admin/members`를 반환합니다.

### 적용 범위

```java
@ControllerAdvice(assignableTypes = AdminMemberController.class)
```

## AdminProductController

### 변경 전

- 요청 처리 method와 `@ExceptionHandler(AdminProductException.class)`를 함께 가집니다.

### 변경 후

- 요청 처리 method만 가집니다.
- 예외 처리는 `AdminProductExceptionHandler`가 담당합니다.

## AdminMemberController

### 변경 전

- 요청 처리 method와 `@ExceptionHandler(MemberException.class)`를 함께 가집니다.

### 변경 후

- 요청 처리 method만 가집니다.
- 예외 처리는 `AdminMemberExceptionHandler`가 담당합니다.

## 관계

```text
AdminProductController
  -> AdminProductException
  -> AdminProductExceptionHandler
  -> redirect:/admin/products + flash error

AdminMemberController
  -> MemberException
  -> AdminMemberExceptionHandler
  -> redirect:/admin/members + flash error
```
