# ADR-0001: 관리자 상품 화면 예외 처리 방식으로 Flash Attribute Redirect 사용

## 상태

Accepted

## 맥락

관리자 상품 화면에서 존재하지 않는 상품 또는 카테고리를 조회하는 경우가 있다.

예:

- 존재하지 않는 상품 수정 페이지 접근
- 상품 생성 또는 수정 시 존재하지 않는 카테고리 ID 전달

이 상황은 API 응답이 아니라 HTML 관리자 화면 흐름에서 발생한다. 사용자는 예외 발생 후에도 관리자 상품 목록 화면에서 작업을 이어갈 수 있어야 한다.

고려한 선택지는 다음과 같다.

1. `/admin/products`로 redirect하고 flash attribute로 오류 메시지를 전달한다.
2. `admin/error.html` 또는 공통 error view를 반환하고 HTTP 404 status를 설정한다.

## 결정

관리자 상품 화면에서는 상품 미존재 또는 카테고리 미존재 예외 발생 시 `/admin/products`로 redirect하고, flash attribute에 오류 메시지를 담아 사용자에게 안내한다.

예상 흐름:

1. `AdminProductService`에서 관리자 상품 도메인 예외가 발생한다.
2. `AdminProductController` 또는 관리자 전용 예외 핸들러에서 예외를 처리한다.
3. `RedirectAttributes`에 `error` 메시지를 추가한다.
4. `/admin/products`로 redirect한다.
5. 상품 목록 화면에서 flash message를 1회 표시한다.

## 이유

관리자 상품 화면은 API가 아니라 서버 렌더링 기반의 관리 UI다. 존재하지 않는 리소스에 접근했을 때 별도 오류 화면으로 사용자를 멈추게 하기보다, 상품 목록으로 돌려보내고 오류 메시지를 보여주는 편이 현재 화면 흐름에 더 자연스럽다.

또한 별도의 error view를 추가하지 않아도 되므로 변경 범위가 작고, 기존 관리자 상품 목록 화면을 중심으로 사용자 경험을 유지할 수 있다.

## 결과

장점:

- 구현 범위가 작다.
- 관리자 사용자가 상품 목록으로 돌아가 작업을 계속할 수 있다.
- 오류 메시지를 화면에 한 번만 표시할 수 있다.
- API 예외 처리와 HTML 화면 예외 처리를 분리할 수 있다.

단점:

- HTTP 상태 코드 관점에서는 404를 직접 반환하는 방식보다 의미가 약하다.
- 잘못된 URL 접근도 최종 응답은 redirect 후 200이 될 수 있다.
- 오류 전용 화면이 필요한 복잡한 관리자 기능에는 부족할 수 있다.

## 대안

### Error View + HTTP 404

존재하지 않는 상품 또는 카테고리 예외 발생 시 `admin/error.html` 또는 공통 error view를 반환하고 HTTP 404 상태를 설정하는 방식도 고려했다.

이 방식은 HTTP 의미론에 더 가깝고, 오류 상황을 명확하게 표현할 수 있다. 다만 현재 관리자 상품 화면은 단순 CRUD 흐름이고, 오류 후 사용자가 다시 상품 목록으로 이동해야 하는 경우가 많으므로 이번 리팩토링 범위에서는 redirect 방식이 더 적합하다고 판단했다.

## 적용 범위

- `AdminProductService`의 상품 미존재 예외
- `AdminProductService`의 카테고리 미존재 예외
- 관리자 상품 화면 요청
- `/admin/products` 목록 화면의 flash error message 표시

API 상품 조회, 수정, 삭제 예외 처리에는 이 결정을 적용하지 않는다.
