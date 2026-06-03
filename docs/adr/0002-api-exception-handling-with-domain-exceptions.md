# ADR-0002: API 예외 처리 방식으로 도메인 예외와 GlobalExceptionHandler 사용

## 상태

Accepted

## 맥락

상품, 옵션, 회원, 주문, 위시, 카테고리 API에서 잘못된 요청이나 존재하지 않는 리소스 접근이 반복적으로 발생한다.

예:

- 존재하지 않는 상품, 옵션, 카테고리 조회
- 잘못된 상품명, 옵션명, 수량 전달
- 인증되지 않은 사용자 또는 권한이 없는 위시 접근
- 주문 생성 시 존재하지 않는 옵션 전달

이 상황을 각 컨트롤러에서 직접 `ResponseEntity`로 처리하면 HTTP 상태 코드, 오류 코드, 응답 본문 형식이 도메인마다 달라질 수 있다. API 사용자는 예외 상황에서도 일관된 JSON 오류 응답을 받아야 한다.

고려한 선택지는 다음과 같다.

1. 각 컨트롤러에서 예외 상황을 직접 판단하고 `ResponseEntity`를 반환한다.
2. 서비스 또는 도메인 계층에서 도메인 예외를 발생시키고 `GlobalExceptionHandler`에서 공통 `ErrorResponse`로 변환한다.
3. `IllegalArgumentException`, `NoSuchElementException` 같은 범용 예외를 그대로 사용하고 공통 핸들러에서 처리한다.

## 결정

API 예외 처리는 도메인별 예외를 발생시키고, `GlobalExceptionHandler`에서 HTTP 상태 코드와 `ErrorResponse`로 변환한다.

예상 흐름:

1. 서비스 또는 도메인 객체에서 비즈니스 규칙 위반이나 리소스 미존재를 감지한다.
2. `ProductNotFoundException`, `OptionValidationException`, `WishNotFoundException` 같은 도메인 예외를 발생시킨다.
3. `GlobalExceptionHandler`가 예외 타입에 맞는 HTTP 상태 코드와 오류 코드를 선택한다.
4. API 클라이언트에는 `ErrorResponse` 형식의 JSON 응답을 반환한다.

## 이유

API 오류 응답은 여러 도메인에서 반복적으로 사용되는 경계다. 컨트롤러마다 직접 응답을 만들면 오류 형식과 상태 코드가 쉽게 흩어지고, 새로운 도메인 예외가 추가될 때 기존 패턴을 따라가기 어렵다.

도메인 예외를 사용하면 예외의 의미가 코드에 드러난다. 예를 들어 단순한 `IllegalArgumentException`보다 `ProductCategoryNotFoundException`이 어떤 도메인 규칙이 깨졌는지 더 명확하게 표현한다.

또한 `GlobalExceptionHandler`를 중심으로 오류 응답을 변환하면 컨트롤러는 요청과 응답 흐름에 집중하고, 예외 응답 정책은 한 곳에서 관리할 수 있다.

## 결과

장점:

- API 오류 응답 형식을 일관되게 유지할 수 있다.
- 컨트롤러의 예외 처리 중복을 줄일 수 있다.
- 예외 타입만 보고 어떤 도메인 규칙이 위반되었는지 파악하기 쉽다.
- 새로운 도메인 예외를 추가할 때 처리 위치가 명확하다.

단점:

- 도메인별 예외 클래스 수가 늘어난다.
- `GlobalExceptionHandler`에 예외 매핑이 많아질 수 있다.
- 단순한 예외 상황에도 별도 예외 타입을 만들지 판단해야 한다.

## 대안

### 컨트롤러에서 직접 ResponseEntity 반환

각 컨트롤러 메서드에서 예외 상황을 판단하고 `ResponseEntity`를 직접 반환하는 방식도 고려했다.

이 방식은 흐름이 한 메서드 안에 있어 단순해 보인다. 다만 도메인이 늘어날수록 컨트롤러에 예외 처리 분기가 반복되고, 동일한 오류 상황이라도 응답 형식이나 상태 코드가 달라질 가능성이 높다.

### 범용 예외 사용

`IllegalArgumentException`, `NoSuchElementException` 같은 범용 예외를 그대로 사용하는 방식도 고려했다.

이 방식은 별도 예외 클래스를 만들지 않아 구현량이 적다. 다만 예외 타입만으로 도메인 의미를 구분하기 어렵고, 같은 범용 예외가 서로 다른 HTTP 상태 코드로 매핑되어야 할 때 처리 기준이 모호해진다.

## 적용 범위

- REST API 컨트롤러의 JSON 오류 응답
- 상품, 옵션, 회원, 주문, 위시, 카테고리 도메인 예외
- `GlobalExceptionHandler`
- `ErrorResponse`

관리자 HTML 화면의 redirect, flash message 기반 예외 처리는 이 결정을 적용하지 않는다.
